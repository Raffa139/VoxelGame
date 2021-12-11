package de.re.voxelgame.core;

public final class BlockGeometry {
  // Visual: https://www.math3d.org/OdaojzOmz

  private BlockGeometry() {
  }

  public static final Vertex[] FRONT = {
      new Vertex(-0.5f,  0.5f, 0.5f, 0.5f, 0.0f),
      new Vertex(-0.5f, -0.5f, 0.5f, 0.5f, 0.5f),
      new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.5f),
      new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.5f),
      new Vertex(0.5f,  0.5f, 0.5f, 0.0f, 0.0f),
      new Vertex(-0.5f,  0.5f, 0.5f, 0.5f, 0.0f)
  };

  public static final Vertex[] BACK = {
      new Vertex(0.5f,  0.5f, -0.5f, 0.5f, 0.0f),
      new Vertex(0.5f, -0.5f, -0.5f, 0.5f, 0.5f),
      new Vertex(-0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(-0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(-0.5f,  0.5f, -0.5f, 0.0f, 0.0f),
      new Vertex(0.5f,  0.5f, -0.5f, 0.5f, 0.0f)
  };

  public static final Vertex[] LEFT = {
      new Vertex(-0.5f,  0.5f, -0.5f, 0.5f, 0.0f),
      new Vertex(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f),
      new Vertex(-0.5f, -0.5f,  0.5f, 0.0f, 0.5f),
      new Vertex(-0.5f, -0.5f,  0.5f, 0.0f, 0.5f),
      new Vertex(-0.5f,  0.5f,  0.5f, 0.0f, 0.0f),
      new Vertex(-0.5f,  0.5f, -0.5f, 0.5f, 0.0f)
  };

  public static final Vertex[] RIGHT = {
      new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f),
      new Vertex(0.5f, -0.5f,  0.5f, 0.5f, 0.5f),
      new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(0.5f,  0.5f, -0.5f, 0.0f, 0.0f),
      new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f)
  };

  public static final Vertex[] TOP = {
      new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f),
      new Vertex(0.5f,  0.5f, -0.5f, 0.5f, 0.5f),
      new Vertex(-0.5f,  0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(-0.5f,  0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(-0.5f,  0.5f,  0.5f, 0.0f, 0.0f),
      new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f)
  };

  public static final Vertex[] BOTTOM = {
      new Vertex(-0.5f, -0.5f,  0.5f, 0.5f, 0.0f),
      new Vertex(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f),
      new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
      new Vertex(0.5f, -0.5f,  0.5f, 0.0f, 0.0f),
      new Vertex(-0.5f, -0.5f,  0.5f, 0.5f, 0.0f)
  };
}
