package de.re.voxelgame.gui;

import de.ren.ecs.engine.GLApplication;
import de.ren.ecs.engine.objects.GLVertexArrayManager;
import de.ren.ecs.engine.objects.shader.Shader;
import de.re.voxelgame.VoxelApplication;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class HudRenderer {
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
  }

  public HudRenderer(GLApplication application) throws IOException {
    shader = ((VoxelApplication) application).shaderFromResources("shader/basicHud.vert", "shader/basicHud.frag");
    crossHairVao = GLVertexArrayManager.get()
        .allocateVao()
        .bufferData(crossHairVertices, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L)
        .doFinal();
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
