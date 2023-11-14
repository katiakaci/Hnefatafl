import java.util.ArrayList;

public class CPUPlayer {

	/**
	 * Contient le nombre de noeuds visités (le nombre d'appel à la fonction MinMax ou Alpha Beta)
	 */
	private int numExploredNodes;
	private Board board;
	private int cpu, max, min;
	private final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5, THRONE = 6;

	public CPUPlayer(int cpu){
		this.cpu = cpu;
		this.max = cpu;
		this.min = (cpu == RED? BLACK : RED);
	}

	public int  getNumOfExploredNodes(){
		return numExploredNodes;
	}

	/**
	 * Retourne la liste des meilleurs coups possibles
	 * avec l'algorithme miniMax
	 * @param board
	 * @return
	 */
	public ArrayList<Move> getNextMoveMinMax(Board board) {
		this.numExploredNodes=0;
		this.board=board;
		ArrayList<Move> bestMoves = new ArrayList<>();
		int bestScore = Integer.MIN_VALUE;
		
		for (Move nextMove : board.findPossibleMoves(cpu)) {
			board.play(nextMove.toString(), cpu, false);
			int score = miniMax(min);
			board.cancelMove(nextMove); 

			if(score > bestScore) {
				bestMoves.clear(); 
				bestMoves.add(nextMove);
				bestScore = score;
			}
			else if(score == bestScore)bestMoves.add(nextMove);

		}	
		return bestMoves;
	}

	/**
	 * Retourne la liste des meilleurs coups possibles
	 * avec l'algorithme alpha beta
	 * @param board
	 * @return
	 */
	public ArrayList<Move> getNextMoveAB(Board board){
		this.numExploredNodes=0;
		this.board=board;

		ArrayList<Move> bestMoves = new ArrayList<>();
		int bestScore = Integer.MIN_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		for (Move nextMove : board.findPossibleMoves(cpu)) {
			board.play(nextMove.toString(), cpu, false);
			int score = miniMaxAlphaBeta(min, alpha, beta);
			board.cancelMove(nextMove);

			if(score > bestScore) {
				bestMoves.clear(); 
				bestMoves.add(nextMove);
				bestScore = score;
			}
			else if(score==bestScore) bestMoves.add(nextMove);

			alpha = Math.max(alpha, bestScore);
			if (beta <= alpha) break;
		}	
		return bestMoves;
	}

	/**
	 * Algorithme MiniMax
	 * @param player
	 * @return
	 */
	private int miniMax(int player) {
		numExploredNodes++;
		// Si positionActuelle est finale (victoire ou plus de mouvement possible)
		if (board.evaluate(player) != 0 || board.findPossibleMoves(player).size() == 0) return board.evaluate(cpu);
		else if (player == max) {
			int maxScore = Integer.MIN_VALUE;
			ArrayList<Move> moves = board.findPossibleMoves(player);
//			for(Move nextMove : board.findPossibleMoves(player)) {
			for(Move nextMove : moves) {
				board.play(nextMove.toString(), player, false);
				int score = miniMax(min);
				maxScore = Math.max(maxScore, score);
				board.cancelMove(nextMove);
			}
			return maxScore;
		}
		else {
			int minScore = Integer.MAX_VALUE;;
			ArrayList<Move> moves = board.findPossibleMoves(min);
//			for(Move nextMove : board.findPossibleMoves(player)) {
			for(Move nextMove : moves) {
				board.play(nextMove.toString(), player, false);
				int score = miniMax(max);
				minScore = Math.min(minScore, score);
				board.cancelMove(nextMove);
			}
			return minScore;
		}
	}

	/**
	 * Algorithme Élagage Alpha-Beta
	 * @param player
	 * @param alpha
	 * @param beta
	 * @return
	 */
	private int miniMaxAlphaBeta(int player, int alpha, int beta) {
		numExploredNodes++;

		// Si positionActuelle est finale (victoire ou board plein)
		if (board.evaluate(player) != 0 || board.findPossibleMoves(player).size()==0) return board.evaluate(cpu);

		else if (player == max) {
			int val = Integer.MIN_VALUE;

			for(Move nextMove : board.findPossibleMoves(player)) {
				board.play(nextMove.toString(), player, false);
				int score = miniMaxAlphaBeta(min, Math.max(alpha, val), beta);
				board.cancelMove(nextMove);
				val = Math.max(val, score);
				if(val>=beta) return val;
			}
			return val;
		}

		// si joueur == Min
		else {
			int val = Integer.MAX_VALUE;		

			for(Move nextMove : board.findPossibleMoves(player)) {
				board.play(nextMove.toString(), player, false);
				int score = miniMaxAlphaBeta(max, alpha, Math.min(beta, val));
				board.cancelMove(nextMove);
				val = Math.min(val, score);
				if(val<=alpha) return val;
			}
			return val;			
		}
	}

}
