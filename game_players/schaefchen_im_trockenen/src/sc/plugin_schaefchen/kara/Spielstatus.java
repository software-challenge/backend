package sc.plugin_schaefchen.kara;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sc.plugin_schaefchen.Die;
import sc.plugin_schaefchen.Flower;
import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.Move;
import sc.plugin_schaefchen.Node;
import sc.plugin_schaefchen.Sheep;

/**
 * Ein Kara Frontend welche den aktuellen Spielstatus darstellt 
 * und eine einfache Handhabung in deutscher Sprache erlaubt.
 * 
 * @author and
 */
public class Spielstatus {
	private GameState state;
	private Spieler own;
	private Spieler opp;
	private LinkedList<Schaf> schafe;
	private LinkedList<Blume> blumen;
	private LinkedList<Integer> wuerfel;
	private int[] wuerfelArr;
	private Map<Integer,Feld> felder;
	
	/**
	 * Erzeugt eine Instanz des Kara Frontends für das übergebene GameState Objekt
	 * 
	 * @param state
	 * 		Der Game State für den ein Kara Frontend erzeugt werden soll
	 */
	public Spielstatus(GameState state){
		this.state = state;
		own = new Spieler(state.getCurrentPlayer());
		opp = new Spieler(state.getOtherPlayer());
		felder = new HashMap<Integer, Feld>();
		for (Node n : state.getNodes()) {
			felder.put(n.index, new Feld(n,this));
		}
		schafe = new LinkedList<Schaf>();
		for (Sheep s : state.getSheeps()){
			schafe.add(new Schaf(s,(s.owner == own.getPlayer().getPlayerColor() ? own : opp),s.getNode(),this));
		}
		blumen = new LinkedList<Blume>(); 
		for(Flower f : state.getAllFlowers()) blumen.add(new Blume(f,felder.get(f.node)));
		wuerfel = new LinkedList<Integer>();
		for(Die d : state.getDice()) wuerfel.add(d.value);
	}
	
	/**
	 * Gibt zurück in welcher Runde sich das Spiel gerade befindet
	 */
	public int holeRundenzahl(){
		return state.getTurn();
	}
	
	/**
	 * Gibt den eigenen Spieler zurück
	 */
	public Spieler holeEigenenSpieler(){
		return own;
	}
	
	/**
	 * Gibt den gegnerischen Spieler zurück
	 */
	public Spieler holeGegnerischenSpieler(){
		return opp;
	}
	
	/**
	 * Gibt den letzten gemachtern Zug zurück
	 */
	public Zug holeLetztenZug(){
		return new Zug(state.getLastMove(),this);
	}
	
	/**
	 * Gibt das Feld mit dem angegebenen Index zurück
	 */
	public Feld holeFeldMitIndex(int index){
		return felder.get(index);
	}
	
	/**
	 * Gibt alle Schafe zurück
	 */
	public List<Schaf> holeSchafe(){
		return schafe;
	}
	
	/**
	 * Gibt das Schaf mit dem angegebenen Index zurück
	 */
	public Schaf holeSchafMitIndex(int index){
		Schaf schaf = null;
		for(Schaf s : schafe){
			if(s.holeIndex() == index){
				schaf = s;
			}
		}
		return schaf;
	}
	
	/**
	 * Gibt die eigenen Schafe zurück
	 */
	public List<Schaf> holeEigeneSchafe(){
		return holeSchafeVonSpieler(own);
	}
	
	/**
	 * Gibt die gegnerischen Schafe zurück
	 */
	public List<Schaf> holeGegnerischeSchafe(){
		return holeSchafeVonSpieler(opp);
	}
	
	/**
	 * Gibt die Schafe des angegebenen Spielers zurück
	 */
	public List<Schaf> holeSchafeVonSpieler(Spieler spieler){
		LinkedList<Schaf> sheeps = new LinkedList<Schaf>();
		for (Schaf schaf : schafe) {
			if(schaf.holeHalter() == spieler){
				sheeps.add(schaf);
			}
		}
		return sheeps;
	}
	
