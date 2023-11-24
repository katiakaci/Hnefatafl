import java.util.ArrayList;

public class CPUPlayer {

	private int numExploredNodes;
	private int cpu, max, min;
	private final int BLACK = 2, RED = 4;
	private final int DEPTH = 3;

	public CPUPlayer(int cpu){
		this.cpu = cpu;
		this.max = cpu;
		this.min = (cpu == RED? BLACK : RED);
	}

	public int  getNumOfExploredNodes(){
		return numExploredNodes;
	}

	/**
	 * Retourne la liste des meilleurs coups possibles avec l'algorithme miniMax
	 * @param board
	 * @return les meilleurs coups possibles
	 */
	public ArrayList<Move> getNextMoveMinMax(Board board) {
		this.numExploredNodes = 0;
		int bestScore = Integer.MIN_VALUE;
		ArrayList<Move> bestMoves = new ArrayList<>();
		ArrayList<Move> possibleMoves = board.findPossibleMoves(cpu);

		for (Move nextMove : possibleMoves) {
			Board boardCopy = cloneBoard(board); // cloner à chaque itération
			boardCopy.play(nextMove.toString(), cpu);
			int score = miniMax(min, DEPTH, boardCopy);
			System.out.println("score: "+score);

			if(score > bestScore) {
				bestMoves.clear(); 
				bestMoves.add(nextMove);
				bestScore = score;
			}
			else if(score == bestScore) bestMoves.add(nextMove);
		}	
		return bestMoves;
	}

	/**
	 * Retourne la liste des meilleurs coups possibles avec l'algorithme alpha beta
	 * @param board
	 * @return
	 */
	public ArrayList<Move> getNextMoveAB(Board board){
		this.numExploredNodes = 0;
		int bestScore = Integer.MIN_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		ArrayList<Move> bestMoves = new ArrayList<>();
		ArrayList<Move> possibleMoves = board.findPossibleMoves(cpu);

		for (Move nextMove : possibleMoves) {
			Board boardCopy = cloneBoard(board); // cloner à chaque itération
			boardCopy.play(nextMove.toString(), cpu);
			int score = miniMaxAlphaBeta(min, alpha, beta, DEPTH, boardCopy);

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
	 * @param depth
	 * @param board
	 * @return
	 */
	private int miniMax(int player, int depth, Board board) {
		numExploredNodes++;
		if(depth == 0) return board.evaluate(cpu);

		// Si positionActuelle est finale (victoire ou plus de mouvement possible)
		ArrayList<Move> moves = board.findPossibleMoves(player);
		int evaluation =  board.evaluate(cpu);
		if (evaluation != 0 || moves.size() == 0) return evaluation;

		else if (player == max) {
			int maxScore = Integer.MIN_VALUE;
			for(Move nextMove : moves) {
				Board boardCopy = cloneBoard(board);
				boardCopy.play(nextMove.toString(), player);
				int score = miniMax(min, depth - 1, boardCopy);
				maxScore = Math.max(maxScore, score);
			}
			return maxScore;
		}

		else {
			int minScore = Integer.MAX_VALUE;;
			for(Move nextMove : moves) {
				Board boardCopy = cloneBoard(board);
				boardCopy.play(nextMove.toString(), player);
				int score = miniMax(max, depth - 1, boardCopy);
				minScore = Math.min(minScore, score);
			}
			return minScore;
		}
	}

	/**
	 * Algorithme Élagage Alpha-Beta
	 * @param player
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @param board
	 * @return
	 */
	private int miniMaxAlphaBeta(int player, int alpha, int beta, int depth, Board board) {
		numExploredNodes++;
		if(depth == 0) return board.evaluate(cpu);

		// Si positionActuelle est finale (victoire ou board plein)
		ArrayList<Move> moves = board.findPossibleMoves(player);
		int evaluation =  board.evaluate(cpu);
		if (evaluation == 100 || evaluation == -100 || moves.size()== 0) return evaluation;

		else if (player == max) {
			int val = Integer.MIN_VALUE;
			for(Move nextMove : moves) {
				Board boardCopy = cloneBoard(board);
				boardCopy.play(nextMove.toString(), player);
				int score = miniMaxAlphaBeta(min, Math.max(alpha, val), beta, depth - 1, boardCopy);

				val = Math.max(val, score);
				if(val>=beta) return val;
			}
			return val;
		}
		else {
			int val = Integer.MAX_VALUE;		
			for(Move nextMove : moves) {
				Board boardCopy = cloneBoard(board);
				boardCopy.play(nextMove.toString(), player);
				int score = miniMaxAlphaBeta(max, alpha, Math.min(beta, val), depth - 1, boardCopy);

				val = Math.min(val, score);
				if(val<=alpha) return val;
			}
			return val;			
		}
	}

	private Board cloneBoard(Board ogBoard) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ogBoard.getBoard().length; i++) {
			for (int j = 0; j < ogBoard.getBoard()[i].length; j++) {
				sb.append(ogBoard.getBoard()[i][j]).append(" ");
			}
		}
		String boardValues = sb.toString().trim();
		Board copyBoard = new Board(boardValues);
		return copyBoard;
	}

}
