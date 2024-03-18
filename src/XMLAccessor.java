import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XMLAccessor, reads and writes XML files
 *
 * @author Ian F. Darwin
 * @author Gert Florijn
 * @author Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class XMLAccessor extends Accessor {

	/** Default API to use. */
	protected static final String DEFAULT_API_TO_USE = "dom";

	/** Names of XML tags of attributes */
	protected static final String SHOWTITLE = "showtitle";
	protected static final String SLIDETITLE = "title";
	protected static final String SLIDE = "slide";
	protected static final String ITEM = "item";
	protected static final String LEVEL = "level";
	protected static final String KIND = "kind";
	protected static final String TEXT = "text";
	protected static final String IMAGE = "image";

	/** Text of messages */
	protected static final String PCE = "Parser Configuration Exception";
	protected static final String UNKNOWNTYPE = "Unknown Element type";
	protected static final String NFE = "Number Format Exception";

	private String getTitle(Element element, String tagName) {
		NodeList titles = element.getElementsByTagName(tagName);
		return titles.item(0).getTextContent();
	}

	public void loadFile(Presentation presentation, String filename) throws IOException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(new File(filename));
			Element docElement = document.getDocumentElement();
			presentation.setTitle(getTitle(docElement, SHOWTITLE));
			loadSlides(presentation, docElement);
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException(PCE, e);
		}
	}

	private void loadSlides(Presentation presentation, Element docElement) {
		NodeList slides = docElement.getElementsByTagName(SLIDE);
		for (int slideNumber = 0; slideNumber < slides.getLength(); slideNumber++) {
			Element slideElement = (Element) slides.item(slideNumber);
			Slide slide = createSlide(slideElement);
			presentation.append(slide);
			loadSlideItems(slide, slideElement);
		}
	}

	private Slide createSlide(Element slideElement) {
		Slide slide = new Slide();
		slide.setTitle(getTitle(slideElement, SLIDETITLE));
		return slide;
	}

	private void loadSlideItems(Slide slide, Element slideElement) {
		NodeList slideItems = slideElement.getElementsByTagName(ITEM);
		for (int itemNumber = 0; itemNumber < slideItems.getLength(); itemNumber++) {
			Element item = (Element) slideItems.item(itemNumber);
			loadSlideItem(slide, item);
		}
	}

	protected void loadSlideItem(Slide slide, Element item) {
		int level = parseLevel(item);
		String type = item.getAttribute(KIND);
		if (TEXT.equals(type)) {
			slide.append(new TextItem(level, item.getTextContent()));
		} else if (IMAGE.equals(type)) {
			slide.append(new BitmapItem(level, item.getTextContent()));
		} else {
			System.err.println(UNKNOWNTYPE);
		}
	}

	private int parseLevel(Element item) {
		int level = 1; // default
		String leveltext = item.getAttribute(LEVEL);
		if (!leveltext.isEmpty()) {
			try {
				level = Integer.parseInt(leveltext);
			} catch (NumberFormatException e) {
				System.err.println(NFE);
			}
		}
		return level;
	}

	public void saveFile(Presentation presentation, String filename) throws IOException {
		try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
			out.println("<?xml version=\"1.0\"?>");
			out.println("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">");
			out.println("<presentation>");
			out.print("<showtitle>");
			out.print(presentation.getTitle());
			out.println("</showtitle>");
			saveSlides(presentation, out);
			out.println("</presentation>");
		}
	}

	private void saveSlides(Presentation presentation, PrintWriter out) {
		for (int slideNumber = 0; slideNumber < presentation.getSize(); slideNumber++) {
			Slide slide = presentation.getSlide(slideNumber);
			out.println("<slide>");
			out.println("<title>" + slide.getTitle() + "</title>");
			saveSlideItems(slide, out);
			out.println("</slide>");
		}
	}

	private void saveSlideItems(Slide slide, PrintWriter out) {
		Vector<SlideItem> slideItems = slide.getSlideItems();
		for (SlideItem slideItem : slideItems) {
			out.print("<item kind=");
			if (slideItem instanceof TextItem) {
				out.print("\"text\" level=\"" + slideItem.getLevel() + "\">");
				out.print(((TextItem) slideItem).getText());
			} else if (slideItem instanceof BitmapItem) {
				out.print("\"image\" level=\"" + slideItem.getLevel() + "\">");
				out.print(((BitmapItem) slideItem).getName());
			} else {
				System.out.println("Ignoring " + slideItem);
			}
			out.println("</item>");
		}
	}
}
