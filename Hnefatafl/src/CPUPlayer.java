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
		boolean goodMoveFound = false;
		boolean okMoveFound = false;

		for (Move nextMove : possibleMoves) {

			if(cpu == BLACK) {
				// King va dans un coin
				if(board.moveIsKing(nextMove) && board.moveIsGoingToCorner(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					return bestMoves;
				}

				// King skip les moves qui font en sorte qu'il sera piégé de 3 côtés
				if(board.moveIsKing(nextMove) && board.isKingAlmostTrapped(nextMove.getRowTarget(), nextMove.getColTarget(), nextMove.getRowStart(), nextMove.getColStart())) continue;

				// Si le roi est en danger ne pas jouer d'autres pions
				if(!board.moveIsKing(nextMove) && board.isKingAlmostTrapped(board.getRowKing(), board.getColKing(), nextMove.getRowStart(), nextMove.getColStart())) continue;

				// King va dans une ligne de coin vide
				if(board.moveIsKing(nextMove) && board.moveIsGoingOnEmptySide(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					bestMoveFound = true;
					continue;
				}

				// King fuit s'il est actuellement piégé de 3 côtés 
				if(board.moveIsKing(nextMove) && board.isKingAlmostTrapped(board.getRowKing(), board.getColKing(), nextMove.getRowStart(), nextMove.getColStart()) 
						&& board.canMoveFreeAlmostTrappedKing(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					bestMoveFound = true;
					continue;
				}
				if(!bestMoveFound) {
					// King sort du trone
					if(board.moveIsKing(nextMove) && board.kingIsStillInThrone()) {
						bestMoves.clear();
						bestMoves.add(nextMove);
						bestMoveFound = true;
						continue;
					}
				}
			}

			if(cpu == RED) {			
				// Rouge tue le roi
				if(board.canMoveTrapKing(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					return bestMoves;
				}
				if(board.getNumberOfRedPawns() > 12) {
					// Ne pas deplacer un pion qui est dans une des cases en diagonale
					int rowInit = nextMove.getRowStart();
					int colInit = nextMove.getColStart();
					if(rowInit == 1 && (colInit == 1 || colInit == 11)) continue;
					if(rowInit == 11 && (colInit == 1 || colInit == 11)) continue;
					if(colInit == 0 && (rowInit == 2 || rowInit == 10)) continue;
					if(colInit == 12 && (rowInit == 2 || rowInit == 10)) continue;
					if(rowInit == 0 && (colInit == 2 || colInit == 10)) continue;
					if(rowInit == 12 && (colInit == 2 || colInit == 10)) continue;

					// Bloquer la case en diagonale des coins en priorité
					if(board.blockExit(nextMove)) {
						bestMoves.clear();
						bestMoves.add(nextMove);
						return bestMoves;
					}
				}

				// Si le move fait en sorte qu'il pourra se faire tuer apres, on le skip
				if(board.isMoveDangerousForRedPawn(nextMove)) continue;

				// Un pion va tuer un noir
				if(board.canMoveKillBlackPawns(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					bestMoveFound = true;
					goodMoveFound = true;
					continue;
				}

				// Un pion va encadré le roi et il ne lui restera qu'une case libre
				if(board.canMoveAlmostTrapKing(nextMove)) {
					bestMoves.clear();
					bestMoves.add(nextMove);
					bestMoveFound = true;
					goodMoveFound = true;
					continue;
				}

				if(!goodMoveFound) {

					if(board.moveIsOnSides(nextMove)) {
						bestMoves.clear();
						bestMoves.add(nextMove);
						bestMoveFound = true;
						continue;
					}

					// Un pion va se placer à coté du roi
					else if(board.isMoveNextToKing(nextMove)) {
						bestMoves.clear();
						bestMoves.add(nextMove);
						bestMoveFound = true;
						continue;
					}
				}
			}
			if(!bestMoveFound) {
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
		if(bestMoves.size() == 0) return possibleMoves;
		else return bestMoves;
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
		if (evaluation >= 70 || evaluation <= -70 || board.getNumberOfRedPawns() == 0 || board.getNumberOfBlackPawns() == 0) return evaluation;

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

	/**
	 * Cas où le jeu alterne entre 2 moves, on déplace un autre pion pour bloquer l'adverse
	 * @param board
	 * @return
	 */
	public String getDifferentMove(Board board) {
		ArrayList<Move> possibleMoves = board.findPossibleMoves(cpu);
		Move bestMove = possibleMoves.get(0);

		// S'il reste d'autres pions noirs autre que le roi
		int nbPawns = (cpu == BLACK) ? board.getNumberOfBlackPawns() : board.getNumberOfRedPawns();
		if(nbPawns > 1) {
			for (Move nextMove : possibleMoves) {
				if(board.moveIsKing(nextMove)) continue; // skip les moves du king
				if(nextMove.getColTarget() == board.getColKing()+1 || nextMove.getColTarget() == board.getColKing()-1
						|| nextMove.getRowTarget() == board.getRowKing()+1 || nextMove.getRowTarget() == board.getRowKing()-1) {
					bestMove = nextMove;
					break;
				}
			}
		}
		else {
			int randomIndex = (int)Math.floor(Math.random() * possibleMoves.size());
			bestMove= possibleMoves.get(randomIndex);
		}
		return bestMove.toString();
	}

}
