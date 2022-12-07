import java.util.*;
import java.lang.Math;

class CFBT {
	static boolean playerTurn = true; // true = player's turn, false = computer's turn
	static boolean playerMoveFirst = true;
	static boolean gameWon = false;
	static boolean draw = false;
	static int movesDone = 0;
	static int tokenPosition = 0;
	static int baseScore = 0; // positive for the computer, negative for the player
	static String playerName;
	static String input;
	static int[] legalMoves = new int[7];
	static int[][] board = new int[6][7]; // 0 is empty space, 1 belongs to Player 1, and 2 belongs to the computer.
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to Connect Four.");
		System.out.println("Please enter your name.");
		playerName = sc.nextLine();
		askTurn();
		System.out.println("Object: Connect four of your checkers in a row while preventing your opponent from doing the same. But, look out - your opponent can sneak up on you and win the game!");
		System.out.println("Good luck!");
		System.out.println("Oh yeah, by the way, if you ever need help, type \"help\" for help.\n");
		tokenPrint();
		print();
		while (!gameWon && !draw) {
			if (playerTurn) {
				input = sc.nextLine();
				input = input.toLowerCase(); // Eliminates case sensitivity in the input.
				if (input.equals("help")) {
					System.out.println("-------------------------------------------------------------------");
					System.out.println("Welcome to the help menu.");
					System.out.println("List of commands:");
					System.out.println("\"left\", \"l\", or \"a\": Moves the token one space to the left.");
					System.out.println("\"right\", \"r\", or \"d\": Moves the token one space to the right.");
					System.out.println("\"drop\": Drops the token.");
					System.out.println("\"stop\": Stops the game immediately.");
					System.out.println("Board Key:");
					System.out.println("\"-\": Empty space.");
					System.out.println("\"X\": Your tokens.");
					System.out.println("\"O\": Computer's tokens.");
					System.out.println("-------------------------------------------------------------------");
				} else if (input.equals("left") || input.equals("l") || input.equals("a")) {
					if (tokenPosition == 0) {
						System.out.println("You can't move your token further to the left.");
					} else {
						tokenPosition--;
						tokenPrint();
						print();
					}
				} else if (input.equals("right") || input.equals("r") || input.equals("d")) {
					if (tokenPosition == 6) {
						System.out.println("You can't move your token further to the right.");
					} else {
						tokenPosition++;
						tokenPrint();
						print();
					}
				} else if (input.equals("drop")) {
					if (board[0][tokenPosition] == 0) {
						board = drop(board,tokenPosition,true);
						movesDone++;
						tokenPrint();
						print();
						check(tokenPosition,findy(board,tokenPosition));
						baseScore = updateScore(board,baseScore,tokenPosition,true);
						System.out.println(baseScore);
						playerTurn = false;
					} else {
						System.out.println("That space is already taken. Choose another one.");
					}
				} else if (input.equals("stop")) {
					draw = true;
					System.out.println("Stopping game.");
				} else {
					System.out.println("That is not a valid command. Type \"help\" for help.");
				}
			} else {
				legalMoves = findLegalMoves(board);
				tokenPosition = evaluate(board,6);
				board = drop(board,tokenPosition,false);
				movesDone++;
				tokenPrint();
				print();
				check(tokenPosition,findy(board,tokenPosition));
				baseScore = updateScore(board,baseScore,tokenPosition,false);
				System.out.println(baseScore);
				playerTurn = true;
			}
		}
	}

	static void print() { // Prints the board, with labels.
		int i;
		int j;
		for (i = 0; i < board.length; ++i) {
			System.out.print("| ");
			for (j = 0; j < board[i].length; ++j) {
				if (board[i][j] == 0) {
					System.out.print("- ");
				} else if (board[i][j] == 1) {
					System.out.print("X ");
				} else if (board[i][j] == 2) {
					System.out.print("O ");
				}
			}
			System.out.println("|");
		}
		System.out.println("_________________");
	}

	static void tokenPrint() { // Prints the dropper at the top of the board.
		int i;
		System.out.print("  ");
		for (i = 0; i < 7; i++) {
			if (i == tokenPosition) {
				if (playerTurn) {
					System.out.print("X ");
				} else {
					System.out.print("O ");
				}
			} else {
				System.out.print("  ");
			}
		}
		System.out.println(" ");
	}

	static int[][] localTranslation(int[][] arr,int x,int y) { // Translates coordinates (x, y) into a local array.
		int i;
		int j;
		int[][] output = new int[7][7];
		for (i = -3; i <= 3; ++i) {
			for (j = -3; j <= 3; ++j) {
				if (0 <= y + i && y + i < 6 && 0 <= x + j && x + j < 7) {
					output[i + 3][j + 3] = arr[y + i][x + j];
				} else {
					output[i + 3][j + 3] = 3;
				}
			}
		}
		return output;
	}

	static boolean checkFour(int[][] arr) {
		boolean foundFour = false; // If this is true, then there is a connect 4 on the 7x7 translation array.
		for (int i = 0; i < 4; i++) {
			if (arr[i][3] == arr[i + 1][3] && arr[i + 1][3] == arr[i + 2][3] && arr[i + 2][3] == arr[i + 3][3]) {
				foundFour = true;
			}
			if (arr[i][i] == arr[i + 1][i + 1] && arr[i + 1][i + 1] == arr[i + 2][i + 2] && arr[i + 2][i + 2] == arr[i + 3][i + 3]) {
				foundFour = true;
			}
			if (arr[3][i] == arr[3][i + 1] && arr[3][i + 1] == arr[3][i + 2] && arr[3][i + 2] == arr[3][i + 3]) {
				foundFour = true;
			}
			if (arr[i][6 - i] == arr[i + 1][5 - i] && arr[i + 1][5 - i] == arr[i + 2][4 - i] && arr[i + 2][4 - i] == arr[i + 3][3 - i]) {
				foundFour = true;
			}
		}
		return foundFour;
	}

	static void check(int x,int y) {
		if (checkFour(localTranslation(board,x,y))) {
			System.out.println("game won");
			gameWon = true;
		}
		if (movesDone == 42 && gameWon == false) { // If the moves reaches 42, the board must be full. If there is not a 4 in a row by then, the game is a draw.
			draw = true;
			System.out.println("The game is a draw! GG");
		}
		if (gameWon) {
			if (board[y][x] == 1) {
				System.out.println("Congratulations, " + playerName  + " beat the computer!");
			} else if (board[y][x] == 2) {
				System.out.println("Computer wins!");
			}
		}
	}

	static void askTurn() {
		System.out.println("Do you want to move first or second? Type 1 for first, 2 for second.");
		Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		if (i == 1) {
			playerTurn = true;
			playerMoveFirst = true;
		} else if (i == 2) {
			playerTurn = false;
			playerMoveFirst = false;
		} else {
			System.out.println("Please type either 1 or 2. No spaces.");
			askTurn();
		}
	}

	static int[] findLegalMoves(int[][] arr) {
		int i;
		int j = 0;
		for (i = 0; i < 7; i++) {
			if (arr[0][i] == 0) {
				j++;
			}
		}
		int[] output = new int[j];
		j = 0;
		for (i = 0; i < 7; i++) {
			if (arr[0][i] == 0) {
				output[j] = i;
				j++;
			}
		}
		return output;
	}

	static int[][] drop(int[][] arr,int pos,boolean mover) { // True is player, false is computer
		int i;
		int j;
		int[][] output = new int[arr.length][7];
		for (i = 0; i < arr.length; i++) {
			for (j = 0; j < 7; j++) {
				output[i][j] = arr[i][j];
			}
		}
		for (i = 0; i < arr.length; i++) {
			if (i == arr.length - 1 && output[arr.length - 2][pos] == 0) {
				if (mover) {
					output[i][pos] = 1;
				} else {
					output[i][pos] = 2;
				}
			} else if (output[i][pos] == 0 && output[i + 1][pos] != 0) {
				if (mover) {
					output[i][pos] = 1;
				} else {
					output[i][pos] = 2;
				}
			}
		}
		return output;
	}

	static int findy(int[][] arr,int pos) {
		int i;
		int j = 0; // to prevent error: j might not have been initialized
		for (i = 0; i < 6; i++) { // looping through the rows, starting with the highest one
			if (arr[i][pos] == 0) {
				j = i + 1;
			}
		}
		return j;
	}

	static int evaluate(int[][] arr,int depth) { // Only use even values of depth.
		int[][] states = new int[depth][(int)Math.pow(7,depth)]; // 0 = not yet evaluated, 1 = win for either player or computer, 2 = win for neither player nor computer, 3 = illegal position.
		int[][] evals = new int[depth][(int)Math.pow(7,depth)]; // If states is 1, this shows (at the end) how fast c4 can be forced. If the absolute value is higher, c4 can be forced faster. Positive is for computer, negative is for player. If states is 2, it shows the c3 eval of the position.
		int[][] illegalChecker = new int[6 + depth][7]; // Drop the sequence of moves in this array. If there's anything in layer depth - 1, the position is illegal.
		int[][] expandedBoard = new int[6 + depth][7]; // So the computer doesn't have to compute the expanded board every time.
		int[][] placeholder = new int [6][7];
		for (int i = 0; i < 6 + depth; i++) { // Loop through each square of expandedBoard
			for (int j = 0; j < 7; j++) {
				if (i < depth) { // If the searcher is in the area above the normal board, the space should be blank.
					expandedBoard[i][j] = 0;
				} else {
					expandedBoard[i][j] = arr[i - depth][j];
				}
			}
		}
		for (int i = 0; i < depth; i++) { // Iteration 0 is after 1 move by computer, iteration 1 is after 1 move by computer and 1 move by player, etc. 
			for (int j = 0; j < Math.pow(7,i + 1); j++) { // Loop through the positions of depth i + 1
				if (i == 0) { // We can't access the layer before this, so we have to start from scratch.
					illegalChecker = expandedBoard;
					for (int k = 0; k < i; k++) { // prepare illegalChecker
						illegalChecker = drop(illegalChecker,intToMoves(j,k),!isEven(k));
					}
					for (int k = 0; k < 7; k++) { // Is there anything above the normal board? If so, one or more of the moves was illegal.
						if (illegalChecker[depth - 1][k] != 0) {
							states[i][j] = 3;
						}
					}
					if (states[i][j] != 3) { // If the position is legal
						placeholder = arr;
						for (int k = 0; k < i; k++) {
							placeholder = drop(placeholder,intToMoves(j,k),!isEven(k));
						}
						if (checkFour(localTranslation(placeholder,intToMoves(j,i - 1),findy(placeholder,intToMoves(j,i - 1))))) {
							states[i][j] = 1;
							if (placeholder[intToMoves(j,i - 1)][findy(placeholder,intToMoves(j,i - 1))] == 1) {
								evals[i][j] = -1;
							} else {
								evals[i][j] = 1;
							}
						}
					}
					if (states[i][j] != 3 && states[i][j] != 1) { // If position is legal and does not yet have a c4
						states[i][j] = 2;
						placeholder = arr;
						for (int k = 0; k < i; k++) {
							placeholder = drop(placeholder,intToMoves(j,k),!isEven(k));
						}
						evals[i][j] = updateScore(placeholder,baseScore,intToMoves(j,i),!isEven(i));
					}
				} else { // Check the layer before this one to inherit information.
					if (states[i - 1][(int)Math.floor(j / 7)] == 3) {
						states[i][j] = 3;
					} else {
						illegalChecker = expandedBoard;
						for (int k = 0; k < i; k++) { // prepare illegalChecker
							illegalChecker = drop(illegalChecker,intToMoves(j,k),!isEven(k));
						}
						for (int k = 0; k < 7; k++) { // Is there anything above the normal board? If so, one or more of the moves was illegal.
							if (illegalChecker[depth - 1][k] != 0) {
								states[i][j] = 3;
							}
						}
						if (states[i - 1][(int)Math.floor(j / 7)] == 1) {
							states[i][j] = 1;
							if (evals[i - 1][(int)Math.floor(j / 7)] > 0) {
								evals[i][j] = evals[i - 1][(int)Math.floor(j / 7)] + 1; // Increase it by 1 so that the computer will prefer lines that lead to forced computer c4 faster.
							} else {
								evals[i][j] = evals[i - 1][(int)Math.floor(j / 7)] - 1; // Decrease it by 1 so that the computer will avoid lines that lead to forced player c4 faster. Who knows, the player might miss it!
							}
						} else if (states[i - 1][(int)Math.floor(j / 7)] == 2) {
							placeholder = arr;
							for (int k = 0; k < i; k++) {
								placeholder = drop(placeholder,intToMoves(j,k),!isEven(k));
							}
							if (checkFour(localTranslation(placeholder,intToMoves(j,i - 1),findy(placeholder,intToMoves(j,i - 1))))) { // Check to see if there's a c4
								states[i][j] = 1;
								if (placeholder[intToMoves(j,i - 1)][findy(placeholder,intToMoves(j,i - 1))] == 1) {
									evals[i][j] = -1; // Player wins
								} else {
									evals[i][j] = 1; // Computer wins
								}
							}
						} else { // There's no c4, update c3 count
							states[i][j] = 2;
							placeholder = arr;
							for (int k = 0; k < i; k++) {
								placeholder = drop(placeholder,intToMoves(j,k),!isEven(k));
							}
							evals[i][j] = updateScore(placeholder,evals[i - 1][(int)Math.floor(j / 7)],intToMoves(j,i),!isEven(i));
						}
					}
				}
			}
		}
		return minimax(states[depth - 1],evals[depth - 1],depth);
	}

	static int minimax (int[] states,int[] evals,int depth) {
		int[] hybrid = new int[states.length];
		int[] placeholder = new int[7];
		int[] bias = {0,1,2,3,2,1,0};
		for (int i = 0; i < states.length; i++) {
			if (states[i] == 1) {
				hybrid[i] = evals[i] * 1000;
			} else if (states[i] == 2) {
				hybrid[i] = evals[i];
			} else if (states[i] == 3) {
				hybrid[i] = 1500; // 1500 is the "code" for an illegal move. It gets ignored when calculating minimaxing.
			}
		}
		int[][] layers = new int[depth][(int)Math.pow(7,depth)]; // The last layer is the evaluation after only one computer move.
		layers[0] = hybrid;
		for (int i = 0; i < depth - 1; i++) {
			for (int j = 0; j < Math.pow(7, depth - i - 1); j++) {
				for (int k = 0; k < 7; k++) {
					placeholder[k] = layers[i][(int)(7 * Math.floor(j / 7) + k)]; // To evaluate lowest and highest functions
				}
				if (isEven(i)) {
					layers[i + 1][(int)Math.floor(j / 7)] = lowest(placeholder);
				} else {
					layers[i + 1][(int)Math.floor(j / 7)] = highest(placeholder);
				}
			}
		}
		for (int i = 0; i < 7; i++) {
			placeholder[i] = 5 * layers[depth - 1][i] + bias[i]; // The algorithm should favor spots more in the center.
		}
		for (int i = 0; i < 7; i++) {
			if (placeholder[i] == highest(placeholder)) {
				return i;
			}
		}
		System.out.println("Something went wrong. This is line 388 by the way.");
		return 0;
	}

	static int intToMoves (int input,int digit) { // Converts an integer to a sequence of moves.
		int output = (int)(Math.floor(input / Math.pow(7,Math.floor(Math.log(input) / Math.log(7)) - digit)) % 7);
		return output;
	}

	static boolean isEven (int input) {
		if (input % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	static int updateScore (int[][] arr,int score,int move,boolean turn) { // Adds or subtracts to the score based on the number of connect3s. True = player's turn, false = computer's turn
		int output = score; // If turn is true, the the computer is evaluating the player's move. Otherwise, the computer is evaluating the computer's move.
		int change = 0;
		int[][] local = localTranslation(arr,move,findy(arr,move));
		for (int i = 1; i < 4; i++) {
			if (local[i][3] == local[i + 1][3] && local[i + 1][3] == local[i + 2][3] && (local[i - 1][3] == 0 || local[i + 3][3] == 0)) {
				if (local[i - 1][3] == 0 && local[i + 3][3] == 0) {
					change = change + 3; // The 3 here is rather arbritrary, the true value really depends on the position. In some cases, a double-unconstrained is worth the game, but in others, it is virtually useless.
				} else {
					change++;
				}
			}
			if (local[i][i] == local[i + 1][i + 1] && local[i + 1][i + 1] == local[i + 2][i + 2] && (local[i - 1][i - 1] == 0 || local[i + 3][i + 3] == 0)) {
				if (local[i - 1][i - 1] == 0 && local[i + 3][i + 3] == 0) {
					change = change + 3;
				} else {
					change++;
				}
			}
			if (local[3][i] == local[3][i + 1] && local[3][i + 1] == local[3][i + 2] && (local[3][i - 1] == 0 || local[3][i + 3] == 0)) {
				if (local[3][i - 1] == 0 && local[3][i + 3] == 0) {
					change = change + 3;
				} else {
					change++;
				}
			}
			if (local[i][6 - i] == local[i + 1][5 - i] && local[i + 1][5 - i] == local[i + 2][4 - i] && (local[i - 1][7 - i] == 0 || local[i + 3][3 - i] == 0)) {
				if (local[i - 1][7 - i]  == 0 && local[i + 3][3 - i] == 0) {
					change = change + 3;
				} else {
					change++;
				}
			}
		}
		if (turn) {
			output = output - change;
		} else {
			output = output + change;
		}
		return output;
	}

	static boolean isElementInArray (int[] arr,int x) {
		boolean output = false;
		for (int i = 0; i < arr.length; i++) {
			if (x == arr[i]) {
				output = true;
			}
		}
		return output;
	}

	static int lowest(int[] arr) { // Ignores all occurences of the number 1500
		int output = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (!(1500 == arr[i] || (7500 <= arr[i] && arr[i] <= 7503))) {
				output = Math.min(output,arr[i]);
			}
		}
		return output;
	}

	static int highest(int[] arr) { // Ignores all occurences of the number 1500
		int output = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (!(1500 == arr[i] || (7500 <= arr[i] && arr[i] <= 7503))) {
				output = Math.max(output,arr[i]);
			}
		}
		return output;
	}
}