import java.io.*;
import java.net.*;
import java.util.*;

class Client {

	static Board board;

	public static void main(String[] args) {
		final char ROUGE = '1', NOIR = '2', PROCHAIN = '3', INVALIDE = '4', TERMINER = '5';

		Socket MyClient;
		BufferedInputStream input;
		BufferedOutputStream output;

		try {
			MyClient = new Socket("localhost", 8888);
			input    = new BufferedInputStream(MyClient.getInputStream());
			output   = new BufferedOutputStream(MyClient.getOutputStream());
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

			while(true){
				char cmd = (char)input.read();
				System.out.println("Commande: "+cmd);

				// Debut de la partie en joueur blanc (rouge)	
				if(cmd == ROUGE){
					byte[] aBuffer = new byte[1024];
					int size = input.available();
					//System.out.println("size " + size);
					input.read(aBuffer,0,size);
					String s = new String(aBuffer).trim();
					//					System.out.println("chaine du plateau: "+s);
					board = new Board(s);
					showBoard();

					System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(),0,move.length());
					output.flush();
				}

				// Debut de la partie en joueur Noir
				if(cmd == NOIR){
					System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
					byte[] aBuffer = new byte[1024];

					int size = input.available();
					//System.out.println("size " + size);
					input.read(aBuffer,0,size);
					String s = new String(aBuffer).trim();
					//					System.out.println(s);
					board = new Board(s);
				}

				// Le serveur demande le prochain coup
				// Le message contient aussi le dernier coup joue.
				if(cmd == PROCHAIN){
					byte[] aBuffer = new byte[16];
					int size = input.available();
					System.out.println("size :" + size);
					input.read(aBuffer,0,size);

					String s = new String(aBuffer);
					System.out.println("Dernier coup :"+ s);
					ArrayList<Move> moves = board.findPossibleMoves();
					String possiblesMoves= "";
					for(int i=0;i< moves.size();i++) {
						possiblesMoves+=board.printMove(moves.get(i))+" / ";
					}
					System.out.println("Coups possibles : "+possiblesMoves);
					
					System.out.println("Entrez votre coup : ");
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(),0,move.length());
					output.flush();
				}

				// Le dernier coup est invalide
				if(cmd == INVALIDE){
					System.out.println("Coup invalide, entrez un nouveau coup : ");
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(),0,move.length());
					output.flush();
				}

				// La partie est terminée
				if(cmd == TERMINER){
					byte[] aBuffer = new byte[16];
					int size = input.available();
					input.read(aBuffer,0,size);
					String s = new String(aBuffer);
					System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(),0,move.length());
					output.flush();
				}
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * Imprime le tableau dans la console
	 */
	public static void showBoard() {
		System.out.println();
		for (int x = 0; x < 13; x++) {
			System.out.print(String.format("%3d", 13 - x) + " |");
			for (int y = 0; y < 13; y++) {
				System.out.print("  " + board.getBoard()[y][x]);
			}
			System.out.println();
		}
		System.out.println("_____________________________________________");
		System.out.println("       A  B  C  D  E  F  G  H  I  J  K  L  M\n");
	}
}
