package de.re.voxelgame.core;

public class TestSystem implements CSystem {
  @Override
  public void invoke(Component component, Entity entity) {
    System.out.println("Hello System Test!");
    component.execute();
  }
}
