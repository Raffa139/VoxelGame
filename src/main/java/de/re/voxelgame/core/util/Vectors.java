package de.re.voxelgame.core.util;

import org.joml.Vector3f;

public final class Vectors {
  private Vectors() {
  }

  public static Vector3f mul(Vector3f v, float m) {
    Vector3f res = new Vector3f();
    v.mul(m, res);
    return res;
  }

  public static Vector3f add(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.add(m, res);
    return res;
  }

  public static Vector3f cross(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.cross(m, res);
    return res;
  }
}
