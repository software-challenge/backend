package sc.plugin2019;

import sc.shared.PlayerColor;

public enum FieldState {
  RED,
  BLUE,
  OBSTRUCTED,
  EMPTY;
  public static FieldState from(PlayerColor color) {
    if(color == PlayerColor.RED)
      return RED;
    else
      return BLUE;
  }
}
