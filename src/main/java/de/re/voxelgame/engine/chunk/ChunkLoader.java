package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.Block;
import de.re.voxelgame.engine.BlockFace;
import de.re.voxelgame.engine.Vertex;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static de.re.voxelgame.engine.chunk.Chunk.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class ChunkLoader {
  private ChunkLoader() {
  }

  public static Chunk loadChunk(Vector2f position) {
    List<Vertex> translatedVertices = new ArrayList<>();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        // Max. size in x/z direction
        int SX = CHUNK_SIZE-1;
        int SZ = CHUNK_SIZE-1;

        if ((x == 0 && z == 0) || (x == 0 && z == SZ) || (x == SX && z == 0) || (x == SX && z == SZ)) { // One of 4 corner cases
          if (x == 0 && z == 0) { // NW
            Block block = new Block(BlockFace.BACK, BlockFace.LEFT, BlockFace.TOP, BlockFace.BOTTOM)
                .translate(x, z);

            translatedVertices.addAll(block.getVertices());
          } else if (x == 0 && z == SZ) { // SW
            Block block = new Block(BlockFace.FRONT, BlockFace.LEFT, BlockFace.TOP, BlockFace.BOTTOM)
                .translate(x, z);

            translatedVertices.addAll(block.getVertices());
          } else if (x == SX && z == 0) { // NE
            Block block = new Block(BlockFace.BACK, BlockFace.RIGHT, BlockFace.TOP, BlockFace.BOTTOM)
                .translate(x, z);

            translatedVertices.addAll(block.getVertices());
          } else if (x == SX && z == SZ) { // SE
            Block block = new Block(BlockFace.FRONT, BlockFace.RIGHT, BlockFace.TOP, BlockFace.BOTTOM)
                .translate(x, z);

            translatedVertices.addAll(block.getVertices());
          }
        } else if (((x == 0 || x == SX) && (z > 0 && z < SZ)) || ((z == 0 || z == SZ) && (x > 0 && x < SX))) { // One of 2x2 edge cases
          if (z > 0 && z < SZ) { // W or E
            if (x == 0) { // W
              Block block = new Block(BlockFace.LEFT, BlockFace.TOP, BlockFace.BOTTOM)
                  .translate(x, z);

              translatedVertices.addAll(block.getVertices());
            } else if (x == SX) { // E
              Block block = new Block(BlockFace.RIGHT, BlockFace.TOP, BlockFace.BOTTOM)
                  .translate(x, z);

              translatedVertices.addAll(block.getVertices());
            }
          } else if (x > 0 && x < SX) { // N or S
            if (z == 0) { // N
              Block block = new Block(BlockFace.BACK, BlockFace.TOP, BlockFace.BOTTOM)
                  .translate(x, z);

              translatedVertices.addAll(block.getVertices());
            } else if (z == SZ) { // S
              Block block = new Block(BlockFace.FRONT, BlockFace.TOP, BlockFace.BOTTOM)
                  .translate(x, z);

              translatedVertices.addAll(block.getVertices());
            }
          }
        } else if ((x > 0 && x < SX) && (z > 0 && z < SZ)) { // Remaining middle cases
          Block block = new Block(BlockFace.TOP, BlockFace.BOTTOM)
              .translate(x, z);

          translatedVertices.addAll(block.getVertices());
        }
      }
    }

    float[] vertices = new float[translatedVertices.size() * 5];
    for (int i = 0; i < translatedVertices.size() * 5; i+=5) {
      Vertex v = translatedVertices.get((int) Math.floor(i / 5.0));
      vertices[i] = v.getPositions().x;
      vertices[i+1] = v.getPositions().y;
      vertices[i+2] = v.getPositions().z;
      vertices[i+3] = v.getTextures().x;
      vertices[i+4] = v.getTextures().y;
    }

    int vertexCount = vertices.length;

    int vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);

    int vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0L);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);

    glBindVertexArray(0);

    return new Chunk(position, vaoId, vertexCount);
  }
}
