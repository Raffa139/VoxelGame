package de.re.voxelgame.core.ecs.system;

import de.re.voxelgame.core.GLApplication;

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

      executed = true;
    }
  }
}
