import java.util.*;

class Board
{
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

		boolean isKingInCorner = isKingInCorner();
		boolean isKingTrapped = isKingTrapped();
		
		if(player == RED) {
			// l'objectif des rouge est de tuer le roi et tout les pions adverse
			// prioriser l'élimination des pions.
			
			if(isKingInCorner) return defeat;
			if(isKingTrapped) return victory;
//			if(isKingInCorner && depth == 3) return defeat;
//			if(isKingInCorner && depth == 2) return defeat2;
//			if(isKingInCorner && depth == 1) return defeat3;
//			if(isKingInCorner && depth == 0) return defeat4;
//			if(isKingTrapped && depth == 3) return victory;
//			if(isKingTrapped && depth == 2) return victory2;
//			if(isKingTrapped && depth == 1) return victory3;
//			if(isKingTrapped && depth == 0) return victory4;
			
			//  TODO Ajouter des points quand le move tue des pions adverses ou perd un de ses propres pions
			// else encadrer le roi
			//			else numberOfKilledPawns(player);
		}
		else {			
			// l'objectif des noirs est d'extraire le roi vers un des quatres coins 
			// le second objectifs est de tuer les pions adverse.
			if(isKingInCorner && depth == 3) return victory;
			if(isKingInCorner && depth == 2) return victory2;
			if(isKingInCorner && depth == 1) return victory3;
			if(isKingInCorner && depth == 0) return victory4;
			if(isKingTrapped && depth == 3) return defeat;
			if(isKingTrapped && depth == 2) return defeat2;
			if(isKingTrapped && depth == 1) return defeat3;
			if(isKingTrapped && depth == 0) return defeat4;
			
//			if(isKingInCorner) return victory;
//			if(isKingTrapped) return defeat;
			
			// else // regarder qd le roi est proche du trone
			// else pion
		}
		//		int numberOfBlackKilledPawns = numberOfKilledPawns(BLACK);
		//		int numberOfRedKilledPawns = numberOfKilledPawns(BLACK);

		return draw;
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
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn)+""+MapConversion.getConversionNumberToLetterRow().get(newRow-1);
					//					System.out.println("Pion noir éliminé à la position "+position);
				}
			}
			if(down) {
				if(board[newRow+1][newColumn] == BLACK && (board[newRow+2][newColumn] == player || board[newRow+2][newColumn] == CORNER || (newRow+2 == THRONE && newColumn == THRONE) )) {
					board[newRow+1][newColumn] = EMPTY;
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn)+""+MapConversion.getConversionNumberToLetterRow().get(newRow+1);
					//					System.out.println("Pion noir éliminé à la position "+position);
				}
			}
			if(right) {
				if(board[newRow][newColumn+1] == BLACK && (board[newRow][newColumn+2] == player || board[newRow][newColumn+2] == CORNER || (newRow == THRONE && newColumn+2 == THRONE) )) {
					board[newRow][newColumn+1] = EMPTY;
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn+1)+""+MapConversion.getConversionNumberToLetterRow().get(newRow);
					//					System.out.println("Pion noir éliminé à la position "+position);
				}
			}
			if(left) {
				if(board[newRow][newColumn-1] == BLACK && (board[newRow][newColumn-2] == player || board[newRow][newColumn-2] == CORNER || (newRow == THRONE && newColumn-2 == THRONE) )) {
					board[newRow][newColumn-1] = EMPTY;
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn-1)+""+MapConversion.getConversionNumberToLetterRow().get(newRow);
//							getConversionNumberToLetterColumn.get(newColumn-1)+""+conversionNumberToLetterRow.get(newRow);
					//					System.out.println("Pion noir éliminé à la position "+position);
				}
			}
		}
		else if(player == BLACK) {
			if(up) {
				if(board[newRow-1][newColumn] == RED && (board[newRow-2][newColumn] == player || board[newRow-2][newColumn] == KING || board[newRow-2][newColumn] == CORNER || (newRow-2==THRONE && newColumn==THRONE) )) {
					board[newRow-1][newColumn] = EMPTY;
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn)+""+MapConversion.getConversionNumberToLetterRow().get(newRow-1);
//							conversionNumberToLetterColumn.get(newColumn)+""+conversionNumberToLetterRow.get(newRow-1);
					//					System.out.println("Pion rouge éliminé à la position "+position);
				}
			}
			if(down) {
				if(board[newRow+1][newColumn] == RED && (board[newRow+2][newColumn] == player || board[newRow+2][newColumn] == KING || board[newRow+2][newColumn] == CORNER || (newRow+2==THRONE && newColumn==THRONE) )) {
					board[newRow+1][newColumn] = EMPTY;
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn)+""+MapConversion.getConversionNumberToLetterRow().get(newRow+1);
//							conversionNumberToLetterColumn.get(newColumn)+""+conversionNumberToLetterRow.get(newRow+1);
					//					System.out.println("Pion rouge éliminé à la position "+position);
				}
			}
			if(right) {
				if(board[newRow][newColumn+1] == RED && (board[newRow][newColumn+2] == player || board[newRow][newColumn+2] == KING || board[newRow][newColumn+2] == CORNER || (newRow==THRONE && newColumn+2==THRONE) )) {
					board[newRow][newColumn+1] = EMPTY;
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn+1)+""+MapConversion.getConversionNumberToLetterRow().get(newRow);
//							conversionNumberToLetterColumn.get(newColumn+1)+""+conversionNumberToLetterRow.get(newRow);
					//					System.out.println("Pion rouge éliminé à la position "+position);
				}
			}
			if(left) {
				if(board[newRow][newColumn-1] == RED && (board[newRow][newColumn-2] == player || board[newRow][newColumn-2] == KING || board[newRow][newColumn-2] == CORNER || (newRow==THRONE && newColumn-2==THRONE) )) {
					board[newRow][newColumn-1] = EMPTY;
					String position = MapConversion.getConversionNumberToLetterColumn().get(newColumn-1)+""+MapConversion.getConversionNumberToLetterRow().get(newRow);
//							conversionNumberToLetterColumn.get(newColumn-1)+""+conversionNumberToLetterRow.get(newRow);
					//					System.out.println("Pion rouge éliminé à la position "+position);
				}
			}
		}
	}

	public void printBoard() {
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

	public int[][] getBoard() {
		return board;
	}

	public boolean moveIsKing(Move nextMove) {
		return nextMove.getColStart() == this.colKing && nextMove.getRowStart() == this.rowKing;
	}

	public boolean moveIsWinner(Move nextMove) {
		return (nextMove.getColTarget() == 12 && nextMove.getRowTarget() == 12)
				||(nextMove.getColTarget() == 12 && nextMove.getRowTarget() == 0)
				||(nextMove.getColTarget() == 0 && nextMove.getRowTarget() == 12)
				||(nextMove.getColTarget() == 0 && nextMove.getRowTarget() == 0);
	}

	public boolean canMoveTrapKing(Move nextMove) {
		int col = nextMove.getColTarget();
		int row = nextMove.getRowTarget();

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
	
	private int distanceManhattan(int col, int row) {
		return Math.abs(col-this.colKing)+Math.abs(row-this.rowKing);
	}

}