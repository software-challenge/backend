package shared.sc.plugin2015.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import shared.sc.plugin2015.BuildMove;
import shared.sc.plugin2015.GameState;
import shared.sc.plugin2015.Move;
import shared.sc.plugin2015.MoveType;
import shared.sc.plugin2015.Player;
import shared.sc.plugin2015.PlayerColor;
import shared.sc.plugin2015.SelectMove;
import shared.sc.plugin2015.Tower;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class GameStateConverter implements Converter {

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return clazz.equals(GameState.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

		GameState gameState = (GameState) value;

		writer.addAttribute("turn", Integer.toString(gameState.getTurn()));
		writer.addAttribute("start", gameState.getStartPlayerColor().toString().toLowerCase());
		writer.addAttribute("current", gameState.getCurrentPlayerColor().toString().toLowerCase());
		writer.addAttribute("type", gameState.getCurrentMoveType().toString().toLowerCase());

		writer.startNode("red");
		context.convertAnother(gameState.getRedPlayer());
		writer.endNode();
		writer.startNode("blue");
		context.convertAnother(gameState.getBluePlayer());
		writer.endNode();

		List<Tower> towers = gameState.getTowers();
		for (Tower tower : towers) {
			if (tower.getHeight() > 0) {
				writer.startNode("tower");
				writer.addAttribute("city", Integer.toString(tower.city));
				writer.addAttribute("slot", Integer.toString(tower.slot));
				writer.addAttribute("red", Integer.toString(tower.getRedParts()));
				writer.addAttribute("blue", Integer.toString(tower.getBlueParts()));
				writer.addAttribute("owner", tower.getOwner().toString().toLowerCase());
				writer.endNode();
			}
		}

		Move move = gameState.getLastMove();
		if (move != null) {
			writer.startNode("move");
			writer.addAttribute("type", move.getMoveType() == MoveType.BUILD ? "build" : "select");
			context.convertAnother(move);
			writer.endNode();
		}

		if (gameState.gameEnded()) {
			writer.startNode("condition");
			String winner = (gameState.winner() == null) ? "none" : gameState.winner() + "";
			writer.addAttribute("winner", winner);
			writer.addAttribute("reason", gameState.winningReason());
			writer.endNode();
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		GameState gameState = new GameState(true);

		try {

			Field field = GameState.class.getDeclaredField("turn");
			field.setAccessible(true);
			field.set(gameState, Integer.parseInt(reader.getAttribute("turn")));
			field.setAccessible(false);

			field = GameState.class.getDeclaredField("startPlayer");
			field.setAccessible(true);
			field.set(gameState, PlayerColor.valueOf(reader.getAttribute("start").toUpperCase()));
			field.setAccessible(false);

			field = GameState.class.getDeclaredField("currentPlayer");
			field.setAccessible(true);
			field.set(gameState, PlayerColor.valueOf(reader.getAttribute("current").toUpperCase()));
			field.setAccessible(false);

			field = GameState.class.getDeclaredField("currentMoveType");
			field.setAccessible(true);
			field.set(gameState, MoveType.valueOf(reader.getAttribute("type").toUpperCase()));
			field.setAccessible(false);

			Field towerField = GameState.class.getDeclaredField("towers");
			towerField.setAccessible(true);
			List<Tower> towers = (ArrayList<Tower>) towerField.get(gameState);
			towerField.setAccessible(false);

			while (reader.hasMoreChildren()) {

				reader.moveDown();

				String nodeName = reader.getNodeName();
				if (nodeName.equals("red") || nodeName.equals("blue")) {
					Player player = (Player) context.convertAnother(gameState, Player.class);
					PlayerColor playerColor = nodeName.equals("red") ? PlayerColor.RED : PlayerColor.BLUE;

					Field playerField = GameState.class.getDeclaredField(nodeName);
					playerField.setAccessible(true);
					playerField.set(gameState, player);
					playerField.setAccessible(false);

					Field colorField = Player.class.getDeclaredField("color");
					colorField.setAccessible(true);
					colorField.set(player, playerColor);
					colorField.setAccessible(false);
				} else if (nodeName.equals("tower")) {
					int city = Integer.parseInt(reader.getAttribute("city"));
					int slot = Integer.parseInt(reader.getAttribute("slot"));
					int red = Integer.parseInt(reader.getAttribute("red"));
					int blue = Integer.parseInt(reader.getAttribute("blue"));
					PlayerColor owner = PlayerColor.valueOf(reader.getAttribute("owner").toUpperCase());

					Tower tower = towers.get(city * Constants.SLOTS + slot);
					if (owner == PlayerColor.RED) {
						tower.addPart(PlayerColor.BLUE, blue);
						tower.addPart(PlayerColor.RED, red);
					} else {
						tower.addPart(PlayerColor.RED, red);
						tower.addPart(PlayerColor.BLUE, blue);
					}
				} else if (nodeName.equals("move")) {
					MoveType moveType = MoveType.valueOf(reader.getAttribute("type").toUpperCase());
					Move move;

					if (moveType == MoveType.SELECT) {
						move = (Move) context.convertAnother(gameState, SelectMove.class);
					} else {
						move = (Move) context.convertAnother(gameState, BuildMove.class);
					}

					Field moveField = GameState.class.getDeclaredField("lastMove");
					moveField.setAccessible(true);
					moveField.set(gameState, move);
					moveField.setAccessible(false);

				} else if (nodeName.equals("condition")) {
					PlayerColor winner;
					try {
						winner = PlayerColor.valueOf(reader.getAttribute("winner").toUpperCase());
					} catch (IllegalArgumentException ex) {
						winner = null;
					}
					String reason = reader.getAttribute("reason");
					gameState.endGame(winner, reason);
				}

				reader.moveUp();

			}

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return gameState;

	}
}
