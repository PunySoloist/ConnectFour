import java.util.*; // 98
import java.lang.Math;

class Test {
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
		for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                board = drop(board, i, false);
                baseScore = updateScore(board,baseScore,i,false);
            }
        }
        System.out.println(baseScore);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("");
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

	static int[][] drop(int[][] arr,int pos,boolean mover) {
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
			if (local[i][3] == local[i + 1][3] && local[i + 1][3] == local[i + 2][3]) {
				if (local[i - 1][3] == 0 && local[i + 3][3] == 0) {
					change = change + 3; // The 3 here is rather arbritrary, the true value really depends on the position. In some cases, a double-unconstrained is worth the game, but in others, it is virtually useless.
				} else {
					change++;
				}
			}
			if (local[i][i] == local[i + 1][i + 1] && local[i + 1][i + 1] == local[i + 2][i + 2]) {
				if (local[i - 1][i - 1] == 0 && local[i + 3][i + 3] == 0) {
					change = change + 3;
				} else {
					change++;
				}
			}
			if (local[3][i] == local[3][i + 1] && local[3][i + 1] == local[3][i + 2]) {
				if (local[3][i - 1] == 0 && local[3][i + 3] == 0) {
					change = change + 3;
				} else {
					change++;
				}
			}
			if (local[i][6 - i] == local[i + 1][5 - i] && local[i + 1][5 - i] == local[i + 2][4 - i]) {
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

	static int lowest(int a, int b, int c, int d, int e, int f, int g) {
		int output = Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(a,b),c),d),e),f),g);
		return output;
	}

	static int highest(int a, int b, int c, int d, int e, int f, int g) {
		int output = Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(a,b),c),d),e),f),g);
		return output;
	}

	static int lowest(int[] arr) {
		int output = arr[0];
		for (int i = 1; i < arr.length; i++) {
			output = Math.min(arr[i - 1],arr[i]);
		}
		return output;
	}

	static int highest(int[] arr) {
		int output = arr[0];
		for (int i = 1; i < arr.length; i++) {
			output = Math.max(arr[i - 1],arr[i]);
		}
		return output;
	}
}