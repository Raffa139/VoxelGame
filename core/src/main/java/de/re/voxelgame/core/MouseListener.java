package de.re.voxelgame.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public final class MouseListener {
  private static final boolean[] BUTTONS = new boolean[12];

  private static double lastPosX;
  private static double lastPosY;

  private static double scrollOffsetX;
  private static double scrollOffsetY;

  private static boolean mouseMoved = false;

  private MouseListener() {
  }

  public static double getLastPosX() {
    return lastPosX;
  }

  public static double getLastPosY() {
    return lastPosY;
  }

  public static double getScrollOffsetX() {
    return scrollOffsetX;
  }

  public static double getScrollOffsetY() {
    return scrollOffsetY;
  }

  public static boolean buttonPressed(int button) {
    return BUTTONS[button];
  }

  public static boolean hasMouseMoved() {
    return mouseMoved;
  }

  static void cursorPosCallback(long window, double xPos, double yPos) {
    lastPosX = xPos;
    lastPosY = yPos;
    mouseMoved = true;
  }

  static void mouseButtonCallback(long window, int button, int action, int mods) {
    if (action == GLFW_PRESS) {
      BUTTONS[button] = true;
    } else if (action == GLFW_RELEASE) {
      BUTTONS[button] = false;
    }
  }

  static void scrollCallback(long window, double xOffset, double yOffset) {
    scrollOffsetX = xOffset;
    scrollOffsetY = yOffset;
  }
}
