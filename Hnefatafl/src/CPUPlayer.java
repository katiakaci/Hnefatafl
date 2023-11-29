import java.util.ArrayList;

public class CPUPlayer {

	private int cpu, max, min;
	private final int BLACK = 2, RED = 4;
	private final int DEPTH = 2;

	public CPUPlayer(int cpu){
		this.cpu = cpu;
		this.max = cpu;
		this.min = (cpu == RED? BLACK : RED);
	}

	/**
	 * Retourne la liste des meilleurs coups possibles avec l'algorithme miniMax
	 * @param board
	 * @return ArrayList des meilleurs coups possibles
	 */
	public ArrayList<Move> getNextMoveMinMax(Board board) {
		int bestScore = Integer.MIN_VALUE;
		ArrayList<Move> bestMoves = new ArrayList<>();
		ArrayList<Move> possibleMoves = board.findPossibleMoves(cpu);

		for (Move nextMove : possibleMoves) {
			Board boardCopy = cloneBoard(board);
			boardCopy.play(nextMove.toString(), cpu);
			int score = miniMax(min, DEPTH, boardCopy);

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
	 * Algorithme MiniMax
	 * @param player
	 * @param depth
	 * @param board
	 * @return score
	 */
	private int miniMax(int player, int depth, Board board) {
		if(depth == 0) return board.evaluate(cpu, depth);

		// Si positionActuelle est finale (victoire, défaite ou plus de move possible pour un des deux joueurs)
		int evaluation =  board.evaluate(cpu, depth);
		if (evaluation == 100 || evaluation == -100 || board.getNumberOfPawnsOnBoardFor(RED) == 0 || board.getNumberOfPawnsOnBoardFor(BLACK) == 0) return evaluation;

		ArrayList<Move> moves = board.findPossibleMoves(player);
		if (player == max) {
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
	 * Retourne la liste des meilleurs coups possibles avec l'algorithme alpha beta
	 * @param board
	 * @return
	 */
	public ArrayList<Move> getNextMoveAB(Board board) {
		int bestScore = Integer.MIN_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		ArrayList<Move> bestMoves = new ArrayList<>();
		ArrayList<Move> possibleMoves = board.findPossibleMoves(cpu);
		boolean bestMoveFound = false;

		for (Move nextMove : possibleMoves) {
			
			// King va dans un coin
				if(cpu == BLACK && board.moveIsKing(nextMove) && board.moveIsGoingToCorner(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					return bestMoves;
				}

				// King skip les moves qui font en sorte qu'il sera piégé de 3 côtés
				if(cpu == BLACK && board.moveIsKing(nextMove) && board.isKingAlmostTrapped(nextMove.getRowTarget(), nextMove.getColTarget())) continue;

				// King va dans une ligne de coin vide
				if(cpu == BLACK && board.moveIsKing(nextMove) && board.moveIsGoingOnEmptySide(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					bestMoveFound = true; // permet de ne plus vérifier les autres moves sauf un move de coin ou un move dangereux
					continue;
				}

				// Si le roi est en danger ne pas jouer d'autres pions
				if(cpu == BLACK && !board.moveIsKing(nextMove) && board.isKingAlmostTrapped(board.getRowKing(), board.getColKing())) continue;

				// King fuit s'il est actuellement piégé de 3 côtés 
				if(cpu == BLACK && board.moveIsKing(nextMove) && board.isKingAlmostTrapped(board.getRowKing(), board.getColKing()) 
						&& board.canMoveFreeAlmostTrappedKing(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					bestMoveFound = true;
					continue;
				}
				
				// TODO ajouter aller dans une ligne vide au centre
				if(cpu == BLACK && board.moveIsKing(nextMove) && board.moveIsGoingOnEmptyRowOrColumn(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					continue;
				}
				
			// noir:
			// TODO Cas bizarre où le jeu bloque et alterne entre 2 move (donc match nul)(voir screenshot), faire si move pareil depuis 3 moves changer
			// TODO ajouter quoi faire si tes dans une ligne vide au centre
			
			if(!bestMoveFound) {
				// Rouge encadre le roi TODO
				//				if(cpu == RED && board.canMoveTrapKing(nextMove)) {
				//					bestMoves.clear();
				//					bestMoves.add(nextMove);
				//					return bestMoves;
				//				}	

				Board boardCopy = cloneBoard(board);
				boardCopy.play(nextMove.toString(), cpu);
				int score = miniMaxAlphaBeta(min, alpha, beta, DEPTH, boardCopy);
				if(score > bestScore) {
					bestMoves.clear(); 
					bestMoves.add(nextMove);
					bestScore = score;
				}
				else if(score == bestScore) bestMoves.add(nextMove);
				alpha = Math.max(alpha, bestScore);
				if (beta <= alpha) break;
			}
		}
		return bestMoves;
	}

	/**
	 * Algorithme Élagage Alpha-Beta
	 * @param player
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @param board
	 * @return score
	 */
	private int miniMaxAlphaBeta(int player, int alpha, int beta, int depth, Board board) {					
		if(depth == 0) return board.evaluate(cpu, depth);

		// Si positionActuelle est finale (victoire, défaite ou plus de move possible pour un des deux joueurs)
		int evaluation =  board.evaluate(cpu, depth);
		if (evaluation >= 70 || evaluation <= -70 || board.getNumberOfPawnsOnBoardFor(RED) == 0 || board.getNumberOfPawnsOnBoardFor(BLACK) == 0) return evaluation;

		ArrayList<Move> moves = board.findPossibleMoves(player);
		if (player == max) {
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
