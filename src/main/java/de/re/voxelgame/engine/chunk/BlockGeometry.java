package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.Vertex;

public final class BlockGeometry {
  // Visual: https://www.math3d.org/OdaojzOmz

  private BlockGeometry() {
  }

  public static final Vertex[] FRONT = {
      new Vertex(0.0f,  1.0f, 1.0f),
      new Vertex(0.0f, 0.0f, 1.0f),
      new Vertex(1.0f, 0.0f, 1.0f),
      new Vertex(1.0f, 0.0f, 1.0f),
      new Vertex(1.0f,  1.0f, 1.0f),
      new Vertex(0.0f,  1.0f, 1.0f)
  };

  public static final Vertex[] BACK = {
      new Vertex(1.0f,  1.0f, 0.0f),
      new Vertex(1.0f, 0.0f, 0.0f),
      new Vertex(0.0f, 0.0f, 0.0f),
      new Vertex(0.0f, 0.0f, 0.0f),
      new Vertex(0.0f,  1.0f, 0.0f),
      new Vertex(1.0f,  1.0f, 0.0f)
  };

  public static final Vertex[] LEFT = {
      new Vertex(0.0f,  1.0f, 0.0f),
      new Vertex(0.0f, 0.0f, 0.0f),
      new Vertex(0.0f, 0.0f,  1.0f),
      new Vertex(0.0f, 0.0f,  1.0f),
      new Vertex(0.0f,  1.0f,  1.0f),
      new Vertex(0.0f,  1.0f, 0.0f)
  };

  public static final Vertex[] RIGHT = {
      new Vertex(1.0f,  1.0f,  1.0f),
      new Vertex(1.0f, 0.0f,  1.0f),
      new Vertex(1.0f, 0.0f, 0.0f),
      new Vertex(1.0f, 0.0f, 0.0f),
      new Vertex(1.0f,  1.0f, 0.0f),
      new Vertex(1.0f,  1.0f,  1.0f)
  };

  public static final Vertex[] TOP = {
      new Vertex(1.0f,  1.0f,  1.0f),
      new Vertex(1.0f,  1.0f, 0.0f),
      new Vertex(0.0f,  1.0f, 0.0f),
      new Vertex(0.0f,  1.0f, 0.0f),
      new Vertex(0.0f,  1.0f,  1.0f),
      new Vertex(1.0f,  1.0f,  1.0f)
  };

  public static final Vertex[] BOTTOM = {
      new Vertex(0.0f, 0.0f,  1.0f),
      new Vertex(0.0f, 0.0f, 0.0f),
      new Vertex(1.0f, 0.0f, 0.0f),
      new Vertex(1.0f, 0.0f, 0.0f),
      new Vertex(1.0f, 0.0f,  1.0f),
      new Vertex(0.0f, 0.0f,  1.0f)
  };
}
