package de.re.voxelgame.engine.world;

public class ChunkMesh {
  private final int vaoId;

  private final int vertexCount;

  public ChunkMesh(int vaoId, int vertexCount) {
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;
  }

  public boolean containsVertices() {
    return vaoId >= 0 && vertexCount > 0;
  }

  public int getVaoId() {
    return vaoId;
  }

  public int getVertexCount() {
    return vertexCount;
  }
}
