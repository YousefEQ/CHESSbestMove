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
public class Queen extends ChessPiece {

    public Queen(boolean isWhite, Position pos) {
        super(isWhite, pos);
        this.type = Type.QUEEN;
        
    }

    @Override
    public ArrayList<Position> generateEndPositions() {

        ArrayList<Position> targetPositions = new ArrayList<>();

        //=== Generate straight moves ===

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

        //=== Generate Diagonal Moves ===
        
        // 4 directions, initialize counters
        int col = pos.getCol();
        int row = pos.getRow();
        int count=0;

        // NW
        while (col != 0 && row != 7) {
            col--;
            row++;
            count++;
        }
        if (count != 0) targetPositions.add(new Position(col, row));

        // reset counters
        col = pos.getCol(); row = pos.getRow(); count=0;

        // NE
        while (col != 7 && row != 7) {
            col++;
            row++;
            count++;
        }
        if (count != 0) targetPositions.add(new Position(col, row));

        // reset counters
        col = pos.getCol(); row = pos.getRow(); count=0;


        // SW
        while (col != 0 && row != 0) {
            col--;
            row--;
            count++;
        }
        if (count != 0) targetPositions.add(new Position(col, row));

        // reset counters
        col = pos.getCol(); row = pos.getRow(); count=0;

        // SE
        while (col != 7 && row != 0) {
            col++;
            row--;
            count++;
        }
        if (count != 0) targetPositions.add(new Position(col, row));

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
