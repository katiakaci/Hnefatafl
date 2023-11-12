//
//import java.util.ArrayList;
//
//public class GenerateurMouvement {
//
//	private static final int ROUGE = 4;
//	private static final int NOIR = 2;
//
//	private boolean MouvementValide(int[][] tableau, int colInitiale, int ligneInitiale, int colFinal, int ligneFinal){
//
//		// verifie si c'est le roi qui se déplace vers les coins ou si l'on veut
//		// se déplacer dans une case autre que celle actuelle.
//		if(ligneInitiale == ligneFinal && colInitiale == colFinal){ return  false; }
//		if(tableau[colFinal][ligneFinal] == 1 && tableau[colInitiale][ligneInitiale] != 5){return false;}
//
//
//		//lors d'un mouvement est  vertical, verifie qu'il n'y a pas d'autre piece entre la position initial et finale
//		if (colInitiale == colFinal && ligneInitiale != ligneFinal) {
//			if (ligneFinal > ligneInitiale) {
//				for (int i = ligneInitiale + 1; i <= ligneFinal; i++) {
//					if (tableau[colInitiale][i] > 1) {
//						return false;
//					}
//				}
//			} else {
//				for (int i = ligneFinal; i < ligneInitiale; i++) {
//					if (tableau[colInitiale][i] > 1) {
//						return false;
//					}
//				}
//			}
//		}
//		//lors d'un mouvement est a la horizontal, verifie qu'il n'y a pas d'autre piece entre la position initial et finale
//		if (ligneInitiale == ligneFinal && colInitiale != colFinal) {
//			if (colFinal > colInitiale) {
//				for (int i = colInitiale + 1; i <= colFinal; i++) {
//					if (tableau[i][ligneInitiale] > 1) {
//						return false;
//					}
//				}
//			} else {
//				for (int i = colFinal; i < colInitiale; i++) {
//					if (tableau[i][ligneInitiale] > 1) {
//						return false;
//					}
//				}
//			}
//		}
//		return true;
//	}
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
//}