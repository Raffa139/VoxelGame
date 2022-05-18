package de.re.voxelgame.camera;

import de.re.engine.Camera;
import de.re.voxelgame.world.WorldPosition;

public class VoxelCamera extends Camera {
  public VoxelCamera(WorldPosition worldPosition, float fov) {
    super(worldPosition.getVector(), fov);
  }

  @Override
  public void update(float deltaTime, boolean allowTurn) {
    super.update(deltaTime, allowTurn);
  }

  public WorldPosition getWorldPosition() {
    return new WorldPosition(pos);
  }

  public void setWorldPosition(WorldPosition worldPosition) {
    pos = worldPosition.getVector();
  }
}
