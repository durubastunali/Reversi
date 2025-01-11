import java.util.Scanner;

public class Reversi {
    private char[][] board = new char[8][8];
    private final int evaluationComparison;
    private final int gameMode;
    private final int depth;
    private long startTime;

    public Reversi(int gameMode, int evaluationComparison, int depth) {
        this.gameMode = gameMode;
        this.evaluationComparison = evaluationComparison;
        this.depth = depth;
    }

    public void startGame() { // Start the game by initializing the board
        initializeBoard();
        switch (gameMode) { // Decide on the game mode
            case 1 -> playHumanVsHuman();
            case 2 -> playHumanVsAI();
            case 3 -> playAIvsAI();
            default -> System.out.println("Invalid game mode");
        }
    }

    private void initializeBoard() { // Board initialization
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

    private void printBoard() { // Printing the board
        for (int i = 0; i < 8; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("  A B C D E F G H");

    }

    private void playHumanVsHuman() { // Human vs Human game mode
        char currentPlayer = 'X'; // X starts the game
        char opponentPlayer = 'O';
        while (true) {
            printBoard();
            System.out.print("Player " + currentPlayer + ": ");
            Scanner scanner = new Scanner(System.in); // Take the move from the user
            String move = scanner.nextLine();
            if (makeMove(move, currentPlayer)) {
                if (canMove(opponentPlayer)) { // If the opponent can move, flip turns
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                    opponentPlayer = (opponentPlayer == 'X') ? 'O' : 'X';
                }
            } else {
                System.out.println("Invalid move");
            }
            if (checkGameOver()) { // Check if game is finished
                calculateScore(); // Calculate the score
                break;
            }
        }
    }

    private void playHumanVsAI() { // Human vs AI mode
        char currentPlayer = 'X';
        char opponentPlayer = 'O';
        while (true) {
            printBoard();
            if (currentPlayer == 'X') {
                System.out.print("Player X: ");
                Scanner scanner = new Scanner(System.in); // Take the move from the user
                String move = scanner.nextLine();
                if (makeMove(move, currentPlayer)) { // If the opponent can move, flip turns
                    if (canMove(opponentPlayer)) {
                        currentPlayer = 'O';
                        opponentPlayer = 'X';
                    }
                } else {
                    System.out.println("Invalid move");
                }
            } else {
                System.out.println("Player O is thinking");
                startTime = System.currentTimeMillis(); // Start the timer of AI player
                String aiMove = alphaBetaSearch(currentPlayer); // Get the move with Alpha-Beta Pruning search
                if (aiMove != null) {
                    if (makeMove(aiMove, currentPlayer)) {
                        if (canMove(opponentPlayer)) { // If the opponent can move, flip turns
                            currentPlayer = 'X';
                            opponentPlayer = 'O';
                        }
                    }
                } else { // If AI move is null, it means AI cannot move, flip turns
                    currentPlayer = 'X';
                    opponentPlayer = 'O';
                }
            }
            if (checkGameOver()) { // Check if game is over
                calculateScore(); // Calculate the score
                break;
            }
        }
    }

    private void playAIvsAI() { // AI vs AI game mode
        char currentPlayer = 'X';
        while (true) {
            printBoard();
            if (checkGameOver()) { // Check if the game is over
                calculateScore(); // Calculate the score
                break;
            }
            System.out.println("Player " + currentPlayer + " is thinking");
            startTime = System.currentTimeMillis();
            String aiMove = alphaBetaSearch(currentPlayer);
            if (aiMove == null) { // If AI move is null, AI cannot move, flip turns
                System.out.println("Player " + currentPlayer + " cannot make a move");
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                continue;
            }
            makeMove(aiMove, currentPlayer); // After making a move, flip turns
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }
    }

    private boolean makeMove(String move, char player) {
        int row = move.charAt(1) - '1'; // Parse the input taken for row
        int column = move.charAt(0) - 'A'; // Parse the input taken for column

        // Check empty and in borders, else return false
        if (row < 0 || row >= 8 || column < 0 || column >= 8 || board[row][column] != '.') {
            return false;
        }

        boolean valid = false;
        char opponent = (player == 'X') ? 'O' : 'X'; // Determine the opponent

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {

                if (rowOffset == 0 && columnOffset == 0) { // Skip the moved tile
                    continue;
                }

                int adjacentRow = row + rowOffset; // Calculate the adjacent row position
                int adjacentColumn = column + columnOffset; // Calculate the adjacent column position
                boolean hasOpponent = false;

                // Traverse in that direction as long as there is an opponent disc
                while (adjacentRow >= 0 && adjacentRow < 8 && adjacentColumn >= 0 && adjacentColumn < 8 &&
                        board[adjacentRow][adjacentColumn] == opponent) {
                    adjacentRow += rowOffset;
                    adjacentColumn += columnOffset;
                    hasOpponent = true;
                }

                // Check if there's a player disc at the end of the direction
                if (hasOpponent && adjacentRow >= 0 && adjacentRow < 8 && adjacentColumn >= 0 && adjacentColumn < 8 &&
                        board[adjacentRow][adjacentColumn] == player) {
                    valid = true;
                    adjacentRow = row + rowOffset; // Restart from the original positioning of the alignment
                    adjacentColumn = column + columnOffset;

                    // Flip the opponent discs into player discs
                    while (board[adjacentRow][adjacentColumn] == opponent) {
                        board[adjacentRow][adjacentColumn] = player; // Turn the discs
                        adjacentRow += rowOffset;
                        adjacentColumn += columnOffset;
                    }
                }
            }
        }

        if (valid) { // Place the disc
            board[row][column] = player;
        }

        return valid; // Return whether the process is valid or not
    }

    private String alphaBetaSearch(char player) {
        int bestScore = Integer.MIN_VALUE;
        String bestMove = null;
        char opponent = (player == 'X') ? 'O' : 'X';

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == '.') {
                    char[][] backupBoard = copyBoard(); // Copy the board to save the original state
                    if (makeMove(parseToString(row, col), player)) { // Make the possible move and call minimax algorithm
                        int score = minimax(depth, false, Integer.MIN_VALUE, Integer.MAX_VALUE, opponent, row, col);
                        board = backupBoard;
                        if (score > bestScore) { // Alpha-Beta pruning
                            bestScore = score;
                            bestMove = parseToString(row, col);
                        }
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(int depth, boolean isMaximizing, int alpha, int beta, char player, int inputRow, int inputColumn) {
        // Check the time limit and if the game is over
        if (System.currentTimeMillis() - startTime > 10000 || depth == 0  || checkGameOver()) {
            if (gameMode == 2) { // If Human vs AI mode, return the best evaluation method: H3
                return getEvaluation3(player);
            } else if (gameMode == 3) { // AI vs AI game mode
                if (evaluationComparison == 1) { // H1 vs H2
                    if (player == 'X') {
                        return getEvaluation1(player);
                    } else if (player == 'O') {
                        return getEvaluation2(inputRow, inputColumn);
                    }
                } else if (evaluationComparison == 2) { // H1 vs H3
                    if (player == 'X') {
                        return getEvaluation1(player);
                    } else if (player == 'O') {
                        return getEvaluation3(player);
                    }
                } else if (evaluationComparison == 3) { // H2 vs H3
                    if (player == 'X') {
                        return getEvaluation2(inputRow, inputColumn);

                    } else if (player == 'O') {
                        return getEvaluation3(player);
                    }
                }
            }
        }

        char opponent = (player == 'X') ? 'O' : 'X'; // Determine opponent

        if (isMaximizing) { // Maximize mode
            int maxEvaluationValue = Integer.MIN_VALUE;
            for (int row = 0; row < 8; row++) {
                for (int column = 0; column < 8; column++) {
                    if (board[row][column] == '.') {
                        char[][] originalBoard = copyBoard(); // Save the current board state
                        if (makeMove(parseToString(row, column), player)) { // Make the move
                            // Send for minimizing
                            int eval = minimax(depth - 1, false, alpha, beta, opponent, row, column);
                            board = originalBoard; // Restore the original board
                            maxEvaluationValue = Math.max(maxEvaluationValue, eval); // Set the max evaluation value
                            alpha = Math.max(alpha, eval); // Set alpha value
                            if (beta <= alpha) { // Alpha-beta pruning
                                return maxEvaluationValue; // Return maximum evaluation value with alpha-beta pruning
                            }
                        }
                    }
                }
            }
            return maxEvaluationValue; // Eventually return the maximum evaluation value
        } else { // Minimize mode
            int minEvaluationValue = Integer.MAX_VALUE;
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == '.') {
                        char[][] originalBoard = copyBoard(); // Save the current board state
                        if (makeMove(parseToString(row, col), player)) { // Make the move
                            // Send for maximizing
                            int eval = minimax(depth - 1, true, alpha, beta, opponent, row, col);
                            board = originalBoard; // Restore the original board
                            minEvaluationValue = Math.min(minEvaluationValue, eval); // Set the min evaluation value
                            beta = Math.min(beta, eval); // Set beta value
                            if (beta <= alpha) { // Alpha-beta pruning
                                return minEvaluationValue; // Return minimum evaluation value with alpha-beta pruning
                            }
                        }
                    }
                }
            }
            return minEvaluationValue; // Eventually return the minimum evaluation value
        }
    }

