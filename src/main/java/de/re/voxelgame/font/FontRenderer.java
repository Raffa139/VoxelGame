package de.re.voxelgame.font;

import de.ren.ecs.engine.GLApplication;
import de.ren.ecs.engine.objects.sampler.Sampler2D;
import de.ren.ecs.engine.objects.shader.Shader;
import de.re.voxelgame.VoxelApplication;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class FontRenderer {
  private final Map<Character, Glyph> glyphs;

  private final Shader shader;

  private final Sampler2D fontAtlas;

  private final Matrix4f projection;

  private final int glyphVao;
  private final int glyphVbo;

  public FontRenderer(GLApplication application, Path font, Sampler2D fontAtlas) throws IOException {
    glyphs = FontLoader.loadFont(font);
    shader = ((VoxelApplication) application).shaderFromResources("shader/font.vert", "shader/font.frag");
    this.fontAtlas = fontAtlas;
    projection = new Matrix4f().ortho2D(0.0f, 1080.0f, 0.0f, 720.0f);

    glyphVao = glGenVertexArrays();
    glyphVbo = glGenBuffers();

    initMemory();
  }

  public void renderText(String text, float x, float y, float scale, Vector3f color) {
    shader.use();
    shader.setMatrix4("iProjection", projection);
    shader.setVec3("iTextColor", color);
    fontAtlas.bind(0);

    glBindVertexArray(glyphVao);
    glDisable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    for (char c : text.toCharArray()) {
      Glyph glyph = glyphs.get(c);

      Vector2f origin = glyph.getTextureCoordinates();
      Vector2f bottomLeft = origin.add(new Vector2f(0, glyph.getSize2().y), new Vector2f());
      Vector2f topRight = origin.add(new Vector2f(glyph.getSize2().x, 0), new Vector2f());
      Vector2f bottomRight = origin.add(new Vector2f(glyph.getSize2().x, glyph.getSize2().y), new Vector2f());

      float xPos = x + glyph.getBearing().x * scale;
      float yPos = y - (glyph.getSize().y + glyph.getBearing().y - 75) * scale;

      float width = glyph.getSize().x * scale;
      float height = glyph.getSize().y * scale;

      float[] vertices = {
          // Top left
          xPos, yPos + height, origin.x, -origin.y,
          // Bottom left
          xPos, yPos, bottomLeft.x, -bottomLeft.y,
          // Bottom right
          xPos + width, yPos, bottomRight.x, -bottomRight.y,
          xPos + width, yPos, bottomRight.x, -bottomRight.y,
          // Top right
          xPos + width, yPos + height, topRight.x, -topRight.y,
          // Top left
          xPos, yPos + height, origin.x, -origin.y
      };

      glBindBuffer(GL_ARRAY_BUFFER, glyphVbo);
      glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
      glBindBuffer(GL_ARRAY_BUFFER, 0);

      glDrawArrays(GL_TRIANGLES, 0, vertices.length);

      x += glyph.getAdvance() * scale;
    }

    glBindVertexArray(0);
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);
  }

  private void initMemory() {
    glBindVertexArray(glyphVao);
    glBindBuffer(GL_ARRAY_BUFFER, glyphVbo);
    glBufferData(GL_ARRAY_BUFFER, 6 * 4 * 4, GL_DYNAMIC_DRAW);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * 4, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
  }
}
