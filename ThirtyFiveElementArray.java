import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// Trevor Phillips

public class ThirtyFiveElementArray implements CheckersGameState {
	
	// 35-element array
	// a space (' ') at index i indicates a blank location
	// a b at index i indicates a black chip
	// a B at index i indicates a black king
	// a w at index i indicates a red chip
	// a W at index i indicates a red king
	private char[] locations;
	private static List<Integer> invalidLocations = new LinkedList<Integer>(Arrays.asList(9, 18, 27));
	private String player;
	
	public ThirtyFiveElementArray() {
		// given size 36 so we can refer to indices as they are shown in diagrams (rather than starting at 0)
		locations = new char[36];
		player = "???";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ThirtyFiveElementArray)) return false;
		ThirtyFiveElementArray other = (ThirtyFiveElementArray)o;
		if (!player.equals(other.player)) return false;
		for (int i = 0; i < locations.length; i++) {
			if (!(locations[i] == other.locations[i])) return false;
		}
		return true;
	}
	
	// returns the starting configuration of any checkers game
	public static ThirtyFiveElementArray initialState() {
		ThirtyFiveElementArray initial = new ThirtyFiveElementArray();
		initial.player = new String(PLAYER1);
		for (int i = 1; i <= 13; i++) {
			if (validLocation(i)) {
				initial.locations[i] = 'b'; // set initial locations of black chips
			}
		}
		for (int i = 14; i <= 22; i++) {
			if (validLocation(i)) {
				initial.locations[i] = ' '; // set empty locations
			}
		}
		for (int i = 23; i <= 35; i++) {
			if (validLocation(i)) {
				initial.locations[i] = 'w'; // set initial locations of red chips
			}
		}
		return initial;
	}
	
	/* ********** HELPER METHODS ********** */
	
	// checks that the location is within bounds
	private static boolean validLocation(int loc) {
		if (loc < 1 || loc > 35) return false;
		if (invalidLocations.contains(loc)) return false;
		return true;
	}
	
	// true if the chip character is a king
	private boolean isKing(char chip) {
		return chip == 'B' || chip == 'W';
	}
	
	private boolean canMoveForward(char chip) { return player.equals(PLAYER1) || isKing(chip); }
	private boolean canMoveBackward(char chip) { return player.equals(PLAYER2) || isKing(chip); }
	
	// true if the chip character passed in belongs to the player who's move it is
	private boolean isCurrentPlayersChip(char chip) {
		// player 1's turn (black) and chip is black
		if (player.equals(PLAYER1) && (chip == 'b' || chip == 'B')) return true;
		// player 2's turn (red) and chip is red
		if (player.equals(PLAYER2) && (chip == 'w' || chip == 'W')) return true;
		return false;
	}
	
	// true if the chip character passed in belongs to the player who's move it ISN'T
	private boolean isOpponentPlayersChip(char chip) {
		// player 1's turn (black) and chip is red
		if (player.equals(PLAYER1) && (chip == 'w' || chip == 'W')) return true;
		// player 2's turn (red) and chip is black
		if (player.equals(PLAYER2) && (chip == 'b' || chip == 'B')) return true;
		return false;
	}
	
	private boolean shouldKing(String player, char chip, int location) {
		// do not king if it is already a king
		if (!validLocation(location) || isKing(chip)) return false;
		// black player has reached the other end of the board and should be kinged
		if (player.equals(PLAYER1) && location >= 32 && location <= 35) return true;
		// red player has reached the other end of the board and should be kinged
		if (player.equals(PLAYER2) && location >= 1 && location <= 4) return true;
		return false;
	}
	
	private boolean canJump(int start, int jumped, int finish) {
		if (!validLocation(start) || !validLocation(jumped) || !validLocation(finish)) return false;
		if (!canMoveForward(locations[start]) && (jumped > start || finish > start)) return false;
		if (!canMoveBackward(locations[start]) && (jumped < start || finish < start)) return false;
		return isCurrentPlayersChip(locations[start]) && isOpponentPlayersChip(locations[jumped]) && locations[finish] == ' ';
	}
	
	// returns a game state configured exactly like the current one, but a different object
	// that way the clone can be modified without modifying the original state (used in result() method)
	private ThirtyFiveElementArray cloneMe() {
		ThirtyFiveElementArray duplicate = new ThirtyFiveElementArray();
		for (int i = 0; i < locations.length; i++) {
			duplicate.locations[i] = locations[i];
		}
		duplicate.player = new String(player);
		return duplicate;
	}
	
	private void makeKing(int location) {
		if (!validLocation(location)) return;
		if (locations[location] == 'b') locations[location] = 'B';
		else if (locations[location] == 'w') locations[location] = 'W';
	}
	
	// standard move involving no jumps (can be 1 step in any of the 4 diagonals)
	private Move standardMove(int start, int finish) {
		if (validLocation(finish) && locations[finish] == ' ') {
			ArrayList<Integer> moves = new ArrayList<Integer>(Arrays.asList(start, finish));
			return new Move(player, start, finish, null, moves, shouldKing(player, locations[start], finish));
		}
		return null;
	}
	
	private LinkedList<Move> simpleJumps(int location) {
		int[][] relativeJumps = new int[][] {
				// start,   removed chip, finish location
				{ location, location + 4, location + 8 },
				{ location, location + 5, location + 10 },
				{ location, location - 4, location - 8 },
				{ location, location - 5, location - 10 }
		};
		
		LinkedList<Move> jumps = new LinkedList<Move>();
		// test jumping over 1 chip in the 4 diagonal directions
		for (int[] jump : relativeJumps) {
			// test if this jump is actually possible
			if (canJump(jump[0], jump[1], jump[2])) {
				// if yes, we remove the chip at location[1]
				ArrayList<Integer> removed = new ArrayList<Integer>(Arrays.asList(jump[1]));
				ArrayList<Integer> moves = new ArrayList<Integer>(Arrays.asList(jump[0], jump[2]));
				boolean kinged = shouldKing(player, locations[jump[0]], jump[2]);
				// create the jump move
				Move m = new Move(player, jump[0], jump[2], removed, moves, kinged);
				jumps.add(m);
			}
		}
		return jumps;
	}
	
	private Move traceback(SearchNode node) {
		// simple properties to determine about this sequence of jump moves
		int endingLocation = node.fromParentToCurrent.toLocation;
		String player = node.current.player();
		// trace back to find the original chip location and all removed chips
		int startingLocation = node.fromParentToCurrent.fromLocation;
		boolean kinged = node.fromParentToCurrent.movedChipBecomesKing;
		ArrayList<Integer> removed = new ArrayList<Integer>();
		ArrayList<Integer> locations = new ArrayList<Integer>(Arrays.asList(endingLocation));
		for (Integer removedLoc : node.fromParentToCurrent.removedChips) {
			removed.add(removedLoc);
		}
		SearchNode temp = node.parent;
		while (temp != null && temp.fromParentToCurrent != null) {
			startingLocation = temp.fromParentToCurrent.fromLocation;
			locations.add(0, temp.fromParentToCurrent.toLocation);
			kinged = kinged || temp.fromParentToCurrent.movedChipBecomesKing;
			for (Integer removedLoc : temp.fromParentToCurrent.removedChips) {
				removed.add(removedLoc);
			}
			temp = temp.parent;
		}
		locations.add(0, startingLocation);
		return new Move(player, startingLocation, endingLocation, removed, locations, kinged);
	}
	
	private LinkedList<Move> jumpMoves(int location) {
		// DFS to determine all possible sequences of jump moves from a chip's given location
		LinkedList<Move> jumps = new LinkedList<Move>();
		
		SearchNode root = new SearchNode(null, this, null, true);
		ArrayList<SearchNode> stack = new ArrayList<SearchNode>();
		LinkedList<Move> initialJumps = simpleJumps(location);
		for (Move jump : initialJumps) {
			ThirtyFiveElementArray result = (ThirtyFiveElementArray)this.result(jump);
			result.player = new String(player); // do not reverse the player in this special case
			SearchNode node = new SearchNode(root, result, jump, false);
			stack.add(node);
		}
		while (!stack.isEmpty()) {
			SearchNode v = stack.remove(0);
			if (!v.visited) {
				v.visited = true;
				ThirtyFiveElementArray currentState = (ThirtyFiveElementArray)v.current;
				int newLocationOfJumpingChip = v.fromParentToCurrent.toLocation;
				LinkedList<Move> adjacencies = currentState.simpleJumps(newLocationOfJumpingChip);
				if (adjacencies.size() == 0) {
					// the last jump resulted in a terminal state allowing no more jumps
					jumps.add(traceback(v));
				} else {
					// add nodes for the DFS
					for (Move jump : adjacencies) {
						ThirtyFiveElementArray result = (ThirtyFiveElementArray)currentState.result(jump);
						result.player = new String(currentState.player); // do not reverse the player
						SearchNode node = new SearchNode(v, result, jump, false);
						stack.add(0, node);
					}
				}
			}
		}
		
		return jumps;
	}
		
	/* ********** CheckersGameState IMPLEMENTATION METHODS ********** */
	
	public String player() {
		return player;
	}
	
	public List<Move> actions() {
		List<Move> actions = new LinkedList<Move>();
		List<Move> requiredMoves = new LinkedList<Move>();
		for (int i = 1; i <= 35; i++) {
			if (validLocation(i)) {
				char chip = locations[i];
				if (!isCurrentPlayersChip(chip)) continue;
				if (canMoveForward(chip)) {
					Move forwardLeft = standardMove(i, i + 4);
					Move forwardRight = standardMove(i, i + 5);
					if (forwardLeft != null) actions.add(forwardLeft);
					if (forwardRight != null) actions.add(forwardRight);
				}
				if (canMoveBackward(chip)) {
					Move backwardLeft = standardMove(i, i - 5);
					Move backwardRight = standardMove(i, i - 4);
					if (backwardLeft != null) actions.add(backwardLeft);
					if (backwardRight != null) actions.add(backwardRight);
				}
				LinkedList<Move> jumps = jumpMoves(i);
				if (jumps.size() > 0) {
					actions.addAll(jumps);
					requiredMoves.addAll(jumps);
				}
			}
		}
		if (requiredMoves.size() > 0) {
			actions.clear();
			// if one or more jumps are possible, one of them must be made
			// a standard non-jumping move cannot be made in this case
			for (Move action : requiredMoves) actions.add(action);
		}
		return actions;
	}
	
	public CheckersGameState result(Move x) {
		ThirtyFiveElementArray newState = cloneMe();
		
		// test for invalid moves...
		if (!x.playerMakingMove.equals(player)) return null; // player cannot make a move if it's not his turn
		int fromLocationIndex = x.fromLocation;
		int toLocationIndex = x.toLocation;
		if (player.equals(PLAYER1)) {
			// Black's move, so chip at 'fromLocationIndex' must be either 'b' or 'B'
			if (!(locations[fromLocationIndex] == 'b' || locations[fromLocationIndex] == 'B')) return null;
		} else {
			// Red's move, so chip at 'fromLocationIndex' must be either 'w' or 'W'
			if (!(locations[fromLocationIndex] == 'w' || locations[fromLocationIndex] == 'W')) return null;
		}
		
		// validated move, so update the newState to reflect changes
		char chipBeingMoved = newState.locations[fromLocationIndex];
		newState.locations[fromLocationIndex] = ' '; // old location is now empty
		newState.locations[toLocationIndex] = chipBeingMoved; // new location gets the chip
		// change chip to capital letter if it needs to be kinged
		if (x.movedChipBecomesKing) newState.makeKing(toLocationIndex);
		// turn locations where chips were removed into blank locations
		for (Integer removedLocation : x.removedChips) {
			newState.locations[removedLocation] = ' ';
		}
		// reverse who's move it is
		newState.player = newState.player.equals(PLAYER1) ? new String(PLAYER2) : new String(PLAYER1);
		return newState;
	}
	
	public void printState() {
		String boardRepresentation = "********************\n";
		int count = 0;
		boolean leadingBlank = true;
		for (int i = 1; i <= 35; i++) {
			if (invalidLocations.contains(i)) continue;
			if (count == 4) {
				boardRepresentation += "\n";
				leadingBlank = !leadingBlank;
				count = 0;
			}
			if (leadingBlank) boardRepresentation += "- " + locations[i] + " ";
			else boardRepresentation += locations[i] + " - ";
			count++;
		}
		boardRepresentation += "\n";
		System.out.println(boardRepresentation + player + "'s move\n********************");
	}
}

