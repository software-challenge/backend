package sc.plugin2014.laylogic;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;

/**
 * Diese Klasse überprüft Steinreihen auf Regelkonformität.
 * 
 * @author ffi
 * 
 */
public class BasicShapeAndColorChecker {
	public static void checkValidColorOrShape(List<Stone> stonesRow)
			throws InvalidMoveException {
		if (!checkValidColorInRow(stonesRow)
				&& !checkValidShapeInRow(stonesRow)) {
			throw new InvalidMoveException("Keine valide Reihe"); // TODO add
																	// row desc
		}
	}

	private static boolean checkValidColorInRow(List<Stone> stonesRow) {
		List<StoneColor> seenColors = new ArrayList<StoneColor>();
		List<StoneShape> seenShapes = new ArrayList<StoneShape>();

		for (Stone stone : stonesRow) {
			if (!checkForSameColor(seenColors, stone.getColor())
					|| !checkForDifferentShapes(seenShapes, stone.getShape())) {
				return false;
			}
		}

		return true;
	}

	private static boolean checkForSameColor(List<StoneColor> seenColors,
			StoneColor stoneColor) {
		if (!seenColors.contains(stoneColor)) {
			seenColors.add(stoneColor);
			if (seenColors.size() > 1) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkForDifferentShapes(List<StoneShape> seenShapes,
			StoneShape stoneShape) {
		if (seenShapes.contains(stoneShape)) {
			return false;
		} else {
			seenShapes.add(stoneShape);
		}
		return true;
	}

	private static boolean checkValidShapeInRow(List<Stone> stonesRow) {
		List<StoneShape> seenShapes = new ArrayList<StoneShape>();
		List<StoneColor> seenColors = new ArrayList<StoneColor>();

		for (Stone stone : stonesRow) {
			if (!checkForSameShape(seenShapes, stone.getShape())
					|| !checkForDifferentColors(seenColors, stone.getColor())) {
				return false;
			}
		}

		return true;
	}

	private static boolean checkForSameShape(List<StoneShape> seenShapes,
			StoneShape stoneShape) {
		if (!seenShapes.contains(stoneShape)) {
			seenShapes.add(stoneShape);
			if (seenShapes.size() > 1) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkForDifferentColors(List<StoneColor> seenColors,
			StoneColor stoneColor) {
		if (seenColors.contains(stoneColor)) {
			return false;
		} else {
			seenColors.add(stoneColor);
		}
		return true;
	}
}
