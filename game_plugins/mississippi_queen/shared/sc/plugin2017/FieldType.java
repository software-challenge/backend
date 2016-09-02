package sc.plugin2017;

public enum FieldType {
	/**
	 * Wasserfeld, auf ihm kann sich normal bewegt werden
	 */
	WATER,
	/**
	 * Inselfeld, es kann nicht überwunden werden und kein Spieler kann darauf stehen
	 */
	BLOCKED,
	/**
	 * Passagierfeld mit Anleger in Richtung 0
	 */
	PASSENGER0,
	/**
   * Passagierfeld mit Anleger in Richtung 1
   */
  PASSENGER1,
  /**
   * Passagierfeld mit Anleger in Richtung 2
   */
  PASSENGER2,
  /**
   * Passagierfeld mit Anleger in Richtung 3
   */
  PASSENGER3,
  /**
   * Passagierfeld mit Anleger in Richtung 4
   */
  PASSENGER4,
  /**
   * Passagierfeld mit Anleger in Richtung 5
   */
  PASSENGER5,
	/**
	 * Ein Zielfeld
	 */
	GOAL,
	/**
   * Ein Sandbankfeld
   */
  SANDBANK,
  /**
   * Ein Feld mit einem Baumstamm, der weggerammt werden muss um es zu passieren
   */
  LOG;

  public boolean isPassenger() {
    return equals(PASSENGER0) || equals(PASSENGER1) || equals(PASSENGER2) || equals(PASSENGER3) || equals(PASSENGER4) || equals(PASSENGER5);
  }
}
