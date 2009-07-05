package sc.plugin2010.framework;

import sc.plugin2010.util.GameUtil;

/**
 * Eine Werkzeug Klasse, um nützliche und häufig gebrauchte Funktionen zur
 * Verfügung zu stellen
 */
public class Werkzeuge
{
	/**
	 * berechnet die benötigte Karottenanzahl, die man haben muss, um
	 * <code>zugAnzahl</code> Fehler weiterzuziehen
	 * 
	 * @param zugAnzahl
	 *            die Anzahl an Feldern, die man nach vorne möchte
	 * @return Karottenanzahl, welche für diesen Zug benötigt wird
	 */
	public static int berechneBenoetigeKarotten(int zugAnzahl)
	{
		return GameUtil.calculateCarrots(zugAnzahl);
	}
}
