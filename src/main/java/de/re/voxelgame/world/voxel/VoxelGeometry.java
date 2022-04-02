package de.re.voxelgame.world.voxel;

public final class VoxelGeometry {
  // Visual: https://www.math3d.org/OdaojzOmz

  private VoxelGeometry() {
  }

  public static final VoxelVertex[] FRONT = {
      new VoxelVertex(0.0f,  1.0f, 1.0f),
      new VoxelVertex(0.0f, 0.0f, 1.0f),
      new VoxelVertex(1.0f, 0.0f, 1.0f),
      new VoxelVertex(1.0f, 0.0f, 1.0f),
      new VoxelVertex(1.0f,  1.0f, 1.0f),
      new VoxelVertex(0.0f,  1.0f, 1.0f)
  };

  public static final VoxelVertex[] BACK = {
      new VoxelVertex(1.0f,  1.0f, 0.0f),
      new VoxelVertex(1.0f, 0.0f, 0.0f),
      new VoxelVertex(0.0f, 0.0f, 0.0f),
      new VoxelVertex(0.0f, 0.0f, 0.0f),
      new VoxelVertex(0.0f,  1.0f, 0.0f),
      new VoxelVertex(1.0f,  1.0f, 0.0f)
  };

  public static final VoxelVertex[] LEFT = {
      new VoxelVertex(0.0f,  1.0f, 0.0f),
      new VoxelVertex(0.0f, 0.0f, 0.0f),
      new VoxelVertex(0.0f, 0.0f,  1.0f),
      new VoxelVertex(0.0f, 0.0f,  1.0f),
      new VoxelVertex(0.0f,  1.0f,  1.0f),
      new VoxelVertex(0.0f,  1.0f, 0.0f)
  };

  public static final VoxelVertex[] RIGHT = {
      new VoxelVertex(1.0f,  1.0f,  1.0f),
      new VoxelVertex(1.0f, 0.0f,  1.0f),
      new VoxelVertex(1.0f, 0.0f, 0.0f),
      new VoxelVertex(1.0f, 0.0f, 0.0f),
      new VoxelVertex(1.0f,  1.0f, 0.0f),
      new VoxelVertex(1.0f,  1.0f,  1.0f)
  };

  public static final VoxelVertex[] TOP = {
      new VoxelVertex(1.0f,  1.0f,  1.0f),
      new VoxelVertex(1.0f,  1.0f, 0.0f),
      new VoxelVertex(0.0f,  1.0f, 0.0f),
      new VoxelVertex(0.0f,  1.0f, 0.0f),
      new VoxelVertex(0.0f,  1.0f,  1.0f),
      new VoxelVertex(1.0f,  1.0f,  1.0f)
  };

  public static final VoxelVertex[] BOTTOM = {
      new VoxelVertex(0.0f, 0.0f,  1.0f),
      new VoxelVertex(0.0f, 0.0f, 0.0f),
      new VoxelVertex(1.0f, 0.0f, 0.0f),
      new VoxelVertex(1.0f, 0.0f, 0.0f),
      new VoxelVertex(1.0f, 0.0f,  1.0f),
      new VoxelVertex(0.0f, 0.0f,  1.0f)
  };
}
