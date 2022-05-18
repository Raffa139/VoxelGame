package de.re.voxelgame.world.chunk;

import de.re.engine.GLApplication;
import de.re.engine.objects.Framebuffer;
import de.re.engine.objects.sampler.Sampler2D;
import de.re.engine.objects.sampler.Sampler2DArray;
import de.re.engine.objects.shader.Shader;
import de.re.voxelgame.VoxelApplication;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ChunkRenderer {
  private final ChunkSystem chunkSystem;

  private final Shader shader;
  private final Shader waterShader;

  public ChunkRenderer(GLApplication application) throws IOException {
    chunkSystem = application.getEcs().getSystem(ChunkSystem.class);
    shader = ((VoxelApplication) application).shaderFromResources("shader/chunk.vert", "shader/chunk.frag");
    waterShader = ((VoxelApplication) application).shaderFromResources("shader/chunk.vert", "shader/water.frag");
  }

  public void render(Framebuffer normalVoxelBuffer, Framebuffer transparentVoxelBuffer,
                     Sampler2DArray textureArray, Sampler2D normalMap) {
    // First rendering pass for transparent voxels
    renderTransparentVoxels(transparentVoxelBuffer, normalMap);

    // Second rendering pass for normal voxels
    renderNormalVoxels(normalVoxelBuffer, textureArray);
  }

  public void renderNormalVoxels(Framebuffer fbo, Sampler2DArray textureArray) {
    fbo.bind();

    textureArray.bind(0);

    shader.use();
    shader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));

    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);

    for (Chunk chunk : chunkSystem.getChunks()) {
      if (chunk.hasMesh()) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getWorldPosition().getVector());

        shader.use();
        shader.setMatrix4("iModel", model);

        glBindVertexArray(chunk.getMesh().getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, chunk.getMesh().getVertexCount());
        glBindVertexArray(0);
      }
    }
  }

  public void renderTransparentVoxels(Framebuffer fbo, Sampler2D normalMap) {
    fbo.bind();

    normalMap.bind(0);

    waterShader.use();
    waterShader.setVec3("iColor", new Vector3f(0.0f, 0.0f, 0.5f));
    waterShader.setVec3("iLightDirection", new Vector3f(2.0f, 0.6f, -1.0f));

    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);

    for (Chunk chunk : chunkSystem.getChunks()) {
      if (chunk.hasMesh()) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getWorldPosition().getVector());

        waterShader.use();
        waterShader.setMatrix4("iModel", model);

        glBindVertexArray(chunk.getMesh().getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, chunk.getMesh().getVertexCount());
        glBindVertexArray(0);
      }
    }
  }
}
