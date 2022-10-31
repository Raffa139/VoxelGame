package de.re.voxelgame.font;

import org.joml.Vector2i;

public class Glyph {
  private final int id;

  private final int advance;

  private final Vector2i positionInAtlas;

  private final Vector2i size;

  private final Vector2i bearing;

  public Glyph(int id, int advance, Vector2i positionInAtlas, Vector2i size, Vector2i bearing) {
    this.id = id;
    this.advance = advance;
    this.positionInAtlas = positionInAtlas;
    this.size = size;
    this.bearing = bearing;
  }

  public int getId() {
    return id;
  }

  public int getAdvance() {
    return advance;
  }

  public Vector2i getPositionInAtlas() {
    return positionInAtlas;
  }

  public Vector2i getSize() {
    return size;
  }

  public Vector2i getBearing() {
    return bearing;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Glyph glyph = (Glyph) o;

    return id == glyph.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return "Glyph{" +
        "id=" + id +
        ", advance=" + advance +
        ", positionInAtlas=" + positionInAtlas +
        ", size=" + size +
        ", bearing=" + bearing +
        '}';
  }
}
