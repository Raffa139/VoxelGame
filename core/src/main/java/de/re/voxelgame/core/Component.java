package de.re.voxelgame.core;

public abstract class Component {
  protected Entity entity;

  public Component(Entity entity) {
    this.entity = entity;
  }
}
