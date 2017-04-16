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

import com.naumovich.entity.NodeThread;

@SuppressWarnings("serial")
public class Field extends JPanel {
	public static final int NNUM = com.naumovich.network.TestNetwork.NODES_NUM;
	private static ArrayList<NodeThread> nodes = new ArrayList<NodeThread>(NNUM);
	public static final String STATISTICS_FILE_1 = "statistics.txt";
	public static final String STATISTICS_FILE_2 = "statistics2.txt";
	Random rand = new Random();
	private static ArrayList<ArrayList<Integer>> edgesMatrix = makeMatrix();
	//private int[][] edgMatrix = new int[NNUM][NNUM];
	
	public boolean paused;
	
	public NodeThread getNode(int i) {
		return nodes.get(i);
	}
	public int getNodeAmount() {
		return nodes.size();
	}
	public static ArrayList<NodeThread> getNodes() {
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
		ArrayList<ArrayList<Integer>> edgesMatrix = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < NNUM; i++) {
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			for (int j = 0; j < NNUM; j++) {
				tmp.add(0);
			}
			edgesMatrix.add(tmp);
		}
		return edgesMatrix;

	}
	public void addNode() {
		nodes.add(new NodeThread(this));
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
			nodes.get(rand.nextInt(NNUM)).setDistributeFlag(true);
		}
		
	}
	public void turnOffNodes() {
		for (int i = 0; i < 10; i++) {
			nodes.get(rand.nextInt(NNUM)).getNode().setOnline(false);
		}
	}
	public void turnOnAllNodes() {
		for (NodeThread n: nodes) {
			n.getNode().setOnline(true);
		}
	}
	public void collectStatistics() {
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
		for (NodeThread n : nodes) {
			ArrayList<Integer> row = new ArrayList<Integer>();
			row.add(0, n.getNode().getPersNum()); // first column - node's number
			row.add(1, n.getNode().getChunkStorage().size()); // second column - number of storing chunks
			row.add(2, n.getNode().getAmountOfRestransmitted()); // third column - number of retransmissions made
			list.add(row);
		}
		writeToFile(list, STATISTICS_FILE_1);
		
		long path = 0; long msg = 0; long status = 0;
		for (NodeThread n : nodes) {
			path += n.getNode().getAmountOfFindingPath();
			msg += n.getNode().getAmountOfMsgChecks();
			status += n.getNode().getAmountOfNodeStatusChecks();
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
		for (NodeThread n: nodes) {
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
		for (int i = 0; i < NNUM; i++) {
			for (int j = 0; j < NNUM; j++) {
				if (i == j) System.out.print("- ");
				else
					System.out.print(edgesMatrix.get(i).get(j) + " ");
			}
			System.out.print("\n");
		}
	}

}
