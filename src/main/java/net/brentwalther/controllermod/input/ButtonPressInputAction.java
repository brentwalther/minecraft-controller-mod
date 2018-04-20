package net.brentwalther.controllermod.input;

public class ButtonPressInputAction implements VirtualInputAction {

  public static ButtonPressInputAction create(int mouseButton, PressState state) {
    return new ButtonPressInputAction(mouseButton, state);
  }

  private final int mouseButton;
  private final PressState state;

  // Good candidate to turn into an @AutoValue when I figure out how to make
  // it work in gradle.
  private ButtonPressInputAction(int mouseButton, PressState state) {
    this.mouseButton = mouseButton;
    this.state = state;
  }

  public int mouseButton() {
    return mouseButton;
  }

  public PressState state() {
    return state;
  }
    @Override
    public void perform() {
        VirtualMouse.INSTANCE.setMouseButton(this);
    }
}
