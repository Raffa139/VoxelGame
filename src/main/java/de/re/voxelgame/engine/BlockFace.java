package de.re.voxelgame.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockFace {
  public static final BlockFace FRONT = new BlockFace(BlockGeometry.FRONT);
  public static final BlockFace BACK = new BlockFace(BlockGeometry.BACK);
  public static final BlockFace LEFT = new BlockFace(BlockGeometry.LEFT);
  public static final BlockFace RIGHT = new BlockFace(BlockGeometry.RIGHT);
  public static final BlockFace TOP = new BlockFace(BlockGeometry.TOP);
  public static final BlockFace BOTTOM = new BlockFace(BlockGeometry.BOTTOM);

  private final List<Vertex> vertices;

  private BlockFace(Vertex[] vertices) {
    this(Arrays.asList(vertices));
  }

  private BlockFace(List<Vertex> vertices) {
    this.vertices = vertices;
  }

  public BlockFace translate(float x, float y, float z) {
    List<Vertex> translated = new ArrayList<>();

    for (Vertex v : vertices) {
      Vertex vertex =
          new Vertex(v.getPositions().x + x, v.getPositions().y + y, v.getPositions().z + z, v.getTextures());
      translated.add(vertex);
    }

    return new BlockFace(translated);
  }

  public List<Vertex> getVertices() {
    return vertices;
  }
}
