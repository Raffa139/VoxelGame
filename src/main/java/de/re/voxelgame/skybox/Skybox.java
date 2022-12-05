package de.re.voxelgame.skybox;

import de.ren.ecs.engine.geometry.Geometry;
import de.ren.ecs.engine.geometry.Polygon;
import de.ren.ecs.engine.objects.GLVertexArrayManager;
import de.ren.ecs.engine.objects.sampler.GLSamplerManager;
import de.ren.ecs.engine.objects.sampler.SamplerCube;

import java.io.IOException;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

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

  public int getVaoId() {
    return vaoId;
  }

  public SamplerCube getTexture() {
    return texture;
  }

  public int getVertexCount() {
    return SKYBOX_VERTICES.length;
  }
}
