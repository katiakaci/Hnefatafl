import java.util.*;
import java.util.stream.Collectors;

class Board {
	private final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5, THRONE = 6;
	private int[][] board;
	private int rowKing, colKing;

	public Board(String s) {
		board = new int[13][13];
		String[] boardValues = s.split(" ");
		int x=0, y=0;
		for(int i=0; i<boardValues.length; i++){
			board[x][y] = Integer.parseInt(boardValues[i]);
			if(Integer.parseInt(boardValues[i]) == KING) {
				this.rowKing = x;
				this.colKing = y;
			}
			x++;
			if(x == 13){
				x = 0;
				y++;
			}
		}
		// Les quatres coins du board
		this.board[0][0] = CORNER;
		this.board[0][12] = CORNER;
		this.board[12][0] = CORNER;
		this.board[12][12] = CORNER;
	}

	public ArrayList<Move> findPossibleMoves(int player) {
		ArrayList<Move> possibleMoves = new ArrayList<>();

		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				// Si on a un player rouge, on cherche pour tous les pions rouges du board s'ils ont des cases vides aux alentours
				if(player == RED && board[i][j] == RED) {
					// vérifier en bas du pion
					for(int row = i + 1; row < 13; row++) {
						if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
						else if(board[row][j] == THRONE) continue; // un pion peut sauter par desssus le throne
						else break; // on quitte la boucle si un pion nous bloque le chemin
					}
					// vérifier en haut du pion
					for(int row = i - 1; row >= 0; row--) {
						if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
						else if(board[row][j] == THRONE) continue;
						else break;
					}
					// vérifier à droite du pion
					for(int column = j + 1; column < 13; column++) {
						if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
						else if(board[i][column] == THRONE) continue;
						else break;
					}
					// vérifier à gauche du pion
					for(int column=j-1; column>=0;column--) {
						if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
						else if(board[i][column] == THRONE) continue;
						else break;
					}
				}
				else if(player == BLACK || player == KING) {
					// on cherche pour tous les pions noirs s'ils ont des cases vides aux alentours
					if(board[i][j] == BLACK) {
						for(int row=i+1; row<13;row++) {
							if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
							else if(board[row][j] == THRONE) continue;
							else break;
						}
						for(int row=i-1; row>=0;row--) {
							if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
							else if(board[row][j] == THRONE) continue;
							else break;
						}
						for(int column=j+1; column<13;column++) {
							if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
							else if(board[i][column] == THRONE) continue;
							else break;
						}
						for(int column=j-1; column>=0;column--) {
							if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
							else if(board[i][column] == THRONE) continue;
							else break;
						}
					}
					// on cherche pour le roi s'il a des cases vides, un throne ou un coin aux alentours
					else if(board[i][j] == KING) {
						for(int row=i+1; row<13;row++) {
							if(board[row][j] == EMPTY || board[row][j] == THRONE || board[row][j] == CORNER) possibleMoves.add(new Move(i,j,row,j));
							else break;
						}
						for(int row=i-1; row>=0;row--) {
							if(board[row][j] == EMPTY || board[row][j] == THRONE || board[row][j] == CORNER) possibleMoves.add(new Move(i,j,row,j));
							else break;
						}
						for(int column=j+1; column<13;column++) {
							if(board[i][column] == EMPTY || board[i][column] == THRONE || board[i][column] == CORNER) possibleMoves.add(new Move(i,j,i,column));
							else break;
						}
						for(int column=j-1; column>=0;column--) {
							if(board[i][column] == EMPTY || board[i][column] == THRONE || board[i][column] == CORNER) possibleMoves.add(new Move(i,j,i,column));
							else break;
						}
					}
				}		 
			}
		}
		return possibleMoves;
	}

	public void play(String move, int player) {
		move = move.trim().toUpperCase();
		String start, end;

		// Format D6-D7
		if(move.contains("-")) {
			String[] positions = move.split("-");
			start = positions[0].trim();
			end = positions[1].trim();
		}
		// Format D6D7
		else {
			int indexOfSecondLetter = 1;
			while (indexOfSecondLetter < move.length() && !Character.isLetter(move.charAt(indexOfSecondLetter))) {
				indexOfSecondLetter++;
			}
			start = move.substring(0, indexOfSecondLetter).trim();
			end = move.substring(indexOfSecondLetter).trim();
		}
		int oldColumn = MapConversion.getConversionLetterToNumberColumn().get(start.substring(0, 1));
		int newColumn = MapConversion.getConversionLetterToNumberColumn().get(end.substring(0, 1));
		int oldRow = MapConversion.getConversionLetterToNumberRow().get(Integer.parseInt(start.substring(1)));
		int newRow = MapConversion.getConversionLetterToNumberRow().get(Integer.parseInt(end.substring(1)));

		// Si c'est le roi qui a bougé
		if(this.board[oldRow][oldColumn] == KING) {
			this.board[oldRow][oldColumn] = EMPTY;
			this.board[6][6] = THRONE;
			this.board[newRow][newColumn] = KING;
			this.rowKing = newRow;
			this.colKing = newColumn;
		}
		else {
			this.board[oldRow][oldColumn] = EMPTY;
			this.board[newRow][newColumn] = player;
		}	
		verifyPawnsElimination(newRow, newColumn, player);
	}

	/**
	 * Vérifie s'il y a un gagnant
	 * @param player
	 * @return 100 si le joueur gagne, -100 s'il perd, 1, 2 3 si pion adverse tué, -1, -2, -3 si pion joueur tué, 0 sinon
	 */
	public int evaluate(int player, int depth) {
		int victory = 100, victory2 = 90, victory3 = 80, victory4 = 70;
		int defeat = -100, defeat2 = -90, defeat3 = -80, defeat4 = -70;
		int draw = 0;

		if(player == RED) {
			if(isKingInCorner()) return defeat;
			if(isKingTrapped()) return victory;
		}
		else {
			if(depth == 3 && isKingInCorner()) return victory;
			if(depth == 2 && isKingInCorner()) return victory2;
			if(depth == 1 && isKingInCorner()) return victory3;
			if(depth == 0 && isKingInCorner()) return victory4;
			if(depth == 3 && isKingTrapped()) return defeat;
			if(depth == 2 && isKingTrapped()) return defeat2;
			if(depth == 1 && isKingTrapped()) return defeat3;
			if(depth == 0 && isKingTrapped()) return defeat4;
		}
		return draw;
	}

	/**
	 * Trouve le nombre de pions vivants du joueur recu
	 * @param player
	 * @return le nombre de pions sur le board
	 */
	public int getNumberOfPawnsOnBoardFor(int player) {
		int numberOfPawnsOnBoard = 0;
		for(int i=0; i < board.length; i++){
			for(int j=0; j < board[i].length; j++){
				if(board[i][j] == player) numberOfPawnsOnBoard++;
			}
		}
		return numberOfPawnsOnBoard;
	}

	/**
	 * Selon la position du pion joué, vérifie s'il y a un pion adverse aux alentours
	 * @param newRow rangée du pion joué
	 * @param newColumn colonne du pion joué
	 * @param player joueur ayant effectué le mouvement
	 */
	private void verifyPawnsElimination(int newRow, int newColumn, int player) {
		// CENTRE
		if(newRow < 11 && newColumn < 11 && newRow > 1 && newColumn > 1) checkRowAndColumn(true, true, true, true, newRow, newColumn, player);	

		// RANGÉE 0 et 1
		else if(newRow < 2 && newColumn < 11 && newColumn > 1) checkRowAndColumn(false, true, true, true, newRow, newColumn, player);	

		// RANGÉE 11 et 12
		else if(newRow > 10 && newColumn < 11 && newColumn > 1) checkRowAndColumn(true, false, true, true, newRow, newColumn, player);

		// COLONNE 0 et 1
		else if(newRow < 11 && newRow > 1 && newColumn < 2) checkRowAndColumn(true, true, true, false, newRow, newColumn, player);	

		// COLONNE 11 et 12
		else if(newRow < 11 && newRow > 1 && newColumn > 10) checkRowAndColumn(true, true, false, true, newRow, newColumn, player);

		// TROIS CASES DANS LE COIN EN HAUT À GAUCHE
		else if(newRow < 2 && newColumn < 2) checkRowAndColumn(false, true, true, false, newRow, newColumn, player);	

		// TROIS CASES DANS LE COIN EN HAUT À DROITE
		else if(newRow < 2 && newColumn > 10) checkRowAndColumn(false, true, false, true, newRow, newColumn, player);

		// TROIS CASES DANS LE COIN EN BAS À GAUCHE
		else if(newRow > 10 && newColumn < 2) checkRowAndColumn(true, false, true, false, newRow, newColumn, player);	

		// TROIS CASES DANS LE COIN EN BAS À DROITE
		else if(newRow > 10 && newColumn > 10) checkRowAndColumn(true, false, false, true, newRow, newColumn, player);
	}

	/**
	 * Vérifie s'il y a un pion adverse en haut, en bas, à droite et/ou à gauche du pion joué et l'élimine si c'est le cas.
	 * @param up
	 * @param down
	 * @param right
	 * @param left
	 * @param newRow
	 * @param newColumn
	 * @param player
	 */
	private void checkRowAndColumn(boolean up, boolean down, boolean right, boolean left, int newRow, int newColumn, int player) {	
		if(player == RED) {	
			if(up) {
				if(board[newRow-1][newColumn] == BLACK && (board[newRow-2][newColumn] == player || board[newRow-2][newColumn] == CORNER || (newRow-2 == THRONE && newColumn == THRONE) )) {
					board[newRow-1][newColumn] = EMPTY;
				}
			}
			if(down) {
				if(board[newRow+1][newColumn] == BLACK && (board[newRow+2][newColumn] == player || board[newRow+2][newColumn] == CORNER || (newRow+2 == THRONE && newColumn == THRONE) )) {
					board[newRow+1][newColumn] = EMPTY;
				}
			}
			if(right) {
				if(board[newRow][newColumn+1] == BLACK && (board[newRow][newColumn+2] == player || board[newRow][newColumn+2] == CORNER || (newRow == THRONE && newColumn+2 == THRONE) )) {
					board[newRow][newColumn+1] = EMPTY;
				}
			}
			if(left) {
				if(board[newRow][newColumn-1] == BLACK && (board[newRow][newColumn-2] == player || board[newRow][newColumn-2] == CORNER || (newRow == THRONE && newColumn-2 == THRONE) )) {
					board[newRow][newColumn-1] = EMPTY;
				}
			}
		}
		else if(player == BLACK) {
			if(up) {
				if(board[newRow-1][newColumn] == RED && (board[newRow-2][newColumn] == player || board[newRow-2][newColumn] == KING || board[newRow-2][newColumn] == CORNER || (newRow-2==THRONE && newColumn==THRONE) )) {
					board[newRow-1][newColumn] = EMPTY;
				}
			}
			if(down) {
				if(board[newRow+1][newColumn] == RED && (board[newRow+2][newColumn] == player || board[newRow+2][newColumn] == KING || board[newRow+2][newColumn] == CORNER || (newRow+2==THRONE && newColumn==THRONE) )) {
					board[newRow+1][newColumn] = EMPTY;
				}
			}
			if(right) {
				if(board[newRow][newColumn+1] == RED && (board[newRow][newColumn+2] == player || board[newRow][newColumn+2] == KING || board[newRow][newColumn+2] == CORNER || (newRow==THRONE && newColumn+2==THRONE) )) {
					board[newRow][newColumn+1] = EMPTY;
				}
			}
			if(left) {
				if(board[newRow][newColumn-1] == RED && (board[newRow][newColumn-2] == player || board[newRow][newColumn-2] == KING || board[newRow][newColumn-2] == CORNER || (newRow==THRONE && newColumn-2==THRONE) )) {
					board[newRow][newColumn-1] = EMPTY;
				}
			}
		}
	}

	public int[][] getBoard() {
		return board;
	}

	public int getRowKing() {
		return rowKing;
	}

	public int getColKing() {
		return colKing;
	}


	// ************************ MÉTHODES POUR LES NOIRS *********************************************

	/**
	 * Vérifie si le roi est encerclé par 4 pions rouges, un mur, un coin ou le throne
	 * @return true si le roi est encerclé, false sinon
	 */
	private boolean isKingTrapped() {
		// Il y a un mur à un des quatre côtés
		if(rowKing == 0) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				return true;
		}
		else if(rowKing == 12) {
			if((board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				return true;
		}
		if(colKing == 0) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE))
				return true;
		}
		else if(colKing == 12) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				return true;
		}
		// Encadré des 4 côtés
		if(rowKing > 0 && rowKing < 12 && colKing > 0 && colKing < 12) {	
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				return true;
		}
		return false;
	}

	/**
	 * Vérifie si le roi est dans un des quatre coins
	 * @return true si le roi est dans un coin, false sinon
	 */
	private boolean isKingInCorner() {
		return (colKing == 0 && rowKing == 0) || (colKing == 0 && rowKing == 12) || (colKing == 12 && rowKing == 0) || (colKing == 12 && rowKing == 12);
	}

	/**
	 * Vérifie si le mouvement est un roi
	 * @param nextMove
	 * @return
	 */
	public boolean moveIsKing(Move nextMove) {
		return nextMove.getColStart() == this.colKing && nextMove.getRowStart() == this.rowKing;
	}

	/**
	 * Vérifie si un move permet d'aller dans un des quatre coins
	 * @param nextMove
	 * @return
	 */
	public boolean moveIsGoingToCorner(Move nextMove) {
		return (nextMove.getColTarget() == 12 && nextMove.getRowTarget() == 12)
				||(nextMove.getColTarget() == 12 && nextMove.getRowTarget() == 0)
				||(nextMove.getColTarget() == 0 && nextMove.getRowTarget() == 12)
				||(nextMove.getColTarget() == 0 && nextMove.getRowTarget() == 0);
	}

	/**
	 * Vérifie si un move permet d'aller dans un côté où un coin est accessible
	 * @param nextMove
	 * @return
	 */
	public boolean moveIsGoingOnEmptySide(Move nextMove) {
		boolean isSideEmptyAndKingThere = false;
		int column = nextMove.getColTarget();
		int row = nextMove.getRowTarget();

		// Vérifier qu'on est sur le côté droit ou gauche
		if(column == 12 || column == 0) {
			isSideEmptyAndKingThere = true;
			// Vérifier si la colonne est vide en bas
			for(int i=row; i<12; i++) {
				if(board[i][column] != EMPTY){
					isSideEmptyAndKingThere = false;	
					break;
				}
			}
			// Si la colonne n'était pas vide par en bas, on vérifie en haut
			if(isSideEmptyAndKingThere == false) {
				isSideEmptyAndKingThere = true;	
				for(int i=row; i>0; i--) {
					if(board[i][column] != EMPTY){
						isSideEmptyAndKingThere = false;
						break;
					}
				}
			}
		}

		// Vérifier qu'on est sur le côté haut ou bas
		else if(row == 0 || row == 12) {
			isSideEmptyAndKingThere = true;
			// Vérifier si la colonne est vide à droite
			for(int i=column; i<12; i++) {
				if(board[row][i] != EMPTY){
					isSideEmptyAndKingThere = false;	
					break;
				}
			}
			// Si la colonne n'était pas vide à droite, on vérifie à gauche
			if(isSideEmptyAndKingThere == false) {
				isSideEmptyAndKingThere = true;	
				for(int i=column; i>0; i--) {
					if(board[row][i] != EMPTY){
						isSideEmptyAndKingThere = false;
						break;
					}
				}
			}
		}
		return isSideEmptyAndKingThere;
	}

	/**
	 * Vérifie si le move fait en sorte qu'on est encadré de trois côtés
	 * @param nextMove
	 * @return
	 */
	public boolean isKingAlmostTrapped(int rowKing, int colKing, int rowStart, int colStart) {
		// si next move depart cest throne		
		boolean isKingInThrone = false;
		if(colStart == 6 && rowStart == 6) {
			this.board[6][6] = THRONE;
			isKingInThrone = true;
		}

		// Il y a un mur à un des quatre côtés
		if(rowKing == 0) {
			if( ((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE) && (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE))
					|| ((board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE) && (board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE))
					|| ((board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE) && (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)) ){
				if(isKingInThrone) this.board[6][6] = KING;
				return true;
			}
		}
		else if(rowKing == 12) {
			if( ((board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE) && (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE))
					|| ((board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE) && (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE))
					|| ((board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE) && (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) ){
				if(isKingInThrone) this.board[6][6] = KING;
				return true;
			}
		}
		if(colKing == 0) {
			if( ((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE) && (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE))
					|| ((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE) && (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE))
					|| ((board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE) && (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)) ){
				if(isKingInThrone) this.board[6][6] = KING;
				return true;
			}
		}
		else if(colKing == 12) {
			if( ((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE) && (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE))
					|| ((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE) && (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE))
					|| ((board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE) && (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) ){
				if(isKingInThrone) this.board[6][6] = KING;
				return true;
			}
		}
		// Encadré des 4 côtés
		if(rowKing > 0 && rowKing < 12 && colKing > 0 && colKing < 12) {	
			if( ((board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)
					)||((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
							&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
							&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)
							) || ((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
									&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
									&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)
									)||((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
											&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
											&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
											)){
				if(isKingInThrone) this.board[6][6] = KING;
				return true;
			}
		}
		if(isKingInThrone) this.board[6][6] = KING;
		return false;
	}

	/**
	 * Vérifie si le move permet de dégager un roi qui est entouré de trois côtés
	 * (vérifie s'il y a au moins 2 cases libres à côté de lui)
	 * @param nextMove
	 * @return
	 */
	public boolean canMoveFreeAlmostTrappedKing(Move nextMove) {
		int col = nextMove.getColTarget();
		int row = nextMove.getRowTarget();

		// Il y a un mur à un des quatre côtés
		if(row == 0) {
			if ((board[row][col+1] == EMPTY && board[row][col-1] == EMPTY) 
					|| (board[row][col+1] == EMPTY && board[row+1][col] == EMPTY) 
					|| (board[row][col-1] == EMPTY && board[row+1][col] == EMPTY)) return true;
		}
		else if(row == 12) {
			if((board[row][col+1] == EMPTY && board[row][col-1] == EMPTY)
					|| (board[row][col+1] == EMPTY && board[row-1][col] == EMPTY)
					|| (board[row][col-1] == EMPTY && board[row-1][col] == EMPTY)) return true;
		}
		if(col == 0) {
			if((board[row+1][col] == EMPTY && board[row-1][col] == EMPTY) 
					|| (board[row+1][col] == EMPTY && board[row][col+1] == EMPTY) 
					|| (board[row-1][col] == EMPTY && board[row][col+1] == EMPTY)) return true;
		}
		else if(col == 12) {
			if ((board[row+1][col] == EMPTY && board[row-1][col] == EMPTY) 
					|| (board[row+1][col] == EMPTY && board[row][col-1] == EMPTY)
					|| (board[row-1][col] == EMPTY && board[row][col-1] == EMPTY)) return true;
		}
		// Aucun mur
		if(row > 0 && row < 12 && col > 0 && col < 12) {	
			if((board[row][col+1] == EMPTY && board[row][col-1] == EMPTY)
					|| (board[row][col+1] == EMPTY && board[row+1][col] == EMPTY) 
					|| (board[row][col-1] == EMPTY && board[row+1][col] == EMPTY) 
					|| (board[row-1][col] == EMPTY && board[row][col+1] == EMPTY) 
					|| (board[row][col-1] == EMPTY && board[row-1][col] == EMPTY) 
					|| (board[row+1][col] == EMPTY && board[row-1][col] == EMPTY) 
					) return true;
		}
		return false;
	}

	// ************************ MÉTHODES POUR LES ROUGES *********************************************

	/**
	 * verifie si les coins sont bloquer
	 */
	public ArrayList<Move> isCornerTrap(ArrayList<Move> possibleMoves) {
		// Bloquer la case en diagonale des coins en priorité
		ArrayList<Move> bestCloseOut;

		if(this.board[1][11] == EMPTY || this.board[11][1] == EMPTY || this.board[1][1] == EMPTY || this.board[11][11] == EMPTY) {

			// Chercher tous les moves vers une case en diagonale des coins
			bestCloseOut = (ArrayList<Move>) possibleMoves.stream().filter(
					m -> 
					((m.getRowTarget() == 1 && m.getColTarget() == 1) ||
							(m.getRowTarget() == 1 && m.getColTarget() == 11) ||
							(m.getRowTarget() == 11 && m.getColTarget() == 1) ||
							(m.getRowTarget() == 11 && m.getColTarget() == 11)) && ((m.getRowStart() != 1 && m.getColStart() != 1) ||
									(m.getRowStart() != 1 && m.getColStart() != 11) ||
									(m.getRowStart() != 11 && m.getColStart() != 1) ||
									(m.getRowStart() != 11 && m.getColStart() != 11))
					).collect(Collectors.toList());

			// Vérifier qu'on ne part pas d'une case en diagonale des coins vers une autre
			//			bestCloseOut = (ArrayList<Move>) bestCloseOut.stream().filter(
			//					m -> 
			//					(m.getRowStart() == 1 && m.getColStart() == 1) ||
			//					(m.getRowStart() == 1 && m.getColStart() == 11) ||
			//					(m.getRowStart() == 11 && m.getColStart() == 1) ||
			//					(m.getRowStart() == 11 && m.getColStart() == 11)
			//					).collect(Collectors.toList());

		}
		// Ensuite former une diagonale pour fermer les coins
		else if (board[0][2] == EMPTY || board[2][0] == EMPTY || board[0][10] == EMPTY || board[2][12] == EMPTY 
				|| board[10][12] == EMPTY || board[12][10] == EMPTY || board[10][0] == EMPTY || board[12][2] == EMPTY){
			// Chercher tous les moves vers une case en diagonale des coins
			bestCloseOut = (ArrayList<Move>) possibleMoves.stream().filter(
					m -> 
					((m.getRowTarget() == 0 && m.getColTarget() == 2) ||
							(m.getRowTarget() == 2 && m.getColTarget() == 0) ||
							(m.getRowTarget() == 0 && m.getColTarget() == 10) ||
							(m.getRowTarget() == 2 && m.getColTarget() == 12) ||
							(m.getRowTarget() == 10 && m.getColTarget() == 12) ||
							(m.getRowTarget() == 12 && m.getColTarget() == 10) ||
							(m.getRowTarget() == 10 && m.getColTarget() == 0) ||
							(m.getRowTarget() == 12 && m.getColTarget() == 2)) 
					&&
					(m.getRowStart() != 1 && m.getColStart() != 1) ||
					(m.getRowStart() != 1 && m.getColStart() != 11) ||
					(m.getRowStart() != 11 && m.getColStart() != 1) ||
					(m.getRowStart() != 11 && m.getColStart() != 11) ||
					(m.getRowStart() != 0 && m.getColStart() != 2) ||
					(m.getRowStart() != 2 && m.getColStart() != 0) ||
					(m.getRowStart() != 0 && m.getColStart() != 10) ||
					(m.getRowStart() != 2 && m.getColStart() != 12) ||
					(m.getRowStart() != 10 && m.getColStart() != 12) ||
					(m.getRowStart() != 12 && m.getColStart() != 10) ||
					(m.getRowStart() != 10 && m.getColStart() != 0) ||
					(m.getRowStart() != 12 && m.getColStart() != 2)
					).collect(Collectors.toList());;

					// Vérifier qu'on ne part pas d'une case à bloquer vers une autre
					//					bestCloseOut = (ArrayList<Move>) bestCloseOut.stream().filter(
					//							m -> 
					//							(m.getRowStart() == 1 && m.getColStart() == 1) ||
					//							(m.getRowStart() == 1 && m.getColStart() == 11) ||
					//							(m.getRowStart() == 11 && m.getColStart() == 1) ||
					//							(m.getRowStart() == 11 && m.getColStart() == 11) ||
					//							(m.getRowStart() == 0 && m.getColStart() == 2) ||
					//							(m.getRowStart() == 2 && m.getColStart() == 0) ||
					//							(m.getRowStart() == 0 && m.getColStart() == 10) ||
					//							(m.getRowStart() == 2 && m.getColStart() == 12) ||
					//							(m.getRowStart() == 10 && m.getColStart() == 12) ||
					//							(m.getRowStart() == 12 && m.getColStart() == 10) ||
					//							(m.getRowStart() == 10 && m.getColStart() == 0) ||
					//							(m.getRowStart() == 12 && m.getColStart() == 2)
					//							).collect(Collectors.toList());;
					return bestCloseOut;
		}
		// Toutes les cases des coins sont déjà occupées (par un rouge ou par un noir)
		else {
			return null;
		}

		return null;
	}

	/**
	 * Vérifie si le move mange un pion
	 * @param nextMove
	 * @return
	 */
	public boolean isMoveBesideKing(Move nextMove) {
		int col = nextMove.getColTarget();
		int row = nextMove.getRowTarget();

		if((row-1 >= 0 && row-1 <= 12) && (this.board[row-1][col] == KING)) return true;
		if((row+1 >= 0 && row+1 <= 12) && (this.board[row+1][col] == KING))	return true;
		if((col-1 >= 0 && col-1 <= 12) && (this.board[row][col-1] == KING))	return true;
		if((col+1 >= 0 && col+1 <= 12) && (this.board[row][col+1] == KING)) return true;

		//les dernieres lignes et colonnes
		if((row==0 && (col>=2 || col<=10)) && (this.board[row+1][col]==KING || this.board[row][col+1]==KING || this.board[row][col-1]==KING  )) return true;
		if((row==12 && (col>=2 || col<=10)) && (this.board[row-1][col]==KING || this.board[row][col+1]==KING || this.board[row][col-1]==KING  )) return true;
		if((col==0 && (row>=2 || row<=10)) && (this.board[row][col+1]==KING || this.board[row+1][col]==KING || this.board[row-1][col]==KING  )) return true;
		if((col==12 && (row>=2 || row<=10)) && (this.board[row][col-1]==KING || this.board[row+1][col]==KING || this.board[row-1][col]==KING  )) return true;

		if((row==0 && col==1) && (this.board[row+1][col]==KING || this.board[row][col+1]==KING ))return true;
		if((row==0 && col==11) && (this.board[row+1][col]==KING || this.board[row][col-1]==KING ))return true;

		if((row==12 && col==1) && (this.board[row-1][col]==KING || this.board[row][col+1]==KING ))return true;
		if((row==12 && col==11) && (this.board[row-1][col]==KING || this.board[row][col-1]==KING ))return true;

		if((row==1 && col==0) && (this.board[row+1][col]==KING || this.board[row][col+1]==KING ))return true;
		if((row==11 && col==0) && (this.board[row-1][col]==KING || this.board[row][col+1]==KING ))return true;

		if((row==1 && col==12) && (this.board[row+1][col]==KING || this.board[row][col-1]==KING ))return true;
		if((row==11 && col==12) && (this.board[row-1][col]==KING || this.board[row][col-1]==KING ))return true;


		return false;
	}

	//revoir la methode
	public boolean moveKillPawns(Move nextMove) {
		int col = nextMove.getColTarget();
		int row = nextMove.getRowTarget();

		if(row>=2 && col<=10 && col>=2 && row<=10) 
		{
			if((board[row-2][col] == RED && board[row-1][col] == BLACK) || (board[row+2][col] == RED || board[row+1][col] == BLACK)
					|| (board[row][col-2] == RED && board[row][col-1] == BLACK) || (board[row][col+2] == RED || board[row][col+1] == BLACK))
			{
				return true;
			}
		}

		//		if((row==12 || row==0) && col>=2 && row<=10) 
		//		{
		//			if(board[row][col+2] == RED || board[row+2][col] == RED || board[row][col-2] == RED || board[row][col+2] == RED)
		//			{
		//				return true;
		//			}
		//		}
		//		

		return false;

	}

	// TODO
	public boolean canMoveTrapKing(Move nextMove) {
		int col = nextMove.getColTarget();
		int row = nextMove.getRowTarget();

		boolean trap = false;

		this.board[row][col]=RED;

		// Il y a un mur à un des quatre côtés
		if(rowKing == 0) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				//return true;
				trap =true;
		}
		else if(rowKing == 12) {
			if((board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				//return true;
				trap =true;
		}
		if(colKing == 0) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE))
				//return true;
				trap =true;
		}
		else if(colKing == 12) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				//return true;
				trap =true;
		}
		// Encadré des 4 côtés
		if(rowKing > 0 && rowKing < 12 && colKing > 0 && colKing < 12) {	
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				//return true;
				trap =true;
		}


		this.board[row][col]=EMPTY;

		return trap;
		//return false;
	}





	// ************************ MÉTHODES DÉCHETS *********************************************
	// TODO
	private int distanceManhattanForKing(int col, int row) {
		return Math.abs(col-this.colKing)+Math.abs(row-this.rowKing);
	}

	/**
	 * Trouve le nombre de pions morts du joueur recu
	 * @param player
	 * @return le nombre de pions tués
	 */
	private int getNumberOfKilledPawns(int player) {
		int numberOfPawnsInitially = (player == BLACK) ? 12: 24;
		int numberOfKilledPawns = numberOfPawnsInitially - getNumberOfPawnsOnBoardFor(player);
		return numberOfKilledPawns;
	}

	// TODO : connecter ça avec le AlphaBeta
	/**
	 * Stratégie ROUGE pour bloquer toutes les sorties (12 pions rouges nécessaires)
	Avant même d'essayer de manger les pions adverses (sauf si on peut manger le roi)
	Ensuite, quand les sorties sont bloquées,dans l'idéal les 12 autres pions rouges 
	jouent en 12 vs 13 pour capturer les adversaires. Les pions qui bloquent les sorties
	ne bougent que si ils peuvent capturer le roi
	 * @param possibleMoves
	 * @return
	 */
	private ArrayList<Move> blockExit(ArrayList<Move> possibleMoves) {
		// Bloquer la case en diagonale des coins en priorité
		ArrayList<Move> bestCloseOut;
		if(board[1][1] == EMPTY || board[1][11] == EMPTY || board[11][1] == EMPTY || board[11][11] == EMPTY) {
			// Chercher tous les moves vers une case en diagonale des coins
			bestCloseOut = (ArrayList<Move>) possibleMoves.stream().filter(
					m -> 
					(m.getRowTarget() == 1 && m.getColTarget() == 1) ||
					(m.getRowTarget() == 1 && m.getColTarget() == 11) ||
					(m.getRowTarget() == 11 && m.getColTarget() == 1) ||
					(m.getRowTarget() == 11 && m.getColTarget() == 11)
					).collect(Collectors.toList());;

					// Vérifier qu'on ne part pas d'une case en diagonale des coins vers une autre
					bestCloseOut = (ArrayList<Move>) bestCloseOut.stream().filter(
							m -> 
							(m.getRowStart() == 1 && m.getColStart() == 1) ||
							(m.getRowStart() == 1 && m.getColStart() == 11) ||
							(m.getRowStart() == 11 && m.getColStart() == 1) ||
							(m.getRowStart() == 11 && m.getColStart() == 11)
							).collect(Collectors.toList());;
							return bestCloseOut;
		}
		// Ensuite former une diagonale pour fermer les coins
		else if (board[0][2] == EMPTY || board[2][0] == EMPTY || board[0][10] == EMPTY || board[2][12] == EMPTY 
				|| board[10][12] == EMPTY || board[12][10] == EMPTY || board[10][0] == EMPTY || board[12][2] == EMPTY){
			// Chercher tous les moves vers une case en diagonale des coins
			bestCloseOut = (ArrayList<Move>) possibleMoves.stream().filter(
					m -> 
					(m.getRowTarget() == 0 && m.getColTarget() == 2) ||
					(m.getRowTarget() == 2 && m.getColTarget() == 0) ||
					(m.getRowTarget() == 0 && m.getColTarget() == 10) ||
					(m.getRowTarget() == 2 && m.getColTarget() == 12) ||
					(m.getRowTarget() == 10 && m.getColTarget() == 12) ||
					(m.getRowTarget() == 12 && m.getColTarget() == 10) ||
					(m.getRowTarget() == 10 && m.getColTarget() == 0) ||
					(m.getRowTarget() == 12 && m.getColTarget() == 2)
					).collect(Collectors.toList());;

					// Vérifier qu'on ne part pas d'une case à bloquer vers une autre
					bestCloseOut = (ArrayList<Move>) bestCloseOut.stream().filter(
							m -> 
							(m.getRowStart() == 1 && m.getColStart() == 1) ||
							(m.getRowStart() == 1 && m.getColStart() == 11) ||
							(m.getRowStart() == 11 && m.getColStart() == 1) ||
							(m.getRowStart() == 11 && m.getColStart() == 11) ||
							(m.getRowStart() == 0 && m.getColStart() == 2) ||
							(m.getRowStart() == 2 && m.getColStart() == 0) ||
							(m.getRowStart() == 0 && m.getColStart() == 10) ||
							(m.getRowStart() == 2 && m.getColStart() == 12) ||
							(m.getRowStart() == 10 && m.getColStart() == 12) ||
							(m.getRowStart() == 12 && m.getColStart() == 10) ||
							(m.getRowStart() == 10 && m.getColStart() == 0) ||
							(m.getRowStart() == 12 && m.getColStart() == 2)
							).collect(Collectors.toList());;
							return bestCloseOut;
		}
		// Toutes les cases des coins sont déjà occupées (par un rouge ou par un noir)
		else {
			return null;
		}
	}

	// TODO METHODE MARCHE PAS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public boolean moveIsGoingOnEmptyRowOrColumn(Move nextMove) {
		int column = nextMove.getColTarget();
		int row = nextMove.getRowTarget();
		// En bas
		for(int i=row; i<13; i++) {
			if(board[i][column] != EMPTY && board[i][column] != THRONE) break;
			if(i==12) return true;
		}
		// En haut
		for(int i=row; i>=0; i--) {
			if(board[i][column] != EMPTY || board[i][column] != THRONE)	break;
			if(i==0) return true;
		}
		// À droite
		for(int i=column; i<13; i++) {
			if(board[row][i] != EMPTY || board[row][i] != THRONE) break;
			if(i==12) return true;
		}
		// À gauche
		for(int i=column; i>=0; i--) {
			if(board[row][i] != EMPTY || board[row][i] != THRONE) break;
			if(i==0) return true;
		}
		return false;
	}

	void printBoard() {
		String board = Arrays.deepToString(this.board).replace("], ", "]\n").replace("[[", "[").replace("]]", "]");
		board = board.replace('2', 'N').replace('4', 'R').replace('1', 'C').replace('5', 'K').replace('6', 'T');
		System.out.println("\n"+board);
	}

	public void printPossibleMoves() {
		// NOIR
		String possiblesMovesBlack= "";
		for(Move m : findPossibleMoves(BLACK)) possiblesMovesBlack += m.toString()+" / ";
		System.out.println("\nCoups possibles pour les NOIRS: "+possiblesMovesBlack);
		System.out.println("Nombre de coups possibles pour les NOIRS: "+findPossibleMoves(BLACK).size());

		// ROUGE 
		//		String possiblesMoves= "";
		//		for(Move m : findPossibleMoves(RED)) possiblesMoves+=m.toString()+" / ";
		//		System.out.println("Coups possibles pour les ROUGES: "+possiblesMoves);
		//		System.out.println("Nombre de coups possibles pour les ROUGES: "+findPossibleMoves(RED).size());
	}

}