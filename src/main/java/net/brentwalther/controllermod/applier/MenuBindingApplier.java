package net.brentwalther.controllermod.applier;

import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.input.VirtualMouse;
import net.brentwalther.controllermod.proto.ConfigurationProto;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.MenuPointer;
import net.brentwalther.controllermod.util.PositionOnScreen;

public class MenuBindingApplier extends AbstractBindingApplier {

  private final MenuPointer menuPointer;

  public MenuBindingApplier(MenuPointer pointer) {
    this.menuPointer = pointer;
  }

  @Override
  public void onLoad(Configuration config) {
    if (config.get().hasReticlePosition()) {
      ConfigurationProto.GlobalConfig.Position position = config.get().getReticlePosition();
      menuPointer.unhide(PositionOnScreen.from(position));
    } else {
      menuPointer.unhide(VirtualMouse.INSTANCE.getMousePosition());
    }
  }

  @Override
  public void onUnload(Configuration config) {
    super.onUnload(config);
    menuPointer.hide();
    config.commitToMemory(
        config
            .get()
            .toBuilder()
            .setReticlePosition(
                ConfigurationProto.GlobalConfig.Position.newBuilder()
                    .setX(menuPointer.getPosition().getMouseX())
                    .setY(menuPointer.getPosition().getMouseY()))
            .build());
  }

  @Override
  public Runnable getRenderRunnable() {
    return () -> menuPointer.draw();
  }

  @Override
  public ScreenContext getScreenContext() {
    return ScreenContext.MENU;
  }
}
