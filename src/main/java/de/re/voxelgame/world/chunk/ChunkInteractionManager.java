package de.re.voxelgame.world.chunk;

import de.re.engine.MouseListener;
import de.re.voxelgame.camera.CrossHairTarget;
import de.re.voxelgame.camera.VoxelCamera;
import de.re.voxelgame.intersection.AABB;
import de.re.voxelgame.intersection.Ray;
import de.re.voxelgame.intersection.RayCaster;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.voxel.VoxelType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ChunkInteractionManager {
  private final ChunkManager chunkManager;

  private final CrossHairTarget crossHairTarget;

  private WorldPosition lastVoxelInCrossHair;

  public ChunkInteractionManager(ChunkManager chunkManager) {
    this.chunkManager = chunkManager;
    crossHairTarget = new CrossHairTarget(chunkManager);
    lastVoxelInCrossHair = new WorldPosition(0.0f);
  }

  public void update(VoxelCamera camera) {
    crossHairTarget.update(camera.getPos(), camera.getFront());
  }

  public void highlightVoxel() {
    WorldPosition voxelInCrossHair = crossHairTarget.getTargetedVoxel();

    if (!crossHairTarget.isTargetInRange() ||
        !voxelInCrossHair.getCurrentChunkPosition().equals(lastVoxelInCrossHair.getCurrentChunkPosition())) {
      chunkManager.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), null);
      chunkManager.reloadChunk(lastVoxelInCrossHair.getCurrentChunkPosition(), null);
      lastVoxelInCrossHair = voxelInCrossHair;
    } else {
      chunkManager.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), voxelInCrossHair.getAbsolutePositionInCurrentChunk());
    }
  }

  public void placeVoxel(VoxelType type) {
    WorldPosition placeableVoxel = crossHairTarget.getPlaceableVoxel();

    if (crossHairTarget.isTargetInRange()) {
      Vector3f chunkPos = placeableVoxel.getCurrentChunkPosition();
      Vector3f voxelPos = placeableVoxel.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);
      if (chunk != null) {
        chunk.placeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z, type);
      }
    }
  }

  public void removeVoxel() {
    WorldPosition voxelInCrossHair = crossHairTarget.getTargetedVoxel();

    if (crossHairTarget.isTargetInRange()) {
      Vector3f chunkPos = voxelInCrossHair.getCurrentChunkPosition();
      Vector3f voxelPos = voxelInCrossHair.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkManager.getChunkPositionMap().get(chunkPos);
      if (chunk != null) {
        chunk.removeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z);
      }
    }
  }

  public WorldPosition calculateMouseCursorIntersection(VoxelCamera camera, Matrix4f projection, float resolutionX, float resolutionY) {
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