	/**
	 * Liefert alle Schafe auf einem Feld
	 */
	public List<Schaf> holeSchafeAufFeld(Feld f){
		return holeSchafeAufFeld(f.holeIndex());
	}
	
	/**
	 * Liefert alle Schafe auf einem Feld
	 */
	public List<Schaf> holeSchafeAufFeld(int position){
		LinkedList<Schaf> sheeps = new LinkedList<Schaf>();
		for (Schaf schaf : schafe) {
			if(schaf.holePosition().holeIndex() == position) sheeps.add(schaf);
		}
		return sheeps;
	}
	
	/**
	 * Liefert Schaf mit angegeben Index zurück, ansonsten Null
	 */
	public Schaf holeSchaf(int index){
		Schaf sheep = null;
		for (Schaf schaf : schafe) {
			if(schaf.holeIndex() == index){
				sheep = schaf;
				break;
			}
		}
		return sheep;
	}
	
	/**
	 * Liefert die Liste alle Blumen
	 */
	public List<Blume> holeBlumen(){
		return blumen;
	}
	
	/**
	 * Liefert die Blume(n) auf dem Feld
	 */
	public Blume holeBlumeAufFeld(Feld f){
		return holeBlumeAufFeld(f.holeIndex());
	}
	
	/**
	 * Liefert die Blume(n) auf dem Feld mit dem gegebenen Index
	 * gibt es dort keine Blume, so wird null zurückgegeben
	 */
	public Blume holeBlumeAufFeld(int index){
		for (Blume blume : blumen) {
			if(blume.holeFeld().holeIndex() == index) return blume;
		}
		return null;
	}
	
	/**
	 * Gibt die Anzahl der im Spiel befindlichen Blumen zurück
	 */
	public int holeBlumenAnzahl(){
		int anzahl = 0;
		for(Blume blume : blumen) anzahl+=blume.holeAnzahl();
		return anzahl;
	}
	
	/**
	 * Liefert eine Liste der verfügbaren Würfel
	 */
	public List<Integer> holeVerfuegbareWuerfel(){
		return wuerfel;
	}
	
	/**
	 * Liefert die verfügbaren Wuerfel als Array
	 */
	public int[] holeVerfuegbareWuerfelArray(){
		int[] arr = new int[wuerfel.size()];
		for (int i = 0; i < wuerfel.size(); i++) {
			arr[i] = wuerfel.get(i).intValue();
		}
		return arr;
	}
	
	/**
	 * Liefert verfügbare Wuerfel als int Array 
	 * a[0] = 0 => 0 Wuerfel mit Wert 0 verfügbar
	 * ...
	 * a[5] = 2 => 2 Wuerfel mit Wert 5 verfügbar
	 * 
	 * Kann verwendet werden um schnell nachzuschlagen!
	 */
	public int[] holeWuerfelArray(){
		if(wuerfelArr == null){
			wuerfelArr = new int[7];
			for (Integer i : wuerfel) {
				wuerfelArr[i]++;
			}
		}
		return wuerfelArr;
	}
	
	/**
	 * Gibt den hoechten verfuegbaren Wuerfelwert zurueck 
	 */
	public int holeHoechstenWuerfel(){
		int highest = 0;
		for (Integer i : wuerfel) {
			if(highest < i) highest = i;
		}
		return highest;
	}
	
	/**
	 * Gibt den eigenen Status zurueck
	 * [0] hat gewonnen?
	 * [1] Schafe im Spiel
     * [2] gefangene Schafe
     * [3] gestohlene Schafe
     * [4] gesammelte Blumen
     * [5] gefressene Blumen
     * [6] Punkte
	 */
	public int[] holeEigenenStatus(){
		return holeSpielerStatus(own);
	}
	
	/**
	 * Gibt den gegnerischen Status zurueck
	 * [0] hat gewonnen?
	 * [1] Schafe im Spiel
     * [2] gefangene Schafe
     * [3] gestohlene Schafe
     * [4] gesammelte Blumen
     * [5] gefressene Blumen
     * [6] Punkte
	 */
	public int[] holeGegnerStatus(){
		return holeSpielerStatus(opp);
	}
	
