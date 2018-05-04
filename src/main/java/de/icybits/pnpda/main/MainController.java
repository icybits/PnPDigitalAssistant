package de.icybits.pnpda.main;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.icybits.pnpda.view.CenterView;
import de.icybits.pnpda.view.SideView;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class MainController {

  @FXML
  private ListView<Object> searchList;
  @FXML
  private TextField searchField;
  @FXML
  private MenuButton searchMenu;
  @FXML
  private TabPane centerView;
  @FXML
  private TabPane sideView;

  @MainBus
  @Inject
  private EventBus mainBus;


  @FXML
  private void initialize() {
    this.mainBus.register(this);
  }

  @Subscribe
  public void openCenterView(CenterView view) {

  }

  @Subscribe
  public void openSideView(SideView view) {

  }
}
