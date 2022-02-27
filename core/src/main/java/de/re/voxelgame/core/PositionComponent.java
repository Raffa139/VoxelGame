package de.re.voxelgame.core;

import org.joml.Vector3f;

public class PositionComponent implements Component {
  private Vector3f position;

  public PositionComponent() {
    position = new Vector3f(0.0f);
  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }
}
