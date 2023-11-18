import java.util.*;

class Board implements Cloneable
{
	private final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5, THRONE = 6;
	private int[][] board;
	private int rowKing, colKing;
	private Map<Integer, String> conversionNumberToLetterColumn = new HashMap<>();
	private Map<String, Integer> conversionLetterToNumberColumn = new HashMap<>();
	private ArrayList<String> eliminatedPawns = new ArrayList<>();

	/**
	 * Initialise le plateau de la console
	 * @param s
	 */
	public Board(String s) {
		board = new int[13][13];
		String[] boardValues = s.split(" ");
		int x=0,y=0;
		for(int i=0; i<boardValues.length;i++){
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

		// Créer deux maps permettant de convertir une lettre en indice de tableau et vice-versa
		char[] rows = "ABCDEFGHIJKLM".toCharArray();
		for(int i=0; i<rows.length;i++) {
			conversionNumberToLetterColumn.put(i, String.valueOf(rows[i]));
			conversionLetterToNumberColumn.put(String.valueOf(rows[i]), i);
		}
	}

	/**
	 * Générer les coups possibles pour le joueur recu
	 * @param player
	 * @return la liste des coups possibles pour le joueur
	 */
	public ArrayList<Move> findPossibleMoves(int player) {
		ArrayList<Move> possibleMoves = new ArrayList<>();

		for(int i=0; i<board.length; i++) {
			for(int j=0; j<board[i].length; j++) {

				// Si on a un player rouge, on cherche pour tous les pions rouges du board s'ils ont des cases vides aux alentours
				if(player == RED && board[i][j] == RED) {
					// vérifier en bas du pion
					for(int row=i+1; row<13;row++) {
						if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
						else if(board[row][j] == THRONE) continue; // un pion peut sauter par desssus le throne
						else break; // on quitte la boucle si un pion nous bloque le chemin
					}
					// vérifier en haut du pion
					for(int row=i-1; row>=0;row--) {
						if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
						else if(board[row][j] == THRONE) continue;
						else break;
					}
					// vérifier à droite du pion
					for(int column=j+1; column<13;column++) {
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

	private boolean isValidMove(int oldRow, int oldColumn, int newRow, int newColumn){
		// il n'y a pas de pion sur la case de départ
		if(board[oldRow][oldColumn] == EMPTY || board[oldRow][oldColumn] == CORNER || board[oldRow][oldColumn] == THRONE) return false;

		// il y a déjà un pion sur la case d'arrivée
		if(board[newRow][newColumn] == RED || board[newRow][newColumn] == BLACK || board[newRow][newColumn] == KING) return false;

		// il n'y a pas de changement de case
		if(oldColumn == newColumn && oldRow == newRow) return  false;

		// le mouvement n'est pas vertical ou horizontal
		if(oldColumn != newColumn && oldRow != newRow) return  false;

		// un pion, qui n'est pas le roi, essaie d'aller sur un coin ou le throne
		if((board[newRow][newColumn] == CORNER || board[newRow][newColumn] == THRONE) && board[oldRow][oldColumn] != KING) return false;

		// Verifie qu'il n'y a pas d'autres pieces entre la position initiale et finale pour un mouvement VERTICAL
		if (oldRow == newRow && oldColumn != newColumn) {
			if (newColumn > oldColumn) {
				for (int i = oldColumn + 1; i <= newColumn; i++) {
					if (board[oldRow][i] > 1 && board[oldRow][i] < 6) return false;
				}
			} else {
				for (int i = newColumn; i < oldColumn; i++) {
					if (board[oldRow][i] > 1 && board[oldRow][i] < 6) return false;
				}
			}
		}

		// Verifie qu'il n'y a pas d'autres pieces entre la position initiale et finale pour un mouvement HORIZONTAL
		if (oldColumn == newColumn && oldRow != newRow) {
			if (newRow > oldRow) {
				for (int i = oldRow + 1; i <= newRow; i++) {
					if (board[i][oldColumn] > 1) return false;
				}
			} else {
				for (int i = newRow; i < oldRow; i++) {
					if (board[i][oldColumn] > 1) return false;
				}
			}
		}
		return true;
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
		// Format D6D7 (TODO à enlever qd le ai sera fait pcq on recoit tjrs le 1e format)
		else {
			int indexOfSecondLetter = 1;
			while (indexOfSecondLetter < move.length() && !Character.isLetter(move.charAt(indexOfSecondLetter))) {
				indexOfSecondLetter++;
			}
			start = move.substring(0, indexOfSecondLetter).trim();
			end = move.substring(indexOfSecondLetter).trim();
		}

		int oldColumn = conversionLetterToNumberColumn.get(start.substring(0, 1));
		int newColumn = conversionLetterToNumberColumn.get(end.substring(0, 1));
		int oldRow = Integer.parseInt(start.substring(1))-1;
		int newRow = Integer.parseInt(end.substring(1))-1;

		// TODO Condition isValidMove à enlever quand le ai sera terminé pcq ca sera tjrs valide normalement!!!!!!!!!
		if(isValidMove(oldRow, oldColumn, newRow, newColumn)) {
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
			System.out.println("Coup joué: "+oldRow+" "+oldColumn+" - "+newRow+" "+newColumn);
			System.out.println("eliminatedPawns size : "+eliminatedPawns.size());
		}
//		else System.out.println("Coup invalide: "+oldRow+" "+oldColumn+" - "+newRow+" "+newColumn);
	}

	/**
	 * Vérifie s'il y a un gagnant
	 * @param player
	 * @return 100 si le joueur gagne, -100 s'il perd, et 0 s'il n'y a aucun gagnant
	 */
	public int evaluate(int player) {
		int victory = 100, defeat = -100, draw = 0;
		if(player == RED) {
			if(isKingTrapped()) return victory;
			else if(isKingInCorner()) return defeat;
		}
		else {
			if(isKingTrapped()) return defeat;
			else if(isKingInCorner()) return victory;
		}
		return draw;
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

	private boolean isKingInCorner() {
		return (board[0][0] == KING || board[0][12] == KING || board[12][0] == KING || board[12][12] == KING);
	}

	/**
	 * Annule le mouvement recu et restore les pions tués par ce mouvement
	 * @param move
	 */
	public void cancelMove(Move move) {	
		int oldRow = move.getRowStart()-1;
		int oldColumn = move.getColStart();
		int newRow = move.getRowTarget()-1;
		int newColumn = move.getColTarget();

		int player = this.board[newRow][newColumn];

		// Si c'est le roi qui a bougé
		if(player == KING) {
			this.board[newRow][newColumn] = EMPTY;
			this.board[6][6] = THRONE;
			this.board[oldRow][oldColumn] = KING;
			this.rowKing = oldRow;
			this.colKing = oldColumn;
		}
		else {
			this.board[oldRow][oldColumn] = player;
			this.board[newRow][newColumn] = EMPTY;
		}

		// TODO verifier ca!!!!!!!!!
		// Remettre les pions tués
		for(String pawn: eliminatedPawns) {
			int rowOfPawn = Integer.parseInt(pawn.substring(1));
			int columnOfPawn = conversionLetterToNumberColumn.get(pawn.substring(0, 1));
			this.board[rowOfPawn][columnOfPawn] = opponentOfPlayer(player);
		}
		eliminatedPawns.clear();
	}

	/**
	 * Selon la position du pion joué, vérifie s'il y a un pion adverse aux alentours
	 * @param newRow rangée du pion joué
	 * @param newColumn colonne du pion joué
	 * @param player joueur ayant effectué le mouvement
	 */
	private void  verifyPawnsElimination(int newRow, int newColumn, int player) {
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
	 * Le pion tué est ajouté à la liste des pions mangées
	 * @param up
	 * @param down
	 * @param right
	 * @param left
	 * @param newRow
	 * @param newColumn
	 * @param player
	 */
	private void checkRowAndColumn(boolean up, boolean down, boolean right, boolean left, int newRow, int newColumn, int player) {
		int opponent = opponentOfPlayer(player);
		if(up) {
			if(board[newRow-1][newColumn] == opponent && (board[newRow-2][newColumn] == player || board[newRow-2][newColumn] == CORNER || (newRow-2==THRONE && newColumn==THRONE) )) {
				board[newRow-1][newColumn] = EMPTY;
				eliminatedPawns.add(conversionNumberToLetterColumn.get(newColumn)+""+(newRow-1));
				System.out.println("Pion "+opponent+" éliminé à la position: ["+(newRow-1)+", "+newColumn+"]");
			}
		}
		if(down) {
			if(board[newRow+1][newColumn] == opponent && (board[newRow+2][newColumn] == player || board[newRow+2][newColumn] == CORNER || (newRow+2==THRONE && newColumn==THRONE) )) {
				board[newRow+1][newColumn] = EMPTY;
				eliminatedPawns.add(conversionNumberToLetterColumn.get(newColumn)+""+(newRow+1));
				System.out.println("Pion "+opponent+" éliminé à la position: ["+(newRow+1)+", "+newColumn+"]");
			}
		}
		if(right) {
			if(board[newRow][newColumn+1] == opponent && (board[newRow][newColumn+2] == player || board[newRow][newColumn+2] == CORNER || (newRow==THRONE && newColumn+2==THRONE) )) {
				board[newRow][newColumn+1] = EMPTY;
				eliminatedPawns.add(conversionNumberToLetterColumn.get(newColumn+1)+""+newRow);
				System.out.println("Pion "+opponent+" éliminé à la position: ["+newRow+", "+(newColumn+1)+"]");
			}
		}
		if(left) {
			if(board[newRow][newColumn-1] == opponent && (board[newRow][newColumn-2] == player || board[newRow][newColumn-2] == CORNER || (newRow==THRONE && newColumn-2==THRONE) )) {
				board[newRow][newColumn-1] = EMPTY;
				eliminatedPawns.add(conversionNumberToLetterColumn.get(newColumn-1)+""+newRow);
				System.out.println("Pion "+opponent+" éliminé à la position: ["+newRow+", "+(newColumn-1)+"]");
			}
		}
	}

	private int opponentOfPlayer(int player) {
		return player == RED ? BLACK : RED; 
	}

	public void printPossibleMoves(int player) {
		String possiblesMoves= "";
		for(Move m : findPossibleMoves(player)) {
			possiblesMoves+=conversionNumberToLetterColumn.get(m.getRowStart())+""+m.getColStart()+"-"+conversionNumberToLetterColumn.get(m.getRowTarget())+""+m.getColTarget()+" / ";
		}
		System.out.println("Coups possibles pour "+player+": "+possiblesMoves);
	}

	public void printBoard() {
		System.out.println();
		for (int x = 0; x < 13; x++) {
			System.out.print(String.format("%3d", 0 + x) + " |");
			for (int y = 0; y < 13; y++) {
				System.out.print("  " + board[y][x]);
			}
			System.out.println();
		}
		System.out.println("_____________________________________________");
		System.out.println("       A  B  C  D  E  F  G  H  I  J  K  L  M\n");
	}

	public int[][] getBoard() {
		return board;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}