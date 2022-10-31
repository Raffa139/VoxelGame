package de.re.voxelgame;

import de.re.engine.util.ResourceLoader;
import de.re.voxelgame.font.FontLoader;
import de.re.voxelgame.font.Glyph;

import java.io.IOException;
import java.util.Map;

public class Main {
  public static void main(String[] args) throws IOException {
    Map<Character, Glyph> font = FontLoader.loadFont(ResourceLoader.locateResource("font/calibri/calibri.fnt", Main.class).toPath());
    for (char c : font.keySet()) {
      Glyph glyph = font.get(c);
      System.out.println(c + ": " + glyph);
    }

    //new VoxelApplication(1080, 720, "Voxel Application").run();
  }
}
