package de.icybits.pnpda.main;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Our main class for our pen and paper digital assistant (AKA: PnPDA). Here we initialize the assistant and show
 * our main view. The PnPDA is a JavaFX Application, so we extend {@link Application} here.
 *
 * @author Iceac Sarutobi
 */
public class Main extends Application {

  /**
   * For various reasons, do never add any other code, then {@link #launch(Class, String...)} to
   * this method. This is because we want to enable native code for JavaFX-Application startup. so
   * be aware that this method is generally never called. The earliest place to do the stuff you
   * want to do is in the {@link #init()} method of the {@link Application} class.
   */
  public static void main(String[] args) {
    launch(Main.class, args);
  }

  @Override
  public void start(Stage primaryStage) {

  }
}
