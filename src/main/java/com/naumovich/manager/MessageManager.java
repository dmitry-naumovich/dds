package com.naumovich.manager;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.NodeThread;
import com.naumovich.domain.message.ChunkMessage;
import com.naumovich.domain.message.Message;
import com.naumovich.network.MessageContainer;
import com.naumovich.util.Dijkstra;
import com.naumovich.util.tuple.TwoTuple;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Slf4j
public class MessageManager {

    private static final String MESSAGE_TYPE_CHUNK = "ChunkMessage";
    private static final String MESSAGE_TYPE_BACKUP = "BackupMessage";

    private Node owner;
    private List<Message> backupMessages = new ArrayList<>();

    public MessageManager(Node owner) {
        this.owner = owner;
    }

    public void checkMessageContainer() {
        List<Message> msgs = MessageContainer.allMsgs;
        synchronized (msgs) {
            for (Iterator<Message> iterator = msgs.iterator(); iterator.hasNext(); ) {
                Message m = iterator.next();
                owner.incrementAmountOfMsgChecks();
                if (owner.equals(m.getPath().get(0)) && owner.equals(m.getDestination())) { // receive and store
                    receiveMessage(owner, m);
                    iterator.remove();
                } else if (owner.equals(m.getPath().get(0)) && !owner.equals(m.getDestination())) { // retransmit
                    owner.incrementAmountOfNodeStatusChecks();
                    if (!m.getDestination().isOnline()) {
                        log.debug(owner.getLogin() + ": WARNING! While retransmitting I've found out " + m.getDestination() + " is offline. And I discard this");
                        iterator.remove();
                    } else if (m.getPath().get(1).isOnline()) {
                        retransmitMessage(owner, m);
                    } else {
                        findNewPathAndRetransmit(owner, m);
                    }
                }
            }
        }
    }

    public void receiveMessage(Node owner, Message m) {
        switch (m.getClass().getSimpleName()) {
            case MESSAGE_TYPE_CHUNK:
                log.debug(owner.getLogin() + ": I accept " + m.getData() + " to store it");
                owner.getChunkStorage().add((Chunk) m.getData());
                break;
            case MESSAGE_TYPE_BACKUP:
                backupMessage(owner, m);
                break;
        }
    }

    public void backupMessage(Node owner, Message m) {
        Node receiver = ((TwoTuple<Node, Chunk>) m.getData()).first;
        Chunk chunkToCopy = ((TwoTuple<Node, Chunk>) m.getData()).second;

        Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
        dijAlg.execute(owner);
        List<Node> path = dijAlg.getPath(receiver);
        owner.incrementAmountOfFindingPath();
        log.debug(owner.getLogin() + ": I have to complete backup of " + chunkToCopy + " to " + receiver
                + ". Path is: " + path);
        if (path != null) {
            Message newM = new ChunkMessage(path, owner.getChunkStorage().getChunkByName(chunkToCopy.getChunkName()));
            newM.excludeFirstNodeFromPath();
            owner.getNodeThread().setBackupFlag(true);
            backupMessages.add(newM);
        }
    }

    public void retransmitMessage(Node owner, Message m) {
        owner.incrementAmountOfNodeStatusChecks();
        switch (m.getClass().getSimpleName()) {
            case MESSAGE_TYPE_CHUNK:
                log.debug(owner.getLogin() + ": I retransmit " + m.getData() + " further");
                break;
            case MESSAGE_TYPE_BACKUP:
                log.debug(owner.getLogin() + ": I retransmit BackupMessage of " + ((TwoTuple<NodeThread, Chunk>) m.getData()).second + " further");
                break;
        }
        owner.incrementAmountOfRestransmitted();
        m.excludeFirstNodeFromPath(); // i.e. retransmit
    }

    public void findNewPathAndRetransmit(Node owner, Message m) {
        owner.incrementAmountOfNodeStatusChecks();
        //log.debug(owner.getLogin() + ": WARNING! While retransmitting I've found out " + m.getPath().get(1) + " is offline");
        // no action needed because source checks other nodes' status itself
        //iterator.remove();
        Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
        dijAlg.execute(owner);
        List<Node> path = dijAlg.getPath(m.getDestination());
        owner.incrementAmountOfFindingPath();
        log.debug(owner.getLogin() + ": WARNING! While retransmitting I've found out " + m.getPath().get(1)
                + " is offline. New path to destination " + m.getDestination() + " is: " + path);
        if (path != null) {
            m.setPath(path);
            m.excludeFirstNodeFromPath();
        }
    }

    public void makeBackup() {
        List<Message> messages = MessageContainer.allMsgs;
        synchronized (messages) {
            messages.addAll(backupMessages);
        }
    }
}
