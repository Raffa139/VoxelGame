package de.re.voxelgame.world.chunk;

import de.re.engine.GLApplication;
import de.re.engine.ecs.system.ApplicationSystem;
import de.re.voxelgame.util.Pair;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.noise.OpenSimplexNoise;
import org.joml.Vector3f;

import java.util.*;

public class ChunkLoadingSystem extends ApplicationSystem {
  private static final int CHUNK_LOADING_AMOUNT_PER_FRAME = 2;
  private static final int CHUNK_UNLOADING_AMOUNT_PER_FRAME = 2;

  private final Queue<Vector3f> loadingQueue = new LinkedList<>();

  private final Queue<Chunk> unloadingQueue = new LinkedList<>();

  private final Set<Pair<Vector3f, Chunk>> futureChunks = new HashSet<>();

  private final OpenSimplexNoise noise = new OpenSimplexNoise(139L);

  public ChunkLoadingSystem(GLApplication application) {
    super(application);
  }

  @Override
  public void invoke() {
    for (int i = 0; i < CHUNK_LOADING_AMOUNT_PER_FRAME; i++) {
      performLoading();
    }

    for (int i = 0; i < CHUNK_UNLOADING_AMOUNT_PER_FRAME; i++) {
      performUnloading();
    }
  }

  public void queue(Vector3f chunk) {
    if (!loadingQueue.contains(chunk)) {
      loadingQueue.add(chunk);
    }
  }

  public void dequeue(Chunk chunk) {
    if (!unloadingQueue.contains(chunk)) {
      unloadingQueue.add(chunk);
    }
  }

  public void removeFuture(Pair<Vector3f, Chunk> future) {
    futureChunks.remove(future);
  }

  public int getQueuedAmount() {
    return loadingQueue.size();
  }

  public int getDequeuedAmount() {
    return unloadingQueue.size();
  }

  public Set<Pair<Vector3f, Chunk>> getFutureChunks() {
    return Set.copyOf(futureChunks);
  }

  private void performLoading() {
    if (loadingQueue.peek() != null) {
      var position = loadingQueue.poll();
      var chunk = ChunkLoader.generateChunk(new WorldPosition(position), noise);
      futureChunks.add(new Pair<>(position, chunk));
    }
  }

  private void performUnloading() {
    if (unloadingQueue.peek() != null) {
      var chunk = unloadingQueue.poll();
      if (chunk.hasMesh()) {
        ChunkLoader.unloadChunkMesh(chunk.getMesh());
      }
    }
  }
}
