
public class SearchNode {

	SearchNode parent;
	CheckersGameState current;
	Move fromParentToCurrent;
	boolean visited;
	
	public SearchNode(SearchNode p, CheckersGameState c, Move m, boolean v) {
		parent = p;
		current = c;
		fromParentToCurrent = m;
		visited = v;
	}
	
}
