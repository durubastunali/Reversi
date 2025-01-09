package Duru;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Take the game mode as an input
        System.out.println("1. Human vs Human");
        System.out.println("2. Human vs AI");
        System.out.println("3. AI vs AI");
        System.out.print("Select a game mode: ");

        int mode = scanner.nextInt();

        int heuristic = 0;
        int depth = 0;

        // Take the evaluation method comparison option as an input
        if (mode == 3) {
            System.out.println("\n1. h1 vs h2");
            System.out.println("2. h1 vs h3");
            System.out.println("3. h2 vs h3");
            System.out.print("Select a heuristic: ");

            heuristic = scanner.nextInt();
        }

        // Take the depth of the minimax tree as an input
        if (mode != 1) {
            System.out.print("\nEnter the depth of the tree: ");
            depth = scanner.nextInt();
        }

        System.out.println();

        (new Reversi(mode, heuristic, depth)).startGame();
    }
}
