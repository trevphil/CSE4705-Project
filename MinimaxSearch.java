import java.util.List;

public class MinimaxSearch {
	
	private String myPlayer;
	private static final int FIXED_DEPTH = 6;
	
	public MinimaxSearch(String mp) {
		myPlayer = mp;
	}

	public Move minimaxDecision(GameState state) {
		List<Move> actions = state.actions();
		if (actions.size() == 0) return null;
		
		Move bestMove = null;
		boolean maximizingFirst = state.player().equals(myPlayer);
		double bestScore = maximizingFirst ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		int currentDepth = 1;
		for (Move move : actions) {
			GameState successor = state.result(move);
			double score = maximizingFirst ? minValue(successor, currentDepth) : maxValue(successor, currentDepth);
			if ((maximizingFirst && score >= bestScore) || (!maximizingFirst && score <= bestScore)) {
				bestScore = score;
				bestMove = move;
			}
		}
		
		if (bestScore == 0 && bestMove == null) return state.actions().get(0);
		return bestMove;
	}
	
	private double maxValue(GameState state, int currentDepth) {
		if (state.isTerminal() || currentDepth == FIXED_DEPTH) return state.utility(myPlayer);
		double bestScore = Integer.MIN_VALUE;
		for (Move move : state.actions()) {
			GameState successor = state.result(move);
			double score = minValue(successor, currentDepth + 1);
			if (score >= bestScore) bestScore = score;
		}
		return bestScore;
	}
	
	private double minValue(GameState state, int currentDepth) {
		if (state.isTerminal() || currentDepth == FIXED_DEPTH) return state.utility(myPlayer);
		double bestScore = Integer.MAX_VALUE;
		for (Move move : state.actions()) {
			GameState successor = state.result(move);
			double score = maxValue(successor, currentDepth + 1);
			if (score <= bestScore) bestScore = score;
		}
		return bestScore;
	}
	
}