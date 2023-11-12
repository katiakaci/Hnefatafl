import java.io.*;
import java.net.*;
import java.util.*;

class Client {

	static Board board;
	static BufferedInputStream input;
	static BufferedOutputStream output;
	static BufferedReader console;
	static Socket MyClient;
	static int aiPlayer;

	static final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5;

	public static void main(String[] args) {
		try {
			MyClient = new Socket("localhost", 8888);
			input    = new BufferedInputStream(MyClient.getInputStream());
			output   = new BufferedOutputStream(MyClient.getOutputStream());
			console = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				char cmd = (char)input.read();
				System.out.println("\nCommande: "+cmd);
				if(cmd == '1') startGameAsRed();
				if(cmd == '2') startGameAsBlack();
				if(cmd == '3') nextMove();
				if(cmd == '4')invalidMove();
				if(cmd == '5')endGame();
				System.out.println("\nle ai est: "+aiPlayer);
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void startGameAsRed() throws IOException {
		byte[] aBuffer = new byte[1024];
		int size = input.available();
		//System.out.println("size " + size);
		input.read(aBuffer,0,size);
		String s = new String(aBuffer).trim();
		// System.out.println("chaine du plateau: "+s);
		board = new Board(s);
		aiPlayer = RED;
		showBoard();

		System.out.print("Nouvelle partie! Vous jouer rouge, entrez votre premier coup : ");
		String move = null;
		move = console.readLine();
		output.write(move.getBytes(),0,move.length());
		output.flush();
	}

	private static void startGameAsBlack() throws IOException {
		System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des rouges");
		byte[] aBuffer = new byte[1024];
		int size = input.available();
		// System.out.println("size " + size);
		input.read(aBuffer,0,size);
		String s = new String(aBuffer).trim();
		// System.out.println(s);
		board = new Board(s);
		aiPlayer = BLACK;
	}

	private static void nextMove() throws IOException {
		// Le serveur demande le prochain coup
		// Le message contient aussi le dernier coup joue.
		byte[] aBuffer = new byte[16];
		int size = input.available();
		//		System.out.println("size :" + size);
		input.read(aBuffer,0,size);
		String s = new String(aBuffer);
		System.out.println("Dernier coup :"+ s);


		// A ENLEVER : !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		ArrayList<Move> moves = board.findPossibleMoves(aiPlayer);
		String possiblesMoves= "";
		for(int i=0;i< moves.size();i++) {
			possiblesMoves+=board.printMove(moves.get(i))+" / ";
		}
		System.out.println("Coups possibles pour "+aiPlayer+": "+possiblesMoves);
		// A ENLEVER : !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


		System.out.print("Entrez votre coup : ");
		String move = null;
		move = console.readLine();
		output.write(move.getBytes(),0,move.length());
		output.flush();
	}

	private static void invalidMove() throws IOException {
		System.out.print("Coup invalide, entrez un nouveau coup : ");
		String move = null;
		move = console.readLine();
		output.write(move.getBytes(),0,move.length());
		output.flush();
	}

	private static void endGame() throws IOException {
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

	/**
	 * Imprime le tableau dans la console
	 */
	private static void showBoard() {
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
