package de.re.voxelgame.engine.world;

import de.re.voxelgame.engine.VoxelCamera;
import de.re.voxelgame.engine.voxel.VoxelType;
import org.joml.Vector3f;

public class ChunkInteractionManager {
  private final ChunkManager chunkManager;

  private final VoxelCamera camera;

  private WorldPosition lastVoxelInCrossHair;

  public ChunkInteractionManager(ChunkManager chunkManager, VoxelCamera camera) {
    this.chunkManager = chunkManager;
    this.camera = camera;
    lastVoxelInCrossHair = new WorldPosition(0.0f);
  }

  public void highlightVoxel() {
    WorldPosition voxelInCrossHair = camera.getCrossHairTarget().getTargetedVoxel();

    if (!camera.getCrossHairTarget().isTargetInRange() ||
        !voxelInCrossHair.getCurrentChunkPosition().equals(lastVoxelInCrossHair.getCurrentChunkPosition())) {
      chunkManager.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), null);
      chunkManager.reloadChunk(lastVoxelInCrossHair.getCurrentChunkPosition(), null);
      lastVoxelInCrossHair = voxelInCrossHair;
    } else {
      chunkManager.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), voxelInCrossHair.getAbsolutePositionInCurrentChunk());
    }
  }

  public void placeVoxel() {
    WorldPosition placeableVoxel = camera.getCrossHairTarget().getPlaceableVoxel();

    if (camera.getCrossHairTarget().isTargetInRange()) {
      Vector3f chunkPos = placeableVoxel.getCurrentChunkPosition();
      Vector3f voxelPos = placeableVoxel.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);
      if (chunk != null) {
        chunk.placeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z, VoxelType.WOOD);
      }
    }
  }

  public void removeVoxel() {
    WorldPosition voxelInCrossHair = camera.getCrossHairTarget().getTargetedVoxel();

    if (camera.getCrossHairTarget().isTargetInRange()) {
      Vector3f chunkPos = voxelInCrossHair.getCurrentChunkPosition();
      Vector3f voxelPos = voxelInCrossHair.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);
      if (chunk != null) {
        chunk.removeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z);
      }
    }
  }
}
