package de.icybits.plugin.loader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
class PluginLoaderTest {

  private static Path temporaryTestDirectory;
  private static Path yesJarFile;
  private static Path nonJarFile;

  @BeforeAll
  static void createTemporaryFolderStructure(@TempDir Path temporaryTestDirectory) throws URISyntaxException, IOException {
    yesJarFile = Paths.get(PluginLoaderTest.class.getResource("/de/icybits/plugin/loader/yes.jar").toURI());
    nonJarFile = Paths.get(PluginLoaderTest.class.getResource("/de/icybits/plugin/loader/non.jar").toURI());

    Files.copy(yesJarFile, temporaryTestDirectory.resolve(yesJarFile.getFileName()));
    Files.copy(nonJarFile, temporaryTestDirectory.resolve(nonJarFile.getFileName()));
    Path subFolder = temporaryTestDirectory.resolve("subFolder");
    subFolder.toFile().mkdir();
    Files.copy(yesJarFile, subFolder.resolve(yesJarFile.getFileName()));
    Files.copy(nonJarFile, subFolder.resolve(nonJarFile.getFileName()));

    PluginLoaderTest.temporaryTestDirectory = temporaryTestDirectory;
  }

  @Test
  void createNewPluginLoaderWithType() {
    PluginLoader<Object> loader = new PluginLoader<>(Object.class);
    Assertions.assertNotNull(loader);
  }

  @Test
  void throwsExceptionOnNewPluginWithNullType() {
    Assertions.assertThrows(NullPointerException.class, () -> new PluginLoader<>(null));
  }

  @Test
  void hasExceptionIfPluginLocationFolderNotExist() {
    PluginLoader<Object> loader = new PluginLoader<>(Object.class);
    PluginLoader.Result<Object> result = loader.load();
    Assertions.assertEquals(1, result.getExceptions().size());
  }

  @Test
  void hasExceptionAndNoPluginIfNonJarIsLoaded() {
    PluginLoader<Object> loader = new PluginLoader<>(Object.class);
    loader.setLocation(nonJarFile);
    PluginLoader.Result<Object> result = loader.load();
    Assertions.assertEquals(1, result.getExceptions().size());
    Assertions.assertEquals(0, result.getPlugins().size());
  }

  @Test
  void hasPluginAndNoExceptionIfYesJarIsLoaded() {
    PluginLoader<Object> loader = new PluginLoader<>(Object.class);
    loader.setLocation(yesJarFile);
    PluginLoader.Result<Object> result = loader.load();
    Assertions.assertEquals(0, result.getExceptions().size());
    Assertions.assertEquals(1, result.getPlugins().size());
    Assertions.assertTrue(result.getPlugins().get(0) instanceof TestPlugin);
  }

  @Test
  void hasPluginsAndExceptionsIfTestPluginFolderIsUsed() {
    PluginLoader<Object> loader = new PluginLoader<>(Object.class);
    loader.setLocation(temporaryTestDirectory);
    PluginLoader.Result<Object> result = loader.load();
    Assertions.assertEquals(2, result.getPlugins().size());
    Assertions.assertTrue(result.getPlugins().get(0) instanceof TestPlugin);
    Assertions.assertTrue(result.getPlugins().get(1) instanceof TestPlugin);
    Assertions.assertEquals(2, result.getExceptions().size());
  }
}