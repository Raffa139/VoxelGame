package de.re.voxelgame.camera;

import de.ren.ecs.engine.GLApplication;
import de.ren.ecs.engine.ecs.system.ApplicationSystem;

public class VoxelCameraSystem extends ApplicationSystem {
  private VoxelCamera camera;

  public VoxelCameraSystem(GLApplication application) {
    super(application);
  }

  @Override
  public void invoke() {

  }

  public VoxelCamera getCamera() {
    return camera;
  }

  public void setCamera(VoxelCamera camera) {
    this.camera = camera;
  }
}
