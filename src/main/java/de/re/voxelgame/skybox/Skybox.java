package de.re.voxelgame.skybox;

import de.re.engine.Camera;
import de.re.engine.geometry.Geometry;
import de.re.engine.geometry.Polygon;
import de.re.engine.objects.GLVertexArrayManager;
import de.re.engine.objects.sampler.GLSamplerManager;
import de.re.engine.objects.sampler.SamplerCube;
import de.re.engine.objects.shader.Shader;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Skybox {
  private static final float[] SKYBOX_VERTICES = Geometry.ofPolygon(Polygon.CUBE).getVerticesFlat();

  private final int vaoId;

  private final SamplerCube texture;

  public Skybox(Path right, Path left, Path top, Path bottom, Path back, Path front) throws IOException {
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
    glDisable(GL_CULL_FACE);

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
