package de.re.voxelgame.core;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Chunk {
    public static final int CHUNK_SIZE = 16;

    // Visual: https://www.math3d.org/OdaojzOmz
    private final Vertex[] front = {
            new Vertex(-0.5f,  0.5f, 0.5f, 0.5f, 0.0f),
            new Vertex(-0.5f, -0.5f, 0.5f, 0.5f, 0.5f),
            new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.5f),
            new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.5f),
            new Vertex(0.5f,  0.5f, 0.5f, 0.0f, 0.0f),
            new Vertex(-0.5f,  0.5f, 0.5f, 0.5f, 0.0f)
    };

    private final Vertex[] back = {
            new Vertex(0.5f,  0.5f, -0.5f, 0.5f, 0.0f),
            new Vertex(0.5f, -0.5f, -0.5f, 0.5f, 0.5f),
            new Vertex(-0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(-0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(-0.5f,  0.5f, -0.5f, 0.0f, 0.0f),
            new Vertex(0.5f,  0.5f, -0.5f, 0.5f, 0.0f)
    };

    private final Vertex[] left = {
            new Vertex(-0.5f,  0.5f, -0.5f, 0.5f, 0.0f),
            new Vertex(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f),
            new Vertex(-0.5f, -0.5f,  0.5f, 0.0f, 0.5f),
            new Vertex(-0.5f, -0.5f,  0.5f, 0.0f, 0.5f),
            new Vertex(-0.5f,  0.5f,  0.5f, 0.0f, 0.0f),
            new Vertex(-0.5f,  0.5f, -0.5f, 0.5f, 0.0f)
    };

    private final Vertex[] right = {
            new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f),
            new Vertex(0.5f, -0.5f,  0.5f, 0.5f, 0.5f),
            new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(0.5f,  0.5f, -0.5f, 0.0f, 0.0f),
            new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f)
    };

    private final Vertex[] top = {
            new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f),
            new Vertex(0.5f,  0.5f, -0.5f, 0.5f, 0.5f),
            new Vertex(-0.5f,  0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(-0.5f,  0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(-0.5f,  0.5f,  0.5f, 0.0f, 0.0f),
            new Vertex(0.5f,  0.5f,  0.5f, 0.5f, 0.0f)
    };

    private final Vertex[] bottom = {
            new Vertex(-0.5f, -0.5f,  0.5f, 0.5f, 0.0f),
            new Vertex(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f),
            new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.5f),
            new Vertex(0.5f, -0.5f,  0.5f, 0.0f, 0.0f),
            new Vertex(-0.5f, -0.5f,  0.5f, 0.5f, 0.0f)
    };

    private int vaoId;

    private int vertexCount;

    private Vector2f position;

    public Chunk(Vector2f position) {
        this.position = position;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void prepare() {
        List<Vertex> translatedVertices = new ArrayList<>();

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                // Position in world
                int X = (int) (x + position.x * CHUNK_SIZE);
                int Z = (int) (z + position.y * CHUNK_SIZE);

                // Max. size in x/z direction
                int SX = (int) (position.x+1) * (CHUNK_SIZE-1);
                int SZ = (int) (position.y+1) * (CHUNK_SIZE-1);

                if ((X == 0 && Z == 0) || (X == 0 && Z == SZ) || (X == SX && Z == 0) || (X == SX && Z == SZ)) { // One of 4 corner cases
                    // Corner
                    translatedVertices.addAll(translateFullBlock(X, Z));
                } else if (((X == 0 || X == SX) && (Z > 0 && Z < SZ)) || ((Z == 0 || Z == SZ) && (X > 0 && X < SX))) { // One of 2x2 edge cases
                    // Edge
                    translatedVertices.addAll(translateFullBlock(X, Z));
                } else if ((X > 0 && X < SX) && (Z > 0 && Z < SZ)) { // Remaining middle cases
                    // Middle
                    translatedVertices.addAll(translateMiddleBlock(X, Z));
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

        vertexCount = vertices.length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0L);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);

        glBindVertexArray(0);
    }

    private List<Vertex> translateFullBlock(int x, int z) {
        List<Vertex> translated = new ArrayList<>();

        translated.addAll(translateVertices(front, x, z));
        translated.addAll(translateVertices(back, x, z));
        translated.addAll(translateVertices(left, x, z));
        translated.addAll(translateVertices(right, x, z));
        translated.addAll(translateVertices(top, x, z));
        translated.addAll(translateVertices(bottom, x, z));

        return translated;
    }

    private List<Vertex> translateMiddleBlock(int x, int z) {
        List<Vertex> translated = new ArrayList<>();

        translated.addAll(translateVertices(top, x, z));
        translated.addAll(translateVertices(bottom, x, z));

        return translated;
    }

    private List<Vertex> translateVertices(Vertex[] vertices, int x, int z) {
        List<Vertex> translated = new ArrayList<>();

        for (Vertex v : vertices) {
            Vertex vertex = new Vertex(v.getPositions().x + x, v.getPositions().y, v.getPositions().z + z, v.getTextures().x, v.getTextures().y);
            translated.add(vertex);
        }

        return translated;
    }
}
