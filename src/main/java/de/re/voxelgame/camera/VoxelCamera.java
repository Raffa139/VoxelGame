package de.re.voxelgame.camera;

import de.re.engine.camera.SimpleCamera;
import de.re.voxelgame.world.WorldPosition;

public class VoxelCamera extends SimpleCamera {
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
