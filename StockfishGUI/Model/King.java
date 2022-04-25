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
public class King extends ChessPiece {

    public King(boolean isWhite, Position pos) {
        super(isWhite, pos);
        this.type = Type.KING;
    }

    @Override
    public ArrayList<Position> generateEndPositions() {
        // 8 total positions, 1 in each direction

        ArrayList<Position> targetPositions = new ArrayList<>();

        int currCol = pos.getCol();
        int currRow = pos.getRow();

        System.out.println(currCol + " " + currRow);
        
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                // dont want to add current position
                if (i==0 && j==0) continue;

                int checkCol = currCol+i;
                int checkRow = currRow+j;

                // check if col out of bounds
                if (0 > checkCol || 7 < checkCol) {
                    continue;
                }
                // check if row out of bounds
                if (0 > checkRow || 7 < checkRow) {
                    continue;
                }
                targetPositions.add(new Position(checkCol,checkRow));

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
