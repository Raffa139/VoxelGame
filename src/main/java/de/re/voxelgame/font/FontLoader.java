package de.re.voxelgame.font;

import org.joml.Vector2i;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class FontLoader {
  private FontLoader() {
  }

  public static Map<Character, Glyph> loadFont(Path font) {
    Map<Character, Glyph> glyphs = new HashMap<>();

    try (BufferedReader br = Files.newBufferedReader(font)) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("char") && !line.startsWith("chars")) {
          Glyph glyph = loadLine(line);
          glyphs.put((char) glyph.getId(), glyph);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return glyphs;
  }

  private static Glyph loadLine(String line) {
    int id = loadValue("id", line);
    int x = loadValue("x", line);
    int y = loadValue("y", line);
    int width = loadValue("width", line);
    int height = loadValue("height", line);
    int xOffset = loadValue("xoffset", line);
    int yOffset = loadValue("yoffset", line);
    int advance = loadValue("xadvance", line);

    return new Glyph(id, advance, new Vector2i(x, y), new Vector2i(width, height), new Vector2i(xOffset, yOffset));
  }

  private static int loadValue(String attribute, String line) {
    int start = line.indexOf(attribute) + attribute.length() + 1;
    int end = start + 1;

    while (line.charAt(end) != ' ') {
      end++;
    }

    return Integer.parseInt(line.substring(start, end));
  }
}
