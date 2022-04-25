/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author zamil
 */
public abstract class ChessPiece {
    
    public boolean isWhite; // true or false
    Position pos;
    Type type;
    public boolean isPinned;
    Position pinningPiece;

    public ChessPiece(boolean isWhite, Position pos) {
        this.isWhite = isWhite;
        this.pos = pos;
        this.isPinned = false;
        pinningPiece = null;
    }
    /**
     * A function that indicates the colour of this piece
     * @return true if white, false is black
     */
    public boolean isWhite() {
        return isWhite;
    }
    
    public void setPin(boolean b) {
        this.isPinned = b;
    }

    /**
     * 
     * @returns the type of the Piece (Rook, Knight, Bishop, Queen, King, Pawn) 
     */
    public Type getType() {
        return this.type;
    };
    
    /**
     * Creates an array of all positions that the piece can move if the board
     * was empty.
     * @return the array of possible moves
     */
    public abstract ArrayList<Position> generateEndPositions();
    
    /**
     * Generate moves between current position and "other" position
     * @param other
     * @return
     */
    /*
    public abstract ArrayList<Position> generateMoves(Position pos); */

    @Override
    public String toString() {
        return "ChessPiece [isPinned=" + isPinned + ", isWhite=" + isWhite + ", pinningPiece=" + pinningPiece + ", pos="
                + pos + ", type=" + type + "]";
    }
    
    
    
}