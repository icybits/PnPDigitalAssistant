package de.icybits.pnpda.plugin;

import java.util.List;

public interface Result<T> {

  List<T> getPlugins();

  List<Exception> getExceptions();

}
