package de.re.voxelgame.engine;

import de.re.voxelgame.core.util.Vectors;
import de.re.voxelgame.engine.world.Chunk;
import de.re.voxelgame.engine.world.ChunkManager;
import de.re.voxelgame.engine.world.WorldPosition;
import org.joml.Vector3f;

public class CrossHairTarget {
  private static final float MAX_RANGE = 8.0f;
  private static final float SAMPLE_RATE = 0.1f;

  private final ChunkManager chunkManager;

  private WorldPosition targetedVoxel;

  private WorldPosition placeableVoxel;

  private boolean targetInRange = false;

  public CrossHairTarget(ChunkManager chunkManager) {
    this.chunkManager = chunkManager;
  }

  public void update(Vector3f cameraPos, Vector3f cameraDirection) {
    targetInRange = false;

    for (float t = 0.0f; t < MAX_RANGE; t += SAMPLE_RATE) {
      targetedVoxel = new WorldPosition(Vectors.add(cameraPos, Vectors.mul(cameraDirection.normalize(), t)));
      Vector3f chunkPos = targetedVoxel.getCurrentChunkPosition();
      Vector3f voxelPos = targetedVoxel.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);

      byte voxelId = chunk != null ? chunk.getVoxelId((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z) : 0;
      if (voxelId != 0) {
        placeableVoxel = new WorldPosition(Vectors.add(cameraPos, Vectors.mul(cameraDirection.normalize(), t-SAMPLE_RATE)));
        targetInRange = true;
        break;
      }
    }
  }

  public WorldPosition getTargetedVoxel() {
    return targetedVoxel;
  }

  public WorldPosition getPlaceableVoxel() {
    return placeableVoxel;
  }

  public boolean isTargetInRange() {
    return targetInRange;
  }
}
