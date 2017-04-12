import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {

	ThirtyFiveElementArray game;
	String myPlayer;
	Move myMove;
	
	public GameEngine(String p) {
		game = ThirtyFiveElementArray.initialState();
		myPlayer = p;
	}
	
	public void updateGameAfterOpponentMove(String opponentMove) {
		String opponent = opponentMove.substring(5, 10);
		ArrayList<Integer> locations = new ArrayList<Integer>();
		String temp = opponentMove.substring(10, opponentMove.length());
		int indexOfCloseParen = temp.indexOf(')');
		while (indexOfCloseParen != -1) {
			int loc = Move.rowColToSamuel(temp.substring(indexOfCloseParen - 4, indexOfCloseParen + 1));
			locations.add(loc);
			temp = temp.substring(indexOfCloseParen + 1, temp.length());
			indexOfCloseParen = temp.indexOf(')');
		}
		Move m = new Move(opponent, locations, game.chipAtLocation(locations.get(0)));
		game = (ThirtyFiveElementArray)game.result(m);
	}
	
	public void updateGameAfterMyMove() {
		game = (ThirtyFiveElementArray)game.result(myMove);
	}
	
	public String getMove() {
		// as of right now, returns a random move
		List<Move> moves = game.actions();
		if (moves.size() == 0) {
			if (game.player().equals(myPlayer)) {
				System.out.println("GAME OVER! LOSS!");
			} else {
				System.out.println("GAME OVER! WIN!");
			}
			return "GAME OVER";
		}
		int randomNum = ThreadLocalRandom.current().nextInt(0, moves.size()); // random integer
		myMove = moves.get(randomNum);
		return myMove.serverString();
	}
	
}
