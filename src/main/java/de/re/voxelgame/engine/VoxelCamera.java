package de.re.voxelgame.engine;

import de.re.voxelgame.core.Camera;
import de.re.voxelgame.engine.world.WorldPosition;

public class VoxelCamera extends Camera {
  private final CrossHairTarget crossHairTarget;

  public VoxelCamera(WorldPosition worldPosition, CrossHairTarget crossHairTarget) {
    super(worldPosition.getVector());
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
    this.pos = worldPosition.getVector();
  }

  public CrossHairTarget getCrossHairTarget() {
    return crossHairTarget;
  }
}
