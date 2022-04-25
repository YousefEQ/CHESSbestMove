/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author zamil
 */
public class Position {
    
    private int col; // x axis
    private int row; // y axis
    private int index; // position in a 1-D array
    private String algebraic; // notation such as "d4"
    
    /**
     * Converts Cartesian coordinates to algebraic notation
     * @param col the column or "file"
     * @param row the row or "rank"
     * @return the algebraic notation for the associated Cartesian coordinates
     */
    private String cartesianToAlgebraic(int col, int row) {
        // convert 'col' to ASCII character, then convert to string.
        String file = String.valueOf((char)(col + 'a'));
        
        // (8-'row') because bottom row considered the first row in chess.
        String rank = String.valueOf(row + 1);
        
        return file+rank;
    }
    
    /**
     * Converts algebraic notation to Cartesian coordinates
     * @param algebraic
     * @return Cartesian coordinates. Example: (3,4) will be returned as "34"
     */
    private String algebraicToCartesian(String algebraic) {
        String col = String.valueOf(algebraic.charAt(0) - 'a');
        String row = String.valueOf(algebraic.charAt(1) - '1');
        
        return col+row;
    }
    
    /**
     * Creates a Position object based on Cartesian input
     * @param col
     * @param row 
     */
    public Position(int col, int row) {
        this.col = col;
        this.row = row;
        
        // calculate index
        this.index = row*8 + col;
        
        // calculate algebraic notation
        this.algebraic = cartesianToAlgebraic(col, row);
    }
    
    /**
     * Creates a Position object based on algebraic input
     * @param algebraic 
     */
    public Position(String algebraic) {
        this.algebraic = algebraic;
        
        // extract Cartesian coords
        String coords = algebraicToCartesian(algebraic);
        this.col = coords.charAt(0) - '0';
        this.row = coords.charAt(1) - '0';
        this.index = row*8 + col;
    }
    
    /**
     * Creates a Position objected based on the index in a 1-D array
     * @param index 
     */
    public Position(int index) {
        this.index = index;
        this.row = index/8;
        this.col = index - row*8;
        this.algebraic = this.cartesianToAlgebraic(col, row);
        
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setAlgebraic(String algebraic) {
        this.algebraic = algebraic;
    }
    
    /**
     * Returns a nicely formatted string for the position
     * @return a string containing algebraic notation, column, row, and index
     */
    @Override
    public String toString() {
        return String.format("Algebraic: %s, Cartesian: (%d, %d), Index: %d", algebraic, col, row, index);
    }
    
    /**
     * Get the row of this piece on the chess board
     * @return the row
     */
    public int getRow() {
        return row;
    }
    
    /**
     * Get the column of this piece on the chess board
     * @return the column
     */
    public int getCol() {
        return col;
    }
    
    /**
     * Get the algebraic notation for the position of this piece
     * @return the algebraic notation
     */
    public String getAlgebraic() {
        return algebraic;
    }
    
    /**
     * Get the index if the piece was to be mapped in 1-D array
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Position other = (Position) obj;
        if (this.getIndex() != other.getIndex()) {
            return false;
        }

        return true;
    }

    public int[] getDirection(Position other) {

        Integer colDiff = other.getCol() - this.getCol();
        Integer rowDiff = other.getRow() - this.getRow();

        // how much is column going to be incremented by - could be -1, 0, or 1
        int colIncrement = colDiff.compareTo(0);
        int rowIncrement = rowDiff.compareTo(0);

        return new int[] {colIncrement, rowIncrement};
    }
    
    /**
     * Assume other position in the same file, rank, or, diagonal
     * @param other
     * @return
     */
    public int numSquaresBetween(Position other) {
        Integer colDiff = this.getCol() - other.getCol();
        Integer rowDiff = this.getRow() - other.getRow();

        // if same rank => rowDiff must be 0
        // if same file => colDiff must be 0
        // if diagonal => |xDiff| = |yDiff|
        return Math.max(Math.abs(colDiff), Math.abs(rowDiff));
    }

    
    
}
