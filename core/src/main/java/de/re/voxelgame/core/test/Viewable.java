package de.re.voxelgame.core.test;

public class Viewable {
  private final int vaoId;

  private final int vertexCount;

  public Viewable(int vaoId, int vertexCount) {
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;
  }

  public int getVaoId() {
    return vaoId;
  }

  public int getVertexCount() {
    return vertexCount;
  }
}
