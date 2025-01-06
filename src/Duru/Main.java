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
        int depth = 0;

        if (mode == 2) {
            System.out.println("\n1. Human");
            System.out.println("2. AI");
            System.out.print("Enter the starter player: ");

        }

        if (mode != 1) {
            System.out.println("\n1. h1");
            System.out.println("2. h2");
            System.out.println("3. h3");
            System.out.print("Select a heuristic: ");

            heuristic = scanner.nextInt();

            System.out.print("\nEnter the depth of the tree: ");
            depth = scanner.nextInt();
        }

        System.out.println();

        (new Reversi(mode, heuristic, depth)).startGame();
    }
}
