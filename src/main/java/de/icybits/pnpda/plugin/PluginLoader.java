package de.icybits.pnpda.plugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Load plugin classes from jars under given URI/Path.
 * TODO add example to class description.
 *
 * @author Iceac Sarutobi
 */
public class PluginLoader {

  private static final Logger log = LogManager.getLogger(PluginLoader.class);
  private static final String PLUGIN_CLASS = "Plugin-Class";

  /**
   * Load Plugins at given URI.
   *
   * @param source the uri to a jar or folder
   * @return The {@link Result} containing the loaded Plugins and/or exceptions.
   * @throws IOException Most likely if the given file or folder is not accessible.
   */
  public static Result load(URI source) throws IOException {
    return load(Paths.get(source));
  }

  /**
   * Load Plugins at given Path.
   *
   * @param source the uri to a jar or folder
   * @return The {@link Result} containing the loaded Plugins and/or exceptions.
   * @throws IOException Most likely if the given file or folder is not accessible.
   */
  public static Result load(Path source) throws IOException {
    final List<Plugin> pluginList = new ArrayList<>();
    final List<File> fileList = new ArrayList<>();
    final List<Exception> exceptionList = new ArrayList<>();

    Files.walkFileTree(source, new JarCollector(fileList));

    for (File file : fileList) {
      try {
        pluginList.add(getPluginFromJarFile(file));
      } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
        log.debug(e);
        exceptionList.add(e);
      }
    }

    return new Result() {
      @Override
      public List<Plugin> getPlugins() {
        return Collections.unmodifiableList(pluginList);
      }

      @Override
      public List<Exception> getExceptions() {
        return Collections.unmodifiableList(exceptionList);
      }
    };
  }

  private static Plugin getPluginFromJarFile(File file) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    JarFile jarFile = new JarFile(file);
    String className = jarFile.getManifest().getMainAttributes().getValue(PLUGIN_CLASS);
    URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});
    Class<?> pluginClass = loader.loadClass(className);
    Class<? extends Plugin> plugin = pluginClass.asSubclass(Plugin.class);
    return plugin.newInstance();
  }

  /**
   * The Result of the PluginLoader load method. Contains the Plugins and/or Exceptions.
   */
  public interface Result {

    List<Plugin> getPlugins();

    List<Exception> getExceptions();

  }

  private static class JarCollector extends SimpleFileVisitor<Path> {

    private final List<File> destination;

    JarCollector(List<File> destination) {
      this.destination = destination;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
      if (attrs.isDirectory() || attrs.isOther() || attrs.isSymbolicLink() || !attrs.isRegularFile()) {
        log.debug("non file in visitFile method: " + path);
        return FileVisitResult.CONTINUE;
      }
      File file = path.toFile();
      if (!isJar(file)) {
        log.debug("non jar file in visitFile method: " + path);
        return FileVisitResult.CONTINUE;
      }
      log.debug("jar file found: " + path);
      this.destination.add(file);
      return FileVisitResult.CONTINUE;
    }

    private boolean isJar(File file) {
      String name = file.getName();
      return name.endsWith(".jar") || name.endsWith(".zip");
    }
  }
}
