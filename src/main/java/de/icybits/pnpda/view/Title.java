package de.icybits.pnpda.view;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Use this to display the title of your view in the {@link javafx.scene.control.Tab#textProperty()}.
 * This is possible with either the {@link #value()} value of this annotation while annotating your
 * view class, or by annotating a method which returns a String or a {@link
 * javafx.beans.property.StringProperty} which will be bound to the {@link
 * javafx.scene.control.Tab#textProperty()} property. If you set the value of this annotation while
 * annotating a method the return value of the method is used first. If the return value of the
 * method is null then the value of the annotation is used. If neither returns a value then a
 * default value is used the default value may change with each version of the PNPDA.
 *
 * @author Iceac Sarutobi
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface Title {

  /**
   * @return The String to display the title of your view.
   */
  String value();
}
