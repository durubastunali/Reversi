package Duru;

import java.util.Scanner;

public class Reversi {

    private final int gameMod;
    private final int heuristic;
    private final int minimaxDepth;
    private char player = 'X', opponent = 'O'; //O: white, X: black
    private boolean writeMode = false;
    private boolean xCanMove = true;
    private boolean oCanMove = true;

    //Black player starts first

    private char[][] board = new char[8][8];
    private char[][] copyBoard = new char[8][8];


    public Reversi(int gameMod, int heuristic, int minimaxDepth) {
        this.gameMod = gameMod;
        this.heuristic = heuristic;
        this.minimaxDepth = minimaxDepth;
    }

    public void startGame() {
        initializeBoard();
        if (gameMod == 1) {
            playHumanVsHuman();
        } else if (gameMod == 2) {
            playHumanVsAI();
        } else if (gameMod == 3) {
            playAIvsAI();
        } else {
            System.out.println("Invalid game mod");
        }
    }

    private void playHumanVsHuman() {
        printBoard();
        while (true) {

            turnHuman();

            xCanMove = playerCanMove('X', 'O');
            oCanMove = playerCanMove('O', 'X');

            printBoard();

            if (!xCanMove && !oCanMove) {
                calculateScore();
                break;
            }
        }
    }

    private void playHumanVsAI() {
        printBoard();

        while (true) {


            if (player == 'X') {
                turnHuman();
            } else if (player == 'O') {
                Node node = alphaBetaSearch(); // hamle verecek burada şunu yap dicek
                turnAI(node.row, node.column);
            }

            xCanMove = playerCanMove('X', 'O');
            oCanMove = playerCanMove('O', 'X');

            printBoard();

            if (!xCanMove && !oCanMove) {
                calculateScore();
                break;
            }
        }
    }

    private void playAIvsAI() {
        printBoard();
        while (true) {

            Node node = alphaBetaSearch(); // hamle verecek burada şunu yap dicek

            if(node == null){
                if (player == 'X' && oCanMove) {
                    player = 'O';
                    opponent = 'X';
                } else if (player == 'O' && xCanMove) {
                    player = 'X';
                    opponent = 'O';
                }else{
                    calculateScore();
                    break;
                }
                continue;
            }
            turnAI(node.row, node.column);

            xCanMove = playerCanMove('X', 'O');
            oCanMove = playerCanMove('O', 'X');

            printBoard();

            if (!xCanMove && !oCanMove) {
                calculateScore();
                break;
            }
        }
    }

