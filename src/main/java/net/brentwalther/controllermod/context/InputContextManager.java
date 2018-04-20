package net.brentwalther.controllermod.context;

import com.google.common.collect.ImmutableList;
import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputButton;
import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.binding.BindingFactory;
import net.brentwalther.controllermod.binding.BindingManager;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.context.ScreenContextBindingApplier.AxisValueUpdate;
import net.brentwalther.controllermod.context.ScreenContextBindingApplier.ButtonStateUpdate;
import net.brentwalther.controllermod.device.DeviceManager;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.input.VirtualKeyboard;
import net.brentwalther.controllermod.input.VirtualMouse;
import net.brentwalther.controllermod.proto.ConfigurationProto;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.MenuPointer;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InputContextManager {

  /** The minimum time we would wait between poll update cycles. */
  private static final long MIN_INPUT_POLL_FREQUENCY_MS = 1000 / 60; // For at least 60fps updates

  /**
   * A mapping of screens to the ScreenContextBindingApplier that should handle device context while
   * on that screen.
   */
  private final Map<ScreenContext, ScreenContextBindingApplier> controlContextRegistry;

  private final BindingManager bindingManager;
  private final Configuration config;
  private final DeviceManager deviceManager;
  private final MenuPointer pointer;
  private long lastTickTime;
  private ScreenContextBindingApplier screenContextBindingApplier;

  public InputContextManager(Configuration config) {
    this.config = config;
    this.lastTickTime = Minecraft.getSystemTime();
    this.deviceManager = DeviceManager.createNew();
    this.pointer = new MenuPointer();
    this.bindingManager = new BindingManager(config, new BindingFactory(config, this.pointer));
    this.controlContextRegistry = new HashMap<>();
    registerContext(new InGameScreenContextBindingApplier());
    registerContext(new MenuScreenContextBindingApplier(pointer));
  }

  public void registerContext(ScreenContextBindingApplier context) {
    context.setBindings(
        bindingManager.getButtonBindsForContext(context.getScreenContext()),
        bindingManager.getAxisBindsForContext(context.getScreenContext()));
    controlContextRegistry.put(context.getScreenContext(), context);
  }

  //  /**
  //    * TODO(brentwalther): figure out how to test type parameter values and also see if it could
  //    * be possible to inject the constructor with the correct binding map given it's
  //    * ScreenContext (maybe modify the enum to make you specify implementing class?). If we did
  //    * that, we could stop needing to call setBindings().
  //    */
  //  public void registerContext(Class contextClass) {
  //    if (!ScreenContextBindingApplier.class.isAssignableFrom(contextClass)) {
  //      ControllerMod.disableMod(
  //          "You attempted to register a control context that didn't implement
  // ScreenContextBindingApplier.");
  //    }
  //    Constructor<ScreenContextBindingApplier>[] constructors =
  // contextClass.getDeclaredConstructors();
  //    if (constructors.length != 1 ||
  //        constructors[0].getParameterTypes().length != 2 ||
  //        !MultiMap.class.isAssignableFrom(constructors[0].getParameterTypes()[0]) ||
  //        !MultiMap.class.isAssignableFrom(constructors[0].getParameterTypes()[1])) {
  //        !constructors[0].getParameterTypes()[0].getTypeParameters()[0].getName().isEmpty()) {
  //      constructors[0].newInstance()
  //  }

  public void tick() {
    long timeNow = Minecraft.getSystemTime();
    long msSinceLastTick = timeNow - lastTickTime;

    if (msSinceLastTick < MIN_INPUT_POLL_FREQUENCY_MS) {
//      return;
    }
    lastTickTime = timeNow;

    switchControlContextsIfNecessary();
    if (screenContextBindingApplier != null) {
      handleDeviceInput();
      for (Iterator<VirtualInputAction> actionIter = screenContextBindingApplier.getInputActions();
          actionIter.hasNext(); ) {
        actionIter.next().perform();
      }
      VirtualMouse.INSTANCE.flushMouseMovements();
    }
  }

  public void postGuiRender() {
    if (screenContextBindingApplier != null
        && screenContextBindingApplier.getRenderRunnable() != null) {
      screenContextBindingApplier.getRenderRunnable().run();
    }
  }

  private void switchControlContextsIfNecessary() {
    if (screenContextBindingApplier == null
        || screenContextBindingApplier.getScreenContext() != getCurrentScreenContext()) {
      if (screenContextBindingApplier != null) {
        ControllerMod.getLogger()
            .info(
                "Unloading screen control context "
                    + screenContextBindingApplier.getScreenContext());
        screenContextBindingApplier.onUnload(config);
      }
      VirtualKeyboard.INSTANCE.resetKeyState();
      screenContextBindingApplier = controlContextRegistry.get(getCurrentScreenContext());
      if (screenContextBindingApplier != null) {
        ControllerMod.getLogger()
            .info(
                "Loading screen control context " + screenContextBindingApplier.getScreenContext());
        screenContextBindingApplier.onLoad(config);
      }
    }
  }

  private void handleDeviceInput() {
    deviceManager.poll();
    screenContextBindingApplier.processButtonUpdates(
        deviceManager
            .getButtonChanges()
            .stream()
            .map(
                (buttonChange) -> {
                  return new ButtonStateUpdate(
                      deviceXInputButtonToProtoXInputButton(buttonChange.button),
                      buttonChange.wasJustPressed
                          ? PressState.IS_BECOMING_PRESSED
                          : PressState.IS_BECOMING_UNPRESSED);
                })
            .collect(ImmutableList.toImmutableList()));
    screenContextBindingApplier.processAxisUpdates(
        deviceManager
            .getAxisValues()
            .stream()
            .map(
                (axisValue) -> {
                  return new AxisValueUpdate(
                      deviceXInputAxisToProtoXInputAxis(axisValue.axis), axisValue.value);
                })
            .collect(ImmutableList.toImmutableList()));
  }

  private static ConfigurationProto.XInputButton deviceXInputButtonToProtoXInputButton(
      XInputButton button) {
    switch (button) {
      case A:
        return ConfigurationProto.XInputButton.A;
      case B:
        return ConfigurationProto.XInputButton.B;
      case X:
        return ConfigurationProto.XInputButton.X;
      case Y:
        return ConfigurationProto.XInputButton.Y;
      case BACK:
        return ConfigurationProto.XInputButton.BACK;
      case START:
        return ConfigurationProto.XInputButton.START;
      case LEFT_SHOULDER:
        return ConfigurationProto.XInputButton.LEFT_SHOULDER;
      case RIGHT_SHOULDER:
        return ConfigurationProto.XInputButton.RIGHT_SHOULDER;
      case LEFT_THUMBSTICK:
        return ConfigurationProto.XInputButton.LEFT_THUMBSTICK;
      case RIGHT_THUMBSTICK:
        return ConfigurationProto.XInputButton.RIGHT_THUMBSTICK;
      case DPAD_UP:
        return ConfigurationProto.XInputButton.DPAD_UP;
      case DPAD_DOWN:
        return ConfigurationProto.XInputButton.DPAD_DOWN;
      case DPAD_LEFT:
        return ConfigurationProto.XInputButton.DPAD_LEFT;
      case DPAD_RIGHT:
        return ConfigurationProto.XInputButton.DPAD_RIGHT;
      case GUIDE_BUTTON:
        return ConfigurationProto.XInputButton.GUIDE_BUTTON;
      default:
        return ConfigurationProto.XInputButton.UNKNOWN_BUTTON;
    }
  }

  private static ConfigurationProto.XInputAxis deviceXInputAxisToProtoXInputAxis(XInputAxis axis) {
    switch (axis) {
      case LEFT_THUMBSTICK_X:
        return ConfigurationProto.XInputAxis.LEFT_THUMBSTICK_X;
      case LEFT_THUMBSTICK_Y:
        return ConfigurationProto.XInputAxis.LEFT_THUMBSTICK_Y;
      case RIGHT_THUMBSTICK_X:
        return ConfigurationProto.XInputAxis.RIGHT_THUMBSTICK_X;
      case RIGHT_THUMBSTICK_Y:
        return ConfigurationProto.XInputAxis.RIGHT_THUMBSTICK_Y;
      case LEFT_TRIGGER:
        return ConfigurationProto.XInputAxis.LEFT_TRIGGER;
      case RIGHT_TRIGGER:
        return ConfigurationProto.XInputAxis.RIGHT_TRIGGER;
      case DPAD:
        return ConfigurationProto.XInputAxis.DPAD;
      default:
        return ConfigurationProto.XInputAxis.UNKNOWN_AXIS;
    }
  }

  public ScreenContext getCurrentScreenContext() {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.currentScreen == null) {
      return ScreenContext.IN_GAME;
    } else {
      return ScreenContext.MENU;
    }
  }
}
