package net.brentwalther.controllermod.binding;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.proto.ConfigurationProto.BindingType;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.AxisThreshold;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingManager {

  private final Configuration config;
  private final BindingFactory bindingFactory;

  private final Map<ScreenContext, Multimap<XInputButton, ButtonBinding>> buttonBindingsByContext;
  private final Map<ScreenContext, Multimap<XInputAxis, AxisBinding>> axisBindingsByContext;
  private final Map<XInputAxis, Float> axisThresholds;

  public BindingManager(Configuration config, BindingFactory bindingFactory) {
    this.config = config;
    this.bindingFactory = bindingFactory;

    // Initialize the default axis thresholds and then apply any custom ones from the configuration.
    this.axisThresholds = getDefaultAxisThresholds();
    for (AxisThreshold threshold : config.get().getAxisThresholdList()) {
      axisThresholds.put(threshold.getAxis(), threshold.getThreshold());
    }

    // Initialize the default bindings and then apply and custom ones from the configuration.
    this.buttonBindingsByContext = new HashMap<>();
    this.axisBindingsByContext = new HashMap<>();
    applyBindings(getDefaultBindings());
    applyBindings(config.get().getCustomBindingList());
  }

  public ImmutableMultimap<XInputButton, ButtonBinding> getButtonBindsForContext(
      ScreenContext context) {
    return ImmutableMultimap.copyOf(
        buttonBindingsByContext.getOrDefault(context, ImmutableMultimap.of()));
  }

  public ImmutableMultimap<XInputAxis, AxisBinding> getAxisBindsForContext(ScreenContext context) {
    return ImmutableMultimap.copyOf(
        axisBindingsByContext.getOrDefault(context, ImmutableMultimap.of()));
  }

  private void applyBindings(List<ControlBinding> customBindingList) {
    for (ControlBinding binding : customBindingList) {
      switch (binding.getControlCase()) {
        case AXIS:
          if (!axisBindingsByContext.containsKey(binding.getScreenContext())) {
            axisBindingsByContext.put(binding.getScreenContext(), ArrayListMultimap.create());
          }
          ControllerMod.getLogger().info("Adding binding for " + binding.getScreenContext() + " " + binding.getAxis());
          axisBindingsByContext
              .get(binding.getScreenContext())
              .put(
                  binding.getAxis(),
                  bindingFactory.getAxisBinding(
                      binding.getType(), axisThresholds.get(binding.getAxis())));
        case BUTTON:
          if (!buttonBindingsByContext.containsKey(binding.getScreenContext())) {
            buttonBindingsByContext.put(binding.getScreenContext(), ArrayListMultimap.create());
          }
          buttonBindingsByContext
              .get(binding.getScreenContext())
              .put(binding.getButton(), bindingFactory.getButtonBinding(binding.getType()));
      }
    }
  }

  private static Map<XInputAxis, Float> getDefaultAxisThresholds() {
    Map<XInputAxis, Float> thresholdMap = new HashMap<>();
    for (XInputAxis axis : XInputAxis.values()) {
      switch (axis) {
        case LEFT_THUMBSTICK_X:
        case LEFT_THUMBSTICK_Y:
        case RIGHT_THUMBSTICK_X:
        case RIGHT_THUMBSTICK_Y:
          thresholdMap.put(axis, 0.25f);
          break;
        case LEFT_TRIGGER:
        case RIGHT_TRIGGER:
        case DPAD:
          thresholdMap.put(axis, 0.5f);
      }
    }
    return thresholdMap;
  }

  private static ImmutableList<ControlBinding> getDefaultBindings() {
    ImmutableList.Builder<ControlBinding> defaultBindings = ImmutableList.builder();

    defaultBindings.add(
        makeAxisBinding(ScreenContext.IN_GAME, XInputAxis.LEFT_THUMBSTICK_X, BindingType.STRAFE));
    defaultBindings.add(
        makeAxisBinding(ScreenContext.IN_GAME, XInputAxis.LEFT_THUMBSTICK_Y, BindingType.WALK));
    defaultBindings.add(
        makeAxisBinding(
            ScreenContext.IN_GAME, XInputAxis.RIGHT_THUMBSTICK_X, BindingType.CAMERA_X));
    defaultBindings.add(
        makeAxisBinding(
            ScreenContext.IN_GAME, XInputAxis.RIGHT_THUMBSTICK_Y, BindingType.CAMERA_Y));
    defaultBindings.add(
        makeAxisBinding(
            ScreenContext.IN_GAME, XInputAxis.LEFT_TRIGGER, BindingType.USE_ITEM_PLACE_BLOCK));
    defaultBindings.add(
        makeAxisBinding(
            ScreenContext.IN_GAME, XInputAxis.RIGHT_TRIGGER, BindingType.ATTACK_DESTROY));
    defaultBindings.add(
        makeButtonBinding(ScreenContext.IN_GAME, XInputButton.START, BindingType.TOGGLE_MENU));
    defaultBindings.add(
        makeButtonBinding(ScreenContext.IN_GAME, XInputButton.Y, BindingType.OPEN_CLOSE_INVENTORY));
    defaultBindings.add(
        makeButtonBinding(ScreenContext.IN_GAME, XInputButton.X, BindingType.SWAP_ITEM_IN_HANDS));
    defaultBindings.add(makeButtonBinding(ScreenContext.IN_GAME, XInputButton.A, BindingType.JUMP));

    defaultBindings.add(
        makeAxisBinding(ScreenContext.MENU, XInputAxis.LEFT_THUMBSTICK_X, BindingType.POINTER_X));
    defaultBindings.add(
        makeAxisBinding(ScreenContext.MENU, XInputAxis.LEFT_THUMBSTICK_Y, BindingType.POINTER_Y));
    defaultBindings.add(
        makeButtonBinding(ScreenContext.MENU, XInputButton.B, BindingType.TOGGLE_MENU));
    defaultBindings.add(
        makeButtonBinding(ScreenContext.MENU, XInputButton.A, BindingType.MENU_CLICK));

    return defaultBindings.build();
  }

  private static ControlBinding makeAxisBinding(
      ScreenContext context, XInputAxis axis, BindingType type) {
    return ControlBinding.newBuilder()
        .setScreenContext(context)
        .setAxis(axis)
        .setType(type)
        .build();
  }

  private static ControlBinding makeButtonBinding(
      ScreenContext context, XInputButton button, BindingType type) {
    return ControlBinding.newBuilder()
        .setScreenContext(context)
        .setButton(button)
        .setType(type)
        .build();
  }
}
