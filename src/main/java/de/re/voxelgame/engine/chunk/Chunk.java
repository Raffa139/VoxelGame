package de.re.voxelgame.engine.chunk;

import org.joml.Vector3f;

public class Chunk {
  public static final int CHUNK_SIZE = 16;

  private final Vector3f position;

  private final int vaoId;

  private final int vertexCount;

  public Chunk(Vector3f position, int vaoId, int vertexCount) {
    this.position = position;
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;
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
