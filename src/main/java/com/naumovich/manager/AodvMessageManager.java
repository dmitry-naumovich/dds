package com.naumovich.manager;

import com.naumovich.domain.Node;
import com.naumovich.domain.message.Message;

/**
 * Created by dzmitry on 4.5.17.
 */
public class AodvMessageManager {

    public void receiveMessage(Node owner, Message m) {
        switch (m.getClass().getSimpleName()) {
            case "ChunkMessage":
                break;
            case "RouteRequest":
                break;
            case "RouteReply":
                break;
            case "RouteError":
                break;
            case "BackupMessage":
                break;

        }
    }
}
