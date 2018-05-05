package de.icybits.pnpda.plugin;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Load plugin classes from jars in given folder.
 *
 * @author Iceac Sarutobi
 */
public class PluginLoader {

  public static final String PLUGIN_CLASS = "Plugin-Class";

  private final File folder;

  /**
   * Initialize with the given folder.
   *
   * @param folder the folder to look in
   * @throws NullPointerException If the given folder is null.
   */
  public PluginLoader(File folder) {
    this.folder = requireNonNull(folder, "The folder parameter must not be null.");
  }

  public File getFolder() {
    return this.folder;
  }

  /**
   * Load all {@link Plugin} classes from the jar files.
   *
   * @return Instances of all found plugins.
   */
  public List<Plugin> load() {
    File[] files = this.getFolder().listFiles(this::isJar);
    if (files == null) {
      return new ArrayList<>();
    }
    List<Plugin> pluginList = new ArrayList<>();
    for (File file : files) {
      try {
        // TODO Optimize code.
        JarFile jarFile = new JarFile(file);
        String className = jarFile.getManifest().getMainAttributes().getValue(PLUGIN_CLASS);
        URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        Class<?> pluginClass = loader.loadClass(className);
        Class<? extends Plugin> plugin = pluginClass.asSubclass(Plugin.class);
        Plugin instance = plugin.newInstance();
        pluginList.add(instance);
      } catch (IOException | ClassNotFoundException | ClassCastException | IllegalAccessException | InstantiationException e) {
        e.printStackTrace();
        // TODO Logging when available.
      }
    }
    return pluginList;
  }

  private boolean isJar(File file) {
    if (file != null) {
      return file.getName().endsWith(".jar");
    }
    return false;
  }

}
