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
public class Bishop extends ChessPiece {

    String movementType;
    public Bishop(boolean isWhite, Position pos) {
        super(isWhite, pos);
        this.type = Type.BISHOP;
        this.movementType = "SlidingDiagonal";
    }

    @Override
    public ArrayList<Position> generateEndPositions() {

        ArrayList<Position> targetPositions = new ArrayList<>();

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