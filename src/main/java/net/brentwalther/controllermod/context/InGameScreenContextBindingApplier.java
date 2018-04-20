package net.brentwalther.controllermod.context;

import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;

public class InGameScreenContextBindingApplier extends AbstractScreenContextBindingApplier {

  @Override
  public Runnable getRenderRunnable() {
    return null;
  }

  @Override
  public void onLoad(Configuration config) {}

  @Override
  public ScreenContext getScreenContext() {
    return ScreenContext.IN_GAME;
  }
}
