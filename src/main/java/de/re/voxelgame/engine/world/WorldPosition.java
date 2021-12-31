package de.re.voxelgame.engine.world;

import org.joml.Vector3f;

public class WorldPosition {
  private Vector3f vector;

  public WorldPosition(float d) {
    this(d, d, d);
  }

  public WorldPosition(float x, float y, float z) {
    this(new Vector3f(x, y, z));
  }

  public WorldPosition(Vector3f vector) {
    this.vector = vector;
  }

  public Vector3f getCurrentChunkPosition() {
    return new Vector3f(
        (float) Math.floor(vector.x / Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.y / Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.z / Chunk.CHUNK_SIZE)
    );
  }

  public Vector3f getPositionInCurrentChunk() {
    return new Vector3f(
        (float) Math.floor(vector.x % Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.y % Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.z % Chunk.CHUNK_SIZE)
    );
  }

  public Vector3f getVector() {
    return vector;
  }
}