	/**
	 * Gibt den Status des angegebenen Spielers zurueck
	 */
	public int[] holeSpielerStatus(Spieler spieler){
		return state.getPlayerStats(spieler.getPlayer());
	}
	
	/**
	 * Gibt eigenen Namen zurueck
	 */
	public String holeEigenenNamen(){
		return state.getCurrentPlayer().getDisplayName();
	}
	
	/**
	 * Gibt gegnerischen Namen zurueck
	 */
	public String holeGegnerNamen(){
		return state.getOtherPlayer().getDisplayName();
	}
	
	/**
	 * Gibt zurueck ob der Zug des Schafes mit dem angegeben Index auf das 
	 * Feld mit dem gegebenen Index moeglich ist
	 */
	public boolean istZugMoeglich(int schafIndex, int zielIndex){
		return istZugMoeglich(holeSchafMitIndex(schafIndex), holeFeldMitIndex(zielIndex));
	}
	
	/**
	 * Gibt zurueck ob der Zug des Schafes mit dem angegeben Index auf das 
	 * gegebene Feld moeglich ist
	 */
	public boolean istZugMoeglich(int schafIndex, Feld ziel){
		return istZugMoeglich(holeSchafMitIndex(schafIndex), ziel);
	}
	
	/**	 
	 * Gibt zurueck ob der Zug des gegebenen Schafes auf das Feld 
	 * mit dem gegebenen Index moeglich ist
	 */
	public boolean istZugMoeglich(Schaf schaf, int zielIndex){
		return istZugMoeglich(schaf, holeFeldMitIndex(zielIndex));
	}
	
	/**
	 * Gibt zurueck ob der Zug des gegebenen Schafes auf das 
	 * gegebene Feld moeglich ist
	 */
	public boolean istZugMoeglich(Schaf schaf, Feld ziel){
		return state.isValidTarget(schaf.getSheep(), ziel.getNode());
	}
	
	/**
	 * Gibt zurueck ob der Zug moeglich ist
	 */
	public boolean istZugMoeglich(Zug zug){
		return istZugMoeglich(zug.holeSchaf(), zug.holeZiel());
	}
	
	/**
	 * Gibt die von einem Schaf erreichbaren Knoten zurueck
	 */
	public Set<Integer> holeErreichbareFelder(Schaf schaf){
		return holeErreichbareZieleVonFeld(schaf.holePosition());
	}
	
	/**
	 * Gibt die von einem Feld erreichbaren Felder zurueck
	 */
	public Set<Integer> holeErreichbareZieleVonFeld(Feld feld){
		return state.getReacheableNodes(feld.getNode());
	}
	
	/**
	 * Gibt die von dem gegebenen Schaf mit den aktuell verfuegbaren
	 * Wuerfeln erreichbaren Felder zurueck
	 */
	public Set<Integer> holeAktuellErreichbareFelder(Schaf schaf){
		return state.getValidReacheableNodes(schaf.getSheep());
	}
	
	/**
	 * Gibt die von dem gegebenen Schaf mit den aktuell verfuegbaren
	 * Wuerfeln erreichbaren Felder zurueck
	 */
	public int[] holeAktuellErreichbareFelderArray(Schaf schaf){
		Set<Integer> valids = holeAktuellErreichbareFelder(schaf);
		int[] validArr = new int[valids.size()];
		int i = 0;
		for (Integer val : valids){
			validArr[i] = val;
			i++;
		}
		return validArr;
	}
	
	/**
	 * Hole alle gueltige Züge fuer gegebenen Spieler
	 */
	public List<Zug> holeMoeglicheZuegeFuerSpieler(Spieler spieler){
		List<Move> moves = state.getValidMoves(spieler.getPlayer().getPlayerColor());
		List<Zug> zuege = new LinkedList<Zug>();
		for (Move move : moves) {
			zuege.add(new Zug(move,this));
		}
		return zuege;
	}
	
	/**
	 * Holt alle eigenen moeglichen Zuege
	 */
	public List<Zug> holeMoeglicheZuege(){
		return holeMoeglicheZuegeFuerSpieler(own);
	}
}
