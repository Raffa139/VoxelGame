package de.re.voxelgame.core.ecs.system;

import de.re.voxelgame.core.GLApplication;
import de.re.voxelgame.core.ecs.component.LocationComponent;
import de.re.voxelgame.core.ecs.entity.BasicEntity;
import de.re.voxelgame.core.ecs.entity.StaticEntity;
import org.joml.Vector3f;

import java.util.Set;

public class TestSystem extends ApplicationSystem {
  private HelloSystem helloSystem;

  private boolean executed = false;

  public TestSystem(GLApplication application) {
    super(application);
    helloSystem = application.getEcs().getSystem(HelloSystem.class);
  }

  @Override
  public void invoke() {
    if (!executed) {
      System.out.println("Invoke hello from Test-System!");
      helloSystem.print();

      Set<BasicEntity> basicEntities = application.getEcs().getEntities(BasicEntity.class);
      for (BasicEntity entity : basicEntities) {
        System.out.println(entity.getComponent(LocationComponent.class).getNormalizedDirection());

        System.out.println(entity.getPosition());
        entity.setPosition(new Vector3f(10.0f));
        System.out.println(entity.getPosition());
      }

      Set<StaticEntity> staticEntities = application.getEcs().getEntities(StaticEntity.class);
      for (StaticEntity entity : staticEntities) {
        System.out.println(entity.getRandomInt());
      }

      executed = true;
    }
  }
}
