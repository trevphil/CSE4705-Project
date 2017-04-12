
public class SearchNode {

	private SearchNode parent;
	private CheckersGameState current;
	private Move fromParentToCurrent;
	private boolean visited;
	
	public SearchNode(SearchNode p, CheckersGameState c, Move m, boolean v) {
		parent = p;
		current = c;
		fromParentToCurrent = m;
		visited = v;
	}
	
	public SearchNode parent() { return parent; }
	public CheckersGameState gameState() { return current; }
	public Move move() { return fromParentToCurrent; }
	public boolean visited() { return visited; }
	public void setVisited() { visited = true; }
	
}
