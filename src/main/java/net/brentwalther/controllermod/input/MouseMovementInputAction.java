package net.brentwalther.controllermod.input;

public class MouseMovementInputAction implements VirtualInputAction {

  private final float dx;
  private final float dy;

  public MouseMovementInputAction(float dx, float dy) {
    this.dx = dx;
    this.dy = dy;
  }

  @Override
  public void perform() {
    VirtualMouse.INSTANCE.moveMouse(Math.round(dx), Math.round(dy));
    //    mouse.setMousePosition(mouse.getMousePosition().moveMouseBy());
  }
}
