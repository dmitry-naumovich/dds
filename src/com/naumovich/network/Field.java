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
	
	public static final String STATISTICS_FILE_1 = "statistics.txt";
	public static final String STATISTICS_FILE_2 = "statistics2.txt";
	
	private static ArrayList<ArrayList<Integer>> edgesMatrix = makeMatrix();
	//private int[][] edgMatrix = new int[NODES_NUM][NODES_NUM];
	
	public boolean paused;
	
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
	private Timer repaintTimer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			repaint();
		}
	});
	
	public Field() {
		setBackground(Color.WHITE);
		repaintTimer.start();
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
			nodeThreads.get(new Random().nextInt(NODES_NUM)).setDistributeFlag(true);
		}
		
	}
	public void turnOffNodes() {
		for (int i = 0; i < 10; i++) {
			nodes.get(new Random().nextInt(NODES_NUM)).setOnline(false);
		}
	}
	public void turnOnAllNodes() {
		for (Node n: nodes) {
			n.setOnline(true);
		}
	}
	public void collectStatistics() {
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
		for (Node n : nodes) {
			ArrayList<Integer> row = new ArrayList<Integer>();
			row.add(0, n.getPersNum()); // first column - node's number
			row.add(1, n.getChunkStorage().size()); // second column - number of storing chunks
			row.add(2, n.getAmountOfRestransmitted()); // third column - number of retransmissions made
			list.add(row);
		}
		writeToFile(list, STATISTICS_FILE_1);
		
		long path = 0; long msg = 0; long status = 0;
		for (Node n : nodes) {
			path += n.getAmountOfFindingPath();
			msg += n.getAmountOfMsgChecks();
			status += n.getAmountOfNodeStatusChecks();
		}
		writeToFile(new long[] {path / nodes.size(), msg / nodes.size(), status / nodes.size()}, STATISTICS_FILE_2);
	}
	private void writeToFile(ArrayList<ArrayList<Integer>> list, String fileName) {
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
		    for (int i = 0; i < list.size(); i++) {
		    	for (int j = 0; j < list.get(0).size(); j++) {
		    		writer.append(list.get(i).get(j).toString() + ";");
		    	}
		    	writer.append("\r\n");
		    	
		    }
		} catch (IOException ex) {
		  ex.toString();
		} finally {
		   try {
			   writer.close();
		   } catch (Exception ex) {
			   ex.toString();
		   }
		}
	}
	private void writeToFile(long[] array, String fileName) {
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
		    for (int i = 0; i < array.length; i++) {
		    		writer.append(String.valueOf(array[i]) + ";");
		    }
		} catch (IOException ex) {
		  ex.toString();
		} finally {
		   try {
			   writer.close();
		   } catch (Exception ex) {
			   ex.toString();
		   }
		}
	}
	public void paintComponent(Graphics g) {
		//System.out.println(g.getClass().getSimpleName().toString());
		//g.drawString("", x, y);
		super.paintComponent(g);
		
		Graphics2D canvas = (Graphics2D) g;
		for (NodeThread n: nodeThreads) {
			n.paint(canvas); // draw the node as the ball
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(Integer.toString(n.getNode().getPersNum()), (int)(n.getX() - NodeThread.getRadius()), (int)(n.getY() + NodeThread.getRadius()));
			
		}
		//canvas.drawLine(x1, y1, x2, y2);
	}
	public synchronized void pause() {
		paused = true;
	}
	public synchronized void canMove(NodeThread node) throws InterruptedException {
		if (paused) {
			wait();
		}
	}
	public synchronized void resume() {
		paused = false;
		notifyAll();
	}
	
	public synchronized void showEdgesMatrix() {
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
