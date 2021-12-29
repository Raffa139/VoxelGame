package de.re.voxelgame.engine.world;

import de.re.voxelgame.core.util.Vectors;
import de.re.voxelgame.engine.intersection.AABB;
import org.joml.Vector3f;

public class Chunk {
  public static final int CHUNK_SIZE = 32;

  private final Vector3f position;

  private final AABB boundingBox;

  private final int vaoId;

  private final int vertexCount;

  public Chunk(Vector3f position, int vaoId, int vertexCount) {
    this.position = position;
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;

    Vector3f worldPos = getWorldPosition();
    this.boundingBox = new AABB(worldPos, Vectors.add(worldPos, CHUNK_SIZE-1));
  }

  public boolean containsVertices() {
    return vaoId >= 0 && vertexCount > 0;
  }

  public Vector3f getPosition() {
    return position;
  }

  public Vector3f getWorldPosition() {
    return Vectors.mul(position, CHUNK_SIZE);
  }

  public AABB getBoundingBox() {
    return boundingBox;
  }

  public int getVaoId() {
    return vaoId;
  }

  public int getVertexCount() {
    return vertexCount;
  }
}
