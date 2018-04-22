package net.brentwalther.controllermod.context;

import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.proto.ConfigurationProto;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.MenuPointer;

public class MenuBindingApplier extends AbstractBindingApplier {

  private final MenuPointer menuPointer;

  public MenuBindingApplier(MenuPointer pointer) {
    this.menuPointer = pointer;
  }

  @Override
  public void onLoad(Configuration config) {
    if (config.get().hasReticlePosition()) {
      ConfigurationProto.GlobalConfig.Position position = config.get().getReticlePosition();
      }
      menuPointer.unhide();
  }

  @Override
  public void onUnload(Configuration config) {
    super.onUnload(config);
    menuPointer.unhide();
//    config.commitToMemory(
//        config
//            .get()
//            .toBuilder()
//            .setReticlePosition(
//                ConfigurationProto.GlobalConfig.Position.newBuilder()
//                    .setX(cursor.getScreenX())
//                    .setY(cursor.getScreenY()))
//            .build());

    // Draw it off screen before nulling it out
//    this.menuPointer.hide();
  }

  //  @Override
  //  public Runnable getTickRunnable() {
  //    return () -> {
  //      long msSinceLastTick = Minecraft.getSystemTime() - lastTickTime;
  //      float secondsSinceLastTick = msSinceLastTick / 1000f;
  //      float dx = TARGET_PIXEL_PER_SECOND_MOVEMENT * secondsSinceLastTick *
  // this.xJoystickPosition;
  //      float dy = TARGET_PIXEL_PER_SECOND_MOVEMENT * secondsSinceLastTick *
  // this.yJoystickPosition;
  //
  //      //      ControllerMod.getLogger()
  //      //          .info(
  //      //              String.format(
  //      //                  "Overall dx/dy from last tick (%.3fs): (%d, %d) from controller
  // positions:
  //      // %.2f %.2f",
  //      //                  secondsSinceLastTick, dx, dy, this.xJoystickPosition,
  //      // this.yJoystickPosition));
  //
  //      cursor.updateXY(dx, dy);
  //      VirtualMouse.INSTANCE.setMousePosition(cursor.getMinecraftX(), cursor.getMinecraftY());
  //
  //      while (!inputActions.isEmpty()) {
  //        inputActions.poll().perform();
  //      }
  //
  //      lastTickTime = Minecraft.getSystemTime();
  //    };
  //  }

  @Override
  public Runnable getRenderRunnable() {
    return () -> menuPointer.draw();
  }

  @Override
  public ScreenContext getScreenContext() {
    return ScreenContext.MENU;
  }
}
