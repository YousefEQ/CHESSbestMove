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
public class Pawn extends ChessPiece {

    public Pawn(boolean isWhite, Position pos) {
        super(isWhite, pos);
        this.type = Type.PAWN;
    }

    @Override
    /**
     * Queening, enPassant, and captures must be handled by ChessBoard
     * First 2 indices represent attacking squares for pawn
     */
    public ArrayList<Position> generateEndPositions() {
        ArrayList<Position> targetPositions = new ArrayList<>();

        int currCol = pos.getCol();
        int currRow = pos.getRow();

        // Check for colour
        if (this.isWhite()) {
            // Add diagonal captures
            targetPositions.add(new Position(currCol-1, currRow+1));
            targetPositions.add(new Position(currCol+1, currRow+1));

            // Add single move forward
            targetPositions.add(new Position(currCol, currRow+1));

            // Double moves
            // check for rank 2 => row 1
            if (currRow == 1) {
                targetPositions.add(new Position(currCol, currRow+2));
            }
            
        } else {
            // Add diagonal captures
            targetPositions.add(new Position(currCol-1, currRow-1));
            targetPositions.add(new Position(currCol+1, currRow-1));

            // Add single move forward
            targetPositions.add(new Position(currCol, currRow-1));

            // Double moves
            // check for rank 7 => row 6
            if (currRow == 6) {
                targetPositions.add(new Position(currCol, currRow-2));
            }
        }
        return targetPositions;
    }

    @Override
    public Type getType() {
        return super.getType();
    }

    @Override
    public boolean isWhite() {
        return super.isWhite();
    }
    
    
    
}
