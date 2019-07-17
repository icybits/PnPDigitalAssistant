package de.icybits.spl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;

/**
 * This plugin loader works on a simple contract.
 * Any type of jar/zip file which contains a manifest file,
 * is valid if this manifest file contains a main attribute {@value PLUGIN_CLASS}
 * and this main attribute value is the full class name of a class,
 * which is of the Type {@link T} or extends this type.
 * This class needs a parameter less constructor.
 * The plugin loader then will create an instance of this class.
 * Any possible exception will be collected and can be accessed by the Result.
 *
 * @param <T> the type of the plugin class which needs to be instantiated.
 */
public class PluginLoader<T> {

  private static final Logger log = LogManager.getLogger(PluginLoader.class);
  private static final String PLUGIN_CLASS = "Plugin-Class";

  private Class<T> type;
  private Path location;

  public PluginLoader(Class<T> type) {
    this.type = Objects.requireNonNull(type);
    this.setLocation(Paths.get("plugins"));
  }

  public Path getLocation() {
    return this.location;
  }

  public void setLocation(Path location) {
    this.location = Objects.requireNonNull(location);
  }

  public Result<T> load() {
    final List<T> pluginList = new ArrayList<>();
    final List<File> fileList = new ArrayList<>();
    final List<Exception> exceptionList = new ArrayList<>();

    try {
      Files.walkFileTree(this.getLocation(), new JarCollector(fileList));
    } catch (IOException e) {
      log.debug(e);
      exceptionList.add(e);
    }

    for (File file : fileList) {
      try {
        pluginList.add(this.getPluginFromJarFile(file));
      } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
        log.debug(e);
        exceptionList.add(e);
      }
    }

    return new Result<>() {
      @Override
      public List<T> getPlugins() {
        return Collections.unmodifiableList(pluginList);
      }

      @Override
      public List<Exception> getExceptions() {
        return Collections.unmodifiableList(exceptionList);
      }
    };
  }

  private T getPluginFromJarFile(File file) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    try (JarFile jarFile = new JarFile(file)) {
      if (jarFile.getManifest() == null) {
        throw new IOException("No Manifest");
      }
      if (jarFile.getManifest().getMainAttributes().getValue(PLUGIN_CLASS) == null) {
        throw new IOException("No main attribute: " + PLUGIN_CLASS);
      }
      String className = jarFile.getManifest().getMainAttributes().getValue(PLUGIN_CLASS);
      URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});
      Class<?> pluginClass = loader.loadClass(className);
      Class<? extends T> plugin = pluginClass.asSubclass(this.type);
      Constructor<? extends T> constructor = plugin.getConstructor();
      return constructor.newInstance();
    }
  }

  public interface Result<T> {

    List<T> getPlugins();

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
      if (!this.isJar(file)) {
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
