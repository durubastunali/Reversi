import java.util.Scanner;

public class Reversi {
    private char[][] board = new char[8][8]; // 'X' for Black, 'O' for White, '.' for empty
    private final int evaluationComparison;
    private final int gameMode;
    private final int depth;
    private long startTime;

    public Reversi(int gameMode, int evaluationComparison, int depth) {
        this.gameMode = gameMode;
        this.evaluationComparison = evaluationComparison;
        this.depth = depth;
    }

    public  void startGame() {
        initializeBoard();
        switch (gameMode) {
            case 1 -> playHumanVsHuman();
            case 2 -> playHumanVsAI();
            case 3 -> playAIvsAI();
            default -> System.out.println("Invalid game mode");
        }
    }

    private  void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = '.';
            }
        }
        board[3][3] = 'O';
        board[4][4] = 'O';
        board[3][4] = 'X';
        board[4][3] = 'X';
    }

    // Display the board
    private  void printBoard() {
        for (int i = 0; i < 8; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("  A B C D E F G H");

    }

    // Human vs Human mode
    private  void playHumanVsHuman() {
        char currentPlayer = 'X';
        char opponentPlayer = 'O';
        while (true) {
            printBoard();
            System.out.print("Player " + currentPlayer + ": ");
            Scanner scanner = new Scanner(System.in);
            String move = scanner.nextLine();
            if (makeMove(move, currentPlayer)) {
                if (canMove(opponentPlayer)) {
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                    opponentPlayer = (opponentPlayer == 'X') ? 'O' : 'X';
                }
            } else {
                System.out.println("Invalid move");
            }
            if (isGameOver()) {
                declareWinner();
                break;
            }
        }
    }

    // Human vs AI mode
    private  void playHumanVsAI() {
        char currentPlayer = 'X';
        while (true) {
            printBoard();
            if (isGameOver()) {
                declareWinner();
                break;
            }
            if (currentPlayer == 'X') {
                System.out.println("Player " + currentPlayer + ", make your move (e.g., E3): ");
                Scanner scanner = new Scanner(System.in);
                String move = scanner.nextLine();
                if (makeMove(move, currentPlayer)) {
                    currentPlayer = 'O';
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            } else {
                System.out.println("AI is making a move...");
                startTime = System.currentTimeMillis();
                String aiMove = alphaBetaSearch(currentPlayer);
                if (aiMove != null) makeMove(aiMove, currentPlayer);
                currentPlayer = 'X';
            }
        }
    }

    // AI vs AI mode
    private  void playAIvsAI() {
        char currentPlayer = 'X';
        int skipCount = 0;
        while (true) {
            printBoard();
            if (isGameOver()) {
                declareWinner();
                break;
            }
            System.out.println("AI (" + currentPlayer + ") is making a move...");
            startTime = System.currentTimeMillis();
            String aiMove = alphaBetaSearch(currentPlayer);
            if (aiMove == null) {
                System.out.println("AI (" + currentPlayer + ") has no valid moves. Skipping turn.");
                skipCount++;
                if (skipCount >= 2) {
                    declareWinner();
                    break;
                }
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                continue;
            }
            makeMove(aiMove, currentPlayer);
            skipCount = 0;
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }
    }

    // Check if the move is valid and make it
    private  boolean makeMove(String move, char player) {
        int row = move.charAt(1) - '1';
        int col = move.charAt(0) - 'A';

        if (row < 0 || row >= 8 || col < 0 || col >= 8 || board[row][col] != '.') {
            return false;
        }

        boolean valid = false;
        char opponent = (player == 'X') ? 'O' : 'X';

        int[] dr = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dc = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int d = 0; d < 8; d++) {
            int r = row + dr[d];
            int c = col + dc[d];
            boolean hasOpponent = false;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == opponent) {
                r += dr[d];
                c += dc[d];
                hasOpponent = true;
            }

            if (hasOpponent && r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == player) {
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
    private  String alphaBetaSearch(char player) {
        int bestScore = Integer.MIN_VALUE;
        String bestMove = null;
        char opponent = (player == 'X') ? 'O' : 'X';

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == '.') {
                    char[][] backupBoard = copyBoard();
                    if (makeMove(rowToMove(row, col), player)) {
                        int score = minimax(depth, false, Integer.MIN_VALUE, Integer.MAX_VALUE, opponent, row, col);
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
    private  int minimax(int depth, boolean isMaximizing, int alpha, int beta, char player, int rowIn, int colIn) {
        if (System.currentTimeMillis() - startTime > 10000 || depth == 0  || isGameOver()) {
            if (gameMode == 2) {
                return getEvaluation3(player);
            } else if (gameMode == 3) {
                if (evaluationComparison == 1) {
                    if (player == 'X') {
                        return getEvaluation1(player);
                    } else if (player == 'O') {
                        return getEvaluation2(rowIn, colIn);
                    }
                } else if (evaluationComparison == 2) {
                    if (player == 'X') {
                        return getEvaluation1(player);
                    } else if (player == 'O') {
                        return getEvaluation3(player);
                    }
                } else if (evaluationComparison == 3) {
                    if (player == 'X') {
                        return getEvaluation2(rowIn, colIn);

                    } else if (player == 'O') {
                        return getEvaluation3(player);
                    }
                }
            }
        }

        char opponent = (player == 'X') ? 'O' : 'X';

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == '.') {
                        char[][] backupBoard = copyBoard();
                        if (makeMove(rowToMove(row, col), player)) {
                            int eval = minimax(depth - 1, false, alpha, beta, opponent, row, col);
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
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == '.') {
                        char[][] backupBoard = copyBoard();
                        if (makeMove(rowToMove(row, col), player)) {
                            int eval = minimax(depth - 1, true, alpha, beta, opponent, row, col);
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

    private char[][] copyBoard() {
        char[][] copy = new char[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    private String rowToMove(int row, int col) {
        return "" + (char) (col + 'A') + (row + 1);
    }

    private int getEvaluation1(char player) {
        int playerScore = 0, opponentScore = 0;

        char opponent = (player == 'X') ? 'O' : 'X';

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player) {
                    playerScore ++;
                } else if (board[row][col] == opponent) {
                    opponentScore ++;
                }
            }
        }

        return playerScore - opponentScore;
    }

    private  int getEvaluation2(int row, int col) {
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

        return positionWeights[row][col];
    }

    private  int getEvaluation3(char player) {

        int[][] positionWeights = {
                {500, -20, 10,  5,  5, 10, -20, 500},
                {-20, -40, -5, -5, -5, -5, -40, -20},
                { 10,  -5,  3,  3,  3,  3,  -5,  10},
                { 10,  -5,  3,  0,  0,  3,  -5,  10},
                { 10,  -5,  3,  0,  0,  3,  -5,  10},
                { 10,  -5,  3,  3,  3,  3,  -5,  10},
                {-20, -40, -5, -5, -5, -5, -40, -20},
                {500, -20, 10,  5,  5, 10, -20, 500},
        };

        int playerScore = 0;
        int opponentScore = 0;

        char opponent = (player == 'X') ? 'O' : 'X';

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == player) {
                    playerScore+= positionWeights[i][j];
                } else if (board[i][j] == opponent) {
                    opponentScore+= positionWeights[i][j];
                }
            }
        }
        return playerScore - opponentScore;

    }

    private boolean isGameOver() {
        boolean xCanMove = canMove('X');
        boolean oCanMove = canMove('O');

        return !(xCanMove || oCanMove);
    }

    private boolean canMove(char player) {
        boolean canMove = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == '.') {
                    if (checkValidMove(row, col, player)) {
                        canMove = true;
                    }
                }
            }
        }
        return canMove;
    }

    private boolean checkValidMove(int row, int col, char player) {
        char opponent = (player == 'X') ? 'O' : 'X';

        if (board[row][col] != '.') {
            return false;
        }

        int[] dr = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dc = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int d = 0; d < 8; d++) {
            int r = row + dr[d];
            int c = col + dc[d];
            boolean hasOpponent = false;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == opponent) {
                r += dr[d];
                c += dc[d];
                hasOpponent = true;
            }

            if (hasOpponent && r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == player) {
                return true;
            }
        }
        return false;
    }

    private  void declareWinner() {
        int blackCount = 0, whiteCount = 0;
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'X') blackCount++;
                if (cell == 'O') whiteCount++;
            }
        }
        System.out.println("Game over!");
        System.out.println("Black: " + blackCount + " | White: " + whiteCount);
        if (blackCount > whiteCount) System.out.println("Black wins!");
        else if (whiteCount > blackCount) System.out.println("White wins!");
        else System.out.println("It's a tie!");
    }
}
