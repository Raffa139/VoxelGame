package de.re.voxelgame.core.test;

import de.re.voxelgame.core.GLApplication;

import java.lang.reflect.InvocationTargetException;

public class ApplicationTest extends GLApplication {
  public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    new ApplicationTest(1080, 720, "GL Test").run();
  }

  public ApplicationTest(int width, int height, String title) {
    super(width, height, title);
  }

  public void run() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    float [] triangleVertices = {
        -0.25f, -0.25f, 0.0f,
         0.0f,  0.25f,  0.0f,
         0.25f, -0.25f, 0.0f
    };

    float [] squareVertices = {
        -1.0f, -1.0f, 0.0f,
        -1.0f, -0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,

        -0.5f, -0.5f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        -0.5f, -1.0f, 0.0f
    };

    ecs.addSystem(TestRenderingSystem.class);
    ecs.addSystem(LoadingSystem.class);
    ecs.addEntity(new TestEntity(triangleVertices));
    ecs.addEntity(new TestEntity(squareVertices));

    context.toggleMouseCursor();
    while (glApplicationIsRunning()) {
      beginFrame();

      endFrame();
    }

    quit();
  }
}
