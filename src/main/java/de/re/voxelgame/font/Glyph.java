package de.re.voxelgame.font;

import org.joml.Vector2i;

public class Glyph {
  private final int textureId;

  private final int advance;

  private final Vector2i size;

  private final Vector2i bearing;

  public Glyph(int textureId, int advance, Vector2i size, Vector2i bearing) {
    this.textureId = textureId;
    this.advance = advance;
    this.size = size;
    this.bearing = bearing;
  }

  public int getTextureId() {
    return textureId;
  }

  public int getAdvance() {
    return advance;
  }

  public Vector2i getSize() {
    return size;
  }

  public Vector2i getBearing() {
    return bearing;
  }
}
