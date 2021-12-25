package de.re.voxelgame.engine.voxel;

import org.joml.Vector3f;

public class VoxelVertex {
  private final Vector3f position;

  private final float lightLevel;

  private final int type;

  public VoxelVertex(float x, float y, float z) {
    this(x, y, z, -1.0f, -1);
  }

  public VoxelVertex(float x, float y, float z, float lightLevel, int type) {
    this(new Vector3f(x, y, z), lightLevel, type);
  }

  public VoxelVertex(Vector3f position, float lightLevel, int type) {
    this.position = position;
    this.lightLevel = lightLevel;
    this.type = type;
  }

  public Vector3f getPosition() {
    return position;
  }

  public float getLightLevel() {
    return lightLevel;
  }

  public int getType() {
    return type;
  }
}
