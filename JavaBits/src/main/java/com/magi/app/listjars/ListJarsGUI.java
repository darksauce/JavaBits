package com.magi.app.listjars;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarEntry;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.magi.app.listjars.handler.ListJarsSearchResultHandler;

/**
 * Graphical User Interface for the ListJars application.
 * 
 * Application to list the jars that a particular class can be found in.
 * This app is useful for searching jar libraries for the existence of a class, and to
 * display on-screen which jar library(s) a class appears in.
 * 
 * USAGE:
 * 
 * ListJarsGUI [<search-for-class> [<search-path>]]
 *
 * @see    com.magi.app.listjars.ListJars
 * 
 * @author patkins
 */
public class ListJarsGUI extends JFrame implements ActionListener {

	private static final String    PROPERTY_SEARCH_FOR  = "search.for";
	private static final String    PROPERTY_SEARCH_PATH = "search.path";
	
	private static final int       GBC_NONE  = GridBagConstraints.NONE;
	private static final int       GBC_HORIZ = GridBagConstraints.HORIZONTAL;
	
	private static final Dimension DEFAULT_SIZE   = new Dimension(1024, 768);
	private static final Point     DEFAULT_OFFSET = new Point(50, 50);
	
	private JTextField searchForText  = new JTextField();
	private JTextField searchPathText = new JTextField();
	private JTree tree = null;
	
	private DefaultTreeModel searchResults = null;
	
	public ListJarsGUI() {
		super("List Jars - used for class searching...");
		
		initGui();
	}
	
