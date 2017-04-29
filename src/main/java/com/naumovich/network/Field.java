package com.naumovich.network;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.naumovich.domain.Node;
import com.naumovich.domain.NodeThread;

import static com.naumovich.network.TestNetwork.NODES_NUM;

@SuppressWarnings("serial")
public class Field extends JPanel {

    private static ArrayList<Node> nodes = new ArrayList<>(NODES_NUM);
    private static ArrayList<NodeThread> nodeThreads = new ArrayList<>(NODES_NUM);
    private static final Random rand = new Random();


    private static ArrayList<ArrayList<Integer>> edgesMatrix = makeMatrix();
    //private int[][] edgMatrix = new int[NODES_NUM][NODES_NUM];

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

    public static ArrayList<ArrayList<Integer>> makeMatrix() {
        ArrayList<ArrayList<Integer>> edgesMatrix = new ArrayList<>();
        for (int i = 0; i < NODES_NUM; i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
            for (int j = 0; j < NODES_NUM; j++) {
                tmp.add(0);
            }
            edgesMatrix.add(tmp);
        }
        return edgesMatrix;

    }

    public void addNodeThread() {
        NodeThread newNodeThread = new NodeThread(this);
        nodeThreads.add(newNodeThread);
        nodes.add(newNodeThread.getNode());
    }

    public static ArrayList<ArrayList<Integer>> getEdgesMatrix() {
        return edgesMatrix;
    }

    public void setEdgesMatrixCell(int i, int j, int value) {
        edgesMatrix.get(j).set(i, value); // case it's ArrayList
        //edgMatrix[i][j] = value;        // case it's simple array
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
        for (int i = 0; i < NODES_NUM; i++) {
            for (int j = 0; j < NODES_NUM; j++) {
                if (i == j) System.out.print("- ");
                else
                    System.out.print(edgesMatrix.get(i).get(j) + " ");
            }
            System.out.print("\n");
        }
    }


}
