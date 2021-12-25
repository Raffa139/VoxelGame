package de.re.voxelgame.engine.voxel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoxelFace {
  public static final VoxelFace FRONT = new VoxelFace(VoxelGeometry.FRONT);
  public static final VoxelFace BACK = new VoxelFace(VoxelGeometry.BACK);
  public static final VoxelFace LEFT = new VoxelFace(VoxelGeometry.LEFT);
  public static final VoxelFace RIGHT = new VoxelFace(VoxelGeometry.RIGHT);
  public static final VoxelFace TOP = new VoxelFace(VoxelGeometry.TOP);
  public static final VoxelFace BOTTOM = new VoxelFace(VoxelGeometry.BOTTOM);

  private final List<VoxelVertex> vertices;

  private float lightLevel;

  private VoxelFace(VoxelVertex[] vertices) {
    this(Arrays.asList(vertices), 1.0f);
  }

  private VoxelFace(List<VoxelVertex> vertices, float lightLevel) {
    this.vertices = vertices;
    this.lightLevel = lightLevel;
  }

  public VoxelFace translate(float x, float y, float z, int type) {
    List<VoxelVertex> translated = new ArrayList<>();

    for (VoxelVertex v : vertices) {
      VoxelVertex voxelVertex =
          new VoxelVertex(v.getPosition().x + x, v.getPosition().y + y, v.getPosition().z + z, lightLevel, type);
      translated.add(voxelVertex);
    }

    return new VoxelFace(translated, lightLevel);
  }

  public List<VoxelVertex> getVertices() {
    return vertices;
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }
}
