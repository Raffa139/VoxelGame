package de.re.voxelgame.font;

import org.joml.Vector2f;
import org.joml.Vector2i;

import java.text.NumberFormat;

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

  public float[] calculateVertices(float x, float y, float scale) {
    float xPos = x / 512.0f + bearing.x / 512.0f;// * scale;
    //float xPos = x + bearing.x;// * scale;
    float yPos = y / 512.0f - (size.y / 512.0f - bearing.y / 512.0f);// * scale;
    //float yPos = y  - (size.y - bearing.y);// * scale;

    float width = size.x / 512.0f;// * scale;
    //float width = size.x;// * scale;
    float height = size.y / 512.0f;// * scale;
    //float height = size.y;// * scale;

    Vector2f textures = getTextureCoordinates();

    return new float[]{
        xPos, yPos + height, textures.x, textures.y,
        xPos, yPos, textures.x, textures.y + height,
        xPos + width, yPos, textures.x + width, textures.y + height,
        xPos, yPos + height, textures.x, textures.y,
        xPos + width, yPos, textures.x + width, textures.y + height,
        xPos + width, yPos + height, textures.x + width, textures.y
    };

    /*return new Vector4f[]{
        new Vector4f(xPos, yPos + height, textures.x, textures.y),
        new Vector4f(xPos, yPos, textures.x, textures.y + height),
        new Vector4f(xPos + width, yPos, textures.x + width, textures.y + height),
        new Vector4f(xPos, yPos + height, textures.x, textures.y),
        new Vector4f(xPos + width, yPos, textures.x + width, textures.y + height),
        new Vector4f(xPos + width, yPos + height, textures.x + width, textures.y)
    };*/
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

  public Vector2f getTextureCoordinates() {
    return new Vector2f(positionInAtlas).div(512.0f);
  }

  public Vector2i getSize() {
    return size;
  }

  public Vector2f getSize2() {
    return new Vector2f(size).div(512.0f);
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
        ", positionInAtlas=" + positionInAtlas.toString(NumberFormat.getNumberInstance()) +
        ", textureCoords=" + getTextureCoordinates().toString(NumberFormat.getNumberInstance()) +
        ", size=" + size +
        ", bearing=" + bearing +
        '}';
  }
}
