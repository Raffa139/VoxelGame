package de.re.voxelgame.util;

import de.re.voxelgame.camera.VoxelCameraSystem;
import de.re.voxelgame.world.WorldPosition;
import de.ren.ecs.engine.GLApplication;
import de.re.voxelgame.world.chunk.ChunkLoadingSystem;
import de.re.voxelgame.world.chunk.ChunkSystem;
import de.ren.ecs.engine.ecs.ApplicationSystem;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

public class DebugSystem extends ApplicationSystem {
  private static final DecimalFormat DF = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

  private final ChunkLoadingSystem loadingSystem;

  private final ChunkSystem chunkSystem;

  private final WorldPosition cameraPosition;

  private float lastUpdate;

  private List<String> debugStrings;

  public DebugSystem(GLApplication application) {
    super(application);
    loadingSystem = application.getEcs().getSystem(ChunkLoadingSystem.class);
    chunkSystem = application.getEcs().getSystem(ChunkSystem.class);
    cameraPosition = application.getEcs().getSystem(VoxelCameraSystem.class).getCamera().getWorldPosition();
    lastUpdate = application.getCurrentTime();
    debugStrings = new ArrayList<>();
  }

  @Override
  public void invoke() {
    float currentTime = application.getCurrentTime();
    if (currentTime > lastUpdate + 0.1f) {
      updateDebugStrings();
      lastUpdate = currentTime;
    }
  }

  public List<String> getDebugStrings() {
    return debugStrings;
  }

  private void updateDebugStrings() {
    List<String> debugStrings = new ArrayList<>();

    Vector3f world = cameraPosition.getVector();
    Vector3f chunk = cameraPosition.getAbsolutePositionInCurrentChunk();
    Vector3f chunkWorld = cameraPosition.getCurrentChunkPosition();

    debugStrings.add(format("Cam X: %s, Y: %s, Z: %s (chunk X: %s, chunk Y: %s, chunk Z: %s)", DF.format(world.x), DF.format(world.y), DF.format(world.z), chunk.x, chunk.y, chunk.z));
    debugStrings.add(format("Chunk X: %s, Y: %s, Z: %s", DF.format(chunkWorld.x), DF.format(chunkWorld.y), DF.format(chunkWorld.z)));
    debugStrings.add("Queued for generation: " + loadingSystem.getQueuedAmountToGenerate());
    debugStrings.add("Queued for buffering: " + loadingSystem.getQueuedAmountToBuffer());
    debugStrings.add("Queued for demolishing: " + loadingSystem.getQueuedAmountToDemolish());
    //debugStrings.add("Future meshed: " + loadingSystem.getGeneratedChunks().size());
    //debugStrings.add("Future buffered: " + loadingSystem.getBufferedChunks().size());
    debugStrings.add("Loaded chunks: " + chunkSystem.getChunks().size());
    debugStrings.add("Buffered chunks: " + chunkSystem.getBufferedChunks().size());

    this.debugStrings = debugStrings;
  }
}
