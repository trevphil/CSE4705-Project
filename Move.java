import java.util.ArrayList;

public class Move {

	String playerMakingMove; // either 'Black' or 'White'
	int fromLocation; // the current location of the piece being moved
	int toLocation; // the location the piece being moved will end up after this move
	ArrayList<Integer> removedChips; // ArrayList of locations containing chips that will be removed by this move
	boolean movedChipBecomesKing; // true if, after this move, the chip being moved should be crowned
	
	public Move(String player, int from, int to, ArrayList<Integer> removed, boolean kinged) {
		playerMakingMove = player;
		fromLocation = from;
		toLocation = to;
		removedChips = new ArrayList<Integer>();
		if (removed != null) {
			for (int i : removed) removedChips.add(i);
		}
		movedChipBecomesKing = kinged;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Move)) return false;
		Move other = (Move)o;
		
		if (!playerMakingMove.equals(other.playerMakingMove)) return false;
		if (movedChipBecomesKing != other.movedChipBecomesKing || removedChips.size() != other.removedChips.size()) return false;
		if (fromLocation != other.fromLocation || toLocation != other.toLocation) return false;
		for (int i = 0; i < removedChips.size(); i++) {
			Integer a1 = removedChips.get(i);
			Integer a2 = other.removedChips.get(i);
			if (a1 != a2) return false;
		}
		return true;
	}
	
	public String toString() {
		String str = "'" + playerMakingMove + "' is moving a piece from location ";
		str += fromLocation + " to location " + toLocation;
		if (movedChipBecomesKing) str += " (chip became king)";
		if (removedChips.size() == 0) return str;
		str += "\nChips were removed from the following location(s): ";
		for (Integer location : removedChips) str += location + ", ";
		str = str.substring(0, str.length() - 2); // remove extra comma and space characters
		return str;
	}
	
}