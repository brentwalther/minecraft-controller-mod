package net.brentwalther.controllermod.ui.screen;

import net.brentwalther.controllermod.proto.ConfigurationProto.BindingType;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;

public class BindControlScreen extends ModScreen {
  private final ScreenContext bindingContext;
  private final BindingType bindingType;

  @Override
  public void initGui() {
    super.initGui();
    drawBackground(50);
  }

  public BindControlScreen(ScreenContext bindingContext, BindingType bindingType) {
    this.bindingContext = bindingContext;
    this.bindingType = bindingType;
  }

  public ScreenContext getBindingContext() {
    return bindingContext;
  }

  public BindingType getBindingType() {
    return bindingType;
  }
}
