package de.re.voxelgame.core.test;

import de.re.voxelgame.core.ecs.component.Component;
import de.re.voxelgame.core.ecs.entity.Entity;

public class MeshComponent extends Component {
  private Viewable viewable;

  private float[] vertexPositions;

  public MeshComponent(Entity entity) {
    super(entity);
  }

  public Viewable getViewable() {
    return viewable;
  }

  public void setViewable(Viewable viewable) {
    this.viewable = viewable;
  }

  public float[] getVertexPositions() {
    return vertexPositions;
  }

  public void setVertexPositions(float[] vertexPositions) {
    this.vertexPositions = vertexPositions;
  }

  public boolean isViewable() {
    return viewable != null;
  }
}
