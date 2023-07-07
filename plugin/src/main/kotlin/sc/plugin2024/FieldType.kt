package sc.plugin2024


enum class FieldType {
    /**
     * Wasserfeld, auf ihm kann sich normal bewegt werden
     */
    WATER,

    /**
     * Inselfeld, es kann nicht überwunden werden und kein Spieler kann darauf stehen
     */
    BLOCKED,

    /**
     * Passagierfeld mit Anleger in Richtung 0 (rechts)
     */
    PASSENGER0,

    /**
     * Passagierfeld mit Anleger in Richtung 1 (oben rechts)
     */
    PASSENGER1,

    /**
     * Passagierfeld mit Anleger in Richtung 2 (oben links)
     */
    PASSENGER2,

    /**
     * Passagierfeld mit Anleger in Richtung 3 (links)
     */
    PASSENGER3,

    /**
     * Passagierfeld mit Anleger in Richtung 4 (unten links)
     */
    PASSENGER4,

    /**
     * Passagierfeld mit Anleger in Richtung 5 (unten rechts)
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
     * Ein Feld mit einem Baumstamm, der weggerammt werden muss, um es zu passieren
     */
    LOG,
    
    VOID;

    val isPassenger: Boolean
        get() = equals(PASSENGER0) || equals(PASSENGER1) || equals(PASSENGER2) || equals(PASSENGER3) || equals(
            PASSENGER4
        ) || equals(PASSENGER5)
}