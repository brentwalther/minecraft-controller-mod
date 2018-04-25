package net.brentwalther.controllermod.input;

public class ScrollInputAction implements VirtualInputAction {

  private final int delta;

  public ScrollInputAction(int delta) {
    this.delta = delta;
  }
  @Override
  public void perform() {
    VirtualMouse.INSTANCE.scrollWheel(delta);
  }
}
