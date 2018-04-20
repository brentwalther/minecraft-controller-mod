package net.brentwalther.controllermod.input;

public class KeyPressInputAction implements VirtualInputAction {

  private final int keycode;
  private final PressState state;

  public KeyPressInputAction(int keycode, PressState state) {
    this.keycode = keycode;
    this.state = state;
  }

  @Override
  public void perform() {
    switch (state) {
      case IS_BECOMING_PRESSED:
        VirtualKeyboard.INSTANCE.pressKey(keycode);
        break;
      case IS_BECOMING_UNPRESSED:
        VirtualKeyboard.INSTANCE.releaseKey(keycode);
        break;
    }
  }
}
