package de.re.voxelgame.core.util;

import org.joml.Vector3f;

public final class Vectors {
  private Vectors() {
  }

  public static Vector3f mul(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.mul(m, res);
    return res;
  }

  public static Vector3f mul(Vector3f v, float m) {
    Vector3f res = new Vector3f();
    v.mul(m, res);
    return res;
  }

  public static Vector3f div(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.div(m, res);
    return res;
  }

  public static Vector3f div(Vector3f v, float m) {
    Vector3f res = new Vector3f();
    v.div(m, res);
    return res;
  }

  public static Vector3f add(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.add(m, res);
    return res;
  }

  public static Vector3f add(Vector3f v, float m) {
    Vector3f res = new Vector3f();
    v.add(m, m, m, res);
    return res;
  }

  public static Vector3f sub(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.sub(m, res);
    return res;
  }

  public static Vector3f sub(Vector3f v, float m) {
    Vector3f res = new Vector3f();
    v.sub(m, m, m, res);
    return res;
  }

  public static Vector3f cross(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.cross(m, res);
    return res;
  }

  public static Vector3f min(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.min(m, res);
    return res;
  }

  public static Vector3f max(Vector3f v, Vector3f m) {
    Vector3f res = new Vector3f();
    v.max(m, res);
    return res;
  }
}
