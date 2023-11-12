import java.util.*;

class Board
{
	private final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5;
	private int[][] board;
	private int posKingX, posKingY;
	private Map<Integer, String> boardConversionRow = new HashMap<>();

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
			if(Integer.parseInt(boardValues[i])==5) {
				this.posKingX = x;
				this.posKingY = y;
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
			boardConversionRow.put(i, String.valueOf(rows[i]));
		}
	}

	/**
	 * Générer les coups possibles
	 * @param aiPlayer ROUGE ou NOIR
	 * @return la liste de coups possibles
	 */
	public ArrayList<Move> findPossibleMoves(int aiPlayer) {
		ArrayList<Move> possibleMoves = new ArrayList<>();
		for(int i=0;i<board.length;i++) {
			for(int j=0; j<board[i].length;j++) {
				if(board[i][j] == aiPlayer) {
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
					for(int column=i+1; column<13;column++) {
						if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
						else break;
					}

					// vérifier à gauche du pion
					for(int column=i-1; column>=0;column--) {
						if(board[i][column] == EMPTY) possibleMoves.add(new Move(i,j,i,column));
						else break;
					}
				}
			}
		}
		return possibleMoves;
	}

	public String printMove(Move m) {
		return boardConversionRow.get(m.getRowStart())+""+m.getColStart()+" - "+boardConversionRow.get(m.getRowTarget())+""+m.getColTarget();
	}

	public int[][]  eliminerJetonAdverse(int couleurJoueur, int couleurAdverse, int colonneFin, int rangeeFin) {
		int king = 5;
		//change la couleur du king pour rouge si c est le joueur rouge qui a fait un movement pour manger
		// afin qu un joueur rouge ne puisse pas manger un noir a laide du king.
		if(couleurJoueur == 4) {
			king = 4;
		}
		if (colonneFin < 11) {
			if (this.board[colonneFin + 1][rangeeFin] == couleurAdverse) {
				if (this.board[colonneFin + 2][rangeeFin] == couleurJoueur || this.board[colonneFin + 2][rangeeFin] == 1 || this.board[colonneFin + 2][rangeeFin] == king || (colonneFin + 2 == 6 && rangeeFin == 6)) {
					this.board[colonneFin + 1][rangeeFin] = 0;
				}
			}
		}
		if (rangeeFin < 11) {
			if (this.board[colonneFin][rangeeFin + 1] == couleurAdverse) {
				if (this.board[colonneFin][rangeeFin + 2] == couleurJoueur || this.board[colonneFin][rangeeFin + 2] == 1 || this.board[colonneFin][rangeeFin + 2] == king || (colonneFin == 6 && rangeeFin + 2 == 6)) {
					this.board[colonneFin][rangeeFin + 1] = 0;
				}
			}
		}
		if (colonneFin > 1) {
			if (this.board[colonneFin - 1][rangeeFin] == couleurAdverse) {
				if (this.board[colonneFin - 2][rangeeFin] == couleurJoueur || this.board[colonneFin - 2][rangeeFin] == 1 || this.board[colonneFin - 2][rangeeFin] == king || (colonneFin - 2 == 6 && rangeeFin == 6)) {
					this.board[colonneFin - 1][rangeeFin] = 0;
				}
			}
		}
		if (rangeeFin > 1) {
			if (this.board[colonneFin][rangeeFin - 1] == couleurAdverse) {
				if (this.board[colonneFin][rangeeFin - 2] == couleurJoueur || this.board[colonneFin][rangeeFin - 2] == 1 || this.board[colonneFin][rangeeFin - 2] == king || (colonneFin == 6 && rangeeFin - 2 == 6)) {
					this.board[colonneFin][rangeeFin - 1] = 0;
				}
			}
		}
		return this.board;
	}

	public int[][] getBoard() {
		return board;
	}

	public int getPosKingX() {
		return posKingX;
	}

	public void setPosKingX(int posKingX) {
		this.posKingX = posKingX;
	}

	public int getPosKingY() {
		return posKingY;
	}

	public void setPosKingY(int posKingY) {
		this.posKingY = posKingY;
	}
}