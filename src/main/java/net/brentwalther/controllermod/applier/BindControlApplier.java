package net.brentwalther.controllermod.applier;

import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.device.Control;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;
import net.brentwalther.controllermod.ui.MenuPointer;
import net.brentwalther.controllermod.ui.screen.BindControlScreen;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BindControlApplier extends MenuBindingApplier {
  private final Consumer<ControlBinding> newControlBindingConsumer;

  public BindControlApplier(
      MenuPointer pointer, Consumer<ControlBinding> newControlBindingConsumer) {
    super(pointer);
    this.newControlBindingConsumer = newControlBindingConsumer;
  }

  @Override
  public void processButtonUpdates(List<ButtonStateUpdate> updates) {
    Optional<XInputButton> firstButtonPressed =
        updates
            .stream()
            .filter((update) -> update.state == PressState.IS_BECOMING_PRESSED)
            .map((update) -> update.button)
            .findFirst();
    if (firstButtonPressed.isPresent()) {
      ControllerMod.getLogger().info("Accepting button " + firstButtonPressed.get());
      bindControl(Control.button(firstButtonPressed.get()));
    }
  }

  @Override
  public void processAxisUpdates(List<AxisValueUpdate> updates) {
    Optional<XInputAxis> firstAxisTriggered =
        updates
            .stream()
            .filter((update) -> update.value > 0.75f)
            .map((update) -> update.axis)
            .findFirst();
    if (firstAxisTriggered.isPresent()) {
      bindControl(Control.axis(firstAxisTriggered.get()));
    }
  }

  @Override
  public ScreenContext getScreenContext() {
    return ScreenContext.BIND_KEY;
  }

  private static void bindControl(Control control) {
    if (Minecraft.getMinecraft().currentScreen instanceof BindControlScreen) {
      BindControlScreen screen = (BindControlScreen) Minecraft.getMinecraft().currentScreen;
      screen.bind(control);
    }
  }
}
