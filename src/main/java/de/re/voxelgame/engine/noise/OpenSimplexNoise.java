package de.re.voxelgame.engine.noise;

public class OpenSimplexNoise {
  private final OpenSimplex2F noise;

  public OpenSimplexNoise(long seed) {
    this.noise = new OpenSimplex2F(seed);
  }

  public int voxelNoise2d(float x, float z) {
    double lacunarity = 2.0;
    double persistance = 0.5;

    double v = 0.0;
    for (int octave = 0; octave < 4; octave++) {
      double frequency = Math.pow(lacunarity, octave) / 512.0;
      double amplitude = Math.pow(persistance, octave);

      v += amplitude * noise.noise2(x * frequency, z * frequency);
    }

    if (v < -1.0) v = -1.0;
    int height = (int) map(v, -1.0, 1.0, 0.0, 128.0);

    /*if (height < 0.0 || height > 128.0) {
      System.out.println(height);
    }*/

    return height;
  }

  private double map(double x, double xMin, double xMax, double min, double max) {
    return (x-xMin)/(xMax-xMin) * (max-min) + min;
  }

  private double clamp(double x, double min, double max) {
    if (x < min) return min;
    if (x > max) return max;

    return x;
  }
}
