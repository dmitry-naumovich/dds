package com.naumovich.network;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import com.naumovich.util.MenuItemNames;

public class TestNetwork extends JFrame {

	public static final int NODES_NUM = 40;
	private static final String J_FRAME_TITLE = "DDS algorithm testing";
	private static final int WIDTH = 600;
	private static final int HEIGHT = 480;

	private JMenuItem pauseMenuItem;
	private JMenuItem resumeMenuItem;
	private Field field = new Field();

	public TestNetwork() {
		super(J_FRAME_TITLE);
		setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);
		//setExtendedState(MAXIMIZED_BOTH);
		createMenu();
		getContentPane().add(field, BorderLayout.CENTER);
	}

    public static void main(String[] args) {
        TestNetwork testNet = new TestNetwork();
        testNet.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        testNet.setVisible(true);
    }
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu nodeMenu = new JMenu(MenuItemNames.NODE_MENU_NAME);
		JMenu controlMenu = new JMenu(MenuItemNames.CONTROL_MENU_NAME);
		
		nodeMenu.add(new AbstractAction(MenuItemNames.ACTION_ADD_NODES) {
			public void actionPerformed(ActionEvent event) {
				for (int i = 0; i < NODES_NUM; i++) {
					field.addNodeThread();
				}
				if (!pauseMenuItem.isEnabled() && !resumeMenuItem.isEnabled()) {
					pauseMenuItem.setEnabled(true);
				}
			}
		});
		nodeMenu.add(new AbstractAction(MenuItemNames.ACTION_TURN_OFF_SOME) {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.turnOffSomeNodes();
			}
		}).setEnabled(true);
		nodeMenu.add(new AbstractAction(MenuItemNames.ACTION_TURN_ON_ALL) {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.turnOnAllNodes();
			}
		}).setEnabled(true);
		
		pauseMenuItem = controlMenu.add(new AbstractAction(MenuItemNames.ACTION_STOP_MOTION) {
			public void actionPerformed(ActionEvent event) {
				field.pause();
				pauseMenuItem.setEnabled(false);
				resumeMenuItem.setEnabled(true);
			}
		});
		pauseMenuItem.setEnabled(false);
		
		resumeMenuItem = controlMenu.add(new AbstractAction(MenuItemNames.ACTION_RESUME_MOTION) {
			public void actionPerformed(ActionEvent event) {
				field.resume();
				pauseMenuItem.setEnabled(true);
				resumeMenuItem.setEnabled(false);
			}
		});
		resumeMenuItem.setEnabled(false);
		
		controlMenu.add(new AbstractAction(MenuItemNames.ACTION_SHOW_MATRIX) {
			public void actionPerformed(ActionEvent event) {
				field.pause();
				pauseMenuItem.setEnabled(false);
				resumeMenuItem.setEnabled(true);
				field.showEdgesMatrix();
			}
		}).setEnabled(true);
		controlMenu.add(new AbstractAction(MenuItemNames.ACTION_DISTRIBUTE) {
			@Override
			public void actionPerformed(ActionEvent e) {
				//field.pause();
				//pauseMenuItem.setEnabled(false);
				//resumeMenuItem.setEnabled(true);
				field.distributeFiles();
			}
		}).setEnabled(true);
		controlMenu.add(new AbstractAction(MenuItemNames.ACTION_COLLECT_STATS) {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.collectStatistics();
			}
		}).setEnabled(true);
		
		menuBar.add(nodeMenu);
		menuBar.add(controlMenu);
	}
}
