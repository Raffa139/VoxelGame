package de.re.voxelgame.world.voxel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoxelFace {
  public static final VoxelFace FRONT = new VoxelFace(VoxelGeometry.FRONT, VoxelFaceOrientation.SOUTH);
  public static final VoxelFace BACK = new VoxelFace(VoxelGeometry.BACK, VoxelFaceOrientation.NORTH);
  public static final VoxelFace LEFT = new VoxelFace(VoxelGeometry.LEFT, VoxelFaceOrientation.WEST);
  public static final VoxelFace RIGHT = new VoxelFace(VoxelGeometry.RIGHT, VoxelFaceOrientation.EAST);
  public static final VoxelFace TOP = new VoxelFace(VoxelGeometry.TOP, VoxelFaceOrientation.TOP);
  public static final VoxelFace BOTTOM = new VoxelFace(VoxelGeometry.BOTTOM, VoxelFaceOrientation.BOTTOM);

  private final List<VoxelVertex> vertices;

  private final VoxelFaceOrientation orientation;

  private float lightLevel;

  private VoxelFace(VoxelVertex[] vertices, VoxelFaceOrientation orientation) {
    this(Arrays.asList(vertices), orientation, 1.0f);
  }

  private VoxelFace(List<VoxelVertex> vertices, VoxelFaceOrientation orientation, float lightLevel) {
    this.vertices = vertices;
    this.orientation = orientation;
    this.lightLevel = lightLevel;
  }

  public VoxelFace translate(float x, float y, float z, int textureLayer, boolean highlighted) {
    List<VoxelVertex> translated = new ArrayList<>();

    for (VoxelVertex v : vertices) {
      VoxelVertex voxelVertex =
          new VoxelVertex(v.getPosition().x + x, v.getPosition().y + y, v.getPosition().z + z, lightLevel, textureLayer, highlighted);
      translated.add(voxelVertex);
    }

    return new VoxelFace(translated, orientation, lightLevel);
  }

  public List<VoxelVertex> getVertices() {
    return vertices;
  }

  public VoxelFaceOrientation getOrientation() {
    return orientation;
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }
}
