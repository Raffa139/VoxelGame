package de.re.voxelgame.world.chunk;

import de.re.engine.GLApplication;
import de.re.engine.ecs.system.ApplicationSystem;
import de.re.voxelgame.camera.VoxelCamera;
import de.re.voxelgame.camera.VoxelCameraSystem;
import de.re.voxelgame.util.Pair;
import org.joml.Vector3f;

import java.util.*;

public class ChunkSystem extends ApplicationSystem {
  // Keeps track of chunks & their positions
  // Loading chunks as needed (when camera moves)
  // Memory management of chunk data

  private final Map<Vector3f, Chunk> chunks = new HashMap<>();

  private final VoxelCamera camera;

  private final ChunkLoadingSystem loadingSystem;

  public ChunkSystem(GLApplication application) {
    super(application);
    camera = application.getEcs().getSystem(VoxelCameraSystem.class).getCamera();
    loadingSystem = application.getEcs().getSystem(ChunkLoadingSystem.class);
  }

  @Override
  public void invoke() {
    // Check if loading system has chunks loaded and add them to map
    for (Pair<Vector3f, Chunk> futureChunk : loadingSystem.getFutureChunks()) {
      var chunk = futureChunk.getValue();
      chunks.put(futureChunk.getKey(), chunk);
      chunk.setMesh(ChunkLoader.loadChunkMesh(chunk, null, chunks));
      loadingSystem.removeFuture(futureChunk);
    }

    for (int x = 0; x < 1; x++) {
      for (int z = 0; z < 1; z++) {
        for (int y = -2; y < 2; y++) {
          Vector3f chunk = camera.getWorldPosition().getCurrentChunkPositionOffset(x, y, z);

          // Stop if y-pos is below zero
          if (chunk.y < 0) {
            break;
          }

          // Stop if chunk is already present in map
          if (chunks.containsKey(chunk)) {
            break;
          }

          loadingSystem.queue(chunk);
        }
      }
    }
  }

  public Map<Vector3f, Chunk> getChunkMap() {
    return chunks;
  }

  public Collection<Chunk> getChunks() {
    return chunks.values();
  }
}
