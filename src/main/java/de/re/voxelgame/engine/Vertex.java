package de.re.voxelgame.engine;

import org.joml.Vector3f;

public class Vertex {
  private final Vector3f position;

  private final float lightLevel;

  private final int blockId;

  public Vertex(float x, float y, float z) {
    this(x, y, z, -1.0f, -1);
  }

  public Vertex(float x, float y, float z, float lightLevel, int blockId) {
    this(new Vector3f(x, y, z), lightLevel, blockId);
  }

  public Vertex(Vector3f position, float lightLevel, int blockId) {
    this.position = position;
    this.lightLevel = lightLevel;
    this.blockId = blockId;
  }

  public Vector3f getPosition() {
    return position;
  }

  public float getLightLevel() {
    return lightLevel;
  }

  public int getBlockId() {
    return blockId;
  }
}
