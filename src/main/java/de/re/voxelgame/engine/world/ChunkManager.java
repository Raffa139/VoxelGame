package de.re.voxelgame.engine.world;

import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ChunkManager {
  // Keeps track of chunks & their positions
  // Loading chunks as needed (when camera moves)
  // Memory management of chunk data

  private static final int CHUNK_COUNT = 20;
  private static final int CHUNK_STACKS = 6;

  private final List<Chunk> chunks;

  private final OpenSimplexNoise noise;

  private int x;
  private int z;
  private int y;

  private float lastUpdateTime;

  public ChunkManager(OpenSimplexNoise noise) {
    this.chunks = new ArrayList<>();
    this.noise = noise;
    this.x = 0;
    this.z = 0;
    this.y = 0;
    this.lastUpdateTime = 0.0f;
  }

  public void update(float currentTime, float timeout) {
    if ((currentTime - lastUpdateTime) >= timeout) {
      lastUpdateTime = currentTime;

      if (x < CHUNK_COUNT) {
        if (z < CHUNK_COUNT) {
          if (y < CHUNK_STACKS) {
            chunks.add(ChunkLoader.loadChunkNoise(new Vector3f(x, y, z), noise));
            y++;
          }

          if (y == CHUNK_STACKS) {
            z++;
            y = 0;
          }
        }

        if (z == CHUNK_COUNT) {
          x++;
          z = 0;
        }
      }
    }
  }

  public List<Chunk> getChunks() {
    return chunks;
  }
}
