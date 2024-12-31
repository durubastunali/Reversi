package Necati;

import java.util.Scanner;

public class Necati {
    static final int SIZE = 8;
    static char[][] board = new char[SIZE][SIZE]; // 'B' for Black, 'W' for White, '.' for empty
    private static long startTime;
    private static final long TIME_LIMIT = 10000; // 10 seconds

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
            if (isGameOver()) {
                declareWinner();
                break;
            }
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
                startTime = System.currentTimeMillis();
                String aiMove = getBestMove(currentPlayer);
                if (aiMove != null) makeMove(aiMove, currentPlayer);
                currentPlayer = 'B';
            }
        }
    }

    // AI vs AI mode
    private static void playAIvsAI() {
        char currentPlayer = 'B';
        int skipCount = 0;
        while (true) {
            printBoard();
            if (isGameOver()) {
                declareWinner();
                break;
            }
            System.out.println("AI (" + currentPlayer + ") is making a move...");
            startTime = System.currentTimeMillis();
            String aiMove = getBestMove(currentPlayer);
            if (aiMove == null) {
                System.out.println("AI (" + currentPlayer + ") has no valid moves. Skipping turn.");
                skipCount++;
                if (skipCount >= 2) {
                    declareWinner();
                    break;
                }
                currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
                continue;
            }
            makeMove(aiMove, currentPlayer);
            skipCount = 0;
            currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
        }
    }

    // Check if the move is valid and make it
    private static boolean makeMove(String move, char player) {
        int row = move.charAt(1) - '1';
        int col = move.charAt(0) - 'A';

        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || board[row][col] != '.') {
            return false;
        }

        boolean valid = false;
        char opponent = (player == 'B') ? 'W' : 'B';

        int[] dr = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dc = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int d = 0; d < 8; d++) {
            int r = row + dr[d];
            int c = col + dc[d];
            boolean hasOpponent = false;

            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == opponent) {
                r += dr[d];
                c += dc[d];
                hasOpponent = true;
            }

            if (hasOpponent && r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == player) {
                valid = true;
                r = row + dr[d];
                c = col + dc[d];
                while (board[r][c] == opponent) {
                    board[r][c] = player;
                    r += dr[d];
                    c += dc[d];
                }
            }
        }

        if (valid) {
            board[row][col] = player;
        }
        return valid;
    }

    // Get the best move for AI
    private static String getBestMove(char player) {
        int bestScore = Integer.MIN_VALUE;
        String bestMove = null;
        char opponent = (player == 'B') ? 'W' : 'B';

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == '.') {
                    char[][] backupBoard = copyBoard();
                    if (makeMove(rowToMove(row, col), player)) {
                        int score = minimax(6, false, Integer.MIN_VALUE, Integer.MAX_VALUE, opponent);
                        board = backupBoard;
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = rowToMove(row, col);
                        }
                    }
                }
            }
        }

        return bestMove;
    }

    // Minimax with Alpha-Beta Pruning
    private static int minimax(int depth, boolean isMaximizing, int alpha, int beta, char player) {
        if (System.currentTimeMillis() - startTime > TIME_LIMIT) {
            return evaluateBoard();
        }

        if (depth == 0 || isGameOver()) {
            return evaluateBoard();
        }

        char opponent = (player == 'B') ? 'W' : 'B';

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (board[row][col] == '.') {
                        char[][] backupBoard = copyBoard();
                        if (makeMove(rowToMove(row, col), player)) {
                            int eval = minimax(depth - 1, false, alpha, beta, opponent);
                            board = backupBoard;
                            maxEval = Math.max(maxEval, eval);
                            alpha = Math.max(alpha, eval);
                            if (beta <= alpha) {
                                return maxEval;
                            }
                        }
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (board[row][col] == '.') {
                        char[][] backupBoard = copyBoard();
                        if (makeMove(rowToMove(row, col), player)) {
                            int eval = minimax(depth - 1, true, alpha, beta, opponent);
                            board = backupBoard;
                            minEval = Math.min(minEval, eval);
                            beta = Math.min(beta, eval);
                            if (beta <= alpha) {
                                return minEval;
                            }
                        }
                    }
                }
            }
            return minEval;
        }
    }

    private static char[][] copyBoard() {
        char[][] copy = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    private static String rowToMove(int row, int col) {
        return "" + (char) (col + 'A') + (row + 1);
    }

    private static int evaluateBoard() {
        int[][] positionWeights = {
                {100, -20, 10,  5,  5, 10, -20, 100},
                {-20, -50, -2, -2, -2, -2, -50, -20},
                { 10,  -2,  5,  5,  5,  5,  -2,  10},
                {  5,  -2,  5,  0,  0,  5,  -2,   5},
                {  5,  -2,  5,  0,  0,  5,  -2,   5},
                { 10,  -2,  5,  5,  5,  5,  -2,  10},
                {-20, -50, -2, -2, -2, -2, -50, -20},
                {100, -20, 10,  5,  5, 10, -20, 100}
        };

        int blackScore = 0, whiteScore = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 'B') {
                    blackScore += positionWeights[row][col];
                } else if (board[row][col] == 'W') {
                    whiteScore += positionWeights[row][col];
                }
            }
        }
        return blackScore - whiteScore;
    }

    private static boolean isGameOver() {
        boolean blackHasMove = false;
        boolean whiteHasMove = false;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == '.') {
                    if (canMove(row, col, 'B')) {
                        blackHasMove = true;
                    }
                    if (canMove(row, col, 'W')) {
                        whiteHasMove = true;
                    }
                }
            }
        }
        return !(blackHasMove || whiteHasMove);
    }

    private static boolean canMove(int row, int col, char player) {
        char opponent = (player == 'B') ? 'W' : 'B';

        if (board[row][col] != '.') {
            return false;
        }

        int[] dr = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dc = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int d = 0; d < 8; d++) {
            int r = row + dr[d];
            int c = col + dc[d];
            boolean hasOpponent = false;

            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == opponent) {
                r += dr[d];
                c += dc[d];
                hasOpponent = true;
            }

            if (hasOpponent && r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == player) {
                return true;
            }
        }
        return false;
    }

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
