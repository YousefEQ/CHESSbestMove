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
public class Knight extends ChessPiece {

    public Knight(boolean isWhite, Position pos) {
        super(isWhite, pos);
        this.type = Type.KNIGHT;
    }
    
    @Override
    public ArrayList<Position> generateEndPositions() {
        ArrayList<Position> targetPositions = new ArrayList<>();

        int currCol = pos.getCol();
        int currRow = pos.getRow();

        // 8 directions (2,1),(1,2),(-1,2),(-2,1),(-2,-1),(-1,-2),(1,-2),(2,-1)
        int[] directions = {-2,-1,1,2};
        int xDir, yDir;
        int checkCol, checkRow;
        for (int i = 0; i < 4; ++i) {
            xDir = directions[i];
            for (int j = 0; j < 4; ++j) {
                yDir = directions[j];
                
                if (xDir == yDir || Math.abs(xDir) == Math.abs(yDir)) continue;
                checkCol = currCol + xDir;
                checkRow = currRow + yDir;

                if (0 > checkCol || checkCol > 7) continue;
                if (0 > checkRow || checkRow > 7) continue;

                targetPositions.add(new Position(checkCol, checkRow));

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
