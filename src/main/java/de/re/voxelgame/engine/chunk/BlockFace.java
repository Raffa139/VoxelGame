package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.Vertex;

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

  private float lightLevel;

  private BlockFace(Vertex[] vertices) {
    this(Arrays.asList(vertices), 1.0f);
  }

  private BlockFace(List<Vertex> vertices, float lightLevel) {
    this.vertices = vertices;
    this.lightLevel = lightLevel;
  }

  public BlockFace translate(float x, float y, float z) {
    List<Vertex> translated = new ArrayList<>();

    for (Vertex v : vertices) {
      Vertex vertex =
          new Vertex(v.getPosition().x + x, v.getPosition().y + y, v.getPosition().z + z, lightLevel);
      translated.add(vertex);
    }

    return new BlockFace(translated, lightLevel);
  }

  public List<Vertex> getVertices() {
    return vertices;
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }
}
