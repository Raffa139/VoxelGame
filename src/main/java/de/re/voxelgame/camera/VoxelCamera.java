package de.re.voxelgame.camera;

import de.re.ecs.starter.camera.StarterCamera;
import de.re.voxelgame.world.WorldPosition;

public class VoxelCamera extends StarterCamera {
  public VoxelCamera(WorldPosition worldPosition, float fov) {
    super(worldPosition.getVector(), fov);
  }

  public WorldPosition getWorldPosition() {
    return new WorldPosition(pos);
  }

  public void setWorldPosition(WorldPosition worldPosition) {
    pos = worldPosition.getVector();
  }
}
