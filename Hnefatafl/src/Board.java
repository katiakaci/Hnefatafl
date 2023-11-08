import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Board
{
	private int[][] board;

	public int[][] getBoard() {
		return board;
	}

	/**
	 * Initialise le plateau de la console
	 * 0 = vide
	 * 1 = coin
	 * 2 = noir
	 * 4 = rouge
	 * 5 = roi
	 * @param sBoard
	 */
	public Board(String s) {
		board = new int[13][13];
		String[] boardValues = s.split(" ");
		int x=0,y=0;
		for(int i=0; i<boardValues.length;i++){
			board[x][y] = Integer.parseInt(boardValues[i]);
			x++;
			if(x == 13){
				x = 0;
				y++;
			}
		}
		board[0][0] = 1;
		board[0][12] = 1;
		board[12][0] = 1;
		board[12][12] = 1;
	}


	/**
	 * Générer les coups possibles
	 * @return la liste de coups possibles
	 */
	public ArrayList<Move> findPossibleMoves() {
		ArrayList<Move> possibleMoves = new ArrayList<>();
		for(int i=0;i<board.length;i++) {
			for(int j=0; j<board[i].length;j++) {
				if(board[i][j] == 4) {

					// vérifier en bas du pion
					for(int row=i; row<13;row++) {
						// si on rencontre un pion
						if(board[row][j] == 4 || board[row][j] == 5) break;
						// si on rencontre une case vide
						//(row, column)= start case (i,j) = target case
						else if(board[row][j] == 0) possibleMoves.add(new Move(row,j,i,j));
					}

					// vérifier en haut du pion
					for(int row=i; row==0;row--) {
						if(board[row][j] == 4 || board[row][j] == 5) break;
						else if(board[row][j] == 0) possibleMoves.add(new Move(row,j,i,j));
					}

					// vérifier à droite du pion
					for(int column=i; column<13;column++) {
						if(board[i][column] == 4 || board[i][column] == 5) break;
						else if(board[i][column] == 0) possibleMoves.add(new Move(i,column,i,j));
					}

					// vérifier à gauche du pion
					for(int column=i; column==0;column--) {
						if(board[i][column] == 4 || board[i][column] == 5) break;
						else if(board[i][column] == 0) possibleMoves.add(new Move(i,column,i,j));
					}
				}
			}
		}
		return possibleMoves;
	}

}