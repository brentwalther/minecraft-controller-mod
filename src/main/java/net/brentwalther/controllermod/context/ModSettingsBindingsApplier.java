package net.brentwalther.controllermod.context;

import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.device.DeviceManager;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.MenuPointer;
import net.brentwalther.controllermod.ui.SettingsMenu;
import net.minecraft.client.Minecraft;

public class ModSettingsBindingsApplier extends MenuBindingApplier {
  private final DeviceManager deviceManager;

  public ModSettingsBindingsApplier(MenuPointer pointer, DeviceManager deviceManager) {
    super(pointer);
    this.deviceManager = deviceManager;
  }

  @Override
  public void onLoad(Configuration config) {
    super.onLoad(config);
    Minecraft minecraft = Minecraft.getMinecraft();
    minecraft.displayGuiScreen(new SettingsMenu(minecraft.currentScreen, deviceManager));
  }

  @Override
  public ScreenContext getScreenContext() {
    return ScreenContext.MOD_SETTINGS;
  }
}
