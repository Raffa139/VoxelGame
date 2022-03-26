package de.re.voxelgame;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
  public static void main(String[] args) throws IOException, URISyntaxException {
    new VoxelApplication(1080, 720, "Voxel Application").run();
  }
}
