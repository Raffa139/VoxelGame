package de.re.voxelgame.core.sampler;

import de.re.voxelgame.core.util.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class GLSamplerManager {
  private static GLSamplerManager instant;

  private final List<Integer> samplerIds;

  private GLSamplerManager() {
    samplerIds = new ArrayList<>();
  }

  public static GLSamplerManager get() {
    if (instant == null) {
      instant = new GLSamplerManager();
    }

    return instant;
  }

  public Sampler2D sampler2D(String file) throws IOException, URISyntaxException {
    FileInputStream fin = ResourceLoader.locateResource(file, GLSamplerManager.class).toFileInputStream();
    Sampler2D sampler = new Sampler2D(fin);
    samplerIds.add(sampler.getId());

    return sampler;
  }

  public Sampler2DArray sampler2DArray(int width, int height, String... files) throws IOException, URISyntaxException {
    List<FileInputStream> fins = new ArrayList<>();
    for (String file : files) {
      fins.add(ResourceLoader.locateResource(file, GLSamplerManager.class).toFileInputStream());
    }
    Sampler2DArray sampler = new Sampler2DArray(width, height, fins.toArray(new FileInputStream[]{}));
    samplerIds.add(sampler.getId());

    return sampler;
  }

  public SamplerCube samplerCube(String right, String left, String top, String bottom, String back, String front) throws IOException, URISyntaxException {
    List<FileInputStream> fins = new ArrayList<>();
    for (String file : Arrays.asList(right, left, top, bottom, back, front)) {
      fins.add(ResourceLoader.locateResource(file, GLSamplerManager.class).toFileInputStream());
    }
    SamplerCube sampler = new SamplerCube(fins);
    samplerIds.add(sampler.getId());

    return sampler;
  }

  public void terminate() {
    for (int id : samplerIds) {
      glDeleteTextures(id);
    }
  }
}
