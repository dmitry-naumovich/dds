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

/**
 * This class describes the Field area JPanel of the model. It is responsible for painting all components, storing all
 * nodes and node threads lists, creating node threads and starting them.
 */
public class Field extends JPanel {

    /**
     * Random object for generating random numbers
     */
    private static final Random rand = new Random();

    /**
     * The list containing all network nodes
     */
    @Getter
    private static ArrayList<Node> nodes = new ArrayList<>();

    /**
     * The list containing all created node threads
     */
    private static ArrayList<NodeThread> nodeThreads = new ArrayList<>();

    /**
     * The edges matrix (see graph's notation)
     */
    @Getter
    private static int[][] edgesMatrix;

    /**
     * The flag which shows whether the field is paused or not
     */
    private boolean paused;

    /**
     * Sets background color, creates and starts the repainting timer
     */
    public Field() {
        setBackground(Color.WHITE);
        new Timer(10, ev -> repaint()).start();
    }

    /**
     * Adds the specified amount of nodes to the field
     *
     * @param amount amount of nodes to add
     */
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

    /**
     * Gets node by its login
     * @param login node's login
     * @return found node
     */
    public static Node getNodeByLogin(String login) {
        return nodes.get(Integer.valueOf(login.substring(4)));
    }

    /**
     * Gets node by its id
     * @param id node's id
     * @return found node
     */
    public static Node getNodeById(int id) { return nodes.get(id); }

    /**
     * Creates new node thread, adds it to the collection and starts it
     */
    private void addNodeThread() {
        NodeThread newNodeThread = new NodeThread(this);
        nodeThreads.add(newNodeThread);
        nodes.add(newNodeThread.getNode());
        new Thread(newNodeThread).start();
    }

    /**
     * Sets specified value to the edges matrix cell
     * @param i row index
     * @param j column index
     * @param value the value to be set to the matrix cell
     */
    public void setEdgesMatrixCell(int i, int j, int value) {
        edgesMatrix[i][j] = value;        // case it's simple array
    }

    /**
     * Sets distribute flag to true for the defined amount of random nodes
     */
    public void distributeFiles() {
        for (int i = 0; i < FILES_AMOUNT_TO_DISTRIBUTE; i++) {
            nodeThreads.get(rand.nextInt(nodes.size())).setDistributeFlag(true);
        }
    }

    /**
     * Sets online flag to false for the defined amount of random nodes
     */
    public void turnOffSomeNodes() {
        for (int i = 0; i < NODES_AMOUNT_TO_TURN_OFF; i++) {
            nodes.get(rand.nextInt(nodes.size())).setOnline(false);
        }
    }

    /**
     * Sets online flag to true for all nodes
     */
    public void turnOnAllNodes() {
        nodes.forEach(node -> node.setOnline(true));
    }

    /**
     * Paints the component on the field
     * @param g Graphics used for painting
     */
    @Override
    public void paintComponent(Graphics g) {
        //g.drawString("", x, y);
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
        for (NodeThread n : nodeThreads) {
            n.paint(canvas); // draw the node as the ball
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(Integer.toString(n.getNode().getId()), (int) (n.getX() - RADIUS), (int) (n.getY() + RADIUS));
        }
        //canvas.drawLine(x1, y1, x2, y2);
    }

    /**
     * Sets paused flag to true
     */
    public synchronized void pause() {
        paused = true;
    }

    /**
     * Invokes wait() method on current Field object if paused flag is set to true
     * @throws InterruptedException if interrupted
     */
    public synchronized void canIMove() throws InterruptedException {
        if (paused) {
            wait();
        }
    }

    /**
     * Sets paused flag to false and invokes notifyAll() for current Field object
     */
    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    /**
     * Invokes MathOperations::printEdgesMatrix method passing edges matrix to it
     */
    public void showEdgesMatrix() {
        MathOperations.printEdgesMatrix(edgesMatrix);
    }

    /**
     * Invokes StatisticsCollector::collectStatistics methods passing nodes list to it
     */
    public void collectStatistics() {
        StatisticsCollector.collectStatistics(nodes);
    }

}
