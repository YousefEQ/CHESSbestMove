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
public class Rook extends ChessPiece {
    
    String movementType;

    public Rook(boolean isWhite, Position pos) {
        super(isWhite, pos);
        this.type = Type.ROOK;
        this.movementType = "SlidingStraight";
    }
    
    @Override
    public ArrayList<Position> generateEndPositions() {
        // Rooks can make in straight lines
        ArrayList<Position> targetPositions = new ArrayList<>();

        int currCol = pos.getCol();
        int currRow = pos.getRow();
        // Extreme Left
        if (currCol != 0) targetPositions.add(new Position(0, currRow));

        // Extreme Right
        if (currCol != 7) targetPositions.add(new Position(7, currRow));

        // Top
        if (currRow != 0) targetPositions.add(new Position(currCol, 0));

        // Bottom
        if (currRow != 7) targetPositions.add(new Position(currCol, 7));

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
