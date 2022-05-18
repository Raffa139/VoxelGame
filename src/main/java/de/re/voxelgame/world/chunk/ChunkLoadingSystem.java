package de.re.voxelgame.world.chunk;

import de.re.engine.GLApplication;
import de.re.engine.ecs.system.ApplicationSystem;
import de.re.voxelgame.util.Pair;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.noise.OpenSimplexNoise;
import org.joml.Vector3f;

import java.util.*;

public class ChunkLoadingSystem extends ApplicationSystem {
  private final Queue<Vector3f> loadingQueue = new LinkedList<>();

  private final Set<Pair<Vector3f, Chunk>> futureChunks = new HashSet<>();

  private final OpenSimplexNoise noise = new OpenSimplexNoise(139L);

  public ChunkLoadingSystem(GLApplication application) {
    super(application);
  }

  @Override
  public void invoke() {
    if (loadingQueue.peek() != null) {
      var position = loadingQueue.poll();
      var chunk = ChunkLoader.generateChunk(new WorldPosition(position), noise);
      futureChunks.add(new Pair<>(position, chunk));
    }
  }

  public void queue(Vector3f chunk) {
    loadingQueue.add(chunk);
  }

  public void removeFuture(Pair<Vector3f, Chunk> future) {
    futureChunks.remove(future);
  }

  public Queue<Vector3f> getLoadingQueue() {
    return loadingQueue;
  }

  public Set<Pair<Vector3f, Chunk>> getFutureChunks() {
    return Collections.unmodifiableSet(futureChunks);
  }
}
