package sc.plugin2013;

import sc.plugin2013.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** Container Klasse welche bis zu 3 Züge beherbergt
 * @author fdu
 *
 */
@XStreamAlias(value = "cartagena:move")
public class MoveContainer {
	final public Move	firstMove;
	final public Move 	secondMove;
	final public Move 	thirdMove;
	
	public MoveContainer(){
		this.firstMove = null;
		this.secondMove = null;
		this.thirdMove = null;
	}
	
	public MoveContainer(Move first){
		this.firstMove 	= first;
		this.secondMove = null;
		this.thirdMove 	= null;
	}
	
	public MoveContainer(Move first, Move second){
		this.firstMove 	= first;
		this.secondMove = second;
		this.thirdMove 	= null;
	}
	
	public MoveContainer(Move first, Move second, Move third){
		this.firstMove	= first;
		this.secondMove = second;
		this.thirdMove 	= third;
	}

	public void perform(GameState gameState, Player expectedPlayer) throws InvalidMoveException{
		if(firstMove != null){
			firstMove.perform(gameState,expectedPlayer);
		}
		
		if(secondMove != null){
			secondMove.perform(gameState,expectedPlayer);
		}
		
		if(thirdMove != null){
			thirdMove.perform(gameState,expectedPlayer);
		}
		
	}

}
