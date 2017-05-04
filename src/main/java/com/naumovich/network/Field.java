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

public class Field extends JPanel {

    private static final Random rand = new Random();

    private static ArrayList<Node> nodes = new ArrayList<>();
    private static ArrayList<NodeThread> nodeThreads = new ArrayList<>();
    private static int[][] edgesMatrix;


    private boolean paused;

    public Field() {
        setBackground(Color.WHITE);
        new Timer(10, ev -> repaint()).start();
    }

    public static ArrayList<Node> getNodes() {
        return nodes;
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

    private void addNodeThread() {
        NodeThread newNodeThread = new NodeThread(this);
        nodeThreads.add(newNodeThread);
        nodes.add(newNodeThread.getNode());
        new Thread(newNodeThread).start();
    }

    public static int[][] getEdgesMatrix() {
        return edgesMatrix;
    }

    public void setEdgesMatrixCell(int i, int j, int value) {
        edgesMatrix[i][j] = value;        // case it's simple array
    }

    public void distributeFiles() {
        for (int i = 0; i < 5; i++) {
            nodeThreads.get(rand.nextInt(nodes.size())).setDistributeFlag(true);
        }

    }

    public void turnOffSomeNodes() {
        for (int i = 0; i < 10; i++) {
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
            g.drawString(Integer.toString(n.getNode().getPersNum()), (int) (n.getX() - NodeThread.getRadius()), (int) (n.getY() + NodeThread.getRadius()));
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
