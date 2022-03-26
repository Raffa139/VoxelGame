package de.re.voxelgame.world;

import org.joml.Vector3f;

import java.util.Objects;

public class WorldPosition {
  private final Vector3f vector;

  public WorldPosition(float d) {
    this(d, d, d);
  }

  public WorldPosition(float x, float y, float z) {
    this(new Vector3f(x, y, z));
  }

  public WorldPosition(Vector3f vector) {
    this.vector = vector;
  }

  public WorldPosition copy() {
    return new WorldPosition(getVector());
  }

  public Vector3f getCurrentChunkPosition() {
    return new Vector3f(
        (float) Math.floor(vector.x / Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.y / Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.z / Chunk.CHUNK_SIZE)
    );
  }

  public Vector3f getCurrentChunkPositionOffset(int x, int y, int z) {
    Vector3f chunkPosition = getCurrentChunkPosition();
    return new Vector3f(chunkPosition.x + x, chunkPosition.y + y, chunkPosition.z + z);
  }

  public Vector3f getCurrentAbsoluteChunkPosition() {
    return getCurrentChunkPosition().absolute();
  }

  public Vector3f getPositionInCurrentChunk() {
    return new Vector3f(
        (float) Math.floor(vector.x % Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.y % Chunk.CHUNK_SIZE),
        (float) Math.floor(vector.z % Chunk.CHUNK_SIZE)
    );
  }

  public Vector3f getAbsolutePositionInCurrentChunk() {
    Vector3f pos = getPositionInCurrentChunk();

    if (pos.x < 0.0f && pos.x != -16.0f) {
      float diff = pos.x + 16.0f;
      pos.x = -16.0f - diff;
    }

    if (pos.z < 0.0f && pos.z != -16.0f) {
      float diff = pos.z + 16.0f;
      pos.z = -16.0f - diff;
    }

    return pos.absolute();
  }

  public Vector3f getVector() {
    return new Vector3f(vector);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WorldPosition that = (WorldPosition) o;
    return Objects.equals(vector, that.vector);
  }

  @Override
  public int hashCode() {
    return vector != null ? vector.hashCode() : 0;
  }
}
