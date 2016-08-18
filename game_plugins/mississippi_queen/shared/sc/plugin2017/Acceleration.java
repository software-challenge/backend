package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "acceleration")
public class Acceleration extends Action {

  
  /**
   * Zeigt an welche Nummer die Aktion hat
   */
  @XStreamAsAttribute
  public int order;
  /**
   * Gibt an um wie viel beschleuningt wird, Negative Zahl bedeutet, das entsprechend gebremst wird.
   * Darf nicht 0 sein wirf sonst InvalidMoveException beim ausführen von perform
   */
  @XStreamAsAttribute
  public int acc;
  
  public Acceleration(int acc) {
    this.acc = acc;
  }
  
  public Acceleration(int acc, int order) {
    this.acc = acc;
    this.order = order;
  }
  /**
   * 
   * @param state Gamestate auf dem Beschleunigung ausgeführt wird
   * @param player Spieler für den Beschleunigung ausgeführt wird
   */
  @Override
  public void perform(GameState state, Player player)
    throws InvalidMoveException {

    int speed = player.getSpeed();
    speed += acc;
    if(this.acc == 0) {
      throw new InvalidMoveException("Es kann nicht um den Wert 0 beschleuning werden");
    }
    if(speed > 6 || speed < 1) {
      throw new InvalidMoveException("Geschwindigkeit zu hoch oder zu niedrig");
    }
    if(player.getField(state.getBoard()).getType() == FieldType.SANDBANK) {
      throw new InvalidMoveException("Auf einer Sandbank kann nicht beschleunigt werden.");
    }
    int usedCoal = 0;
    usedCoal = Math.abs(acc) - player.getFreeAcc();
    
    if(usedCoal > 0) {
      player.setCoal(player.getCoal() - usedCoal);
    }
    player.setSpeed(speed);
    player.setMovement(player.getMovement() + acc);
    player.setFreeAcc(0);  
    return;
  }
  
  public Acceleration clone() {
    return new Acceleration(this.acc);
  }
  
  public boolean equals(Object o) {
    if(o instanceof Acceleration) {
      return (this.acc == ((Acceleration) o).acc);
    }
    return false;
  }
  
  public String toString() {
    return "Beschleuningung um " + acc;
  }

}
