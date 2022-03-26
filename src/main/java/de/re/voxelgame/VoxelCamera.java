package de.re.voxelgame;

import de.re.engine.Camera;
import de.re.voxelgame.world.WorldPosition;

public class VoxelCamera extends Camera {
  private final CrossHairTarget crossHairTarget;

  public VoxelCamera(WorldPosition worldPosition, float fov, CrossHairTarget crossHairTarget) {
    super(worldPosition.getVector(), fov);
    this.crossHairTarget = crossHairTarget;
  }

  @Override
  public void update(float deltaTime, boolean allowTurn) {
    super.update(deltaTime, allowTurn);
    crossHairTarget.update(pos, front);
  }

  public WorldPosition getWorldPosition() {
    return new WorldPosition(pos);
  }

  public void setWorldPosition(WorldPosition worldPosition) {
    pos = worldPosition.getVector();
  }

  public CrossHairTarget getCrossHairTarget() {
    return crossHairTarget;
  }
}
