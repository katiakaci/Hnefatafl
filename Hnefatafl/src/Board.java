import java.util.*;

class Board
{
	private final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5, THRONE = 6;
	private int[][] board;
	private int rowKing, colKing;
	private Map<Integer, String> conversionNumberToLetterColumn = new HashMap<>();
	private Map<String, Integer> conversionLetterToNumberColumn = new HashMap<>();
	private ArrayList<String> eliminatedPawns = new ArrayList<>();

	/**
	 * Initialise le plateau de la console
	 * @param sBoard
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

		char[] rows = "ABCDEFGHIJKLM".toCharArray();
		for(int i=0; i<rows.length;i++) {
			conversionNumberToLetterColumn.put(i, String.valueOf(rows[i]));
			conversionLetterToNumberColumn.put(String.valueOf(rows[i]), i);
		}
	}

	// TODO Vérifier cette methode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
	// Ajouter coin et trone si cest le roi ligne 60, 65 etc
	/**
	 * Générer les coups possibles pour les noirs, les rouges et le roi
	 * @return la liste de coups possibles
	 */
	public ArrayList<Move> findPossibleMoves(int player) {
		ArrayList<Move> possibleMoves = new ArrayList<>();
		for(int i=0;i<board.length;i++) {
			for(int j=0; j<board[i].length;j++) {
				if(player == RED) {
					if(board[i][j] == RED) {
						// vérifier en bas du pion
						for(int row=i+1; row<13;row++) {
							// si on rencontre un pion
							if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
							else break;
						}
						// vérifier en haut du pion
						for(int row=i-1; row>=0;row--) {
							if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
							else break;
						}
						// vérifier à droite du pion
						for(int column=j+1; column<13;column++) {
							if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
							else break;
						}
						// vérifier à gauche du pion
						for(int column=j-1; column>=0;column--) {
							if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
							else break;
						}
					}
					
				}
				else {
					if(board[i][j] == BLACK || board[i][j] == KING) {
						// vérifier en bas du pion
						for(int row=i+1; row<13;row++) {
							// si on rencontre un pion
							if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
							else break;
						}
						// vérifier en haut du pion
						for(int row=i-1; row>=0;row--) {
							if(board[row][j] == EMPTY) possibleMoves.add(new Move(i,j,row,j));
							else break;
						}
						// vérifier à droite du pion
						for(int column=j+1; column<13;column++) {
							if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
							else break;
						}
						// vérifier à gauche du pion
						for(int column=j-1; column>=0;column--) {
							if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
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

	/**
	 * Déplace le pion dans le board à la position recue
	 * @param move
	 * @param mark
	 */
	public void play(String move, int player, boolean fromKeyboard) {
		move = move.trim().toUpperCase();
		String start, end;

		if(move.contains("-")) {
			String[] positions = move.split("-");
			start = positions[0].trim();
			end = positions[1].trim();
		}
		else {
			int index = 1;
			while (index < move.length() && !Character.isLetter(move.charAt(index))) {
				index++;
			}
			start = move.substring(0, index).trim();
			end = move.substring(index).trim();
		}

		int oldRow = conversionLetterToNumberColumn.get(start.substring(0, 1));
		int newRow = conversionLetterToNumberColumn.get(end.substring(0, 1));
		int oldColumn, newColumn;
		if(fromKeyboard) {
			oldColumn = Integer. parseInt(start.substring(1))-1;
			newColumn = Integer. parseInt(end.substring(1))-1;
		}
		else {
			oldColumn = Integer. parseInt(start.substring(1));
			newColumn = Integer. parseInt(end.substring(1));
		}

		//Les lignes et colonnes inverser.
//		System.out.println(oldRow+" "+ oldColumn+" "+ newRow+" "+ newColumn);
//		if(isValidMove(oldRow, oldColumn, newRow, newColumn))
		if(isValidMove(oldColumn,oldRow,newColumn,newRow)) {
			// Si c'est le roi qui a bougé
			
			//clonne et ligne inversé
//			if(this.board[oldRow][oldColumn] == KING) {
//				this.board[oldRow][oldColumn] = EMPTY;
//				this.board[6][6] = THRONE;
//				this.board[newRow][newColumn] = KING;
//				this.rowKing = newRow;
//				this.colKing = newColumn;
//			}
			if(this.board[oldColumn][oldRow] == KING) {
				this.board[oldColumn][oldRow] = EMPTY;
				this.board[6][6] = THRONE;
				this.board[newColumn][newRow] = KING;
				this.rowKing = newColumn ;
				this.colKing = newRow;
			}
			else {
				// colonne et ligne inversé
//				this.board[oldRow][oldColumn] = EMPTY;
//				this.board[newRow][newColumn] = player;
				
				this.board[oldColumn][oldRow] = EMPTY;
				this.board[newColumn][newRow] = player;
			}		
			checkIfPawnEliminated(newRow, newColumn, player);
		}
		else System.out.println("Coup invalide");
		
		printBoard();
	}

	public int evaluate(int player) {
		int victory = 100;
		int defeat = -100;
		int draw = 0;

		if(player == RED) {
			if(isKingTrapped()) return victory;
			else if(isKingInCorner()) return defeat;
		}
		else if(player == BLACK) {
			if(isKingTrapped()) return defeat;
			else if(isKingInCorner()) return victory;
		}
		return draw;
	}

	private boolean isKingTrapped() {
		// Il y a un mur à un des quatre côtés
		if(rowKing == 0) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing][colKing+1] == RED || board[rowKing][colKing+1] == CORNER || board[rowKing][colKing+1] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				return true;
		}
		if(rowKing == 12) {
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
		if(colKing == 12) {
			if((board[rowKing+1][colKing] == RED || board[rowKing+1][colKing] == CORNER || board[rowKing+1][colKing] == THRONE)
					&& (board[rowKing-1][colKing] == RED || board[rowKing-1][colKing] == CORNER || board[rowKing-1][colKing] == THRONE)
					&& (board[rowKing][colKing-1] == RED || board[rowKing][colKing-1] == CORNER || board[rowKing][colKing-1] == THRONE)) 
				return true;
		}
		// Encadré des 4 côtés
		if(rowKing>0 && rowKing<12 && colKing>0 && colKing<12) {	
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

	public void cancelMove(Move move) {	
		int oldRow = move.getRowStart();
		int oldColumn = move.getColStart();
		int newRow = move.getRowTarget();
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
		// Remettre les pions tués
		for(String pawn: eliminatedPawns) {
			this.board[Integer.parseInt(pawn.substring(1))][conversionLetterToNumberColumn.get(pawn.substring(0, 1))] = opponentOfPlayer(player);
		}
		eliminatedPawns.clear();
	}

	private void  checkIfPawnEliminated(int newRow, int newColumn, int player) {
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

	private void checkRowAndColumn(boolean up, boolean down, boolean right, boolean left, int newRow, int newColumn, int player) {
		int opponent = opponentOfPlayer(player);
		if(up) {
			if(board[newRow-1][newColumn] == opponent && 
					(board[newRow-2][newColumn] == player || board[newRow-2][newColumn] == CORNER || (newRow-2==THRONE && newColumn==THRONE) )) {
				board[newRow-1][newColumn] = EMPTY;
				eliminatedPawns.add(conversionNumberToLetterColumn.get(newColumn)+""+(newRow-1));
				System.out.println("Pion "+opponent+" éliminé à la position: ["+(newRow-1)+", "+newColumn+"]");
			}
		}
		if(down) {
			if(board[newRow+1][newColumn] == opponent && 
					(board[newRow+2][newColumn] == player || board[newRow+2][newColumn] == CORNER || (newRow+2==THRONE && newColumn==THRONE) )) {
				board[newRow+1][newColumn] = EMPTY;
				eliminatedPawns.add(conversionNumberToLetterColumn.get(newColumn)+""+(newRow+1));
				System.out.println("Pion "+opponent+" éliminé à la position: ["+(newRow+1)+", "+newColumn+"]");
			}
		}
		if(right) {
			if(board[newRow][newColumn+1] == opponent && 
					(board[newRow][newColumn+2] == player || board[newRow][newColumn+2] == CORNER || (newRow==THRONE && newColumn+2==THRONE) )) {
				board[newRow][newColumn+1] = EMPTY;
				eliminatedPawns.add(conversionNumberToLetterColumn.get(newColumn+1)+""+newRow);
				System.out.println("Pion "+opponent+" éliminé à la position: ["+newRow+", "+(newColumn+1)+"]");
			}
		}
		if(left) {
			if(board[newRow][newColumn-1] == opponent && 
					(board[newRow][newColumn-2] == player || board[newRow][newColumn-2] == CORNER || (newRow==THRONE && newColumn-2==THRONE) )) {
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

}