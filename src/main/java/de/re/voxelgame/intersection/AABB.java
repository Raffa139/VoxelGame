package de.re.voxelgame.intersection;

import org.joml.Vector3f;

public class AABB {
  private final Vector3f min;

  private final Vector3f max;

  public AABB(Vector3f min, Vector3f max) {
    this.min = min;
    this.max = max;
  }

  public boolean intersects(AABB other) {
    if (other == null) {
      return false;
    }

    for (int i = 0; i < 3; i++) {
      if (min.get(i) > other.max.get(i)) {
        return false;
      }
      if (max.get(i) < other.min.get(i)) {
        return false;
      }
    }

    return true;
  }

  public Vector3f getMin() {
    return min;
  }

  public Vector3f getMax() {
    return max;
  }
}
