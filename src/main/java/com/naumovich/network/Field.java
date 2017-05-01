package com.naumovich.network;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.naumovich.domain.Node;
import com.naumovich.domain.NodeThread;
import com.naumovich.util.MathOperations;

import static com.naumovich.network.TestNetwork.NODES_NUM;

public class Field extends JPanel {

    private static ArrayList<Node> nodes = new ArrayList<>(NODES_NUM);
    private static ArrayList<NodeThread> nodeThreads = new ArrayList<>(NODES_NUM);
    private static final Random rand = new Random();

    private static int[][] edgesMatrix = new int[NODES_NUM][NODES_NUM];

    private boolean paused;

    public Node getNode(int i) {
        return nodes.get(i);
    }

    public NodeThread getNodeThread(int i) {
        return nodeThreads.get(i);
    }

    public int getNodeAmount() {
        return nodes.size();
    }

    public static ArrayList<NodeThread> getNodeThreads() {
        return nodeThreads;
    }

    public static ArrayList<Node> getNodes() {
        return nodes;
    }

	/*public static ArrayList<NodeThread> getOnlineNodes() {
		ArrayList<NodeThread> onNodes = new ArrayList<NodeThread>();
		for (NodeThread n: nodes) {
			if (n.isOnline()) {
				onNodes.add(n);
			}
		}
		return onNodes;
	}*/

    public Field() {
        setBackground(Color.WHITE);
        new Timer(10, ev -> repaint()).start();
    }

    public void addNodeThread() {
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
            nodeThreads.get(rand.nextInt(NODES_NUM)).setDistributeFlag(true);
        }

    }

    public void turnOffSomeNodes() {
        for (int i = 0; i < 10; i++) {
            nodes.get(new Random().nextInt(NODES_NUM)).setOnline(false);
        }
    }

    public void turnOnAllNodes() {
        for (Node n : nodes) {
            n.setOnline(true);
        }
    }

    public void collectStatistics() {
        StatisticsCollector.collectStatistics(nodes);
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

    synchronized void showEdgesMatrix() {
        MathOperations.printEdgesMatrix(edgesMatrix);
    }

}
