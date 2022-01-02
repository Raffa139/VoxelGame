package de.re.voxelgame.engine.world;

import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Vector3f;

import java.util.*;

public class ChunkManager {
  // Keeps track of chunks & their positions
  // Loading chunks as needed (when camera moves)
  // Memory management of chunk data

  private static final int CHUNK_COUNT = 20;
  private static final int CHUNK_STACKS = 6;

  private final Map<Vector3f, Chunk> chunks;

  private final OpenSimplexNoise noise;

  private int x;
  private int z;
  private int y;

  private float lastUpdateTime;

  public ChunkManager(OpenSimplexNoise noise) {
    this.chunks = new HashMap<>();
    this.noise = noise;
    this.x = 0;
    this.z = 0;
    this.y = 0;
    this.lastUpdateTime = 0.0f;
  }

  public void generate(float currentTime, float timeout) {
    if ((currentTime - lastUpdateTime) >= timeout) {
      lastUpdateTime = currentTime;

      if (x < CHUNK_COUNT) {
        if (z < CHUNK_COUNT) {
          if (y < CHUNK_STACKS) {
            Vector3f position = new Vector3f(x, y, z);
            Vector3f positionS = new Vector3f(x, y, z+1);
            Vector3f positionN = new Vector3f(x, y, z-1);
            Vector3f positionE = new Vector3f(x+1, y, z);
            Vector3f positionW = new Vector3f(x-1, y, z);
            Vector3f positionT = new Vector3f(x, y+1, z);
            Vector3f positionB = new Vector3f(x, y-1, z);

            if (z < CHUNK_COUNT-1) {
              preloadOrGetChunk(positionS);
            }
            if (z > 0) {
              preloadOrGetChunk(positionN);
            }
            if (x < CHUNK_COUNT-1) {
              preloadOrGetChunk(positionE);
            }
            if (x > 0) {
              preloadOrGetChunk(positionW);
            }
            if (y < CHUNK_STACKS-1) {
              preloadOrGetChunk(positionT);
            }
            if (y > 0) {
              preloadOrGetChunk(positionB);
            }
            loadChunk(position, null);

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

  public void reloadChunk(Vector3f position, Vector3f voxelPosition) {
    if (chunks.containsKey(position)) {
      Chunk chunk = chunks.get(position);

      if (chunk.hasMesh()) {
        ChunkLoader.unloadChunkMesh(chunk.getMesh());
      }
      loadChunk(position, voxelPosition);
    }
  }

  public Map<Vector3f, Chunk> getChunkPositionMap() {
    return chunks;
  }

  public Collection<Chunk> getChunks() {
    return chunks.values();
  }

  private Chunk preloadOrGetChunk(Vector3f position) {
    if (!chunks.containsKey(position)) {
      Chunk chunk = ChunkLoader.generateChunk(new WorldPosition(position), noise);
      chunks.put(position, chunk);
      return chunk;
    }

    return chunks.get(position);
  }

  private void loadChunk(Vector3f position, Vector3f voxelPosition) {
    Chunk chunk = preloadOrGetChunk(position);
    chunk.setMesh(ChunkLoader.loadChunkMesh(chunk, voxelPosition, chunks));
  }
}
