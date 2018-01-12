package com.naumovich.network;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import com.naumovich.util.MenuItemNames;
import static com.naumovich.configuration.ModelConfiguration.NODES_AMOUNT_TO_ADD;

/**
 * This class is a JFrame object which is responsible for creating JPanel field object, creating the menu and all its
 * items. Contains main method for triggering application start.
 */
public class TestNetwork extends JFrame {

    /**
     * JFrame title
     */
	private static final String J_FRAME_TITLE = "DDS algorithm testing";

    /**
     * JFrame's width
     */
	private static final int WIDTH = 600;

    /**
     * JFrame's height
     */
	private static final int HEIGHT = 480;

    /**
     * Pause menu item
     */
	private JMenuItem pauseMenuItem;

    /**
     * Resume menu item
     */
	private JMenuItem resumeMenuItem;

    /**
     * JPanel implementation
     */
	private Field field = new Field();

    /**
     * The application main method. Creates JFrame object, makes it visible and enables exit on close
     * @param args command line args
     */
    public static void main(String[] args) {
        TestNetwork testNet = new TestNetwork();
        testNet.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        testNet.setVisible(true);
    }

    /**
     * Construct JFrame object by setting its title, width and height, location on the screen and creates its menu
     */
    private TestNetwork() {
        super(J_FRAME_TITLE);
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);
        //setExtendedState(MAXIMIZED_BOTH);
        createMenu();
        getContentPane().add(field, BorderLayout.CENTER);
    }

    /**
     * Constructs and sets JMenuBar object
     */
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu nodeMenu = new JMenu(MenuItemNames.NODE_MENU_NAME);
		JMenu controlMenu = new JMenu(MenuItemNames.CONTROL_MENU_NAME);

		nodeMenu.add(new AbstractAction(MenuItemNames.ACTION_ADD_NODES) {
			public void actionPerformed(ActionEvent event) {
				field.addNodesToField(NODES_AMOUNT_TO_ADD);
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
