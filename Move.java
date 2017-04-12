import java.util.ArrayList;

public class Move {

	String playerMakingMove; // either 'Black' or 'White'
	int fromLocation; // the current location of the piece being moved
	int toLocation; // the location the piece being moved will end up after this move
	ArrayList<Integer> removedChips; // ArrayList of locations containing chips that will be removed by this move
	ArrayList<Integer> locations; // starts from 'fromLocation' and ends at 'toLocation' but includes all intermediary jumps
	boolean movedChipBecomesKing; // true if, after this move, the chip being moved should be crowned
	
	private static final String[] locationConversions = new String[] {
		"ERROR", // location 0 invalid
		"(7:1)", // location 1
		"(7:3)", // location 2
		"(7:5)", // location 3
		"(7:7)", // location 4
		"(6:0)", // location 5
		"(6:2)", // location 6
		"(6:4)", // location 7
		"(6:6)", // location 8
		"ERROR", // location 9 invalid
		"(5:1)", // location 10
		"(5:3)", // location 11
		"(5:5)", // location 12
		"(5:7)", // location 13
		"(4:0)", // location 14
		"(4:2)", // location 15
		"(4:4)", // location 16
		"(4:6)", // location 17
		"ERROR", // location 18 invalid
		"(3:1)", // location 19
		"(3:3)", // location 20
		"(3:5)", // location 21
		"(3:7)", // location 22
		"(2:0)", // location 23
		"(2:2)", // location 24
		"(2:4)", // location 25
		"(2:6)", // location 26
		"ERROR", // location 27 invalid
		"(1:1)", // location 28
		"(1:3)", // location 29
		"(1:5)", // location 30
		"(1:7)", // location 31
		"(0:0)", // location 32
		"(0:2)", // location 33
		"(0:4)", // location 34
		"(0:6)", // location 35
	};
	
	public Move(String player, int from, int to, ArrayList<Integer> removed, ArrayList<Integer> series, boolean kinged) {
		playerMakingMove = player;
		fromLocation = from;
		toLocation = to;
		removedChips = new ArrayList<Integer>();
		if (removed != null) {
			for (int i : removed) removedChips.add(i);
		}
		locations = new ArrayList<Integer>();
		if (series != null) {
			for (int i : series) locations.add(i);
		}
		movedChipBecomesKing = kinged;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Move)) return false;
		Move other = (Move)o;
		
		if (!playerMakingMove.equals(other.playerMakingMove)) return false;
		if (movedChipBecomesKing != other.movedChipBecomesKing || removedChips.size() != other.removedChips.size()) return false;
		if (locations.size() != other.locations.size()) return false;
		if (fromLocation != other.fromLocation || toLocation != other.toLocation) return false;
		for (int i = 0; i < removedChips.size(); i++) {
			int a1 = removedChips.get(i);
			int a2 = other.removedChips.get(i);
			if (a1 != a2) return false;
		}
		for (int i = 0; i < locations.size(); i++) {
			int a1 = locations.get(i);
			int a2 = other.locations.get(i);
			if (a1 != a2) return false;
		}
		return true;
	}
	
	public String toString() {
		String str = "'" + playerMakingMove + "' is moving a piece from location ";
		str += locationConversions[fromLocation] + " to location " + locationConversions[toLocation];
		if (movedChipBecomesKing) str += " - (chip became king)";
		if (removedChips.size() == 0) return str;
		str += "\nChips were removed from the following location(s): ";
		for (Integer location : removedChips) str += locationConversions[location] + ", ";
		str = str.substring(0, str.length() - 2); // remove extra comma and space characters
		return str;
	}
	
	public String serverString() {
		String str = "";
		for (int i : locations) str += locationConversions[i] + ":";
		str = str.substring(0, str.length() - 1); // remove last colon
		return str;
	}
	
}