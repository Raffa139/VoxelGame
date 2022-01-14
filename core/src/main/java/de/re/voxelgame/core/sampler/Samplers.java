package de.re.voxelgame.core.sampler;

import de.re.voxelgame.core.util.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public final class Samplers {
  private static final List<Integer> SAMPLER_IDS = new ArrayList<>();

  private Samplers() {
  }

  public static Sampler2D sampler2D(String file) throws IOException, URISyntaxException {
    FileInputStream fin = ResourceLoader.locateResource(file, Samplers.class).toFileInputStream();
    Sampler2D sampler = new Sampler2D(fin);
    SAMPLER_IDS.add(sampler.getId());

    return sampler;
  }

  public static Sampler2DArray sampler2DArray(int width, int height, String... files) throws IOException, URISyntaxException {
    List<FileInputStream> fins = new ArrayList<>();
    for (String file : files) {
      fins.add(ResourceLoader.locateResource(file, Samplers.class).toFileInputStream());
    }
    Sampler2DArray sampler = new Sampler2DArray(width, height, fins.toArray(new FileInputStream[]{}));
    SAMPLER_IDS.add(sampler.getId());

    return sampler;
  }

  public static SamplerCube samplerCube(String right, String left, String top, String bottom, String back, String front) throws IOException, URISyntaxException {
    List<FileInputStream> fins = new ArrayList<>();
    for (String file : Arrays.asList(right, left, top, bottom, back, front)) {
      fins.add(ResourceLoader.locateResource(file, Samplers.class).toFileInputStream());
    }
    SamplerCube sampler = new SamplerCube(fins);
    SAMPLER_IDS.add(sampler.getId());

    return sampler;
  }

  public static void terminate() {
    for (int id : SAMPLER_IDS) {
      glDeleteTextures(id);
    }
  }
}
