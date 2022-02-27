package de.re.voxelgame.engine.skybox;

import de.re.voxelgame.core.Camera;
import de.re.voxelgame.core.objects.GLVertexArrayManager;
import de.re.voxelgame.core.objects.sampler.SamplerCube;
import de.re.voxelgame.core.objects.sampler.GLSamplerManager;
import de.re.voxelgame.core.objects.shader.Shader;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Skybox {
  // Skybox geometry
  private static final float[] SKYBOX_VERTICES = {
      -1.0f,  1.0f, -1.0f,
      -1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      1.0f,  1.0f, -1.0f,
      -1.0f,  1.0f, -1.0f,

      -1.0f, -1.0f,  1.0f,
      -1.0f, -1.0f, -1.0f,
      -1.0f,  1.0f, -1.0f,
      -1.0f,  1.0f, -1.0f,
      -1.0f,  1.0f,  1.0f,
      -1.0f, -1.0f,  1.0f,

      1.0f, -1.0f, -1.0f,
      1.0f, -1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,

      -1.0f, -1.0f,  1.0f,
      -1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f, -1.0f,  1.0f,
      -1.0f, -1.0f,  1.0f,

      -1.0f,  1.0f, -1.0f,
      1.0f,  1.0f, -1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      -1.0f,  1.0f,  1.0f,
      -1.0f,  1.0f, -1.0f,

      -1.0f, -1.0f, -1.0f,
      -1.0f, -1.0f,  1.0f,
      1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      -1.0f, -1.0f,  1.0f,
      1.0f, -1.0f,  1.0f
  };

  private final int vaoId;

  private final SamplerCube texture;

  public Skybox(String right, String left, String top, String bottom, String back, String front) throws IOException, URISyntaxException {
    vaoId = GLVertexArrayManager.get()
        .allocateVao()
        .bufferData(SKYBOX_VERTICES, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0L)
        .doFinal();

    texture = GLSamplerManager.get().samplerCube(right, left, top, bottom, back, front);
  }

  public void render(Shader shader, Camera camera) {
    glDepthFunc(GL_LEQUAL);

    Matrix4f skyboxView = camera.getViewMatrix().get3x3(new Matrix3f()).get(new Matrix4f());
    shader.use();
    shader.setMatrix4("iView", skyboxView);

    glBindVertexArray(vaoId);
    texture.bind(0);
    glDrawArrays(GL_TRIANGLES, 0, SKYBOX_VERTICES.length);
    glBindVertexArray(0);
    glDepthFunc(GL_LESS);
  }
}
