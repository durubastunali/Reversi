package Duru;

public class Node {
    public Node parent;
    public int row;
    public int column;

    public Node(Node parent, int row, int col) {
        this.parent = parent;
        this.row = row;
        this.column = col;
    }
}
