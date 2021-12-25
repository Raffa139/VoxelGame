package de.re.voxelgame.engine.world;

import org.joml.Vector3f;

public class Chunk {
  public static final int CHUNK_SIZE = 32;

  private final Vector3f position;

  private final int vaoId;

  private final int vertexCount;

  public Chunk(Vector3f position, int vaoId, int vertexCount) {
    this.position = position;
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;
  }

  public boolean containsVertices() {
    return vaoId >= 0 && vertexCount > 0;
  }

  public Vector3f getPosition() {
    return position;
  }

  public int getVaoId() {
    return vaoId;
  }

  public int getVertexCount() {
    return vertexCount;
  }
}
