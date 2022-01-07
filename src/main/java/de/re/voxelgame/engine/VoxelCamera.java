package de.re.voxelgame.engine;

import de.re.voxelgame.core.Camera;
import de.re.voxelgame.engine.world.WorldPosition;

public class VoxelCamera extends Camera {
  private final CrossHair crossHair;

  public VoxelCamera(WorldPosition worldPosition, CrossHair crossHair) {
    super(worldPosition.getVector());
    this.crossHair = crossHair;
  }

  @Override
  public void update(float deltaTime, boolean allowTurn) {
    super.update(deltaTime, allowTurn);
    crossHair.update(pos, front);
  }

  public WorldPosition getWorldPosition() {
    return new WorldPosition(pos);
  }

  public void setWorldPosition(WorldPosition worldPosition) {
    this.pos = worldPosition.getVector();
  }

  public CrossHair getCrossHair() {
    return crossHair;
  }
}
