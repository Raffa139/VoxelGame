package de.re.voxelgame.core.shader;

import de.re.voxelgame.core.util.ResourceLoader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
  private final int id;

  protected Shader(String vertexFile, String fragmentFile) throws IOException, URISyntaxException {
    String vertexContent = Files.readString(ResourceLoader.locateResource(vertexFile, Shader.class).toPath());
    String fragmentContent = Files.readString(ResourceLoader.locateResource(fragmentFile, Shader.class).toPath());

    int vertShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertShader, vertexContent);
    glCompileShader(vertShader);
    if (glGetShaderi(vertShader, GL_COMPILE_STATUS) == GL_FALSE) {
      System.err.println("ERROR::SHADER::VERTEX::COMPILATION_FAILURE");
      System.err.println(glGetShaderInfoLog(vertShader, vertexContent.length()));
      System.exit(-1);
    }

    int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragShader, fragmentContent);
    glCompileShader(fragShader);
    if (glGetShaderi(fragShader, GL_COMPILE_STATUS) == GL_FALSE) {
      System.err.println("ERROR::SHADER::FRAGMENT::COMPILATION_FAILURE");
      System.err.println(glGetShaderInfoLog(fragShader, fragmentContent.length()));
      System.exit(-1);
    }

    id = glCreateProgram();
    glAttachShader(id, vertShader);
    glAttachShader(id, fragShader);
    glLinkProgram(id);
    if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
      System.err.println("ERROR::SHADER::PROGRAM::LINK_FAILURE");
      System.err.println(glGetShaderInfoLog(id));
      System.exit(-1);
    }

    glDeleteShader(vertShader);
    glDeleteShader(fragShader);
  }

  public int getId() {
    return id;
  }

  public void use() {
    glUseProgram(id);
  }

  public void setBoolean(String name, boolean value) {
    setInt(name, value ? 1 : 0);
  }

  public void setInt(String name, int value) {
    glUniform1i(glGetUniformLocation(id, name), value);
  }

  public void setFloat(String name, float value) {
    glUniform1f(glGetUniformLocation(id, name), value);
  }

  public void setMatrix4(String name, Matrix4f value) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer buffer = value.get(stack.mallocFloat(16));
      glUniformMatrix4fv(glGetUniformLocation(id, name), false, buffer);
    }
  }

  public void setVec3(String name, Vector3f value) {
    glUniform3f(glGetUniformLocation(id, name), value.x, value.y, value.z);
  }

  public void setVec2(String name, Vector2f value) {
    glUniform2f(glGetUniformLocation(id, name), value.x, value.y);
  }
}
