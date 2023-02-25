package de.re.voxelgame.world.chunk;

import de.ren.ecs.engine.GLApplication;
import de.re.voxelgame.util.Pair;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.noise.OpenSimplexNoise;
import de.ren.ecs.engine.ecs.ApplicationSystem;
import org.joml.Vector3f;

import java.util.*;

public class ChunkLoadingSystem extends ApplicationSystem {
  private static final int GENERATION_AMOUNT_PER_FRAME = 2;
  private static final int BUFFERING_AMOUNT_PER_FRAME = 4;
  private static final int DEMOLISHING_AMOUNT_PER_FRAME = 1;

  private final Queue<Vector3f> generationQueue = new LinkedList<>();

  private final Queue<Vector3f> bufferQueue = new LinkedList<>();

  private final Queue<ChunkMesh> demolishingQueue = new LinkedList<>();

  private final Set<Pair<Vector3f, Chunk>> generatedChunks = new HashSet<>();

  private final Set<Pair<Vector3f, Chunk>> bufferedChunks = new HashSet<>();

  private final OpenSimplexNoise noise = new OpenSimplexNoise(new Date().getTime());

  public ChunkLoadingSystem(GLApplication application) {
    super(application);
  }

  @Override
  public void invoke() {
    for (int i = 0; i < GENERATION_AMOUNT_PER_FRAME; i++) {
      performGeneration();
    }

    for (int i = 0; i < BUFFERING_AMOUNT_PER_FRAME; i++) {
      performBuffering();
    }

    for (int i = 0; i < DEMOLISHING_AMOUNT_PER_FRAME; i++) {
      performDemolishing();
    }
  }

  public void generate(Vector3f chunk) {
    if (!generationQueue.contains(chunk)) {
      generationQueue.add(chunk);
    }
  }

  public void buffer(Vector3f position) {
    if (!bufferQueue.contains(position)) {
      bufferQueue.add(position);
    }
  }

  public void demolish(ChunkMesh mesh) {
    if (!demolishingQueue.contains(mesh)) {
      demolishingQueue.add(mesh);
    }
  }

  public void removeGenerated(Pair<Vector3f, Chunk> generated) {
    generatedChunks.remove(generated);
  }

  public void removeBuffered(Pair<Vector3f, Chunk> buffered) {
    bufferedChunks.remove(buffered);
  }

  public int getQueuedAmountToGenerate() {
    return generationQueue.size();
  }

  public int getQueuedAmountToBuffer() {
    return bufferQueue.size();
  }

  public int getQueuedAmountToDemolish() {
    return demolishingQueue.size();
  }

  public Set<Pair<Vector3f, Chunk>> getGeneratedChunks() {
    return Set.copyOf(generatedChunks);
  }

  public Set<Pair<Vector3f, Chunk>> getBufferedChunks() {
    return Set.copyOf(bufferedChunks);
  }

  private void performGeneration() {
    if (generationQueue.peek() != null) {
      var position = generationQueue.poll();
      var chunk = ChunkLoader.generateChunk(new WorldPosition(position), noise);
      generatedChunks.add(new Pair<>(position, chunk));
    }
  }

  private void performBuffering() {
    if (bufferQueue.peek() != null) {
      var position = bufferQueue.poll();
      var chunk = ChunkLoader.generateChunk(new WorldPosition(position), noise);
      bufferedChunks.add(new Pair<>(position, chunk));
    }
  }

  private void performDemolishing() {
    if (demolishingQueue.peek() != null) {
      var mesh = demolishingQueue.poll();
      ChunkLoader.unloadChunkMesh(mesh);
    }
  }
}
