package de.re.voxelgame.util;

import de.re.engine.GLApplication;
import de.re.engine.ecs.system.ApplicationSystem;
import de.re.voxelgame.world.chunk.ChunkLoadingSystem;
import de.re.voxelgame.world.chunk.ChunkSystem;

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
    System.out.println("Queued chunks: " + loadingSystem.getLoadingQueue().size());
    System.out.println("Future chunks: " + loadingSystem.getFutureChunks().size());
    System.out.println("Loaded chunks: " + chunkSystem.getChunks().size());
    System.out.println("------------------------------------------------");
  }
}
