import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {

	ThirtyFiveElementArray game;
	String myPlayer;
	Move myMove;
	HashMap<String, Integer> serverMoveToInt;
	
	public GameEngine(String p) {
		game = ThirtyFiveElementArray.initialState();
		myPlayer = p;
		initHashMap();
	}
	
	private void initHashMap() {
		serverMoveToInt = new HashMap<String, Integer>();
		serverMoveToInt.put("(7:1)", 1);
		serverMoveToInt.put("(7:3)", 2);
		serverMoveToInt.put("(7:5)", 3);
		serverMoveToInt.put("(7:7)", 4);
		serverMoveToInt.put("(6:0)", 5);
		serverMoveToInt.put("(6:2)", 6);
		serverMoveToInt.put("(6:4)", 7);
		serverMoveToInt.put("(6:6)", 8);
		serverMoveToInt.put("(5:1)", 10);
		serverMoveToInt.put("(5:3)", 11);
		serverMoveToInt.put("(5:5)", 12);
		serverMoveToInt.put("(5:7)", 13);
		serverMoveToInt.put("(4:0)", 14);
		serverMoveToInt.put("(4:2)", 15);
		serverMoveToInt.put("(4:4)", 16);
		serverMoveToInt.put("(4:6)", 17);
		serverMoveToInt.put("(3:1)", 19);
		serverMoveToInt.put("(3:3)", 20);
		serverMoveToInt.put("(3:5)", 21);
		serverMoveToInt.put("(3:7)", 22);
		serverMoveToInt.put("(2:0)", 23);
		serverMoveToInt.put("(2:2)", 24);
		serverMoveToInt.put("(2:4)", 25);
		serverMoveToInt.put("(2:6)", 26);
		serverMoveToInt.put("(1:1)", 28);
		serverMoveToInt.put("(1:3)", 29);
		serverMoveToInt.put("(1:5)", 30);
		serverMoveToInt.put("(1:7)", 31);
		serverMoveToInt.put("(0:0)", 32);
		serverMoveToInt.put("(0:2)", 33);
		serverMoveToInt.put("(0:4)", 34);
		serverMoveToInt.put("(0:6)", 35);
	}
	
	public void updateGameAfterOpponentMove(String opponentMove) {
		String opponent = opponentMove.substring(5, 10);
		ArrayList<String> moves = new ArrayList<String>();
		String temp = opponentMove.substring(10, opponentMove.length());
		int indexOfCloseParen = temp.indexOf(')');
		while (indexOfCloseParen != -1) {
			moves.add(temp.substring(indexOfCloseParen - 4, indexOfCloseParen + 1));
			temp = temp.substring(indexOfCloseParen + 1, temp.length());
			indexOfCloseParen = temp.indexOf(')');
		}
		ArrayList<Integer> locations = new ArrayList<Integer>();
		ArrayList<Integer> removed = new ArrayList<Integer>();
		for (int i = 0; i < moves.size() - 1; i++) {
			String move = moves.get(i);
			String next = moves.get(i + 1);
			int loc1 = serverMoveToInt.get(move);
			int loc2 = serverMoveToInt.get(next);
			locations.add(loc1);
			if (Math.abs(loc1 - loc2) > 5) removed.add((loc1 + loc2) / 2);
		}
		locations.add(serverMoveToInt.get(moves.get(moves.size() - 1)));
		int from = locations.get(0);
		int to = locations.get(locations.size() - 1);
		// TODO - change kinged boolean value
		boolean kinged = false;
		Move m = new Move(opponent, from, to, removed, locations, kinged);
		game = (ThirtyFiveElementArray)game.result(m);
	}
	
	public void updateGameAfterMyMove() {
		game = (ThirtyFiveElementArray)game.result(myMove);
	}
	
	public String getMove() {
		// as of right now, returns a random move
		List<Move> moves = game.actions();
		if (moves.size() == 0) {
			System.out.println("GAME OVER! LOSS!");
			return "GAME OVER";
		}
		int randomNum = ThreadLocalRandom.current().nextInt(0, moves.size()); // random integer
		Move move = moves.get(randomNum);
		myMove = move;
		return move.serverString();
	}
	
}
