package com.naumovich.network;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

@SuppressWarnings("serial")
public class TestNetwork extends JFrame {

	public static final int NODES_NUM = 50;
	private static final int WIDTH = 600;
	private static final int HEIGHT = 480;
//	private static final int SMALL_GAP = 5;
//	private static final int MEDIUM_GAP = 10;
	private JMenuItem pauseMenuItem;
	private JMenuItem resumeMenuItem;
	private JMenuItem showEdgesMatrixItem;
	private JMenuItem distributeFileMenuItem;
	private JMenuItem turnOffNodesMenuItem;
	private JMenuItem turnOnAllNodesMenuItem;
	private JMenuItem collectStatsMenuItem;
	private Field field = new Field();
	
	public TestNetwork() {
		
		super("DDS algorithm testing");
		setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);
		//setExtendedState(MAXIMIZED_BOTH);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu nodeMenu = new JMenu("Nodes");
		Action addNodeAction = new AbstractAction("Add nodes") {
			public void actionPerformed(ActionEvent event) {
				//field.addNode();
				for (int i = 0; i < NODES_NUM; i++) {
					field.addNode();
				}
				if (!pauseMenuItem.isEnabled() && !resumeMenuItem.isEnabled()) {
					pauseMenuItem.setEnabled(true);
				}
			}
		};
		menuBar.add(nodeMenu);
		nodeMenu.add(addNodeAction);
		
		Action turnOffNodesAction = new AbstractAction("Turn off some nodes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.turnOffNodes();
			}
		};
		turnOffNodesMenuItem = nodeMenu.add(turnOffNodesAction);
		turnOffNodesMenuItem.setEnabled(true);
		
		Action turnOnNodesAction = new AbstractAction("Turn on all nodes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.turnOnAllNodes();
			}
		};
		turnOnAllNodesMenuItem = nodeMenu.add(turnOnNodesAction);
		turnOnAllNodesMenuItem.setEnabled(true);
		
		JMenu controlMenu = new JMenu("Control");
		menuBar.add(controlMenu);
		Action pauseAction = new AbstractAction("Stop motion") {
			public void actionPerformed(ActionEvent event) {
				field.pause();
				pauseMenuItem.setEnabled(false);
				resumeMenuItem.setEnabled(true);
			}
		};
		pauseMenuItem = controlMenu.add(pauseAction);
		pauseMenuItem.setEnabled(false);
		
		Action resumeAction = new AbstractAction("Resume motion") {
			public void actionPerformed(ActionEvent event) {
				field.resume();
				pauseMenuItem.setEnabled(true);
				resumeMenuItem.setEnabled(false);
			}
		};
		resumeMenuItem = controlMenu.add(resumeAction);
		resumeMenuItem.setEnabled(false);
		
		Action showEdgesMatrixAction = new AbstractAction("Show adjantency matrix") {
			public void actionPerformed(ActionEvent event) {
				field.pause();
				pauseMenuItem.setEnabled(false);
				resumeMenuItem.setEnabled(true);
				field.showEdgesMatrix();
			}
		};
		showEdgesMatrixItem = controlMenu.add(showEdgesMatrixAction);
		showEdgesMatrixItem.setEnabled(true);
		
		Action distributeFileAction = new AbstractAction("Distribute files") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//field.pause();
				//pauseMenuItem.setEnabled(false);
				//resumeMenuItem.setEnabled(true);
				field.distributeFiles();
			}
		};
		distributeFileMenuItem = controlMenu.add(distributeFileAction);
		distributeFileMenuItem.setEnabled(true);
		
		Action collectStatsAction = new AbstractAction("Collect statistics") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.collectStatistics();
			}
		};
		collectStatsMenuItem = controlMenu.add(collectStatsAction);
		collectStatsMenuItem.setEnabled(true);
		
//		JLabel jtField = new JLabel("Name:");
//		JButton buttonS = new JButton("ShowInfo");
//		JButton buttonP = new JButton("Hide");
//		
//		final JPanel aboutNodePanel = new JPanel();
//		aboutNodePanel.setBorder(BorderFactory.createTitledBorder("Node"));
//		final GroupLayout layout2 = new GroupLayout(aboutNodePanel);
//		aboutNodePanel.setLayout(layout2);
//		layout2.setHorizontalGroup(layout2.createSequentialGroup()
//				.addContainerGap()
//				.addGroup(layout2.createParallelGroup(Alignment.LEADING)
//						.addGroup(layout2.createSequentialGroup()
//								.addComponent(jtField))
//						.addGroup(layout2.createSequentialGroup()						
//							.addComponent(buttonS)
//							.addGap(SMALL_GAP)
//							.addComponent(buttonP)))
//				.addContainerGap());
//		layout2.setVerticalGroup(layout2.createSequentialGroup()
//				.addContainerGap()
//				.addGroup(layout2.createParallelGroup(Alignment.LEADING)
//						.addComponent(jtField))
//				.addGap(MEDIUM_GAP)
//				.addGroup(layout2.createParallelGroup(Alignment.LEADING)
//						.addComponent(buttonS)
//						.addComponent(buttonP))
//				.addContainerGap());
//		
//		final GroupLayout layout1 = new GroupLayout(getContentPane());
//		setLayout(layout1);
//		
//		layout1.setVerticalGroup(layout1.createSequentialGroup()
//				.addContainerGap()
//				.addGroup(layout1.createParallelGroup()
//						.addComponent(aboutNodePanel)
//						.addComponent(field))
//				.addContainerGap());				
//		layout1.setHorizontalGroup(layout1.createSequentialGroup()
//				.addContainerGap()
//				.addComponent(aboutNodePanel)
//				.addGap(MEDIUM_GAP)
//				.addComponent(field)
//				.addContainerGap());
		getContentPane().add(field, BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
		TestNetwork testNet = new TestNetwork();
		testNet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testNet.setVisible(true);
	}	
}
