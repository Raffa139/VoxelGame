package de.re.voxelgame.font;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import de.re.engine.GLApplication;
import de.re.engine.objects.shader.Shader;
import de.re.voxelgame.VoxelApplication;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeType.FT_LOAD_RENDER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;

public class FontRenderer {
  private final Map<Character, Glyph> glyphs;

  private final Shader shader;

  private final Matrix4f projection;

  private final int glyphVao;
  private final int glyphVbo;

  public FontRenderer(GLApplication application, File font) throws IOException {
    glyphs = new HashMap<>();
    shader = ((VoxelApplication) application).shaderFromResources("shader/font.vert", "shader/font.frag");
    projection = new Matrix4f().ortho2D(0.0f, 1080.0f, 0.0f, 720.0f);

    glyphVao = glGenVertexArrays();
    glyphVbo = glGenBuffers();

    initMemory();
    initFont(font);
  }

  public void renderText(String text, float x, float y, float scale, Vector3f color) {
    shader.use();
    shader.setMatrix4("iProjection", projection);
    shader.setVec3("iTextColor", color);
    glActiveTexture(GL_TEXTURE0);
    glBindVertexArray(glyphVao);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    for (char c : text.toCharArray()) {
      Glyph glyph = glyphs.get(c);

      float xPos = x + glyph.getBearing().x * scale;
      float yPos = y - (glyph.getSize().y - glyph.getBearing().y) * scale;

      float width = glyph.getSize().x * scale;
      float height = glyph.getSize().y * scale;

      float[] vertices = {
          xPos, yPos + height, 0.0f, 0.0f,
          xPos, yPos, 0.0f, 1.0f,
          xPos + width, yPos, 1.0f, 1.0f,
          xPos, yPos + height, 0.0f, 0.0f,
          xPos + width, yPos, 1.0f, 1.0f,
          xPos + width, yPos + height, 1.0f, 0.0f
      };

      glBindTexture(GL_TEXTURE_2D, glyph.getTextureId());
      glBindBuffer(GL_ARRAY_BUFFER, glyphVbo);
      glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
      glBindBuffer(GL_ARRAY_BUFFER, 0);

      glDrawArrays(GL_TRIANGLES, 0, vertices.length);

      x += (glyph.getAdvance() >> 6) * scale;
    }

    glBindVertexArray(0);
    glBindTexture(GL_TEXTURE_2D, 0);
    glDisable(GL_BLEND);
  }

  private void initMemory() {
    glBindVertexArray(glyphVao);
    glBindBuffer(GL_ARRAY_BUFFER, glyphVbo);
    glBufferData(GL_ARRAY_BUFFER, 6 * 4, GL_DYNAMIC_DRAW);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 4, GL_FLOAT, false, 4, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
  }

  private void initFont(File font) {
    // FreeType (lib not working)
    // https://www.tabnine.com/code/java/classes/com.badlogic.gdx.graphics.g2d.freetype.FreeType
    // https://www.tabnine.com/code/java/methods/com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator/<init>
    // http://www.java2s.com/Code/Jar/g/Downloadgdxfreetypenativesjar.htm
    // https://mvnrepository.com/artifact/com.badlogicgames.gdx/gdx-freetype

    // LWJGL stb bindings
    // https://github.com/LWJGL/lwjgl3/tree/master/modules/samples/src/test/java/org/lwjgl/demo/stb
    // https://www.google.com/search?client=opera&q=lwjgl+font+rendering&sourceid=opera&ie=UTF-8&oe=UTF-8
    //stb_easy_font_print();

    FreeType.Library library = FreeType.initFreeType();
    FreeType.Face face = library.newFace(new FileHandle(font), 0);
    face.setPixelSizes(0, 48);

    for (char c = 0; c < 128; c++) {
      face.loadChar(c, FT_LOAD_RENDER);
      FreeType.GlyphSlot glyphSlot = face.getGlyph();

      int textureId = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, textureId);
      glTexImage2D(
          GL_TEXTURE_2D,
          0,
          GL_RED,
          glyphSlot.getBitmap().getWidth(),
          glyphSlot.getBitmap().getRows(),
          0,
          GL_RED,
          GL_UNSIGNED_BYTE,
          glyphSlot.getBitmap().getBuffer()
      );
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      Glyph glyph = new Glyph(
          textureId,
          glyphSlot.getAdvanceX(),
          new Vector2i(glyphSlot.getBitmap().getWidth(), glyphSlot.getBitmap().getRows()),
          new Vector2i(glyphSlot.getBitmapLeft(), glyphSlot.getBitmapTop())
      );

      glyphs.put(c, glyph);
    }

    face.dispose();
    library.dispose();
  }
}
