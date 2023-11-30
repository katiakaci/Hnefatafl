import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Client {

	static Board board;
	static CPUPlayer cpu;
	static BufferedInputStream input;
	static BufferedOutputStream output;
	static BufferedReader console;
	static Socket MyClient;
	static int aiPlayer, opponent; 
	static String colorAI, colorOpponent;
	static final int EMPTY = 0, CORNER = 1, BLACK = 2, RED = 4, KING = 5;
	static ArrayList<String> moveAI;

	public static void main(String[] args) {
		try {
			MyClient = new Socket("localhost", 8888);
			input    = new BufferedInputStream(MyClient.getInputStream());
			output   = new BufferedOutputStream(MyClient.getOutputStream());
			console = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				char cmd = (char)input.read();
				//				System.out.println("\nCommande: "+cmd);
				if(cmd == '1') startGameAsRed();
				if(cmd == '2') startGameAsBlack();
				if(cmd == '3') nextMove();
				if(cmd == '4')invalidMove();
				if(cmd == '5')endGame();
				//				board.printBoard();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void startGameAsRed() throws IOException {
		byte[] aBuffer = new byte[1024];
		int size = input.available();
		input.read(aBuffer,0,size);
		String s = new String(aBuffer).trim();
		board = new Board(s);
		aiPlayer = RED;
		opponent = BLACK;
		cpu = new CPUPlayer(aiPlayer);
		colorAI = "ROUGES";
		colorOpponent = "NOIRS";

		String move = playWithAlphaBeta();
		board.play(move, aiPlayer);
		output.write(move.getBytes(),0,move.length());
		output.flush();
		moveAI = new ArrayList<>();
		moveAI.add(move);
	}

	private static void startGameAsBlack() throws IOException {
		System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des rouges");
		byte[] aBuffer = new byte[1024];
		int size = input.available();
		input.read(aBuffer,0,size);
		String s = new String(aBuffer).trim();
		board = new Board(s);
		aiPlayer = BLACK;
		opponent = RED;
		colorAI = "NOIRS";
		colorOpponent = "ROUGES";
		cpu = new CPUPlayer(aiPlayer);
		moveAI = new ArrayList<>();
	}

	private static void nextMove() throws IOException {
		// Ordinateur joue :
		byte[] aBuffer = new byte[16];
		int size = input.available();
		input.read(aBuffer,0,size);
		String s = new String(aBuffer);
		System.out.println("Dernier coup pour les "+colorOpponent+":"+ s);
		board.play(s, opponent);

		// AI (réseau) joue :	
		String move = playWithAlphaBeta();

		// Vérifier si on joue le même mouvement trois fois de suite
		if(aiPlayer == BLACK && moveAI.size() > 20) {
			if((moveAI.get(moveAI.size()-1).equals(moveAI.get(moveAI.size()-3))) &&
					(moveAI.get(moveAI.size()-1).equals(moveAI.get(moveAI.size()-5))) &&
					(moveAI.get(moveAI.size()-1).equals(moveAI.get(moveAI.size()-7)))) {
				move = cpu.getDifferentMove(board);
			}
		}
		board.play(move, aiPlayer);
		output.write(move.getBytes(),0, move.length());
		output.flush();
		moveAI.add(move);
	}

	private static void invalidMove() throws IOException {
		System.out.print("Coup invalide");
		String move = playManually();
		board.play(move, aiPlayer);
		board.printBoard();
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

	private static String playManually() throws IOException {
		System.out.print("Tour des "+colorAI+". Entrez votre coup : ");
		return console.readLine();  
	}

	private static String playWithAlphaBeta() {
		ArrayList<Move> moves = cpu.getNextMoveAB(board);
		int randomIndex = (int)Math.floor(Math.random() * moves.size());
		String move = moves.get(randomIndex).toString();
		System.out.println("Tour du ai "+colorAI+", coup joué est : "+move);
		return move;
	}

}
