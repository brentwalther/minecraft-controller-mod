package net.brentwalther.controllermod.ui;

/**
 * An overlay that is meant to be drawn over the current GUI on the screen until it is "expired."
 */
public interface GuiOverlayWithExpiration {
  /** @return true if the overlay is expired and should no longer be drawn. */
  boolean isExpired();

  /** Draw the overlay. Only called if {@link #isExpired} returns false. */
  void drawOverlay();
}
