import java.util.ArrayList;

public class CPUPlayer {

	private int numExploredNodes;
	private Board board;
	//	private Board boardClone;
	private int cpu, max, min;
	private final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5, THRONE = 6;
	private final int DEPTH = 4;

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
	 * @return
	 */
	public ArrayList<Move> getNextMoveMinMax(Board board) {
		this.numExploredNodes = 0;
		this.board = board;
		ArrayList<Move> bestMoves = new ArrayList<>();
		ArrayList<Move> possibleMoves = board.findPossibleMoves(cpu);
		int bestScore = Integer.MIN_VALUE;

		System.out.println("nb de possible moves pour les "+cpu+": "+possibleMoves.size());

		for (Move nextMove : possibleMoves) {
			System.out.println("move: "+nextMove.toString());
			//			boardClone = (Board) board.clone(); // cloner à chaque itération
			//			boardClone.play(nextMove.toString(), cpu);

			board.play(nextMove.toString(), cpu);
			int score = miniMax(min, DEPTH);
			//			this.depth=0;
			board.cancelMove(nextMove); 

			if(score > bestScore) {
				bestMoves.clear(); 
				bestMoves.add(nextMove);
				bestScore = score;
			}
			else if(score == bestScore) bestMoves.add(nextMove);
			//			i++;
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

		ArrayList<Move> possibleMoves = board.findPossibleMoves(cpu);

		for (Move nextMove : possibleMoves) {
			board.play(nextMove.toString(), cpu);
			int score = miniMaxAlphaBeta(min, alpha, beta, 0);
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
	private int miniMax(int player, int depth) {
		numExploredNodes++;

		System.out.println("depth: "+depth);
		if(depth == 0) return board.evaluate(cpu);

		ArrayList<Move> moves = board.findPossibleMoves(player);
		// Si positionActuelle est finale (victoire ou plus de mouvement possible)
		if (board.evaluate(player) != 0 || moves.size() == 0) return board.evaluate(cpu);

		else if (player == max) {
			int maxScore = Integer.MIN_VALUE;
			for(Move nextMove : moves) {
				board.play(nextMove.toString(), player);
				int score = miniMax(min, depth - 1);
				maxScore = Math.max(maxScore, score);
				board.cancelMove(nextMove);
				//				boardClone.cancelMove(nextMove);
			}
			return maxScore;
		}

		else {
			int minScore = Integer.MAX_VALUE;;
			for(Move nextMove : moves) {
				board.play(nextMove.toString(), player);
				int score = miniMax(max, depth - 1);
				minScore = Math.min(minScore, score);
				board.cancelMove(nextMove);
				//				boardClone.cancelMove(nextMove);
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
	private int miniMaxAlphaBeta(int player, int alpha, int beta, int depth) {
		numExploredNodes++;
		if(depth == 0) return board.evaluate(player);

		ArrayList<Move> moves = board.findPossibleMoves(player);

		// Si positionActuelle est finale (victoire ou board plein)
		if (board.evaluate(player) != 0 || moves.size()==0) return board.evaluate(cpu);

		else if (player == max) {
			int val = Integer.MIN_VALUE;
			for(Move nextMove : moves) {
				board.play(nextMove.toString(), player);
				int score = miniMaxAlphaBeta(min, Math.max(alpha, val), beta, depth++);
				board.cancelMove(nextMove);
				val = Math.max(val, score);
				if(val>=beta) return val;
			}
			return val;
		}

		// si joueur == Min
		else {
			int val = Integer.MAX_VALUE;		

			for(Move nextMove : moves) {
				board.play(nextMove.toString(), player);
				int score = miniMaxAlphaBeta(max, alpha, Math.min(beta, val), depth++);
				board.cancelMove(nextMove);
				val = Math.min(val, score);
				if(val<=alpha) return val;
			}
			return val;			
		}
	}


	// aaron:
	//	private int minimaxAB(int[][] board, boolean isMaximizing, int depth, int alpha, int beta) {
	//
	//        //Last node or max depth reached
	//        if (depth == 0 || Board.checkWinner(board) == Board.RED || Board.checkWinner(board) == Board.BLACK) {
	//            return Board.evaluate(getColor(),board);
	//        }
	//        int[][] boardTmp = new int[Board.MAX_ROW][Board.MAX_ROW]; // sauvegarder l'état du board avant de tester un jeu
	//
	//        if (isMaximizing) {
	//            int maxScore = Integer.MIN_VALUE;
	//            ArrayList<Deplacement> possibleDeplacement = Board.getAllPossibleMoves(board, this.color);
	//            for (Deplacement deplacement : possibleDeplacement) {
	//                Board.play(deplacement, getColor(),board,boardTmp);
	//
	//                int score = minimaxAB(board, false, depth - 1, alpha, beta);
	//
	//                Board.undoPlay(deplacement, this.color,board,boardTmp);
	//                maxScore = Math.max(maxScore, score);
	//                alpha = Math.max(alpha, maxScore);
	//
	//                if (beta <= alpha) {
	//                    break;
	//                }
	//            }
	//            return maxScore;
	//        } else {
	//            int minScore = Integer.MAX_VALUE;
	//            ArrayList<Deplacement> possibleMoves = Board.getAllPossibleMoves(board, this.color);
	//
	//            for (Deplacement deplacement : possibleMoves) {
	//                Board.play(deplacement, Board.getOppositeColor(getColor()),board,boardTmp);
	//
	//                int score = minimaxAB(board, true, depth - 1, alpha, beta);
	//
	//                Board.undoPlay(deplacement, this.color,board,boardTmp);
	//                minScore = Math.min(minScore, score);
	//                beta = Math.min(beta, minScore);
	//
	//                if (beta <= alpha) {
	//                    break;
	//                }
	//            }
	//            return minScore;
	//        }
	//    }

}
