import java.util.Scanner;

public class Reversi {

    private final int gameMod;
    private final int heuristic;
    private final int minimaxDepth;
    private final int starter;
    private char player = 'X', opponent = 'O'; //O: white, X: black
    private boolean writeMode = false;
    private boolean xCanMove = true;
    private boolean oCanMove = true;

    //Black player starts first

    private char[][] board = new char[8][8];


    public Reversi(int gameMod, int heuristic, int minimaxDepth, int starter) {
        this.gameMod = gameMod;
        this.heuristic = heuristic;
        this.minimaxDepth = minimaxDepth;
        this.starter = starter;
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
        while (true) {
            printBoard();
            turnHuman();

            if (xCanMove) {
                xCanMove = playerCanMove('X', 'O');
            }

            if (oCanMove) {
                oCanMove = playerCanMove('O', 'X');
            }

            if (!xCanMove && !oCanMove) {
                calculateScore();
                break;
            }
        }
    }

    private void playHumanVsAI() {
        boolean humanTurn = true;
        if (starter == 2) {
            humanTurn = false;
        }

        while(true) {
            printBoard();
            if (humanTurn) {
                turnHuman();
                humanTurn = false;
            } else {
                Node node = alphaBetaSearch(); // hamle verecek burada şunu yap dicek
                turnAI(node.row, node.column);
                humanTurn = true;

            }

            if (xCanMove) {
                xCanMove = playerCanMove('X', 'O');
            }

            if (oCanMove) {
                oCanMove = playerCanMove('O', 'X');
            }

            if (!xCanMove && !oCanMove) {
                calculateScore();
                break;
            }
        }
    }

    private void playAIvsAI() {
        while (true) {
            printBoard();
            Node node = alphaBetaSearch(); // hamle verecek burada şunu yap dicek
            turnAI(node.row, node.column);

            if (xCanMove) {
                xCanMove = playerCanMove('X', 'O');
            }

            if (oCanMove) {
                oCanMove = playerCanMove('O', 'X');
            }

            if (!xCanMove && !oCanMove) {
                calculateScore();
                break;
            }
        }
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
