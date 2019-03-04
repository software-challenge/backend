package sc.plugin2019;

import sc.shared.PlayerColor;

import java.util.Objects;

public enum FieldState {
  RED,
  BLUE,
  OBSTRUCTED,
  EMPTY;
  public static FieldState from(PlayerColor color) {
    switch(color) {
      case RED: return RED;
      case BLUE: return BLUE;
      default: throw new IllegalArgumentException("PlayerColor can only be RED or BLUE");
    }
  }
}
