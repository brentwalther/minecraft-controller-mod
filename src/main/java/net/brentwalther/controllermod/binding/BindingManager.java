package net.brentwalther.controllermod.binding;

import com.google.common.base.Objects;
import com.google.common.collect.*;
import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.proto.ConfigurationProto.BindingType;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.AxisThreshold;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BindingManager {

  private final Configuration config;
  private final BindingFactory bindingFactory;

  private final Map<ScreenContext, Multimap<XInputButton, ButtonBinding>> buttonBindingsByContext;
  private final Map<ScreenContext, Multimap<XInputAxis, AxisBinding>> axisBindingsByContext;
  private final Map<XInputAxis, Float> axisThresholds;
  private final Map<ControlBindingMapKey, ControlBinding> bindingMap;

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
    this.bindingMap = new HashMap<>();
    applyBindings();
  }

  public Consumer<ControlBinding> getNewControlBindingConsumer() {
    return controlBinding -> {
      // Convert to a set and back to ensure we don't introduce duplicate bindings.
      bindingMap.put(new ControlBindingMapKey(controlBinding), controlBinding);
      config.commitToMemory(
          config
              .get()
              .toBuilder()
              .clearCustomBinding()
              .addAllCustomBinding(bindingMap.values())
              .build());
      config.commitToDisk();
      applyBindings();
    };
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

  public ImmutableList<ControlBinding> getBindings() {
    return ImmutableList.copyOf(this.bindingMap.values());
  }

  /**
   * Takes all the bindings in {@link this.bindings} and applies them to the bindings by context
   * maps. Any new bindings will be added and any modified ones will be overwritten.
   */
  private void applyBindings() {
    this.bindingMap.clear();
    Streams.concat(DEFAULT_BINDINGS.stream(), config.get().getCustomBindingList().stream())
        .forEach((binding) -> bindingMap.put(new ControlBindingMapKey(binding), binding));
    for (ControlBinding binding : this.bindingMap.values()) {
      switch (binding.getControlCase()) {
        case AXIS:
          if (!axisBindingsByContext.containsKey(binding.getScreenContext())) {
            axisBindingsByContext.put(binding.getScreenContext(), ArrayListMultimap.create());
          }
          ControllerMod.getLogger()
              .info("Adding binding for " + binding.getScreenContext() + " " + binding.getAxis());
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

  private static ImmutableList<ControlBinding> DEFAULT_BINDINGS =
      ImmutableList.<ControlBinding>builder()
          .add(
              makeAxisBinding(
                  ScreenContext.IN_GAME, XInputAxis.LEFT_THUMBSTICK_X, BindingType.STRAFE))
          .add(
              makeAxisBinding(
                  ScreenContext.IN_GAME, XInputAxis.LEFT_THUMBSTICK_Y, BindingType.WALK))
          .add(
              makeAxisBinding(
                  ScreenContext.IN_GAME, XInputAxis.RIGHT_THUMBSTICK_X, BindingType.CAMERA_X))
          .add(
              makeAxisBinding(
                  ScreenContext.IN_GAME, XInputAxis.RIGHT_THUMBSTICK_Y, BindingType.CAMERA_Y))
          .add(
              makeAxisBinding(
                  ScreenContext.IN_GAME, XInputAxis.LEFT_TRIGGER, BindingType.USE_ITEM_PLACE_BLOCK))
          .add(
              makeAxisBinding(
                  ScreenContext.IN_GAME, XInputAxis.RIGHT_TRIGGER, BindingType.ATTACK_DESTROY))
          .add(
              makeButtonBinding(ScreenContext.IN_GAME, XInputButton.START, BindingType.TOGGLE_MENU))
          .add(
              makeButtonBinding(
                  ScreenContext.IN_GAME, XInputButton.Y, BindingType.OPEN_CLOSE_INVENTORY))
          .add(
              makeButtonBinding(
                  ScreenContext.IN_GAME, XInputButton.X, BindingType.SWAP_ITEM_IN_HANDS))
          .add(makeButtonBinding(ScreenContext.IN_GAME, XInputButton.A, BindingType.JUMP))
          .add(
              makeButtonBinding(
                  ScreenContext.IN_GAME,
                  XInputButton.LEFT_SHOULDER,
                  BindingType.SWITCH_SELECTED_ITEM_LEFT))
          .add(
              makeButtonBinding(
                  ScreenContext.IN_GAME,
                  XInputButton.RIGHT_SHOULDER,
                  BindingType.SWITCH_SELECTED_ITEM_RIGHT))
          .add(
              makeAxisBinding(
                  ScreenContext.MENU, XInputAxis.LEFT_THUMBSTICK_X, BindingType.POINTER_X))
          .add(
              makeAxisBinding(
                  ScreenContext.MENU, XInputAxis.LEFT_THUMBSTICK_Y, BindingType.POINTER_Y))
          .add(makeButtonBinding(ScreenContext.MENU, XInputButton.B, BindingType.TOGGLE_MENU))
          .add(makeButtonBinding(ScreenContext.MENU, XInputButton.A, BindingType.MENU_CLICK))
          .add(
              makeButtonBinding(
                  ScreenContext.MENU, XInputButton.LEFT_SHOULDER, BindingType.MENU_SCROLL_UP))
          .add(
              makeButtonBinding(
                  ScreenContext.MENU, XInputButton.RIGHT_SHOULDER, BindingType.MENU_SCROLL_DOWN))
          .add(
              makeAxisBinding(
                  ScreenContext.MOD_SETTINGS, XInputAxis.LEFT_THUMBSTICK_X, BindingType.POINTER_X))
          .add(
              makeAxisBinding(
                  ScreenContext.MOD_SETTINGS, XInputAxis.LEFT_THUMBSTICK_Y, BindingType.POINTER_Y))
          .add(
              makeButtonBinding(
                  ScreenContext.MOD_SETTINGS, XInputButton.B, BindingType.TOGGLE_MENU))
          .add(
              makeButtonBinding(ScreenContext.MOD_SETTINGS, XInputButton.A, BindingType.MENU_CLICK))
          .add(
              makeButtonBinding(
                  ScreenContext.MOD_SETTINGS,
                  XInputButton.LEFT_SHOULDER,
                  BindingType.MENU_SCROLL_UP))
          .add(
              makeButtonBinding(
                  ScreenContext.MOD_SETTINGS,
                  XInputButton.RIGHT_SHOULDER,
                  BindingType.MENU_SCROLL_DOWN))
          .build();

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

  private static class ControlBindingMapKey {
    private final ControlBinding binding;

    public ControlBindingMapKey(ControlBinding binding) {
      this.binding = binding;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof ControlBindingMapKey)) {
        return false;
      }
      ControlBindingMapKey that = (ControlBindingMapKey) other;
      return Objects.equal(this.binding.getScreenContext(), that.binding.getScreenContext())
          && Objects.equal(this.binding.getType(), that.binding.getType());
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(this.binding.getScreenContext(), this.binding.getType());
    }
  }
}
