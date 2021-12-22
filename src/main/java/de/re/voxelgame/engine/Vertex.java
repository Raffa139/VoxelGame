package de.re.voxelgame.engine;

import org.joml.Vector3f;

public class Vertex {
  private final Vector3f position;

  private final float lightLevel;

  public Vertex(float x, float y, float z) {
    this(x, y, z, -1.0f);
  }

  public Vertex(float x, float y, float z, float lightLevel) {
    this(new Vector3f(x, y, z), lightLevel);
  }

  public Vertex(Vector3f position, float lightLevel) {
    this.position = position;
    this.lightLevel = lightLevel;
  }

  public Vector3f getPosition() {
    return position;
  }

  public float getLightLevel() {
    return lightLevel;
  }
}
