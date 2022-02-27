package de.re.voxelgame.core.ecs.component;

import de.re.voxelgame.core.ecs.entity.Entity;
import org.joml.Vector3f;

public class PositionComponent extends Component {
  private Vector3f position;

  public PositionComponent(Entity entity) {
    super(entity);
    position = new Vector3f(0.0f);
  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }
}
