package de.icybits.pnpda.view;

import javafx.scene.Node;

/**
 * Declares that the implementing class returns a view to display.
 *
 * @author Iceac Sarutobi
 */
public interface View {

  /**
   * Return the view to display.
   * @return The {@link Node} to display.
   */
  Node getRoot();
}
