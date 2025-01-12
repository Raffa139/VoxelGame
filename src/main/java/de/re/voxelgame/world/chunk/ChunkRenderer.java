package de.re.voxelgame.world.chunk;

import de.ren.ecs.engine.GLApplication;
import de.ren.ecs.engine.objects.Framebuffer;
import de.ren.ecs.engine.objects.sampler.Sampler2D;
import de.ren.ecs.engine.objects.sampler.Sampler2DArray;
import de.ren.ecs.engine.objects.shader.Shader;
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
    renderTransparentVoxels(transparentVoxelBuffer, textureArray, normalMap);

    // Second rendering pass for normal voxels
    renderSolidVoxels(normalVoxelBuffer, textureArray);
  }

  private void renderSolidVoxels(Framebuffer fbo, Sampler2DArray textureArray) {
    fbo.bind();

    textureArray.bind(0);

    shader.use();
    shader.setVec3("iHighlightColor", new Vector3f(0.25f, 0.25f, 0.25f));

    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);

    for (Chunk chunk : chunkSystem.getChunks()) {
      if (chunk.hasSolidMesh()) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getWorldPosition().getVector());

        shader.use();
        shader.setMatrix4("iModel", model);

        glBindVertexArray(chunk.getSolidMesh().getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, chunk.getSolidMesh().getVertexCount());
        glBindVertexArray(0);
      }
    }
  }

  private void renderTransparentVoxels(Framebuffer fbo, Sampler2DArray textureArray, Sampler2D normalMap) {
    fbo.bind();

    textureArray.bind(0);
    normalMap.bind(1);

    waterShader.use();
    waterShader.setInt("textureArray", 0);
    waterShader.setInt("normalMap", 1);
    waterShader.setVec3("iLightDirection", new Vector3f(2.0f, 0.6f, -1.0f));

    glClearColor(1.0f, 0.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);

    for (Chunk chunk : chunkSystem.getChunks()) {
      if (chunk.hasTransparentMesh()) {
        Matrix4f model = new Matrix4f();
        model.translate(chunk.getWorldPosition().getVector());

        waterShader.use();
        waterShader.setMatrix4("iModel", model);

        glBindVertexArray(chunk.getTransparentMesh().getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, chunk.getTransparentMesh().getVertexCount());
        glBindVertexArray(0);
      }
    }
  }
}
