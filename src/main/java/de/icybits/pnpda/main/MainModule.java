package de.icybits.pnpda.main;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

public class MainModule implements Module {

  private final Main main;

  public MainModule(Main main) {
    this.main = main;
  }

  @Override
  public void configure(Binder binder) {
    binder.bind(Main.class).toInstance(this.main);
  }

}
