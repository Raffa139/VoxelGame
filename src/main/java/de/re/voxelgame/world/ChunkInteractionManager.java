package de.re.voxelgame.world;

import de.re.engine.MouseListener;
import de.re.voxelgame.VoxelCamera;
import de.re.voxelgame.intersection.AABB;
import de.re.voxelgame.intersection.Ray;
import de.re.voxelgame.intersection.RayCaster;
import de.re.voxelgame.voxel.VoxelType;
import org.joml.Matrix4f;
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

  public void placeVoxel(VoxelType type) {
    WorldPosition placeableVoxel = camera.getCrossHairTarget().getPlaceableVoxel();

    if (camera.getCrossHairTarget().isTargetInRange()) {
      Vector3f chunkPos = placeableVoxel.getCurrentChunkPosition();
      Vector3f voxelPos = placeableVoxel.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);
      if (chunk != null) {
        chunk.placeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z, type);
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

  public WorldPosition calculateMouseCursorIntersection(Matrix4f projection, float resolutionX, float resolutionY) {
    Ray ray = RayCaster.fromMousePosition(MouseListener.getLastPosX(), MouseListener.getLastPosY(), camera, projection, resolutionX, resolutionY);

    for (Chunk chunk : chunkManager.getChunks()) {
      AABB chunkBounding = chunk.getBoundingBox();
      boolean intersects = ray.intersectsAABB(chunkBounding);

      if (intersects) {
        return chunk.getRelativePosition();
      }
    }

    return null;
  }
}
