package de.re.voxelgame.core;

public abstract class ApplicationSystem {
  protected GLApplication application;

  public ApplicationSystem(GLApplication application) {
    this.application = application;
  }

  public abstract void invoke();
}