	private void initGui() {
		super.setLocation(DEFAULT_OFFSET);
		super.setSize(DEFAULT_SIZE);
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		super.setContentPane(mainPanel);
		
		JPanel searchPanel = new JPanel(new GridBagLayout());
		searchPanel.setBackground(Color.lightGray);
		
		JLabel searchForLabel  = new JLabel("Search for Class:");
		JLabel searchPathLabel = new JLabel("Search in Folder:");
		JButton searchButton = new JButton("Search");
		JButton browseButton = new JButton("Browse...");
		
		searchButton.addActionListener(this);
		browseButton.addActionListener(this);
		searchButton.setPreferredSize(browseButton.getPreferredSize());
		
		Insets insets = new Insets(5, 5, 5, 0);
		
		searchPanel.add(searchForLabel,  gridBagConstraints(0, 0, 0.0, 10, GBC_NONE, insets));
		searchPanel.add(searchPathLabel, gridBagConstraints(0, 1, 0.0, 10, GBC_NONE, insets));
		
		Insets insetsRight = new Insets(5, 0, 5, 5);
		
		searchPanel.add(searchForText,  gridBagConstraints(1, 0, 1.0, 0, GBC_HORIZ, insetsRight));
		searchPanel.add(searchPathText, gridBagConstraints(1, 1, 1.0, 0, GBC_HORIZ, insetsRight));
		
		insets = new Insets(3, 3, 3, 3);
		
		searchPanel.add(searchButton, gridBagConstraints(2, 0, 0.0, 10, GBC_NONE, insets));
		searchPanel.add(browseButton, gridBagConstraints(2, 1, 0.0, 10, GBC_NONE, insets));
		
		mainPanel.add(searchPanel, BorderLayout.NORTH);
		
		tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("Empty Search Results")));
		tree.setCellRenderer(new CustomTreeCellRenderer());
		
		JScrollPane treeScroller = new JScrollPane(
			tree, 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		
		mainPanel.add(treeScroller, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("Search")) {
			
			saveProperties();
			
			if (!isEmpty(this.searchForText) && !isEmpty(this.searchPathText)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Search Results");
						ListJarsGUI.this.searchResults = new DefaultTreeModel(rootNode);
						ListJarsGUI.this.tree.setModel(ListJarsGUI.this.searchResults);
						
						ListJarsRunner runner = new ListJarsRunner(
							ListJarsGUI.this.searchPathText.getText(), 
							ListJarsGUI.this.searchForText.getText(), 
							true, true
						);
						
						runner.scan();
						
						expandAllTreeNodes(ListJarsGUI.this.tree, 0, ListJarsGUI.this.tree.getRowCount());
					}
				});
			}
		}
	}

	private void expandAllTreeNodes(JTree tree, int startingIndex, int rowCount) {
	    for(int i = startingIndex; i < rowCount; i++) {
	        tree.expandRow(i);
	    }

	    if (tree.getRowCount() != rowCount) {
	        expandAllTreeNodes(tree, rowCount, tree.getRowCount());
	    }
	}
	
	private void setSearchPath(String searchPath) {
		this.searchPathText.setText(searchPath);
	}


	private void setSearchFor(String searchFor) {
		this.searchForText.setText(searchFor);
	}
	
	private void loadProperties() {
		// Load previous values from properties
		FileReader reader = null;
		try {
			reader = new FileReader(getListJarsPropertyFileName());
			
			Properties prop = new Properties();
			prop.load(reader);
			
			if (prop.getProperty(PROPERTY_SEARCH_FOR) != null && isEmpty(this.searchForText)) {
				this.searchForText.setText(prop.getProperty(PROPERTY_SEARCH_FOR));
			}
			
			if (prop.getProperty(PROPERTY_SEARCH_PATH) != null && isEmpty(this.searchPathText)) {
				this.searchPathText.setText(prop.getProperty(PROPERTY_SEARCH_PATH));
			}
		}
		catch (FileNotFoundException ex) {
			// we don't really care if the file is not present
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			if (reader != null) {
				try { reader.close(); } catch (IOException ex) { }
			}
		}
	}

	private void saveProperties() {
		Properties prop = new Properties();
		
		prop.setProperty(PROPERTY_SEARCH_FOR,  this.searchForText.getText());
		prop.setProperty(PROPERTY_SEARCH_PATH, this.searchPathText.getText());
		
		FileOutputStream out = null;
		
		try {
		    out = new FileOutputStream(getListJarsPropertyFileName());
		    prop.store(out, "listjars");
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			if (out != null) {
				try { out.close(); } catch (IOException ex) { }
			}
		}
	}

	private String getListJarsPropertyFileName() {
		return System.getProperty("user.home") + "/listjars.properties";
	}

	private boolean isEmpty(JTextField textfield) {
		return (textfield.getText().trim().length() == 0);
	}

	private GridBagConstraints gridBagConstraints(int gridx, int gridy, double weightx, int pad, int fill, Insets insets) {
		GridBagConstraints gbCons = new GridBagConstraints();
		gbCons.gridx = gridx;
		gbCons.gridy = gridy;
		gbCons.anchor = GridBagConstraints.LINE_START;
		gbCons.weightx = weightx;
		gbCons.weighty = 1.0;
		gbCons.ipadx = pad;
		gbCons.ipady = pad;
		gbCons.fill = fill;
		gbCons.insets = insets;
		return gbCons;
	}

	public void guiHandleSearchResult(File jarfile, JarEntry entry) {
		
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) this.searchResults.getRoot();
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			// Look for existing Jar entry (if there is one)
			DefaultMutableTreeNode jarNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			if (jarNode.toString().equals(jarfile.toString())) {
				jarNode.add(new DefaultMutableTreeNode(entry));
				this.searchResults.nodeChanged(jarNode);
				return;
			}
		}
		
		DefaultMutableTreeNode jarNode = new DefaultMutableTreeNode(jarfile); 
		rootNode.add(jarNode);
		
		jarNode.add(new DefaultMutableTreeNode(entry));
		this.searchResults.nodeChanged(rootNode);
	}
	
	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

		private Font standardFont = null;
		private Font jarNameFont = null;
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			if (standardFont == null) {
				standardFont = comp.getFont();
				jarNameFont = standardFont.deriveFont(Font.BOLD);
			}
			
			if (value.toString().endsWith(".jar") || value.toString().endsWith("Search Results")) {
				comp.setFont(jarNameFont);
			}
			else {
				comp.setFont(standardFont);
			}
			
			return comp;
		}
		
	}
	
	private class ListJarsRunner extends ListJars implements ListJarsSearchResultHandler {
		public ListJarsRunner(String searchPath, String searchFor, boolean isSorted, boolean includeSubdirs) {
			super(searchPath, searchFor, isSorted, includeSubdirs);
		}
		
		public void handleSearchResult(File jarfile, JarEntry entry) {
			// Delegate to the GUI to handle...
			guiHandleSearchResult(jarfile, entry);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("USAGE: ListJarsGUI [<search-for-class> [<search-path>]]");
		
		ListJarsGUI gui = new ListJarsGUI();
		
		if (args.length > 0) {
			gui.setSearchFor(args[0]);
		}
		
		if (args.length > 1) {
			gui.setSearchPath(args[1]);
		}
		
		gui.loadProperties();
		gui.setVisible(true);
	}

}
