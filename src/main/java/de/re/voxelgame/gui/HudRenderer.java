package de.re.voxelgame.gui;

import de.re.engine.GLContext;
import de.re.engine.objects.GLVertexArrayManager;
import de.re.engine.objects.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class HudRenderer {
  private final GLContext context;

  private final Shader shader;

  private static float[] crossHairVertices;
  private static int crossHairVao;

  static {
    crossHairVertices = new float[]{
        -0.5f, 0.05f, 0.0f,
        -0.5f, -0.05f, 0.0f,
        0.5f, -0.05f, 0.0f,
        0.5f, -0.05f, 0.0f,
        0.5f, 0.05f, 0.0f,
        -0.5f, 0.05f, 0.0f
    };

    crossHairVao = GLVertexArrayManager.get()
        .allocateVao()
        .bufferData(crossHairVertices, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L)
        .doFinal();
  }

  public HudRenderer(GLContext context, Shader shader) {
    this.context = context;
    this.shader = shader;
  }

  public void render() {
    shader.use();
    shader.setMatrix4("iModel", new Matrix4f().scale(0.05f));

    glBindVertexArray(crossHairVao);
    glDrawArrays(GL_TRIANGLES, 0, crossHairVertices.length);

    shader.setMatrix4("iModel", new Matrix4f()
        .rotate((float) Math.toRadians(90.0), new Vector3f(0.0f, 0.0f, 1.0f))
        .scale(0.05f));

    glDrawArrays(GL_TRIANGLES, 0, crossHairVertices.length);
    glBindVertexArray(0);
  }
}