    private Node alphaBetaSearch() {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int bestValue = Integer.MIN_VALUE;
        Node bestMove = null;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (makeMove(i, j, player, opponent)) {
                    copyBoard = cloneBoard(); // Clone the board state
                    int value = minimize(1, alpha, beta,i,j);
                    if (value > bestValue) {
                        bestValue = value;
                        bestMove = new Node(null, i, j);
                    }
                    copyBoard = undoBoardState(); // Undo the move
                }
            }
        }
        return bestMove;
    }

    private int maximize(int depth, int alpha, int beta) {
        if (depth == minimaxDepth || isTerminalState()) {
            return getHeuristic1(player, opponent);
        }

        int value = Integer.MIN_VALUE;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (makeMove(i, j, player, opponent)) {
                    copyBoard = cloneBoard();
                    value = Math.max(value, minimize(depth + 1, alpha, beta,i,j));
                    copyBoard = undoBoardState();

                    if (value >= beta) {
                        return value; // Beta cutoff
                    }
                    alpha = Math.max(alpha, value);
                }
            }
        }
        return value;
    }

    private int minimize(int depth, int alpha, int beta, int row, int column) {
        if (depth == minimaxDepth || isTerminalState()) {
            if(heuristic== 1){
                return getHeuristic1(opponent, player);
            }else if(heuristic== 2){
                return getHeuristic2(row,column);
            }else{
                return getHeuristic3(row, column);
            }
        }

        int value = Integer.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (makeMove(i, j, opponent, player)) {
                    copyBoard = cloneBoard();
                    value = Math.min(value, maximize(depth + 1, alpha, beta));
                    copyBoard = undoBoardState();

                    if (value <= alpha) {
                        return value; // Alpha cutoff
                    }
                    beta = Math.min(beta, value);
                }
            }
        }
        return value;
    }

    private char[][] cloneBoard() {
        char[][] clone = new char[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(copyBoard[i], 0, clone[i], 0, 8);
        }
        return clone;
    }

    private char[][] undoBoardState() {
        char[][] previousState = cloneBoard();
        return previousState;
    }

    private boolean isTerminalState() {
        return !playerCanMove(player, opponent) && !playerCanMove(opponent, player);
    }



    private int getHeuristic1(char customPlayer, char customOpponent) { //Mesela bunu AI kullanacağından playerı ona göre atılmalı?
        int playerScore = 0;
        int opponentScore = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (copyBoard[i][j] == customPlayer) {
                    playerScore++;
                } else if (copyBoard[i][j] == customOpponent) {
                    opponentScore++;
                }
            }
        }

        copyBoard = board.clone(); // Muhtemelen önce hamleyi hayali bi boardda yapcaaz (copy board)
        // o hamlenin evaluationını aldıktan sonra da geri haline getircez? emin değilim

        return playerScore - opponentScore;
    }

    private int getHeuristic2(int row, int column) { //Burada direkt hamleyi alcaz ve kköşeye, kenara yakınlığına bakcaz


        int[][] evaluationBoard = { {100, 80, 80, 80, 80, 80, 80, 100},
                { 80, 50, 50, 50, 50, 50, 50,  80},
                { 80, 50,  0,  0,  0,  0, 50,  80},
                { 80, 50,  0,  0,  0,  0, 50,  80},
                { 80, 50,  0,  0,  0,  0, 50,  80},
                { 80, 50,  0,  0,  0,  0, 50,  80},
                { 80, 50, 50, 50, 50, 50, 50,  80},
                {100, 80, 80, 80, 80, 80, 80, 100}};

        return evaluationBoard[row][column];
    }

    private int getHeuristic3(int row, int column) { // Bu da heuristic 2'nin gelişmişi olabilir
        //Beki buna player - opponent taş sayısı da eklenir


        int[][] evaluationBoard = {
                { 100, -100,  80,  80,  80,  80, -100,  100},
                {-100, -100, -50, -50, -50, -50, -100, -100},
                {  80,  -20,  50,  50,  50,  50,  -20,   80},
                {  80,  -50,  50,   0,   0,  50,   50,   80},
                {  80,  -50,  50,   0,   0,  50,   50,   80},
                {  80,  -20,  50,   0,   0,  50,  -20,   80},
                {-100, -100,  50,  50,  50,  50,  -100, -100},
                {-100, -100,  80,  80,  80,  80,  -100,  100}};

        return evaluationBoard[row][column];
    }

    private void turnHuman() {
        System.out.println(player + "'s Turn. Move (e.g., A1): ");
        Scanner scanner = new Scanner(System.in);
        String move = scanner.nextLine();

        int row = move.charAt(1) - '1';
        int column = move.charAt(0) - 'A';

        writeMode = true;
        if (makeMove(row, column, player, opponent)) {
            if (player == 'X' && oCanMove) {
                player = 'O';
                opponent = 'X';
            } else if (player == 'O' && xCanMove) {
                player = 'X';
                opponent = 'O';
            }
        } else {
            System.out.println("Invalid move");
        }
        writeMode = false;
    }

    private void turnAI(int row, int column) {
        writeMode = true;
        if (makeMove(row, column, player, opponent)) {
            if (player == 'X' && oCanMove) {
                player = 'O';
                opponent = 'X';
            } else if (player == 'O' && xCanMove) {
                player = 'X';
                opponent = 'O';
            }
        } else {
            System.out.println("Invalid move");
        }
        writeMode = false;
    }

    private boolean makeMove(int row, int column, char customPlayer, char customOpponent) {
        int concurrentRow;
        int concurrentColumn;

        boolean valid = false;

        if (!checkInBoundary(row, column)) {
            return false;
        }

        if (board[row][column] != '.') {
            return false;
        }

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                if (i == 0 && j == 0) {
                    continue;
                }

                if (checkInBoundary(row + i, column + j) && board[row + i][column + j] == customOpponent) {
                    concurrentRow = row + i;
                    concurrentColumn = column + j;
                    if (checkValidMove(concurrentRow, concurrentColumn, i, j, customPlayer, customOpponent)) {
                        valid = true;
                    }
                }
            }
        }
        return valid;
    }

    private boolean checkValidMove(int row, int column, int x, int y, char customPlayer, char customOpponent) {
        int nextRow = row + x;
        int nextColumn = column + y;
        while (checkInBoundary(nextRow, nextColumn)) {
            if (board[nextRow][nextColumn] == customPlayer) {
                if (writeMode) {
                    turnDiscs(row, column, nextRow, nextColumn, x, y);
                }
                return true;
            } else if (board[nextRow][nextColumn] == customOpponent) {
                nextRow += x;
                nextColumn += y;
            } else {
                return false;
            }
        }
        return false;
    }

    private void turnDiscs(int row, int column, int finalRow, int finalColumn, int x, int y) {
        int currentRow = row;
        int currentColumn = column;
        while (!(currentRow == finalRow && currentColumn == finalColumn)) {
            board[currentRow][currentColumn] = player;
            currentRow += x;
            currentColumn += y;
        }
        board[row - x][column - y] = player;
    }

    private boolean checkInBoundary(int row, int column) {
        return row >= 0 && row < 8 && column >= 0 && column < 8;
    }

    private boolean playerCanMove(char customPlayer, char customOpponent) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (makeMove(i, j, customPlayer, customOpponent)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void calculateScore() {
        int scoreX = 0;
        int scoreO = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 'X') {
                    scoreX++;
                } else if (board[i][j] == 'O') {
                    scoreO++;
                }
            }
        }

        if (scoreX > scoreO) {
            System.out.println("X wins!");
        } else if (scoreO > scoreX) {
            System.out.println("O wins!");
        } else {
            System.out.println("Draw!");
        }
        System.out.println("X Score: " + scoreX);
        System.out.println("O Score: " + scoreO);
    }

    private void initializeBoard() {
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

    private void printBoard() {
        System.out.println("PLAYER: " + player);
        for (int i = 0; i < 8; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }
}