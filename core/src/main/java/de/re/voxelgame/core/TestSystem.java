package de.re.voxelgame.core;

public class TestSystem extends ApplicationSystem {
  private HelloSystem helloSystem;

  private boolean executed = false;

  public TestSystem(GLApplication application) {
    super(application);
    helloSystem = application.getSystem(HelloSystem.class);
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
