import java.util.Scanner;

public class ReversiGame {
    static final int SIZE = 8;
    static char[][] board = new char[SIZE][SIZE]; // 'B' for Black, 'W' for White, '.' for empty

    public static void main(String[] args) {
        initializeBoard();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select game mode: ");
        System.out.println("1. Human vs Human");
        System.out.println("2. Human vs AI");
        System.out.println("3. AI vs AI");
        int mode = scanner.nextInt();

        switch (mode) {
            case 1 -> playHumanVsHuman();
            case 2 -> playHumanVsAI();
            case 3 -> playAIvsAI();
            default -> System.out.println("Invalid mode.");
        }
    }

    // Initialize board with starting pieces
    private static void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = '.';
            }
        }
        board[3][3] = 'W';
        board[4][4] = 'W';
        board[3][4] = 'B';
        board[4][3] = 'B';
    }

    // Display the board
    private static void printBoard() {
        System.out.println("  A B C D E F G H");
        for (int i = 0; i < SIZE; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Human vs Human mode
    private static void playHumanVsHuman() {
        char currentPlayer = 'B';
        while (true) {
            printBoard();
            System.out.println("Player " + currentPlayer + ", make your move (e.g., E3): ");
            Scanner scanner = new Scanner(System.in);
            String move = scanner.nextLine();
            if (makeMove(move, currentPlayer)) {
                currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
            } else {
                System.out.println("Invalid move. Try again.");
            }
            if (isGameOver()) {
                declareWinner();
                break;
            }
        }
    }

    // Human vs AI mode
    private static void playHumanVsAI() {
        char currentPlayer = 'B';
        while (true) {
            printBoard();
            if (currentPlayer == 'B') {
                System.out.println("Player " + currentPlayer + ", make your move (e.g., E3): ");
                Scanner scanner = new Scanner(System.in);
                String move = scanner.nextLine();
                if (makeMove(move, currentPlayer)) {
                    currentPlayer = 'W';
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            } else {
                System.out.println("AI is making a move...");
                String aiMove = getBestMove(currentPlayer); // Implement Minimax or Alpha-Beta here
                makeMove(aiMove, currentPlayer);
                currentPlayer = 'B';
            }
            if (isGameOver()) {
                declareWinner();
                break;
            }
        }
    }

    // AI vs AI mode
    private static void playAIvsAI() {
        char currentPlayer = 'B';
        while (true) {
            printBoard();
            System.out.println("AI (" + currentPlayer + ") is making a move...");
            String aiMove = getBestMove(currentPlayer); // Implement Minimax or Alpha-Beta here
            makeMove(aiMove, currentPlayer);
            currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
            if (isGameOver()) {
                declareWinner();
                break;
            }
        }
    }

    // Check if the move is valid and make it
    private static boolean makeMove(String move, char player) {
        // Implement move validation and flipping logic
        return true; // Placeholder
    }

    // Get the best move for AI
    private static String getBestMove(char player) {
        // Implement Minimax or Alpha-Beta Pruning here
        return "D3"; // Placeholder
    }

    // Check if the game is over
    private static boolean isGameOver() {
        // Implement game-over logic
        return false; // Placeholder
    }

    // Declare the winner
    private static void declareWinner() {
        int blackCount = 0, whiteCount = 0;
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'B') blackCount++;
                if (cell == 'W') whiteCount++;
            }
        }
        System.out.println("Game over!");
        System.out.println("Black: " + blackCount + " | White: " + whiteCount);
        if (blackCount > whiteCount) System.out.println("Black wins!");
        else if (whiteCount > blackCount) System.out.println("White wins!");
        else System.out.println("It's a tie!");
    }
}
