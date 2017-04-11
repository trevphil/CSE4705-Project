
import java.util.List;

public interface CheckersGameState {
	
	public static final String PLAYER1 = "Black"; // black moves first in checkers
	public static final String PLAYER2 = "White";
	
	String player();
	List<Move> actions();
	CheckersGameState result(Move x); 
	void printState();
	
}