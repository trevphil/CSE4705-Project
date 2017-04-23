import java.util.*;
import java.io.*;

public class GameEngine {

	ThirtyFiveElementArray game;
	String myPlayer;
	Move myMove;
	
	public static double MY_CENTER_PIECES = 1.0;
	public static double MY_CENTER_PIECES_N = 1;
	public static double MY_KINGS = 1.0;
	public static double MY_KINGS_N = 1;
	public static double OPPONENT_PIECES = 1.0;
	public static double OPPONENT_PIECES_N = 1;
	public static double OPPONENT_KINGS = 1.0;
	public static double OPPONENT_KINGS_N = 1;
	private static double[] initialWeights = new double[8];
			
	public GameEngine(String p) {
		game = ThirtyFiveElementArray.initialState();
		myPlayer = p;
		loadWeights();
		mutateWeights();
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
			if (resultingGameValue > valueOfBestMove) {
				indexOfBestMove = i;
				valueOfBestMove = resultingGameValue;
			}
		}
		myMove = moves.get(indexOfBestMove);
		return moves.get(indexOfBestMove).serverString();
	}
	
	private void loadWeights() {
		try {
			File weights = new File("./src/weights.txt");
			Scanner scan = new Scanner(weights);
			String[] parts = scan.nextLine().split(" ");
			MY_CENTER_PIECES = Double.parseDouble(parts[1]);
			MY_CENTER_PIECES_N = Double.parseDouble(parts[2]);
			parts = scan.nextLine().split(" ");
			MY_KINGS = Double.parseDouble(parts[1]);
			MY_KINGS_N = Double.parseDouble(parts[2]);
			parts = scan.nextLine().split(" ");
			OPPONENT_PIECES = Double.parseDouble(parts[1]);
			OPPONENT_PIECES_N = Double.parseDouble(parts[2]);
			parts = scan.nextLine().split(" ");
			OPPONENT_KINGS = Double.parseDouble(parts[1]);
			OPPONENT_KINGS_N = Double.parseDouble(parts[2]);
			initialWeights = new double[] {
				new Double(MY_CENTER_PIECES), new Double(MY_CENTER_PIECES_N),
				new Double(MY_KINGS), new Double(MY_KINGS_N),
				new Double(OPPONENT_PIECES), new Double(OPPONENT_PIECES_N),
				new Double(OPPONENT_KINGS), new Double(OPPONENT_KINGS_N)
			};
			scan.close();
		} catch (FileNotFoundException e) {
			File file = new File("./src/weights.txt");
			try {
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.println("MY_CENTER_PIECES 1.0 1");
				writer.println("MY_KINGS 1.0 1");
				writer.println("OPPONENT_PIECES 1.0 1");
				writer.println("OPPONENT_KINGS 1.0 1");
				writer.close();
				loadWeights();
			} catch (Exception e2) { }
		}
	}
	
	private void mutateWeights() {
		boolean positive = Math.random() > 0.50;
		MY_CENTER_PIECES += (positive ? 1 : -1) * Math.random() * 0.30 * MY_CENTER_PIECES;
		
		positive = Math.random() > 0.50;
		MY_KINGS += (positive ? 1 : -1) * Math.random() * 0.30 * MY_KINGS;
		
		positive = Math.random() > 0.50;
		OPPONENT_PIECES += (positive ? 1 : -1) * Math.random() * 0.30 * OPPONENT_PIECES;
		
		positive = Math.random() > 0.50;
		OPPONENT_KINGS += (positive ? 1 : -1) * Math.random() * 0.30 * OPPONENT_KINGS;
	}
	
	public void saveMutatedWeights() {
		File file = new File("./src/weights.txt");
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			String s = "MY_CENTER_PIECES " + (initialWeights[0] * initialWeights[1] + MY_CENTER_PIECES)/(initialWeights[1] + 1) + " " + (initialWeights[1] + 1);
			writer.println(s);
			s = "MY_KINGS " + (initialWeights[2] * initialWeights[3] + MY_KINGS)/(initialWeights[3] + 1) + " " + (initialWeights[3] + 1);
			writer.println(s);
			s = "OPPONENT_PIECES " + (initialWeights[4] * initialWeights[5] + OPPONENT_PIECES)/(initialWeights[5] + 1) + " " + (initialWeights[5] + 1);
			writer.println(s);
			s = "OPPONENT_KINGS " + (initialWeights[6] * initialWeights[7] + OPPONENT_KINGS)/(initialWeights[7] + 1) + " " + (initialWeights[7] + 1);
			writer.println(s);
			writer.close();
		} catch (Exception e2) { }
	}
	
	public void saveOriginalWeights() {
		// nothing to do
	}
	
}
