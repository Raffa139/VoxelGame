package de.re.voxelgame.core;

import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;

public class BasicEntity extends Entity {
  public BasicEntity() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    addComponent(LocationComponent.class);
    addComponent(PositionComponent.class);
  }

  public Vector3f getPosition() {
    return getComponent(PositionComponent.class).getPosition();
  }

  public void setPosition(Vector3f position) {
    getComponent(PositionComponent.class).setPosition(position);
  }
}
