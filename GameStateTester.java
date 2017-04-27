import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

public class GameStateTester {

	public static void main(String[] args) {
		
		int numSampleMoves = 100; // Number of moves to make for each test
		
		System.out.println("\nTESTING 35-Element Representation:");
		GameState testState = GameState.initialState(); // starting config of checkers game
		testState.printState();
		for (int i = 0; i < numSampleMoves; i++) {
			List<Move> moves = testState.actions();
			if (moves.size() == 0) break;
			int randomNum = ThreadLocalRandom.current().nextInt(0, moves.size()); // random integer
			Move move = moves.get(randomNum);
			System.out.println("Random move chosen: " + move + "\nBoard state after move:");
			testState = testState.result(move);
			testState.printState();
		}

	}
}
