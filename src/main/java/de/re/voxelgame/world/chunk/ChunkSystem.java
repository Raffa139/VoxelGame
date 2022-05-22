package de.re.voxelgame.world.chunk;

import de.re.engine.GLApplication;
import de.re.engine.ecs.system.ApplicationSystem;
import de.re.voxelgame.camera.VoxelCamera;
import de.re.voxelgame.camera.VoxelCameraSystem;
import de.re.voxelgame.util.Pair;
import de.re.voxelgame.world.WorldPosition;
import org.joml.Vector3f;

import java.util.*;

public class ChunkSystem extends ApplicationSystem {
  // Keeps track of chunks & their positions
  // Loading chunks as needed (when camera moves)
  // Memory management of chunk data

  private static final int VIEW_DISTANCE = 8;

  private final Map<Vector3f, Chunk> bufferedChunks = new HashMap<>();

  private final Map<Vector3f, Chunk> chunks = new HashMap<>();

  private final VoxelCamera camera;

  private WorldPosition lastCameraPosition;

  private final ChunkLoadingSystem loadingSystem;

  public ChunkSystem(GLApplication application) {
    super(application);
    camera = application.getEcs().getSystem(VoxelCameraSystem.class).getCamera();
    lastCameraPosition = camera.getWorldPosition().copy();
    loadingSystem = application.getEcs().getSystem(ChunkLoadingSystem.class);
  }

  @Override
  public void invoke() {
    // Check if loading system has buffered chunks and add them to map
    for (Pair<Vector3f, Chunk> buffered : loadingSystem.getBufferedChunks()) {
      bufferedChunks.put(buffered.getKey(), buffered.getValue());
      loadingSystem.removeBuffered(buffered);
    }

    // Check if loading system has chunks loaded and add them to map + load mesh
    for (Pair<Vector3f, Chunk> generated : loadingSystem.getGeneratedChunks()) {
      var chunk = generated.getValue();
      chunks.put(generated.getKey(), chunk);
      chunk.setMesh(ChunkLoader.loadChunkMesh(chunk, null, bufferedChunks));
      loadingSystem.removeGenerated(generated);
    }

    if (!lastCameraPosition.getCurrentChunkPosition().equals(camera.getWorldPosition().getCurrentChunkPosition())) {
      // Fill chunk loading queue
      // TODO: Spiral moving outwards -> to load chunks near camera first
      for (int x = -VIEW_DISTANCE; x < VIEW_DISTANCE; x++) {
        for (int z = -VIEW_DISTANCE; z < VIEW_DISTANCE; z++) {
          for (int y = -VIEW_DISTANCE; y < VIEW_DISTANCE; y++) {
            Vector3f position = camera.getWorldPosition().getCurrentChunkPositionOffset(x, y, z);

            // Stop if chunk is already present in map
            if (chunks.containsKey(position)) {
              break;
            }

            float distance = camera.getWorldPosition().getCurrentChunkPosition().distance(position);
            if (distance < (float) VIEW_DISTANCE) {
              loadingSystem.generate(position);

              buffer(position.add(0.0f, 0.0f, 1.0f, new Vector3f()));  // South
              buffer(position.add(0.0f, 0.0f, -1.0f, new Vector3f())); // North
              buffer(position.add(1.0f, 0.0f, 0.0f, new Vector3f()));  // East
              buffer(position.add(-1.0f, 0.0f, 0.0f, new Vector3f())); // West
              buffer(position.add(0.0f, 1.0f, 0.0f, new Vector3f()));  // Top
              buffer(position.add(0.0f, -1.0f, 0.0f, new Vector3f())); // Bottom
            }
          }
        }
      }

      // Fill chunk unloading queue
      List<Chunk> loadedChunks = new ArrayList<>(getChunks());
      for (Chunk chunk : loadedChunks) {
        Vector3f position = chunk.getRelativePosition().getVector();
        float distance = position.distance(camera.getWorldPosition().getCurrentChunkPosition());

        if (distance >= (float) VIEW_DISTANCE) {
          chunks.remove(chunk.getRelativePosition().getVector());
          if (chunk.hasMesh()) {
            loadingSystem.demolish(chunk.getMesh());
          }
        }
      }

      // Clear chunk buffer
      List<Chunk> buffered = new ArrayList<>(getBufferedChunks());
      for (Chunk chunk : buffered) {
        Vector3f position = chunk.getRelativePosition().getVector();
        float distance = position.distance(camera.getWorldPosition().getCurrentChunkPosition());

        if (distance >= VIEW_DISTANCE * 2.0f) {
          bufferedChunks.remove(chunk.getRelativePosition().getVector());
        }
      }

      lastCameraPosition = camera.getWorldPosition().copy();
    }
  }

  public void reloadChunk(Vector3f position, Vector3f voxelPosition) {
    if (chunks.containsKey(position)) {
      var chunk = chunks.get(position);
      if (chunk.hasMesh()) {
        loadingSystem.demolish(chunk.getMesh());
      }
      chunk.setMesh(ChunkLoader.loadChunkMesh(chunk, voxelPosition, chunks));
    }
  }

  public Map<Vector3f, Chunk> getChunkMap() {
    return chunks;
  }

  public Collection<Chunk> getChunks() {
    return chunks.values();
  }

  public Map<Vector3f, Chunk> getBufferedChunkMap() {
    return bufferedChunks;
  }

  public Collection<Chunk> getBufferedChunks() {
    return bufferedChunks.values();
  }

  private void buffer(Vector3f position) {
    if (!bufferedChunks.containsKey(position)) {
      loadingSystem.buffer(position);
    }
  }
}
