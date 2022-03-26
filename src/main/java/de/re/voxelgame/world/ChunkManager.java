package de.re.voxelgame.world;

import de.re.voxelgame.VoxelCamera;
import de.re.voxelgame.noise.OpenSimplexNoise;
import org.joml.Vector3f;

import java.util.*;

public class ChunkManager {
  // Keeps track of chunks & their positions
  // Loading chunks as needed (when camera moves)
  // Memory management of chunk data

  private static final int CHUNK_COUNT = 10;
  private static final int CHUNK_STACKS = 6;

  private final Map<Vector3f, Chunk> chunks;

  private final OpenSimplexNoise noise;

  private int x;
  private int z;
  private int y;

  private float lastUpdateTime;

  private WorldPosition lastCamPos;

  public ChunkManager(OpenSimplexNoise noise) {
    this.chunks = new HashMap<>();
    this.noise = noise;
    this.x = 0;
    this.z = 0;
    this.y = 0;
    this.lastUpdateTime = 0.0f;
  }

  public void initCamPos(VoxelCamera camera) {
    lastCamPos = camera.getWorldPosition().copy();
  }

  public void update(VoxelCamera camera) {
    if (!lastCamPos.getCurrentChunkPosition().equals(camera.getWorldPosition().getCurrentChunkPosition())) {
      // load new chunks
      for (int x = -2; x < 2; x++) {
        for (int y = -3; y < 3; y++) {
          for (int z = -2; z < 2; z++) {
            Vector3f chunkPosition = camera.getWorldPosition().getCurrentChunkPositionOffset(x, y, z);
            Chunk chunk = chunks.get(chunkPosition);
            boolean chunkPresent = chunk != null && chunk.hasMesh();
            if (!chunkPresent && chunkPosition.y >= 0) {
              Vector3f positionS = chunkPosition.add(0.0f, 0.0f, 1.0f, new Vector3f());
              Vector3f positionN = chunkPosition.add(0.0f, 0.0f, -1.0f, new Vector3f());
              Vector3f positionE = chunkPosition.add(1.0f, 0.0f, 0.0f, new Vector3f());
              Vector3f positionW = chunkPosition.add(-1.0f, 0.0f, 0.0f, new Vector3f());
              Vector3f positionT = chunkPosition.add(0.0f, 1.0f, 0.0f, new Vector3f());
              Vector3f positionB = chunkPosition.add(0.0f, -1.0f, 0.0f, new Vector3f());

              preloadOrGetChunk(positionS);
              preloadOrGetChunk(positionN);
              preloadOrGetChunk(positionE);
              preloadOrGetChunk(positionW);
              preloadOrGetChunk(positionT);
              preloadOrGetChunk(positionB);

              loadChunk(chunkPosition, null);
            }
          }
        }
      }

      lastCamPos = camera.getWorldPosition().copy();
    }
  }

  public void cancelChunks(VoxelCamera camera) {
    List<Chunk> loadedChunks = new ArrayList<>(getChunks());

    for (Chunk chunk : loadedChunks) {
      Vector3f chunkPosition = chunk.getRelativePosition().getVector();
      float distance = chunkPosition.distance(camera.getWorldPosition().getCurrentChunkPosition());

      if (distance >= 5.0f) {
        unloadChunk(chunk.getRelativePosition().getVector());
      }
    }
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

  private void unloadChunk(Vector3f position) {
    if (chunks.containsKey(position)) {
      Chunk chunk = chunks.get(position);
      if (chunk != null && chunk.hasMesh()) {
        ChunkLoader.unloadChunkMesh(chunk.getMesh());
      }

      chunks.remove(position);
    }
  }
}
