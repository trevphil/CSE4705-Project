import java.util.*;
import java.io.*;

public class GameEngine {

	ThirtyFiveElementArray game;
	String myPlayer;
	Move myMove;
	
	public static double MY_PIECES = 1.0;
	public static int MY_PIECES_N = 1;
	public static double MY_KINGS = 1.0;
	public static int MY_KINGS_N = 1;
	public static double OPPONENT_PIECES = 1.0;
	public static int OPPONENT_PIECES_N = 1;
	public static double OPPONENT_KINGS = 1.0;
	public static int OPPONENT_KINGS_N = 1;
	
	public GameEngine(String p) {
		game = ThirtyFiveElementArray.initialState();
		myPlayer = p;
		loadWeights();
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
		
		int indexOfBestMove = 0;
		double valueOfBestMove = -1;
		for (int i = 0; i < moves.size(); i++) {
			double resultingGameValue = ((ThirtyFiveElementArray)game.result(moves.get(i))).evaluate(myPlayer);
			if (resultingGameValue > valueOfBestMove) indexOfBestMove = i;
		}
		return moves.get(indexOfBestMove).serverString();
	}
	
	private void loadWeights() {
		try {
			File weights = new File("weights.txt");
			Scanner scan = new Scanner(weights);
			String[] parts = scan.nextLine().split(" ");
			MY_PIECES = Double.parseDouble(parts[1]);
			MY_PIECES_N = Integer.parseInt(parts[2]);
			parts = scan.nextLine().split(" ");
			MY_KINGS = Double.parseDouble(parts[1]);
			MY_KINGS_N = Integer.parseInt(parts[2]);
			parts = scan.nextLine().split(" ");
			OPPONENT_PIECES = Double.parseDouble(parts[1]);
			OPPONENT_PIECES_N = Integer.parseInt(parts[2]);
			parts = scan.nextLine().split(" ");
			OPPONENT_KINGS = Double.parseDouble(parts[1]);
			OPPONENT_KINGS_N = Integer.parseInt(parts[2]);
			scan.close();
		} catch (FileNotFoundException e) {
			File file = new File("weights.txt");
			try {
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.println("MY_PIECES 1.0 1");
				writer.println("MY_KINGS 1.0 1");
				writer.println("OPPONENT_PIECES 1.0 1");
				writer.println("OPPONENT_KINGS 1.0 1");
				writer.close();
				loadWeights();
			} catch (Exception e2) { }
		}
	}
	
}
