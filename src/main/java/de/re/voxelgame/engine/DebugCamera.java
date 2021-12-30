package de.re.voxelgame.engine;

import de.re.voxelgame.core.Camera;
import de.re.voxelgame.engine.world.Chunk;
import org.joml.Vector3f;

public class DebugCamera extends Camera {
  public DebugCamera(Vector3f pos) {
    super(pos);
  }

  public Vector3f getPositionOfCurrentChunk() {
    return new Vector3f(
        (float) Math.floor(getPos().x / Chunk.CHUNK_SIZE),
        (float) Math.floor(getPos().y / Chunk.CHUNK_SIZE),
        (float) Math.floor(getPos().z / Chunk.CHUNK_SIZE)
    );
  }

  public Vector3f getPositionInCurrentChunk() {
    return new Vector3f(
        getPos().x % Chunk.CHUNK_SIZE,
        getPos().y % Chunk.CHUNK_SIZE,
        getPos().z % Chunk.CHUNK_SIZE
    );
  }
}
