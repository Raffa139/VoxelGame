package de.re.voxelgame.core.test;

import de.re.voxelgame.core.GLApplication;
import de.re.voxelgame.core.ecs.EntityComponentSystem;
import de.re.voxelgame.core.ecs.system.ApplicationSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

public class TestRenderingSystem extends ApplicationSystem {
  private final EntityComponentSystem ecs;

  private final ViewableRenderer renderer;

  public TestRenderingSystem(GLApplication application) {
    super(application);
    ecs = application.getEcs();
    try {
      renderer = new ViewableRenderer();
    } catch (IOException | URISyntaxException e) {
      throw new IllegalStateException("Something went wrong initializing ViewableRenderer!");
    }
  }

  @Override
  public void invoke() {
    renderer.prepare();

    Set<TestEntity> entities = ecs.getEntities(TestEntity.class);
    for (TestEntity entity : entities) {
      MeshComponent mesh = entity.getComponent(MeshComponent.class);
      if (mesh.isViewable()) {
        renderer.render(mesh.getViewable());
      }
    }
  }
}
