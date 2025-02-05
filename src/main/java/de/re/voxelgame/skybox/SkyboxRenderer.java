package de.re.voxelgame.skybox;

import de.ren.ecs.engine.GLApplication;
import de.ren.ecs.engine.objects.shader.Shader;
import de.re.voxelgame.VoxelApplication;
import de.re.voxelgame.camera.VoxelCamera;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SkyboxRenderer {
  private final Shader shader;

  public SkyboxRenderer(GLApplication application) throws IOException {
    shader = ((VoxelApplication) application).shaderFromResources("shader/skybox.vert", "shader/skybox.frag");
  }

  public void render(Skybox skybox, VoxelCamera camera) {
    glDepthFunc(GL_LEQUAL);
    glDisable(GL_CULL_FACE);

    Matrix4f skyboxView = camera.getViewMatrix().get3x3(new Matrix3f()).get(new Matrix4f());
    shader.use();
    shader.setMatrix4("iView", skyboxView);

    glBindVertexArray(skybox.getVaoId());
    skybox.getTexture().bind(0);
    glDrawArrays(GL_TRIANGLES, 0, skybox.getVertexCount());
    glBindVertexArray(0);
    glEnable(GL_CULL_FACE);
    glDepthFunc(GL_LESS);
  }
}