    private char[][] copyBoard() { // Copy the original board content
        char[][] copy = new char[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    private String parseToString(int row, int column) { // Row-Column move into String format
        return "" + (char) (column + 'A') + (row + 1);
    }

    private int getEvaluation1(char player) { // Evaluation method H1: Player Discs - Opponent Discs
        int playerScore = 0, opponentScore = 0;

        char opponent = (player == 'X') ? 'O' : 'X'; // Determine opponent

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player) {
                    playerScore ++; // Calculate how many discs player has on board
                } else if (board[row][col] == opponent) {
                    opponentScore ++; // Calculate how many discs opponent has on board
                }
            }
        }

        return playerScore - opponentScore;
    }

    private int getEvaluation2(int row, int col) { // Evaluation method H2: Tile value
        int[][] tileValue = {
                {100, -20, 10,  5,  5, 10, -20, 100},
                {-20, -50, -2, -2, -2, -2, -50, -20},
                { 10,  -2,  5,  5,  5,  5,  -2,  10},
                {  5,  -2,  5,  0,  0,  5,  -2,   5},
                {  5,  -2,  5,  0,  0,  5,  -2,   5},
                { 10,  -2,  5,  5,  5,  5,  -2,  10},
                {-20, -50, -2, -2, -2, -2, -50, -20},
                {100, -20, 10,  5,  5, 10, -20, 100}
        };

        return tileValue[row][col];
    }

    private  int getEvaluation3(char player) { // Evaluation method H3: Mixture of H1 and H2
        int[][] tileValue = {
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
                    playerScore+= tileValue[i][j]; // Calculate player score with tile values
                } else if (board[i][j] == opponent) {
                    opponentScore+= tileValue[i][j]; // Calculate opponent score with tile values
                }
            }
        }
        return playerScore - opponentScore; // Return player score - opponent score
    }

    private boolean checkGameOver() {
        boolean xCanMove = canMove('X'); // Check if X can move
        boolean oCanMove = canMove('O'); // Check if Y can move

        return !(xCanMove || oCanMove); // If neither of them can move, return true
    }

    private boolean canMove(char player) { // Check if the given player can move
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

    // Exact same code with makeMove method but without turning the discs
    private boolean checkValidMove(int row, int column, char player) {
        char opponent = (player == 'X') ? 'O' : 'X';

        if (board[row][column] != '.') {
            return false;
        }

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {

                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }

                int currentRow = row + rowOffset;
                int currentColumn = column + columnOffset;
                boolean hasOpponent = false;

                while (currentRow >= 0 && currentRow < 8 && currentColumn >= 0 && currentColumn < 8 &&
                        board[currentRow][currentColumn] == opponent) {
                    currentRow += rowOffset;
                    currentColumn += columnOffset;
                    hasOpponent = true;
                }

                if (hasOpponent && currentRow >= 0 && currentRow < 8 && currentColumn >= 0 && currentColumn < 8 &&
                        board[currentRow][currentColumn] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private void calculateScore() { // Calculate the number of tiles for both players
        int blackCount = 0, whiteCount = 0;
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'X') blackCount++;
                if (cell == 'O') whiteCount++;
            }
        }
        System.out.println("\nGAME OVER!"); // Print the scores
        System.out.println("Black Score: " + blackCount);
        System.out.println("White Score: " + whiteCount);

        if (blackCount > whiteCount) { // Determine the winner or if it is a tie
            System.out.println("Black player won");
        } else if (whiteCount > blackCount) {
            System.out.println("White player won");
        } else {
            System.out.println("Tie");
        }
    }
}