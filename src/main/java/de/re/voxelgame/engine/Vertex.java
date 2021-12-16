package de.re.voxelgame.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
  private final Vector3f position;

  private final Vector2f texture;

  private final float lightLevel;

  public Vertex(float x, float y, float z, float u, float v) {
    this(x, y, z, new Vector2f(u, v), -1.0f);
  }

  public Vertex(float x, float y, float z, Vector2f texture, float lightLevel) {
    this(new Vector3f(x, y, z), texture, lightLevel);
  }

  public Vertex(Vector3f position, Vector2f texture, float lightLevel) {
    this.position = position;
    this.texture = texture;
    this.lightLevel = lightLevel;
  }

  public Vector3f getPosition() {
    return position;
  }

  public Vector2f getTexture() {
    return texture;
  }

  public float getLightLevel() {
    return lightLevel;
  }
}
