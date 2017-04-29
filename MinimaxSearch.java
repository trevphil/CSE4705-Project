import java.util.List;

public class MinimaxSearch {
	
	private String myPlayer;
	private static final int FIXED_DEPTH = 12;
	
	public MinimaxSearch(String mp) {
		myPlayer = mp;
	}
	
	public Move minimaxDecision(GameState state) {
		return minimaxDecision(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	private Move minimaxDecision(GameState state, double alpha, double beta) {
		List<Move> actions = state.actions();
		if (actions.size() == 0) return null;
		
		Move bestMove = null;
		boolean maximizingFirst = state.player().equals(myPlayer);
		double bestScore = maximizingFirst ? alpha : beta;
		int currentDepth = 1;
		for (Move move : actions) {
			GameState successor = state.result(move);
			double score = maximizingFirst ? minValue(successor, currentDepth, alpha, beta) : maxValue(successor, currentDepth, alpha, beta);
			if ((maximizingFirst && score >= bestScore) || (!maximizingFirst && score <= bestScore)) {
				bestScore = score;
				bestMove = move;
			}
		}
		
		if (bestScore == 0 && bestMove == null) return state.actions().get(0);
		return bestMove;
	}
	
	private double maxValue(GameState state, int currentDepth, double alpha, double beta) {
		if (state.isTerminal() || currentDepth == FIXED_DEPTH) return state.utility(myPlayer);
		for (Move move : state.getBestMoves(3, myPlayer)) {
			GameState successor = state.result(move);
			double score = minValue(successor, currentDepth + 1, alpha, beta);
			if (score > alpha) alpha = score;
			if (alpha >= beta) break;
		}
		return alpha;
	}
	
	private double minValue(GameState state, int currentDepth, double alpha, double beta) {
		if (state.isTerminal() || currentDepth == FIXED_DEPTH) return state.utility(myPlayer);
		for (Move move : state.actions()) {
			GameState successor = state.result(move);
			double score = maxValue(successor, currentDepth + 1, alpha, beta);
			if (score < beta) beta = score;
			if (alpha >= beta) break;
		}
		return beta;
	}
	
}