package de.re.voxelgame.engine.voxel;

import org.joml.Vector3f;

public class VoxelVertex {
  private final Vector3f position;

  private final float lightLevel;

  private final int textureLayer;

  public VoxelVertex(float x, float y, float z) {
    this(x, y, z, -1.0f, -1);
  }

  public VoxelVertex(float x, float y, float z, float lightLevel, int textureLayer) {
    this(new Vector3f(x, y, z), lightLevel, textureLayer);
  }

  public VoxelVertex(Vector3f position, float lightLevel, int textureLayer) {
    this.position = position;
    this.lightLevel = lightLevel;
    this.textureLayer = textureLayer;
  }

  public Vector3f getPosition() {
    return position;
  }

  public float getLightLevel() {
    return lightLevel;
  }

  public int getTextureLayer() {
    return textureLayer;
  }
}
