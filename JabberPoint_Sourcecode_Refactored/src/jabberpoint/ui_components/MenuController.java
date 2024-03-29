package jabberpoint.ui_components;

import jabberpoint.presentation_parsing.Accessor;
import jabberpoint.presentation_parsing.XMLAccessor;

import java.awt.MenuBar;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * <p>The controller for the menu</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com
 * @author Gert Florijn
 * @author Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class MenuController extends MenuBar {
	private Frame parent;
	private Presentation presentation;
	private static final long serialVersionUID = 227L;

	protected static final String ABOUT = "About";
	protected static final String FILE = "File";
	protected static final String EXIT = "Exit";
	protected static final String GOTO = "Go to";
	protected static final String HELP = "Help";
	protected static final String NEW = "New";
	protected static final String NEXT = "Next";
	protected static final String OPEN = "Open";
	protected static final String PAGENR = "Page number?";
	protected static final String PREV = "Prev";
	protected static final String SAVE = "Save";
	protected static final String VIEW = "View";

	protected static final String TESTFILE = "testPresentation.xml";
	protected static final String SAVEFILE = "savedPresentation.xml";

	public MenuController(Frame frame, Presentation pres) {
		this.parent = frame;
		this.presentation = pres;
		createMenus();
	}

	private void createMenus() {
		Menu fileMenu = createFileMenu();
		Menu viewMenu = createViewMenu();
		Menu helpMenu = createHelpMenu();

		add(fileMenu);
		add(viewMenu);
		setHelpMenu(helpMenu);
	}

	private Menu createFileMenu() {
		Menu fileMenu = new Menu(FILE);
		fileMenu.add(createMenuItem(OPEN, e -> loadPresentation()));
		fileMenu.add(createMenuItem(NEW, e -> clearPresentation()));
		fileMenu.add(createMenuItem(SAVE, e -> savePresentation()));
		fileMenu.addSeparator();
		fileMenu.add(createMenuItem(EXIT, e -> System.exit(0)));
		return fileMenu;
	}

	private void loadPresentation() {
		presentation.clear();
		Accessor xmlAccessor = new XMLAccessor();
		try {
			xmlAccessor.loadFile(presentation, TESTFILE);
			presentation.setSlideNumber(0);
		} catch (IOException exc) {
			JOptionPane.showMessageDialog(parent, "IO Exception: " + exc,
					"Load Error", JOptionPane.ERROR_MESSAGE);
		}
		parent.repaint();
	}

	private void clearPresentation() {
		presentation.clear();
		parent.repaint();
	}

	private void savePresentation() {
		Accessor xmlAccessor = new XMLAccessor();
		try {
			xmlAccessor.saveFile(presentation, SAVEFILE);
		} catch (IOException exc) {
			JOptionPane.showMessageDialog(parent, "IO Exception: " + exc,
					"Save Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private Menu createViewMenu() {
		Menu viewMenu = new Menu(VIEW);
		viewMenu.add(createMenuItem(NEXT, e -> presentation.nextSlide()));
		viewMenu.add(createMenuItem(PREV, e -> presentation.prevSlide()));
		viewMenu.add(createMenuItem(GOTO, e -> gotoSlide()));
		return viewMenu;
	}

	private void gotoSlide() {
		String pageNumberStr = JOptionPane.showInputDialog(parent, PAGENR);
		try {
			int pageNumber = Integer.parseInt(pageNumberStr);
			if (pageNumber > 0 && pageNumber <= presentation.getSize()) {
				presentation.setSlideNumber(pageNumber - 1);
			} else {
				JOptionPane.showMessageDialog(parent, "Page number does not exist within the presentation",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(parent, "Invalid page number", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private Menu createHelpMenu() {
		Menu helpMenu = new Menu(HELP);
		helpMenu.add(createMenuItem(ABOUT, e -> AboutBox.show(parent)));
		return helpMenu;
	}

	private MenuItem createMenuItem(String name, ActionListener listener) {
		MenuItem menuItem = new MenuItem(name, new MenuShortcut(name.charAt(0)));
		menuItem.addActionListener(listener);
		return menuItem;
	}
}
