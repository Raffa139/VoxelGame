package de.re.voxelgame.core.ecs.system;

import de.re.voxelgame.core.GLApplication;

public abstract class ApplicationSystem {
  protected GLApplication application;

  public ApplicationSystem(GLApplication application) {
    this.application = application;
  }

  public abstract void invoke();
}
