package de.re.voxelgame.core;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
  private final int id;

  public Shader(Path vertexPath, Path fragmentPath) throws IOException {
    this(Files.readString(vertexPath), Files.readString(fragmentPath));
  }

  public Shader(String vertexContent, String fragmentContent) {
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

  public void use() {
    glUseProgram(id);
  }

  public void terminate() {
    glDeleteProgram(id);
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

  public void setVec3(String name, int value) {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  public void setVec2(String name, int value) {
    throw new UnsupportedOperationException("Not yet implemented!");
  }
}
