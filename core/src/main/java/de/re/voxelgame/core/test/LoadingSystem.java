package de.re.voxelgame.core.test;

import de.re.voxelgame.core.GLApplication;
import de.re.voxelgame.core.ecs.EntityComponentSystem;
import de.re.voxelgame.core.ecs.system.ApplicationSystem;
import de.re.voxelgame.core.objects.GLVertexArrayManager;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class LoadingSystem extends ApplicationSystem {
  // TODO: Queue meshes & textures for loading
  private final EntityComponentSystem ecs;

  private final Queue<MeshComponent> meshQueue = new LinkedList<>();

  public LoadingSystem(GLApplication application) {
    super(application);
    ecs = application.getEcs();
  }

  @Override
  public void invoke() {
    Set<TestEntity> entities = ecs.getEntities(TestEntity.class);
    for (TestEntity entity : entities) {
      MeshComponent mesh = entity.getComponent(MeshComponent.class);
      if (!mesh.isViewable()) {
        int vaoId = GLVertexArrayManager.get()
            .allocateVao()
            .bufferData(mesh.getVertexPositions(), GL_STATIC_DRAW)
            .enableAttribArray(0)
            .attribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L)
            .doFinal();

        Viewable viewable = new Viewable(vaoId, mesh.getVertexPositions().length);
        mesh.setViewable(viewable);
      }
    }
  }
}
