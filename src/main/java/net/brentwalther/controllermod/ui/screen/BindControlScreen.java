package net.brentwalther.controllermod.ui.screen;

import net.brentwalther.controllermod.device.Control;

public class BindControlScreen extends ModScreen {

  /** The callback that will be executed when a specific control is bound. */
  public interface BindControlCallback {
    void bind(Control control);
  }

  private final BindControlCallback callback;

  @Override
  public void initGui() {
    super.initGui();
    drawBackground(50);
  }

  public BindControlScreen(BindControlCallback callback) {
    this.callback = callback;
  }

  public void bind(Control control) {
    callback.bind(control);
  }
}
