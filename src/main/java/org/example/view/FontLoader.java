package org.example.view;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontLoader {
    public static Font loadFont(String fontFile, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile));
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}