package de.icybits.spl;

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
  private static Path yesZipFile;
  private static Path emptyFile;

  @BeforeAll
  static void createTemporaryFolderStructure(@TempDir Path temporaryTestDirectory) throws URISyntaxException, IOException {
    yesJarFile = Paths.get(PluginLoaderTest.class.getResource("/de/icybits/spl/yes.jar").toURI());
    yesZipFile = Paths.get(PluginLoaderTest.class.getResource("/de/icybits/spl/yes.zip").toURI());
    emptyFile = Paths.get(PluginLoaderTest.class.getResource("/de/icybits/spl/empty.txt").toURI());
    nonJarFile = Paths.get(PluginLoaderTest.class.getResource("/de/icybits/spl/non.jar").toURI());

    Files.copy(yesJarFile, temporaryTestDirectory.resolve(yesJarFile.getFileName()));
    Files.copy(yesZipFile, temporaryTestDirectory.resolve(yesZipFile.getFileName()));
    Files.copy(emptyFile, temporaryTestDirectory.resolve(emptyFile.getFileName()));
    Files.copy(nonJarFile, temporaryTestDirectory.resolve(nonJarFile.getFileName()));
    Path subFolder = temporaryTestDirectory.resolve("subFolder");
    subFolder.toFile().mkdir();
    Files.copy(yesJarFile, subFolder.resolve(yesJarFile.getFileName()));
    Files.copy(yesZipFile, subFolder.resolve(yesZipFile.getFileName()));
    Files.copy(emptyFile, subFolder.resolve(emptyFile.getFileName()));
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
  void hasExceptionAndNoPluginIfYesZipIsLoaded() {
    PluginLoader<Object> loader = new PluginLoader<>(Object.class);
    loader.setLocation(yesZipFile);
    PluginLoader.Result<Object> result = loader.load();
    Assertions.assertEquals(1, result.getExceptions().size());
    Assertions.assertEquals(0, result.getPlugins().size());
  }

  @Test
  void hasExceptionAndNoPluginIfEmptyFileIsLoaded() {
    PluginLoader<Object> loader = new PluginLoader<>(Object.class);
    loader.setLocation(emptyFile);
    PluginLoader.Result<Object> result = loader.load();
    Assertions.assertEquals(0, result.getExceptions().size());
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
    Assertions.assertEquals(4, result.getExceptions().size());
  }
}