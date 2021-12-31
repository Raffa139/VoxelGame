package de.re.voxelgame.engine;

import de.re.voxelgame.core.Camera;
import de.re.voxelgame.engine.world.WorldPosition;

public class DebugCamera extends Camera {
  public DebugCamera(WorldPosition worldPosition) {
    super(worldPosition.getVector());
  }

  public WorldPosition getWorldPosition() {
    return new WorldPosition(pos);
  }

  public void setWorldPosition(WorldPosition worldPosition) {
    this.pos = worldPosition.getVector();
  }
}
