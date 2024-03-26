package jabberpoint.presentation_parsing;

import jabberpoint.ui_components.Presentation;

import java.io.IOException;

/**
 * <p>An jabberpoint.presentation_parsing.Accessor makes it possible to read and write data
 * for a presentation.</p>
 * <p>Non-abstract subclasses should implement the load and save methods.</p>
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
public abstract class Accessor {
	public static final String DEFAULT_EXTENSION = ".xml";

	public Accessor() {
	}

	abstract public void loadFile(Presentation p, String fn) throws IOException;

	abstract public void saveFile(Presentation p, String fn) throws IOException;
}
