package net.brentwalther.controllermod.context;

import net.brentwalther.controllermod.binding.BindingManager;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.device.DeviceManager;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.ControllerSettingsScreen;
import net.brentwalther.controllermod.ui.GuiScreenUtil;
import net.brentwalther.controllermod.ui.MenuPointer;
import net.minecraft.client.Minecraft;

public class ModSettingsBindingsApplier extends MenuBindingApplier {
  private final DeviceManager deviceManager;
  private final BindingManager bindingManager;

  public ModSettingsBindingsApplier(
      MenuPointer pointer, DeviceManager deviceManager, BindingManager bindingManager) {
    super(pointer);
    this.deviceManager = deviceManager;
    this.bindingManager = bindingManager;
  }

  @Override
  public void onLoad(Configuration config) {
    super.onLoad(config);
    if (!(Minecraft.getMinecraft().currentScreen instanceof ControllerSettingsScreen)) {
    GuiScreenUtil.pushScreen(
        new ControllerSettingsScreen(deviceManager, bindingManager));
    }
  }

  @Override
  public ScreenContext getScreenContext() {
    return ScreenContext.MOD_SETTINGS;
  }
}
