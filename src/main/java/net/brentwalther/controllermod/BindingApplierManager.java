package net.brentwalther.controllermod;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.applier.BindControlApplier;
import net.brentwalther.controllermod.applier.BindingApplier;
import net.brentwalther.controllermod.applier.BindingApplier.AxisValueUpdate;
import net.brentwalther.controllermod.applier.BindingApplier.ButtonStateUpdate;
import net.brentwalther.controllermod.applier.InGameBindingApplier;
import net.brentwalther.controllermod.applier.MenuBindingApplier;
import net.brentwalther.controllermod.applier.ModSettingsBindingsApplier;
import net.brentwalther.controllermod.binding.BindingFactory;
import net.brentwalther.controllermod.binding.BindingManager;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.device.DeviceManager;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.input.VirtualKeyboard;
import net.brentwalther.controllermod.input.VirtualMouse;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.MenuPointer;
import net.brentwalther.controllermod.ui.screen.BindControlScreen;
import net.brentwalther.controllermod.ui.screen.ControllerSettingsScreen;
import net.brentwalther.controllermod.util.Conversions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The primary guts of the mod. The {@link BindingApplierManager} handles ticks and will: (1) check
 * the current screen and load the correct {@link BindingApplier}, (2) poll the active input device
 * and get control updates, (3) pass those updates to the binding applier, (4) apply all the {@link
 * VirtualInputAction}s (mouse and keyboard) that came from the applied bindings, and finally (5)
 * run any post-render runnables for that the current binding applier cares about.
 */
public class BindingApplierManager {

  /** The minimum time we would wait between poll update cycles. */
  private static final long MIN_INPUT_POLL_FREQUENCY_MS = 1000 / 60; // For at least 60fps updates

  /**
   * A mapping of screens to the BindingApplier that should handle device applier while on that
   * screen.
   */
  private final Map<ScreenContext, BindingApplier> controlContextRegistry;

  private final BindingManager bindingManager;
  private final Configuration config;
  private final DeviceManager deviceManager;
  private final MenuPointer pointer;
  private long lastTickTime;
  private BindingApplier bindingApplier;

  public BindingApplierManager(Configuration config) {
    this.config = config;
    this.lastTickTime = Minecraft.getSystemTime();
    this.deviceManager = DeviceManager.createNew();
    this.pointer = new MenuPointer();
    this.bindingManager = new BindingManager(config, new BindingFactory(config, this.pointer));
    this.controlContextRegistry = new HashMap<>();
    registerApplier(new InGameBindingApplier());
    registerApplier(new MenuBindingApplier(pointer));
    registerApplier(new ModSettingsBindingsApplier(pointer, deviceManager, bindingManager));
    registerApplier(new BindControlApplier(pointer, bindingManager.getNewControlBindingConsumer()));
  }

  public void registerApplier(BindingApplier applier) {
    controlContextRegistry.put(applier.getScreenContext(), applier);
  }

  //  /**
  //    * TODO(brentwalther): figure out how to test type parameter values and also see if it could
  //    * be possible to inject the constructor with the correct binding map given it's
  //    * ScreenContext (maybe modify the enum to make you specify implementing class?). If we did
  //    * that, we could stop needing to call setBindings().
  //    */
  //  public void registerApplier(Class contextClass) {
  //    if (!BindingApplier.class.isAssignableFrom(contextClass)) {
  //      ControllerMod.disableMod(
  //          "You attempted to register a control applier that didn't implement
  // BindingApplier.");
  //    }
  //    Constructor<BindingApplier>[] constructors =
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
    if (bindingApplier != null) {
      bindingApplier.setBindings(
          bindingManager.getButtonBindsForContext(bindingApplier.getScreenContext()),
          bindingManager.getAxisBindsForContext(bindingApplier.getScreenContext()));
      handleDeviceInput();
      for (Iterator<VirtualInputAction> actionIter = bindingApplier.getInputActions();
          actionIter.hasNext(); ) {
        actionIter.next().perform();
      }
      VirtualMouse.INSTANCE.flushMouseMovements();
    }
  }

  public void postGuiRender() {
    if (bindingApplier != null && bindingApplier.getRenderRunnable() != null) {
      bindingApplier.getRenderRunnable().run();
    }
  }

  private void switchControlContextsIfNecessary() {
    if (bindingApplier == null || bindingApplier.getScreenContext() != getCurrentScreenContext()) {
      if (bindingApplier != null) {
        ControllerMod.getLogger()
            .info("Unloading screen control applier " + bindingApplier.getScreenContext());
        bindingApplier.onUnload(config);
      }
      VirtualKeyboard.INSTANCE.resetKeyState();
      bindingApplier = controlContextRegistry.get(getCurrentScreenContext());
      if (bindingApplier != null) {
        ControllerMod.getLogger()
            .info("Loading screen control applier " + bindingApplier.getScreenContext());
        bindingApplier.onLoad(config);
      }
    }
  }

  private void handleDeviceInput() {
    deviceManager.poll();
    bindingApplier.processButtonUpdates(
        deviceManager
            .getButtonChanges()
            .stream()
            .map(
                (buttonChange) ->
                    new ButtonStateUpdate(
                        Conversions.deviceXInputButtonToProtoXInputButton(buttonChange.button),
                        buttonChange.wasJustPressed
                            ? PressState.IS_BECOMING_PRESSED
                            : PressState.IS_BECOMING_UNPRESSED))
            .collect(ImmutableList.toImmutableList()));
    bindingApplier.processAxisUpdates(
        deviceManager
            .getAxisValues()
            .stream()
            .map(
                (axisValue) ->
                    new AxisValueUpdate(
                        Conversions.deviceXInputAxisToProtoXInputAxis(axisValue.axis),
                        axisValue.value))
            .collect(ImmutableList.toImmutableList()));
  }

  public ScreenContext getCurrentScreenContext() {
    GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
    if (currentScreen == null) {
      return ScreenContext.IN_GAME;
    } else if (currentScreen instanceof BindControlScreen) {
      return ScreenContext.BIND_KEY;
    } else if (currentScreen instanceof GuiControls
        || currentScreen instanceof ControllerSettingsScreen) {
      return ScreenContext.MOD_SETTINGS;
    } else {
      return ScreenContext.MENU;
    }
  }
}
