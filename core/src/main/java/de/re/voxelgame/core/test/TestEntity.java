package de.re.voxelgame.core.test;

import de.re.voxelgame.core.ecs.entity.Entity;

import java.lang.reflect.InvocationTargetException;

public class TestEntity extends Entity {
  public TestEntity(float[] vertices) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    addComponent(MeshComponent.class).setVertexPositions(vertices);
  }
}
