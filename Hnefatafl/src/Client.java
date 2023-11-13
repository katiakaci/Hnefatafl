import java.io.*;
import java.net.*;

class Client {

	static Board board;
	static BufferedInputStream input;
	static BufferedOutputStream output;
	static BufferedReader console;
	static Socket MyClient;
	static int aiPlayer, opponent;
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
		board = new Board(s);
		aiPlayer = RED;
		opponent = BLACK;

		System.out.print("Nouvelle partie! Vous jouer rouge, entrez votre premier coup : ");
		String move = console.readLine();
		board.updateBoard(move, aiPlayer, opponent);

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
		board = new Board(s);
		aiPlayer = BLACK;
		opponent = RED;
	}

	private static void nextMove() throws IOException {
		byte[] aBuffer = new byte[16];
		int size = input.available();
		// System.out.println("size :" + size);
		input.read(aBuffer,0,size);
		String s = new String(aBuffer);
		System.out.println("Dernier coup :"+ s);
		board.updateBoard(s, opponent, aiPlayer);

		// board.printPossibleMoves(aiPlayer);
		System.out.print("Entrez votre coup : ");
		String move = console.readLine();
		board.updateBoard(move, aiPlayer, opponent);

		output.write(move.getBytes(),0,move.length());
		output.flush();
	}

	private static void invalidMove() throws IOException {
		System.out.print("Coup invalide, entrez un nouveau coup : ");
		String move = console.readLine();
		board.updateBoard(move, aiPlayer, opponent);
		output.write(move.getBytes(),0,move.length());
		output.flush();
	}

	private static void endGame() throws IOException {
		byte[] aBuffer = new byte[16];
		int size = input.available();
		input.read(aBuffer,0,size);
		String s = new String(aBuffer);
		System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
		String move = console.readLine();
		output.write(move.getBytes(),0,move.length());
		output.flush();
	}

}
