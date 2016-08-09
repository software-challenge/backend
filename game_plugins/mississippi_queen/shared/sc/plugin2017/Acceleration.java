package sc.plugin2017;

import sc.plugin2017.util.InvalidMoveException;

public class Acceleration extends Action {

  /**
   * Gibt an um wie viel beschleuningt wird, Negative Zahl bedeutet, das entsprechend gebremst wird.
   * Darf nicht 0 sein wirf sonst InvalidMoveException beim ausführen von perform
   */
  public int acc;
  
  public Acceleration(int acc) {
    this.acc = acc;
  }
  /**
   * 
   * @param state Gamestate auf dem Beschleunigung ausgeführt wird
   * @param Spieler für den Beschleunigung ausgeführt wird
   * 
   * @return nicht relevant
   */
  @Override
  public int perform(GameState state, Player player)
    throws InvalidMoveException {

    int speed = player.getSpeed();
    speed += acc;
    if(this.acc == 0) {
      throw new InvalidMoveException("Es kann nicht um den Wert 0 beschleuning werden");
    }
    if(speed > 6 || speed < 1) {
      throw new InvalidMoveException("Geschwindigkeit zu hoch oder zu niedrig");
    }
    int usedCoal = 0;
    if(acc > 1) {
      usedCoal = acc - 1;
    } else if(acc < -1) {
      usedCoal = -acc - 1;
    }
    player.setCoal(player.getCoal() - usedCoal); // geht das hier irgendwie besser? TODO oder eher über farbe des 
    // übergebenen Spieler s machen und dann nur im state ändern
    state.getCurrentPlayer().setCoal(player.getCoal());
    player.setSpeed(speed);
    state.getCurrentPlayer().setSpeed(speed);
      
    return 0;
  }

}
