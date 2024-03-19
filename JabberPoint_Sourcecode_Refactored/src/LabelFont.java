import java.awt.*;

public class LabelFont {
    private static final String FONTNAME = "Dialog";
    private static final int FONTSTYLE = Font.BOLD;
    private static final int FONTHEIGHT = 10;

    public LabelFont() {}
    public Font createFont() {
        return new Font(FONTNAME, FONTSTYLE, FONTHEIGHT);
    }
}
