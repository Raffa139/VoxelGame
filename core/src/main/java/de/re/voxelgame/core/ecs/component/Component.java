package de.re.voxelgame.core.ecs.component;

import de.re.voxelgame.core.ecs.entity.Entity;

public abstract class Component {
  protected Entity entity;

  public Component(Entity entity) {
    this.entity = entity;
  }
}
