package com.naumovich.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.naumovich.abstraction.Dijkstra;
import com.naumovich.abstraction.FourTuple;
import com.naumovich.abstraction.MathOperations;
import com.naumovich.abstraction.TwoTuple;
import com.naumovich.message.ChunkMessage;
import com.naumovich.message.Message;
import com.naumovich.message.ResCopyMessage;
import com.naumovich.network.Field;
import com.naumovich.network.MessageContainer;

public class Node implements Runnable {
	
	private static final double speed = 0.01;
	private static final int radius = 8;
	private Color color;
	private double x;
	private double y;
	private double speedX;
	private double speedY;
	private String nodeID;
	private String login;
	private static int counter = 0;
	private int persNum;
	private boolean distributeFlag;
	private boolean resCopyFlag;
	private List<Chunk> chunkStorage;
	
	private List<Message> resCopyMsgs;
	private ChunkTable chTable;
	private AddressTable addrTable;
	private boolean isOnline;
	private int amountOfRestransmitted = 0;
	private long amountOfMsgChecks = 0;
	private long amountOfNodeStatusChecks = 0;
	private long amountOfFindingPath = 0;
	
	Random rand = new Random();
	private Field field;
	private MessageContainer msgContainer;
	
	public Node(Field field) {
		this.field = field;
		msgContainer = new MessageContainer();
		chunkStorage = Collections.synchronizedList(new ArrayList<Chunk>());
		resCopyMsgs = new ArrayList<Message>();
		nodeID = MathOperations.getRandomHexString(40);
		persNum = counter;
		distributeFlag = false;
		resCopyFlag = false;
		login = "Node" + counter++; 
		isOnline = true;
		addrTable = new AddressTable(this);
		
		double angle = Math.random()*2*Math.PI;
		speedX = speed*Math.cos(angle);
		speedY = speed*Math.sin(angle);
		color = new Color(92, 194, 242);
		x = Math.random()*(field.getSize().getWidth() - 2*radius) + radius;
		y = Math.random()*(field.getSize().getHeight() - 2*radius) + radius;
		Thread thisThread = new Thread(this);
		thisThread.start();
	}
	public boolean getDistributeFlag() {
		return distributeFlag;
	}
	public List<Chunk> getChunkStorage() {
		return chunkStorage;
	}
	public int getAmountOfRestransmitted() {
		return amountOfRestransmitted;
	}
	public void setDistributeFlag(boolean distributeFlag) {
		this.distributeFlag = distributeFlag;
	}
	public String getLogin() {
		return login;
	}
	public int getPersNum() {
		return persNum;
	}
	public void setPersNum(int persNum) {
		this.persNum = persNum;
	}
	public String getNodeID() {
		return nodeID;
	}

	public static int getCounter() {
		return counter;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public static int getRadius() {
		return radius;
	}
	public void paint(Graphics2D canvas) {
		canvas.setColor(color);
		canvas.setPaint(color);
		Ellipse2D.Double node = new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
		canvas.draw(node);
		canvas.fill(node);
	}
	
	public void distributeFile(File file) {
		int n = MathOperations.defineChunksAmount(file.getSize());
		System.out.println(this.getLogin() + ": I distribute file '" + file.getFileName() + "' into " + n + " chunks");
		List<Chunk> chunks = createChunks(file, n);
		chTable = new ChunkTable(n);
		//addrTable = new AddressTable(this);
		List<Chunk> chunksAndCopies = new ArrayList<Chunk>();
		for (Chunk ch : chunks) {
			ArrayList<Chunk> alc = ch.makeCopies();
			chTable.setRow(alc, ch.getOrderNum() - 1);
			chunksAndCopies.addAll(alc);
		}
//		chTable.printTable();
		for (Chunk ch : chunksAndCopies) {
			TwoTuple<Node, Integer> tuple = ch.findNodeForMe();
			addrTable.addRow(ch.getOrderNum(), ch, tuple.first, tuple.second);
			// encryptedChunk = ch.encrypt();
		}
		addrTable.printTable();
		for (Chunk ch : chunksAndCopies) {
			Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
			dijAlg.execute(this); // Dijkstra works
			List<Node> path = dijAlg.getPath(addrTable.getNodeByChunk(ch));
			amountOfFindingPath++;
			System.out.println(this.getLogin() + ": I send " + ch.getChunkName() + " to " + 
								addrTable.getNodeByChunk(ch).getLogin() + ". The way is: " + path);
			if (path != null) {
				Message msg = new ChunkMessage(path, ch); 
				msg.excludeFirstNodeFromPath();
				msgContainer.addMsg(msg);
			}
			
			// moreover, можно дл€ результатов забабахть что-то вроде подсчета, как распредел€ютс€ копии
			// например, на узел 5 попало две копии фрагмента 3
			// или веро€тность того, что на один узел попадут все фрагменты файла
			// (без копий, просто, например, все 8 штук)... было бы интересно
		}
	}
	
	public long getAmountOfMsgChecks() {
		return amountOfMsgChecks;
	}
	public long getAmountOfNodeStatusChecks() {
		return amountOfNodeStatusChecks;
	}
	public long getAmountOfFindingPath() {
		return amountOfFindingPath;
	}
	private ArrayList<Chunk> createChunks(File file, int n) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		for (int i = 0; i < n; i++) {
			chunks.add(new Chunk(this, file.getSize() / n, file.getFileName(), i+1));
		}
		return chunks;
	} 
	
