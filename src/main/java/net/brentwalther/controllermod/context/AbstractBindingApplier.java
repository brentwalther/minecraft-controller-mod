package net.brentwalther.controllermod.context;

import com.google.common.collect.ImmutableMultimap;
import net.brentwalther.controllermod.binding.AxisBinding;
import net.brentwalther.controllermod.binding.ButtonBinding;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractBindingApplier implements BindingApplier {

  private final Queue<VirtualInputAction> inputActions = new ConcurrentLinkedQueue<>();
  private ImmutableMultimap<XInputButton, ButtonBinding> buttonBindings = ImmutableMultimap.of();
  private ImmutableMultimap<XInputAxis, AxisBinding> axisBindings = ImmutableMultimap.of();
  private Set<ButtonBinding> pressedButtonToUnpressOnUnload = new HashSet<>();

  @Override
  public void setBindings(
      ImmutableMultimap<XInputButton, ButtonBinding> buttonBindings,
      ImmutableMultimap<XInputAxis, AxisBinding> axisBindings) {
    this.buttonBindings = buttonBindings;
    this.axisBindings = axisBindings;
  }

  @Override
  public void processButtonUpdates(List<ButtonStateUpdate> updates) {
    // Process each of the button updates
    for (ButtonStateUpdate update : updates) {
      // If the button has any bindings, we grab them and apply the update to each.
      if (buttonBindings.containsKey(update.button)) {
        Iterable<ButtonBinding> bindings = buttonBindings.get(update.button);
        for (ButtonBinding binding : bindings) {
          // If the update is a button down press, we need to stash the binding because the binding
          // could cause the context to change and unload this one. If that happens, we want to make
          // sure that the binding has a chance to perform it's reciprocal button up constants.
          if (update.state == PressState.IS_BECOMING_PRESSED) {
            pressedButtonToUnpressOnUnload.add(binding);
          } else if (update.state == PressState.IS_BECOMING_UNPRESSED) {
            if (!pressedButtonToUnpressOnUnload.contains(binding)) {
              // Since this button was not already recorded as pressed, we apply the updated state
              // to the binding. This commonly happens when the context switches and a button is
              // released in the new context but was pressed in the old context.
              break;
            }
            pressedButtonToUnpressOnUnload.remove(binding);
          }
          for (VirtualInputAction action : binding.update(update.state)) {
            inputActions.offer(action);
          }
        }
      }
    }
  }

  @Override
  public void processAxisUpdates(List<AxisValueUpdate> updates) {
    for (AxisValueUpdate update : updates) {
      if (axisBindings.containsKey(update.axis)) {
        Iterable<AxisBinding> bindings = axisBindings.get(update.axis);
        for (AxisBinding binding : bindings) {
          for (VirtualInputAction action : binding.update(update.value)) {
            inputActions.offer(action);
          }
        }
      }
    }
  }

  @Override
  public Iterator<VirtualInputAction> getInputActions() {
    return new Iterator<VirtualInputAction>() {
      @Override
      public boolean hasNext() {
        return !inputActions.isEmpty();
      }

      @Override
      public VirtualInputAction next() {
        return inputActions.poll();
      }
    };
  }

  @Override
  public void onUnload(Configuration config) {
    // Before we unload and switch binding appliers, we want to go ahead and unpress buttons that
    // were pressed in this context but didn't have a chance to become unpressed while still in
    // this context.
    for (ButtonBinding bindingToUnpress : pressedButtonToUnpressOnUnload) {
      bindingToUnpress.update(PressState.IS_BECOMING_UNPRESSED);
    }
    pressedButtonToUnpressOnUnload.clear();
  }
}
