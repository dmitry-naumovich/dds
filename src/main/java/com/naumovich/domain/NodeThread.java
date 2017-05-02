package com.naumovich.domain;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import com.naumovich.network.Field;

//TODO: override toString (?), hashCode and equals after NodeThread completed
public class NodeThread implements Runnable {

    private static Random rand = new Random();
    private static final double speed = 0.01;
    private static final int radius = 8;
    private static final Color WHITE_COLOR = new Color(255, 255, 255);
    private static final Color BLUE_COLOR = new Color(92, 194, 242);

    private Field field;
	private Node node;
	private Color color;
	private double x;
	private double y;
	private double speedX;
	private double speedY;
	
	private boolean distributeFlag;
	private boolean backupFlag;
	
	public NodeThread(Field field) {
		this.field = field;
		node = new Node(this, field);
		
		double angle = Math.random()*2*Math.PI;
		speedX = speed*Math.cos(angle);
		speedY = speed*Math.sin(angle);
		color = new Color(92, 194, 242);
		x = Math.random()*(field.getSize().getWidth() - 2*radius) + radius;
		y = Math.random()*(field.getSize().getHeight() - 2*radius) + radius;
	}
	
	public void setDistributeFlag(boolean distributeFlag) {
		this.distributeFlag = distributeFlag;
	}
	public void setBackupFlag(boolean backupFlag) {
		this.backupFlag = backupFlag;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public static int getRadius() {
		return radius;
	}
	
	
	public Node getNode() {
		return node;
	}

	public void paint(Graphics2D canvas) {
		canvas.setColor(color);
		canvas.setPaint(color);
		Ellipse2D.Double node = new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
		canvas.draw(node);
		canvas.fill(node);
	}
	
	
	@Override
	public void run() { // runs for every node
		try {
			while (true) {// TODO remove true and fieldCanIMove here and remove wait() and notify() perhaps ?
				field.canIMove(); // checking whether the "pause" is pressed or not
				for (int i = 0; i < 120; i++) {
					if (node.isOnline()) {
						if (backupFlag) {
							node.makeBackup();
							backupFlag = false;
						}
						if (distributeFlag) {
							node.distributeFile(new File("file " + i * rand.nextInt(1000), 100 + rand.nextInt(10000)));
							distributeFlag = false;
						}
						color = BLUE_COLOR;
						node.checkMessageContainer(); // retransmit or receive chunks
						node.checkNodesStatus();
						if (i % 120 == 0) {
							node.findNeighbors();    // find current neighbors and fill the edgesMatrix
						}
					}
					if (!node.isOnline()) {
						color = WHITE_COLOR;
					}
					if (i % 40 == 0) {
						if (x + speedX <= radius) {
							speedX = -speedX;
							x = radius;
						} else if (x + speedX >= field.getWidth() - radius) {
							speedX = -speedX;
							x = new Double(field.getWidth() - radius).intValue();
						} else if (y + speedY <= radius) {
							speedY = -speedY;
							y = radius;
						} else if (y + speedY >= field.getHeight() - radius) {
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
		    //TODO handle an exception in any way

        }
	}
}