	@Override
	public void run() { // runs for every node
		try {
			while (true) {
					field.canMove(this); // checking whether the "pause" is pressed or not
					for (int i = 0; i < 120; i++) {
						if (isOnline) {
							if (resCopyFlag) {
								makeResCopy();
								resCopyFlag = false;
							}
							if (distributeFlag) {
								distributeFile(new File("file " + i*rand.nextInt(1000), 100 + rand.nextInt(10000)));
								setDistributeFlag(false);
							}
							color = new Color(92, 194, 242);
							checkMsgContainer(); // retransmit or receive chunks
							checkNodesStatus();
							if (i % 120 == 0) {
								checkNeighbors();	// find current neighbors and fill the edgesMatrix	
							}
						}
						if (!isOnline) {
							color = new Color(255, 255, 255);
						}
						if (i % 40 == 0) {
							if (x + speedX <= radius) {
								speedX = -speedX;
								x = radius;
							} else
							if (x + speedX >= field.getWidth() - radius) {
								speedX = -speedX;
								x = new Double(field.getWidth() - radius).intValue();
							} else
							if (y + speedY <= radius) {
									speedY = -speedY;
									y = radius;
							} else
							if (y + speedY >= field.getHeight() - radius) {
								speedY = -speedY;
								y = new Double(field.getHeight() - radius).intValue();
							} else {
								x += speedX;
								y += speedY;
							}
							Thread.sleep(1);
						}
						
						
					}
			}
		} catch (InterruptedException ex) {
	}
		
	}
	@SuppressWarnings("unchecked")
	public void checkMsgContainer() {
		List<Message> msgs = MessageContainer.allMsgs;
		synchronized(msgs) {
			for (Iterator<Message> iterator = msgs.iterator(); iterator.hasNext(); ) {
			    Message m = iterator.next();
			    amountOfMsgChecks++;
			    if (this.equals(m.getPath().get(0)) && this.equals(m.getDestination())) { // receive and store

			    	switch (m.getClass().getSimpleName()) {
			    		case "ChunkMessage": 
			    			System.out.println(this + ": I accept " + (Chunk)m.getData() + " to store it");
							chunkStorage.add((Chunk)m.getData());
							break;
			    		case "ResCopyMessage":
							Node receiver = ((TwoTuple<Node, Chunk>)m.getData()).first;
							Chunk chunkToCopy = ((TwoTuple<Node, Chunk>)m.getData()).second;
			    			
			    			Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
			    			dijAlg.execute(this);
			    			List<Node> path = dijAlg.getPath(receiver);
			    			amountOfFindingPath++;
			    			System.out.println(this + ": I have to complete ResCopy of " + chunkToCopy + " to " + receiver
			    					+ ". Path is: " + path);
			    			if (path != null) {
			    				Message newM = new ChunkMessage(path, getChunkByName(chunkToCopy.getChunkName()));
			    				newM.excludeFirstNodeFromPath();
			    				resCopyFlag = true;
			    				resCopyMsgs.add(newM);
			    			}
			    			break;
			    	}
			    	iterator.remove();	
			    }
			    else if (this.equals(m.getPath().get(0)) && !this.equals(m.getDestination())) { // retransmit
			    	amountOfNodeStatusChecks++;
			    	if (!m.getDestination().isOnline) {
			    		
			    		System.out.println(this + ": WARNING! While retransmitting I've found out " + m.getDestination() + " is offline. And I discard this");
			    		iterator.remove();
			    	}
			    	else if (m.getPath().get(1).isOnline()) {
			    		amountOfNodeStatusChecks++;
			    		switch (m.getClass().getSimpleName()) {
				    		case "ChunkMessage": 
				    			System.out.println(this + ": I retransmit " + (Chunk)m.getData() + " further");
								break;
				    		case "ResCopyMessage":
				    			System.out.println(this + ": I retransmit ResCopyMessage of " + ((TwoTuple<Node, Chunk>)m.getData()).second + " further");
				    			break;
			    		}
			    		amountOfRestransmitted++;
						m.excludeFirstNodeFromPath(); // i.e. retransmit
			    	}
			    	else {
			    		amountOfNodeStatusChecks++;
			    		//System.out.println(this + ": WARNING! While retransmitting I've found out " + m.getPath().get(1) + " is offline");
			    		// no action needed because source checks other nodes' status itself
			    		//iterator.remove();
			    		Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
		    			dijAlg.execute(this);
		    			List<Node> path = dijAlg.getPath(m.getDestination());
		    			amountOfFindingPath++;
		    			System.out.println(this + ": WARNING! While retransmitting I've found out " + m.getPath().get(1)
		    					+ " is offline. New path to destination " + m.getDestination() + " is: " + path);
		    			if (path != null) {
		    				m.setPath(path);
		    				m.excludeFirstNodeFromPath();
		    			}
			    	}
				}
			}
		}
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
		if (isOnline == false ) System.out.println(this + ": I'm offline!");
	}
	public void checkNeighbors() { // find neighbors and fill the edgesMatrix
		ArrayList<Node> nodes = Field.getNodes();	
		if (nodes.size() == Field.NNUM) {	
			for (Node n: nodes) {
				amountOfNodeStatusChecks++;
				if (isNeighborWith(n) && n.isOnline()) {
					field.setEdgesMatrixCell(persNum, n.getPersNum(), 1);
					field.setEdgesMatrixCell(n.getPersNum(), persNum, 1);
				} 
				else {
					field.setEdgesMatrixCell(persNum, n.getPersNum(), 0);
					field.setEdgesMatrixCell(n.getPersNum(), persNum, 0);
				}
			}
		}
	}
	public boolean isNeighborWith(Node n) {
		if (this.equals(n)) return false;
		else if (Math.pow(x - n.getX(), 2) + Math.pow(y - n.getY(), 2) <= Math.pow(12*radius, 2)) {
			// here if two nodes are neighbors
			return true;
		}
		return false;
	}
	public void checkNodesStatus() {
		for (int i = 0; i < addrTable.getRowCount(); i++) {
			FourTuple<Integer, Chunk, Node, Integer> tup = addrTable.getRow(i);
			amountOfNodeStatusChecks++;
			if (!tup.third.isOnline()) {
				System.out.println(this + ": I've found out " + tup.third + " is offline");
				TwoTuple<Node, Integer> tup2 = tup.second.findNodeForMe();
				if (tup.second.equals(tup2.first)) {
					break; // same node returned so no more need for reserve copying
				}
				else {
					addrTable.setRow(i, tup2.first, tup2.second);
					int rowOfSender = getNewSender(i);
					Node sender = addrTable.getRow(rowOfSender).third;
					Chunk chunkToSend = addrTable.getRow(rowOfSender).second;
					System.out.println(this + ": new sender of " + chunkToSend + " is " + sender);
					Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
					dijAlg.execute(this);
					List<Node> path = dijAlg.getPath(sender);
					amountOfFindingPath++;
					if (path != null) {
						Message resCopyMsg = new ResCopyMessage(path, new TwoTuple<Node, Chunk>(tup2.first, chunkToSend) );
						resCopyMsg.excludeFirstNodeFromPath();
						msgContainer.addMsg(resCopyMsg);
	    			}
				}
					
			}
		}
	}
	public Integer getNewSender(int i) {
		int orderNum = addrTable.getRow(i).first;
		for (int j = 0; j < addrTable.getRowCount(); j++) {
			FourTuple<Integer, Chunk, Node, Integer> t = addrTable.getRow(j);
			if (t.first == orderNum && !t.third.equals(addrTable.getRow(i).third)) {
				return j;
			}
		}
		return null;
	}
	public void makeResCopy() { 
		List<Message> msgs = MessageContainer.allMsgs;
		synchronized(msgs) {
			for (Message m : resCopyMsgs) {
				msgs.add(m);
			}
		}
	}
	
	public Chunk getChunkByName(String name) { // in storage
		for (Chunk c : chunkStorage) {
			if (c.getChunkName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	@Override
	public boolean equals(Object obj) {
		if (((Node)obj).nodeID == this.nodeID) 
			return true;
		else
			return false;
		
	}
	@Override
	public String toString() {
		return login;
	}
	
	
}
