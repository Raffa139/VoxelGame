package de.re.voxelgame.util;

import de.ren.ecs.engine.GLApplication;
import de.re.voxelgame.world.chunk.ChunkLoadingSystem;
import de.re.voxelgame.world.chunk.ChunkSystem;
import de.ren.ecs.engine.ecs.ApplicationSystem;

public class DebugSystem extends ApplicationSystem {
  private final ChunkLoadingSystem loadingSystem;

  private final ChunkSystem chunkSystem;

  private float lastPrint;

  public DebugSystem(GLApplication application) {
    super(application);
    loadingSystem = application.getEcs().getSystem(ChunkLoadingSystem.class);
    chunkSystem = application.getEcs().getSystem(ChunkSystem.class);
    lastPrint = application.getCurrentTime();
  }

  @Override
  public void invoke() {
    float currentTime = application.getCurrentTime();
    if (currentTime > lastPrint + 4.0f) {
      printDebugInformation();
      lastPrint = currentTime;
    }
  }

  private void printDebugInformation() {
    System.out.println("Queued for generation: " + loadingSystem.getQueuedAmountToGenerate());
    System.out.println("Queued for buffering: " + loadingSystem.getQueuedAmountToBuffer());
    System.out.println("Queued for demolishing: " + loadingSystem.getQueuedAmountToDemolish());
    System.out.println("Future meshed: " + loadingSystem.getGeneratedChunks().size());
    System.out.println("Future buffered: " + loadingSystem.getBufferedChunks().size());
    System.out.println("Loaded chunks: " + chunkSystem.getChunks().size());
    System.out.println("Buffered chunks: " + chunkSystem.getBufferedChunks().size());
    System.out.println("------------------------------------------------");
  }
}
