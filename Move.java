import java.util.ArrayList;
import java.util.HashMap;

public class Move {

	private String playerMakingMove; // either 'Black' or 'White'
	private ArrayList<Integer> locations; // starts from the starting location of 'chip', contains each following location 'chip' moves to
	private char chip;
	private static HashMap<String, Integer> rowColToSamuel;
	private static final String[] samuelToRowCol = new String[] {
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
	
	public Move(String player, ArrayList<Integer> locs, char c) {
		playerMakingMove = player;
		locations = new ArrayList<Integer>();
		for (int l : locs) locations.add(l);
		chip = c;
		initHashMap();
	}
	
	private static void initHashMap() {
		HashMap<String, Integer> rowColToSamuel = new HashMap<String, Integer>();
		rowColToSamuel.put("(7:1)", 1);
		rowColToSamuel.put("(7:3)", 2);
		rowColToSamuel.put("(7:5)", 3);
		rowColToSamuel.put("(7:7)", 4);
		rowColToSamuel.put("(6:0)", 5);
		rowColToSamuel.put("(6:2)", 6);
		rowColToSamuel.put("(6:4)", 7);
		rowColToSamuel.put("(6:6)", 8);
		rowColToSamuel.put("(5:1)", 10);
		rowColToSamuel.put("(5:3)", 11);
		rowColToSamuel.put("(5:5)", 12);
		rowColToSamuel.put("(5:7)", 13);
		rowColToSamuel.put("(4:0)", 14);
		rowColToSamuel.put("(4:2)", 15);
		rowColToSamuel.put("(4:4)", 16);
		rowColToSamuel.put("(4:6)", 17);
		rowColToSamuel.put("(3:1)", 19);
		rowColToSamuel.put("(3:3)", 20);
		rowColToSamuel.put("(3:5)", 21);
		rowColToSamuel.put("(3:7)", 22);
		rowColToSamuel.put("(2:0)", 23);
		rowColToSamuel.put("(2:2)", 24);
		rowColToSamuel.put("(2:4)", 25);
		rowColToSamuel.put("(2:6)", 26);
		rowColToSamuel.put("(1:1)", 28);
		rowColToSamuel.put("(1:3)", 29);
		rowColToSamuel.put("(1:5)", 30);
		rowColToSamuel.put("(1:7)", 31);
		rowColToSamuel.put("(0:0)", 32);
		rowColToSamuel.put("(0:2)", 33);
		rowColToSamuel.put("(0:4)", 34);
		rowColToSamuel.put("(0:6)", 35);
		Move.rowColToSamuel = rowColToSamuel;
	}
	
	public ArrayList<Integer> removedLocations() {
		ArrayList<Integer> removed = new ArrayList<Integer>();
		for (int i = 0; i < locations.size() - 1; i++) {
			int move = locations.get(i);
			int next = locations.get(i + 1);
			if (Math.abs(move - next) > 5) removed.add((move + next) / 2);
		}
		return removed;
	}
	
	public String player() { return playerMakingMove; }
	public char chip() { return chip; }
	public int firstLocation() { return locations.get(0); }
	public int lastLocation() { return locations.get(locations.size() - 1); }
	public static String samuelToRowCol(int loc) { return Move.samuelToRowCol[loc]; }
	public static int rowColToSamuel(String rowCol) { 
		if (Move.rowColToSamuel == null) initHashMap();
		return Move.rowColToSamuel.get(rowCol); 
	}
	
	public boolean isCrowned() {
		if (chip == 'W' || chip == 'B') return false;
		if (playerMakingMove.equals(CheckersGameState.PLAYER1)) {
			for (int l : locations) if (l >= 32 && l <= 35) return true;
		} else if (playerMakingMove.equals(CheckersGameState.PLAYER2)) {
			for (int l : locations) if (l >= 1 && l <= 4) return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Move)) return false;
		Move other = (Move)o;
		
		if (!playerMakingMove.equals(other.playerMakingMove) || locations.size() != other.locations.size()) return false;
		if (isCrowned() != other.isCrowned()) return false;
		for (int i = 0; i < locations.size(); i++) {
			int a1 = locations.get(i);
			int a2 = other.locations.get(i);
			if (a1 != a2) return false;
		}
		ArrayList<Integer> rm1 = removedLocations();
		ArrayList<Integer> rm2 = other.removedLocations();
		if (rm1.size() != rm2.size()) return false;
		for (int i = 0; i < rm1.size(); i++) {
			if (rm1.get(i) != rm2.get(i)) return false;
		}
		
		return true;
	}
	
	public String toString() {
		String str = "'" + playerMakingMove + "' is moving a piece in this sequence: " + serverString();
		if (isCrowned()) str += " - (chip became king)";
		ArrayList<Integer> removed = removedLocations();
		if (removed.size() == 0) return str;
		str += "\nChips were removed from the following location(s): ";
		for (Integer location : removed) str += Move.samuelToRowCol[location] + ", ";
		str = str.substring(0, str.length() - 2); // remove extra comma and space characters
		return str;
	}
	
	public String serverString() {
		String str = "";
		for (int i : locations) str += Move.samuelToRowCol[i] + ":";
		str = str.substring(0, str.length() - 1); // remove last colon
		return str;
	}
	
}