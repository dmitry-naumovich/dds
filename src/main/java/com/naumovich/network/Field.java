package com.naumovich.network;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.naumovich.domain.Node;
import com.naumovich.domain.NodeThread;
import com.naumovich.util.MathOperations;
import com.naumovich.util.StatisticsCollector;
import lombok.Getter;


import static com.naumovich.configuration.ModelConfiguration.*;

public class Field extends JPanel {

    private static final Random rand = new Random();

    @Getter
    private static ArrayList<Node> nodes = new ArrayList<>();
    private static ArrayList<NodeThread> nodeThreads = new ArrayList<>();
    @Getter
    private static int[][] edgesMatrix;

    private boolean paused;

    public Field() {
        setBackground(Color.WHITE);
        new Timer(10, ev -> repaint()).start();
    }

    public void addNodesToField(int amount) {
        pause();
        int totalNodesAmount = amount;
        if (!nodes.isEmpty()) {
            totalNodesAmount += nodes.size();
        }
        edgesMatrix = new int[totalNodesAmount][totalNodesAmount];
        for (int i = 0; i < amount; i++) {
            addNodeThread();
        }
        resume();
    }

    public static Node getNodeByLogin(String login) {
        return nodes.get(Integer.valueOf(login.substring(4)));
    }
    public static Node getNodeByPersNum(int persNum) { return nodes.get(persNum); }

    private void addNodeThread() {
        NodeThread newNodeThread = new NodeThread(this);
        nodeThreads.add(newNodeThread);
        nodes.add(newNodeThread.getNode());
        new Thread(newNodeThread).start();
    }

    public void setEdgesMatrixCell(int i, int j, int value) {
        edgesMatrix[i][j] = value;        // case it's simple array
    }

    public void distributeFiles() {
        for (int i = 0; i < FILES_AMOUNT_TO_DISTRIBUTE; i++) {
            nodeThreads.get(rand.nextInt(nodes.size())).setDistributeFlag(true);
        }

    }

    public void turnOffSomeNodes() {
        for (int i = 0; i < NODES_AMOUNT_TO_TURN_OFF; i++) {
            nodes.get(new Random().nextInt(nodes.size())).setOnline(false);
        }
    }

    public void turnOnAllNodes() {
        for (Node n : nodes) {
            n.setOnline(true);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        //g.drawString("", x, y);
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
        for (NodeThread n : nodeThreads) {
            n.paint(canvas); // draw the node as the ball
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(Integer.toString(n.getNode().getPersNum()), (int) (n.getX() - RADIUS), (int) (n.getY() + RADIUS));
        }
        //canvas.drawLine(x1, y1, x2, y2);
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void canIMove() throws InterruptedException {
        if (paused) {
            wait();
        }
    }

    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    public void showEdgesMatrix() {
        MathOperations.printEdgesMatrix(edgesMatrix);
    }

    public void collectStatistics() {
        StatisticsCollector.collectStatistics(nodes);
    }

}
