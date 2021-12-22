package de.re.voxelgame.engine.noise;

public class OpenSimplexNoise {
  private final OpenSimplex2F noise;

  public OpenSimplexNoise(long seed) {
    this.noise = new OpenSimplex2F(seed);
  }

  public int voxelNoise2d(float x, float z) {
    double freq = 1.0/256.0;
    return (int) Math.round(map(noise.noise2(x*freq, z*freq), -1.0, 1.0, 0.0, 128));
  }

  private double map(double x, double xMin, double xMax, double min, double max) {
    return (x-xMin)/(xMax-xMin) * (max-min) + min;
  }
}
