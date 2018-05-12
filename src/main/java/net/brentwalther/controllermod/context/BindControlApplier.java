package net.brentwalther.controllermod.context;

import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.device.Control;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.BindingType;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;
import net.brentwalther.controllermod.ui.screen.BindControlScreen;
import net.brentwalther.controllermod.ui.GuiScreenUtil;
import net.brentwalther.controllermod.ui.MenuPointer;
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
      newControlBindingConsumer.accept(makeBindResult(Control.button(firstButtonPressed.get())));
      GuiScreenUtil.popScreen();
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
      newControlBindingConsumer.accept(makeBindResult(Control.axis(firstAxisTriggered.get())));
      GuiScreenUtil.popScreen();
    }
  }

  @Override
  public ScreenContext getScreenContext() {
    return ScreenContext.BIND_KEY;
  }

  private static ControlBinding makeBindResult(Control control) {
    ControlBinding.Builder bindingBuilder = ControlBinding.newBuilder();
    if (Minecraft.getMinecraft().currentScreen instanceof BindControlScreen) {
      BindControlScreen screen = (BindControlScreen) Minecraft.getMinecraft().currentScreen;
      bindingBuilder.setScreenContext(screen.getBindingContext());
      bindingBuilder.setType(screen.getBindingType());
    } else {
      bindingBuilder.setScreenContext(ScreenContext.UNKNOWN);
      bindingBuilder.setType(BindingType.UNKNOWN_BINDING);
    }

    switch (control.getType()) {
      case BUTTON:
        bindingBuilder.setButton(control.getButton());
        break;
      case AXIS:
        bindingBuilder.setAxis(control.getAxis());
        break;
    }

    return bindingBuilder.build();
  }
}
