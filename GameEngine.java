import java.util.*;
import java.io.*;

public class GameEngine {

	public GameState game;
	public String myPlayer;
	private Move myMove;
	
	private static final String WEIGHTS_FILE = "./src/weights.txt";
	public static final String PIECES_TAKEN = "PIECES_TAKEN";
	public static final String MY_CENTER_PIECES = "MY_CENTER_PIECES";
	public static final String MY_KINGS = "MY_KINGS";
	public static final String OPPONENT_PIECES = "OPPONENT_PIECES";
	public static final String OPPONENT_KINGS = "OPPONENT_KINGS";
	public static final String SAFE_PAWNS = "SAFE_PAWNS";
	public static final String SAFE_KINGS = "SAFE_KINGS";
	public static final String DEFENDER_PIECES = "DEFENDER_PIECES";
	
	public static final String[] factors = 			{ PIECES_TAKEN, MY_CENTER_PIECES, MY_KINGS, OPPONENT_PIECES, OPPONENT_KINGS, SAFE_PAWNS, SAFE_KINGS, DEFENDER_PIECES	};
	private static final double[] initialValues = 	{ 5.0,			1.0,				1.5,	3.0,				0.5,		  2.0, 		  1.0,			1.0				};
	
	private static HashMap<String, Weight> weights = new HashMap<String, Weight>();
	private static HashMap<String, Weight> mutatedWeights = new HashMap<String, Weight>();
	private static final double P_OFF = 0.33; // the increase (or decrease) in probability that a weight's probability will mutate 
			
	public GameEngine(String p) {
		game = GameState.initialState();
		myPlayer = p;
		loadWeights();
		mutateWeights();
	}
	
	public void updateGameAfterOpponentMove(String opponentMove) {
		String opponent = opponentMove.substring(5, 10); // opponent's color
		ArrayList<Integer> locations = new ArrayList<Integer>();
		String temp = opponentMove.substring(10, opponentMove.length()); // remove the prefix of the opponent's color
		int indexOfCloseParen = temp.indexOf(')');
		while (indexOfCloseParen != -1) {
			int loc = Move.rowColToSamuel(temp.substring(indexOfCloseParen - 4, indexOfCloseParen + 1)); // e.g. "(1:2)"
			locations.add(loc);
			temp = temp.substring(indexOfCloseParen + 1, temp.length()); // trim off the location that was just added
			indexOfCloseParen = temp.indexOf(')');
		}
		Move m = new Move(opponent, locations, game.chipAtLocation(locations.get(0)));
		game = game.result(m);
	}
	
	public void updateGameAfterMyMove() {
		game = game.result(myMove);
	}
	
	public String getMove() {
		List<Move> moves = game.actions();
		if (moves.size() == 0) return null;
		
		MinimaxSearch minimaxSearch = new MinimaxSearch(myPlayer);
		Move bestMove = minimaxSearch.minimaxDecision(game);
		if (bestMove == null) return null;
		
		myMove = bestMove;
		return myMove.serverString();
	}
	
	public static double valueForFactor(String factor) {
		Weight w = mutatedWeights.get(factor);
		return w == null ? 0 : w.value;
	}
	
	private void loadWeights() {
		try {
			File f = new File(WEIGHTS_FILE);
			Scanner scan = new Scanner(f);
			weights = new HashMap<String, Weight>();
			while (scan.hasNextLine()) {
				String[] parts = scan.nextLine().split(" ");
				String name = parts[0];
				double value = Double.parseDouble(parts[1]);
				int sampleSize = Integer.parseInt(parts[2]);
				double probabilityPositive = Double.parseDouble(parts[3]);
				Weight w = new Weight(name, value, sampleSize, probabilityPositive);
				weights.put(name, w);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			File file = new File(WEIGHTS_FILE);
			try {
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				// initialize "weights.txt" file with all factors having a weight of 1 and probability 0.50
				for (int i = 0; i < factors.length; i++) {
					writer.println(factors[i] + " " + initialValues[i] + " 1 0.50");
				}
				writer.close();
				loadWeights();
			} catch (Exception e2) { 
				e.printStackTrace();
			}
		}
	}
	
	private void mutateWeights() {
		mutatedWeights = new HashMap<String, Weight>();
		for (String factor : factors) {
			Weight w = weights.get(factor);
			if (w != null) {
				boolean positive = Math.random() <= w.probabilityPositive;
				double value = w.value + (positive ? 1 : -1) * Math.random();
				int n = w.sampleSize + 1;
				double pPositive = w.probabilityPositive + w.probabilityPositive * (positive ? P_OFF : -P_OFF);
				Weight mutated = new Weight(factor, value, n, pPositive);
				mutatedWeights.put(factor, mutated);
			}
		}
	}
	
	public void saveMutatedWeights() {
		File file = new File("./src/weights.txt");
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			for (String factor : factors) {
				Weight oldWeight = weights.get(factor);
				Weight newWeight = mutatedWeights.get(factor);
				if (oldWeight != null && newWeight != null) {
					int n = newWeight.sampleSize; // = oldWeight.sampleSize + 1
					double averagedValue = (oldWeight.value * oldWeight.sampleSize + newWeight.value) / n;
					double averagedProb = (oldWeight.probabilityPositive * oldWeight.sampleSize + newWeight.probabilityPositive) / n;
					writer.println(factor + " " + averagedValue + " " + n + " " + averagedProb);
				}
			}
			writer.close();
		} catch (Exception e2) { }
	}
	
	public void saveOriginalWeights() {
		// nothing to do
	}
	
}
