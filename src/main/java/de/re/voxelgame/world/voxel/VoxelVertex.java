package de.re.voxelgame.world.voxel;

import org.joml.Vector3f;

public class VoxelVertex {
  private final Vector3f position;

  private final float lightLevel;

  private final int textureLayer;

  private final boolean highlighted;

  public VoxelVertex(float x, float y, float z) {
    this(x, y, z, -1.0f, -1, false);
  }

  public VoxelVertex(float x, float y, float z, float lightLevel, int textureLayer, boolean highlighted) {
    this(new Vector3f(x, y, z), lightLevel, textureLayer, highlighted);
  }

  public VoxelVertex(Vector3f position, float lightLevel, int textureLayer, boolean highlighted) {
    this.position = position;
    this.lightLevel = lightLevel;
    this.textureLayer = textureLayer;
    this.highlighted = highlighted;
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

  public boolean isHighlighted() {
    return highlighted;
  }
}
