package de.re.voxelgame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ResourceLoader {
  private ResourceLoader() {
  }

  public static Resource locateResource(String file, Class<?> clazz) throws FileNotFoundException, URISyntaxException {
    URL url = clazz.getClassLoader().getResource(file);

    if (url == null) {
      throw new FileNotFoundException(file);
    }

    return new Resource(url.toURI());
  }

  public static class Resource {
    private final URI uri;

    private Resource(URI uri) {
      this.uri = uri;
    }

    public Path toPath() {
      return Paths.get(uri);
    }

    public File toFile() {
      return new File(toString());
    }

    public FileInputStream toFileInputStream() throws FileNotFoundException {
      return new FileInputStream(toFile());
    }

    public FileReader toFileReader() throws FileNotFoundException {
      return new FileReader(toFile());
    }

    @Override
    public String toString() {
      return uri.getPath();
    }
  }
}
