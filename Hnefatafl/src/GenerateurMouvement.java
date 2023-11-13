//
//import java.util.ArrayList;
//
//public class GenerateurMouvement {
//
//	private static final int ROUGE = 4;
//	private static final int NOIR = 2;
//
//
//	private ArrayList<Board> genererMouvement(Board board, int player){
//
//		ArrayList<Board> boardArray = new ArrayList<Board>();
//		int[][] actualBoard = board.getBoard();
//		int[][] tmpBoard;
//
//		if(player == ROUGE){
//			for(int i = 0; i < actualBoard.length;i++){
//				for (int j = 0; j < actualBoard[i].length;j++){
//					if (actualBoard[j][i] == ROUGE){
//						//tout les move horizontal
//						for(int k = 0; k < 13; k++){
//							if (k != j){
//								if(MouvementValide(actualBoard, j, i, k, i)){
//									tmpBoard = board.getBoard();
//									tmpBoard[j][i] = 0;
//									tmpBoard[k][i] = ROUGE;
//									tmpBoard = new Board(tmpBoard).eliminerJetonAdverse(ROUGE, NOIR, k, i);
//									Board newBoard = new Board((tmpBoard));
//									if(j == board.getPosKingX() && i == board.getPositionRoiY()){
//										newBoard.setPosKingX(k);
//										newBoard.setPosKingY(i);
//									}else {
//										newBoard.setPositionRoiX(board.getPositionRoiX());
//										newBoard.setPositionRoiY(board.getPositionRoiY());
//									}
//									boardArray.add(newBoard);
//								}else{
//									if(k < j){
//										k = j + 1;
//									}else{
//										k = 13;
//									}
//								}
//							}
//						}
//						//tout les move vertical
//						for(int l = 0; l < 13; l++){
//							if (l != i){
//								if(MouvementValide(actualBoard, j, i, j, l)){
//									tmpBoard = board.getBoard();
//									tmpBoard[j][i] = 0;
//									tmpBoard[j][l] = ROUGE;
//									tmpBoard = new Board(tmpBoard).eliminerJetonAdverse(ROUGE, NOIR, j, l);
//									Board newBoard = new Board(tmpBoard);
//									if(j == board.getPositionRoiX() && i == board.getPositionRoiY()){
//										newBoard.setPosKingX(j);
//										newBoard.setPosKingY(l);
//									}else {
//										newBoard.setPositionRoiX(board.getPosKingX());
//										newBoard.setPositionRoiY(board.getPosKingY());
//									}
//									boardArray.add(newBoard);
//								}else{
//									if(l < i){
//										l = i + 1;
//									}else{
//										l = 13;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		} else if(player == NOIR){
//			for(int i = 0; i < actualBoard.length;i++){
//				for (int j = 0; j < actualBoard[i].length;j++){
//					if (actualBoard[j][i] == ROUGE){
//						//tout les move horizontal
//						for(int k = 0; k < 13; k++){
//							if (k != j){
//								if(MouvementValide(actualBoard, j, i, k, i)){
//									tmpBoard = board.getBoard();
//									tmpBoard[j][i] = 0;
//									tmpBoard[k][i] = ROUGE;
//									tmpBoard = new Board(tmpBoard).eliminerJetonAdverse(ROUGE, NOIR, k, i);
//									Board newBoard = new Board((tmpBoard));
//									if(j == board.getPosKingX() && i == board.getPosKingY()){
//										newBoard.setPosKingX(k);
//										newBoard.setPosKingY(i);
//									}else {
//										newBoard.setPosKingX(board.getPosKingX());
//										newBoard.setPosKingY(board.getPosKingY());
//									}
//									boardArray.add(newBoard);
//								}else{
//									if(k < j){
//										k = j + 1;
//									}else{
//										k = 13;
//									}
//								}
//							}
//						}
//						//tout les move vertical
//						for(int l = 0; l < 13; l++){
//							if (l != i){
//								if(MouvementValide(actualBoard, j, i, j, l)){
//									tmpBoard = board.getBoard();
//									tmpBoard[j][i] = 0;
//									tmpBoard[j][l] = ROUGE;
//									tmpBoard = new Board(tmpBoard).eliminerJetonAdverse(ROUGE, NOIR, j, l);
//									Board newBoard = new Board((tmpBoard));
//									if(j == board.getPosKingX() && i == board.getPosKingY()){
//										newBoard.setPosKingX(j);
//										newBoard.setPosKingY(l);
//									}else {
//										newBoard.setPosKingX(board.getPosKingX());
//										newBoard.setPosKingY(board.getPosKingY());
//									}
//									boardArray.add(newBoard);
//								}else{
//									if(l < i){
//										l = i + 1;
//									}else{
//										l = 13;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//
//		//        for (int i = 0 ; i < boardArray.size(); i++) {
//		//            try {
//		//                writePossibleBoardsToFile(bw, boardArray.get(i));
//		//            } catch (IOException e) {
//		//                e.printStackTrace();
//		//            }
//		//        }
//		return boardArray;
//	}
//	
//	private void  eliminerJetonAdverse(int couleurJoueur, int couleurAdverse, int colonneFin, int rangeeFin) {
//		int king = 5;
//		//change la couleur du king pour rouge si c est le joueur rouge qui a fait un movement pour manger
//		// afin qu un joueur rouge ne puisse pas manger un noir a laide du king.
//		if(couleurJoueur == 4) {
//			king = 4;
//		}
//
//		if (colonneFin < 11) {
//			if (this.board[colonneFin + 1][rangeeFin] == couleurAdverse) {
//				if (this.board[colonneFin + 2][rangeeFin] == couleurJoueur || this.board[colonneFin + 2][rangeeFin] == 1 || this.board[colonneFin + 2][rangeeFin] == king || (colonneFin + 2 == 6 && rangeeFin == 6)) {
//					this.board[colonneFin + 1][rangeeFin] = 0;
//				}
//			}
//		}
//		if (rangeeFin < 11) {
//			if (this.board[colonneFin][rangeeFin + 1] == couleurAdverse) {
//				if (this.board[colonneFin][rangeeFin + 2] == couleurJoueur || this.board[colonneFin][rangeeFin + 2] == 1 || this.board[colonneFin][rangeeFin + 2] == king || (colonneFin == 6 && rangeeFin + 2 == 6)) {
//					this.board[colonneFin][rangeeFin + 1] = 0;
//				}
//			}
//		}
//		if (colonneFin > 1) {
//			if (this.board[colonneFin - 1][rangeeFin] == couleurAdverse) {
//				if (this.board[colonneFin - 2][rangeeFin] == couleurJoueur || this.board[colonneFin - 2][rangeeFin] == 1 || this.board[colonneFin - 2][rangeeFin] == king || (colonneFin - 2 == 6 && rangeeFin == 6)) {
//					this.board[colonneFin - 1][rangeeFin] = 0;
//				}
//			}
//		}
//		if (rangeeFin > 1) {
//			if (this.board[colonneFin][rangeeFin - 1] == couleurAdverse) {
//				if (this.board[colonneFin][rangeeFin - 2] == couleurJoueur || this.board[colonneFin][rangeeFin - 2] == 1 || this.board[colonneFin][rangeeFin - 2] == king || (colonneFin == 6 && rangeeFin - 2 == 6)) {
//					this.board[colonneFin][rangeeFin - 1] = 0;
//				}
//			}
//		}
//	}
//
//
//}