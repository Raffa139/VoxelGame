package de.re.voxelgame.engine.world;

import de.re.voxelgame.core.util.Vectors;
import de.re.voxelgame.engine.intersection.AABB;

public class Chunk {
  public static final int CHUNK_SIZE = 32;

  private final WorldPosition position;

  private final AABB boundingBox;

  private final int vaoId;

  private final int vertexCount;

  public Chunk(WorldPosition position, int vaoId, int vertexCount) {
    this.position = position;
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;

    WorldPosition worldPos = getWorldPosition();
    this.boundingBox = new AABB(worldPos.getVector(), Vectors.add(worldPos.getVector(), CHUNK_SIZE-1));
  }

  public boolean containsVertices() {
    return vaoId >= 0 && vertexCount > 0;
  }

  public WorldPosition getRelativePosition() {
    return position;
  }

  public WorldPosition getWorldPosition() {
    return new WorldPosition(Vectors.mul(position.getVector(), CHUNK_SIZE));
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
