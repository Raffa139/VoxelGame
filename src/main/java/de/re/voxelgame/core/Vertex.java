package de.re.voxelgame.core;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
    private final Vector3f position;

    private final Vector2f texture;

    public Vertex(float x, float y, float z, float u, float v) {
        this(new Vector3f(x, y, z), new Vector2f(u, v));
    }

    public Vertex(Vector3f position, Vector2f texture) {
        this.position = position;
        this.texture = texture;
    }

    public Vector3f getPositions() {
        return position;
    }

    public Vector2f getTextures() {
        return texture;
    }
}
