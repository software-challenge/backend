package sc.player2018;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.player2018.logic.RandomLogic;
import sc.plugin2018.IGameHandler;

/**
 * Erlaubt es verschiedene Logiken zu verwenden und eine davon auszuwählen und
 * Instanzen dieser Logik zu erzeugen
 */
public enum LogicFactory {
  // Verfügbare Taktiken (Implementierungen des IGameHandler) müssen hier
  // eingetragen wie im Beispiel eingetragen und ihre Klasse angegeben werden
  RANDOM(RandomLogic::new),

  // Die Logik die gewählt wird, wenn kein passender Eintrag zu der Eingabe
  // gefunden wurde:
  DEFAULT(RandomLogic::new);

  private static final Logger logger = LoggerFactory.getLogger(LogicFactory.class);
  private final LogicBuilder builder;

  LogicFactory(LogicBuilder builder) {
    this.builder = builder;
  }

  /**
   * Erstellt eine Logik-Instanz und gibt diese zurück
   *
   * @param client Der aktuelle Client
   *
   * @return Eine Instanz der gewaehlten Logik
   *
   * @throws Exception Wenn etwas schief gelaufen ist und keine Instanz erstellt
   *                   werden konnte, wird eine Exception geworfen!
   */
  public IGameHandler getInstance(Starter client) throws Exception {
    logger.debug("Erzeuge Instanz von: {}", name());
    return builder.build(client);
  }

}
