package de.icybits.pnpda.plugin;

import com.google.inject.Module;

/**
 * The interface which must be implemented to add new things to the PnPDA.
 */
public interface Plugin extends Module {

  /**
   * A descriptive name for your plugin.
   *
   * @return The name of the Plugin.
   */
  String getName();

}
