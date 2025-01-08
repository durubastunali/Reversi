package Duru;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Human vs Human");
        System.out.println("2. Human vs AI");
        System.out.println("3. AI vs AI");
        System.out.print("Select a game mode: ");

        int mode = scanner.nextInt();

        int heuristic = 0;
        int depth;

        if (mode == 3) {
            System.out.println("\n1. h1 vs h2");
            System.out.println("2. h1 vs h3");
            System.out.println("3. h2 vs h3");
            System.out.print("Select a heuristic: ");

            heuristic = scanner.nextInt();
        }

        System.out.print("\nEnter the depth of the tree: ");
        depth = scanner.nextInt();

        System.out.println();

        (new Reversi(mode, heuristic, depth)).startGame();
    }
}
