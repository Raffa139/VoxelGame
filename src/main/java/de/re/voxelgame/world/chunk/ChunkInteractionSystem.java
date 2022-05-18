package de.re.voxelgame.world.chunk;

import de.re.engine.GLApplication;
import de.re.engine.MouseListener;
import de.re.engine.ecs.system.ApplicationSystem;
import de.re.voxelgame.camera.CrossHairTarget;
import de.re.voxelgame.camera.VoxelCamera;
import de.re.voxelgame.camera.VoxelCameraSystem;
import de.re.voxelgame.intersection.AABB;
import de.re.voxelgame.intersection.Ray;
import de.re.voxelgame.intersection.RayCaster;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.voxel.VoxelType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class ChunkInteractionSystem extends ApplicationSystem {
  private final ChunkSystem chunkSystem;

  private final VoxelCamera camera;

  private final CrossHairTarget crossHairTarget;

  private WorldPosition lastVoxelInCrossHair;

  private boolean hadTargetInRange = false;

  private float lastPressed = 0.0f;

  public ChunkInteractionSystem(GLApplication application) {
    super(application);
    chunkSystem = application.getEcs().getSystem(ChunkSystem.class);
    camera = application.getEcs().getSystem(VoxelCameraSystem.class).getCamera();
    crossHairTarget = new CrossHairTarget(chunkSystem);
    lastVoxelInCrossHair = new WorldPosition(0.0f);
  }

  @Override
  public void invoke() {
    // Voxel placement & highlighting
    if (!application.getContext().isMouseCursorToggled()) {
      crossHairTarget.update(camera.getPos(), camera.getFront());

      float currentTime = application.getCurrentTime();
      highlightVoxel();

      if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_2) && currentTime > lastPressed + 0.25f) {
        lastPressed = currentTime;
        placeVoxel(VoxelType.WOOD);
      }

      if (MouseListener.buttonPressed(GLFW_MOUSE_BUTTON_1) && currentTime > lastPressed + 0.25f) {
        lastPressed = currentTime;
        removeVoxel();
      }
    }
  }

  private void highlightVoxel() {
    WorldPosition voxelInCrossHair = crossHairTarget.getTargetedVoxel();

    if ((!crossHairTarget.isTargetInRange() && hadTargetInRange) ||
        !voxelInCrossHair.getCurrentChunkPosition().equals(lastVoxelInCrossHair.getCurrentChunkPosition())) {
      chunkSystem.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), null);
      chunkSystem.reloadChunk(lastVoxelInCrossHair.getCurrentChunkPosition(), null);
      lastVoxelInCrossHair = voxelInCrossHair;
      hadTargetInRange = false;
    } else if (crossHairTarget.isTargetInRange()) {
      chunkSystem.reloadChunk(voxelInCrossHair.getCurrentChunkPosition(), voxelInCrossHair.getAbsolutePositionInCurrentChunk());
      hadTargetInRange = true;
    }
  }

  private void placeVoxel(VoxelType type) {
    WorldPosition placeableVoxel = crossHairTarget.getPlaceableVoxel();

    if (crossHairTarget.isTargetInRange()) {
      Vector3f chunkPos = placeableVoxel.getCurrentChunkPosition();
      Vector3f voxelPos = placeableVoxel.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkSystem.getChunkMap().get(chunkPos);
      if (chunk != null) {
        chunk.placeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z, type);
      }
    }
  }

  private void removeVoxel() {
    WorldPosition voxelInCrossHair = crossHairTarget.getTargetedVoxel();

    if (crossHairTarget.isTargetInRange()) {
      Vector3f chunkPos = voxelInCrossHair.getCurrentChunkPosition();
      Vector3f voxelPos = voxelInCrossHair.getAbsolutePositionInCurrentChunk();
      Chunk chunk = chunkSystem.getChunkMap().get(chunkPos);
      if (chunk != null) {
        chunk.removeVoxel((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z);
      }
    }
  }

  private WorldPosition calculateMouseCursorIntersection(VoxelCamera camera, Matrix4f projection, float resolutionX, float resolutionY) {
    Ray ray = RayCaster.fromMousePosition(MouseListener.getLastPosX(), MouseListener.getLastPosY(), camera, projection, resolutionX, resolutionY);

    for (Chunk chunk : chunkSystem.getChunks()) {
      AABB chunkBounding = chunk.getBoundingBox();
      boolean intersects = ray.intersectsAABB(chunkBounding);

      if (intersects) {
        return chunk.getRelativePosition();
      }
    }

    return null;
  }
}
