/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.*;

/**
 *
 * @author zamil
 */
public class ChessBoard {
    
    private final static String DEFAULT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private String fen; // = r3k2r/pp1b1ppp/1qnbpn2/2ppN3/3P1B2/1QPBP3/PP1N1PPP/R4RK1 w kq - 0 1
    public boolean turn; // indicates whose turn it is - white = true, black = false
    public String castling;
    public String enPassant;

    public char[] pieceMap = new char[64];
    public boolean[] threatMap = new boolean[64];
    public ArrayList<ChessPiece> pieces = new ArrayList<>();
    public ArrayList<Move> legalMoves = new ArrayList<>();
    public Position whiteKingPosition;
    public Position blackKingPosition;

    public MyStockfish sf;
    private int maxAnalysisTime;
    private int numThreads;
    
    public String[] stats;
    
    /** TODO:
     * interpret castling rights from FEN
     * interpret turn from FEN
     */
    
    /**
     * Constructor to set up the chessboard in it's starting position
     */
    public ChessBoard() {
        this.fen = DEFAULT_FEN;
        buildBoard();
        sf = new MyStockfish();

        // set defaults
        this.maxAnalysisTime = 1000;
        this.numThreads = 1;
    }
    
    /**
     * Constructor to set up the chess board with a custom FEN
     * @param fen the FEN string to set up the board with
     */
    public ChessBoard(String fen) {
        this.fen = fen;
        buildBoard();
        sf = new MyStockfish();

        // set defaults
        this.maxAnalysisTime = 1000;
        this.numThreads = 1;
    }
    
    /**
     * Sets a custom FEN for the chessboard, then updates the pieceMap[]
     * @param fen the FEN string to set up the board with
     */
    public void setFen(String fen) {
        this.fen = fen;
        buildBoard();
    }
    
    /**
     * Retrieve the FEN of the chessboard
     * @return the FEN string 
     */
    public String getFen() {
        return fen;
    }
    
    @Override
    /**
     * Prints chess board in an 8x8 layout.
     */
    public String toString() {
        
        String boardString = "";
        for (int row = 7; row >= 0; --row) {
            for (int col = 0; col < 8; ++col) {
                boardString += pieceMap[row*8 + col] + " ";
            }
            boardString += "\n";
        }
        return boardString;
    }

    public boolean isWhite(char c) {
        return (pieceMap[c] >= 'A' && pieceMap[c] <= 'Z');
    }

    public boolean isWhite(Position p) {
        return (pieceMap[p.getIndex()] >= 'A' && pieceMap[p.getIndex()] <= 'Z');
    }

    public void printThreatMap() {
        for (int row = 7; row >= 0; --row) {
            for (int col = 0; col <= 7; ++col) {
                int i = (new Position(col, row)).getIndex();
                if (threatMap[i]) {
                    System.out.print(1 + " ");
                } else {
                    System.out.print(0 + " ");
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Fills pieceMap[] based on current FEN
     */
    private void buildBoard() {
        String fenSplit[] = fen.split(" ");
        String fenBoard = fenSplit[0];
        this.turn = (fenSplit[1].equals("w"));
        this.castling = fenSplit[2];
        this.enPassant = fenSplit[3];
        
        int row = 7;
        int col = 0;
        int fen_len = fenBoard.length();
        int index;
        char c;
        
        
        for (int i = 0; i < fen_len; ++i) {
            c = fenBoard.charAt(i);
            index = row*8 + col;
            
            if (c == '/') {
                row--;
                col = 0;
            }
            
            else if (Character.isLetter(c)) {
                switch (c) {
                    case 'r', 'R' -> pieces.add(new Rook(Character.isUpperCase(c), new Position(index)));
                    case 'b', 'B' -> pieces.add(new Bishop(Character.isUpperCase(c), new Position(index)));
                    case 'n', 'N' -> pieces.add(new Knight(Character.isUpperCase(c), new Position(index)));
                    case 'q', 'Q' -> pieces.add(new Queen(Character.isUpperCase(c), new Position(index)));
                    case 'k' -> {
                        pieces.add(new King(Character.isUpperCase(c), new Position(index)));
                        this.blackKingPosition = new Position(index);
                    }
                    case 'K' -> {
                        pieces.add(new King(Character.isUpperCase(c), new Position(index)));
                        this.whiteKingPosition = new Position(index);

                    }
                    case 'p', 'P' -> pieces.add(new Pawn(Character.isUpperCase(c), new Position(index)));
                    default -> {
                    }
                }

                pieceMap[index] = c;
                col++;
            }
            
            else if (Character.isDigit(c)) {
                for (int j = 0; j < c-'0'; ++j) {
                    pieceMap[index] = '.';
                    col++;
                    index++;
                }
            }
            else {
                System.out.println("something went wrong");
            }
        }
    }

    /**
     * Converts algebraic notation to something easily understandable by a human
     * @param move 
     * @return A string in SAN notation
     */
    public String humanReadableMove(Move move) {
        String moveStr = "";
        int startIndex = move.startSquare.getIndex();
        int endIndex = move.targetSquare.getIndex();

        if (pieceMap[startIndex] == 'p' || pieceMap[startIndex] == 'P') {
            moveStr += move.startSquare.getAlgebraic().substring(0,1);
        } else {
            moveStr += String.valueOf(pieceMap[startIndex]).toUpperCase();
        }

        if (pieceMap[endIndex] != '.') {
            moveStr += 'x';
        }
        moveStr += move.targetSquare.getAlgebraic();

        return moveStr;
    }

    public boolean sameColour(char c) {
        if (c == '.') return true;
        return this.turn == ('A' <= c && c < 'Z');
    }
    
    /**
     * Updates FEN, pieceMap, and activePiece (turn) based on the move. Does not have to
     *  be a legal move, nor does it have to be a move from activePiece. Supposed
     *  to be used in Board editor mode
     * @param startSquare The starting square of the move
     * @param targetSquare  The target square of the move
     */
    public void move(Position startSquare, Position targetSquare) {
        
        pieceMap[targetSquare.getIndex()] = pieceMap[startSquare.getIndex()];
        pieceMap[startSquare.getIndex()] = '.';
        

        // create edge case for en passant capture
        if (targetSquare.getAlgebraic().equals(enPassant)) {
            int offSet = turn ? -8 : 8;
            pieceMap[targetSquare.getIndex()+offSet] = '.';
            enPassant = "-";
        }

        updateRow(startSquare.getRow());
        this.turn = !this.turn;
        updateRow(targetSquare.getRow());

        // After moving
        generateThreatMap();
        generateLegalMoves();
    }
    
    /**
     * Returns true if current turn's king is in check
     * @return A list of strings(?) containing the moves
     */
    public boolean isCheck() {
        Position kingPos = turn ? whiteKingPosition : blackKingPosition;
        return threatMap[kingPos.getIndex()];
    }

    /**
     * Generates legal moves from a given position.
     * Side effects: modifies the array "legalMoves"
     */
    public void generateLegalMoves() {

        //ArrayList<Move> moves = new ArrayList<>();
        
        pieces.forEach(piece->{
            // current piece is of the same turn
            if (this.turn == piece.isWhite()) {

                // check enpassant if current piece is pawn
                if (piece.type == Type.PAWN && !enPassant.equals("-")) {
                    
                    Position temp = new Position(enPassant);

                    int offSet = turn ? -1 : 1;

                    if (piece.pos.getRow() == temp.getRow() + offSet) {
                        if (Math.abs(piece.pos.getCol() - temp.getCol())==1) {
                            legalMoves.add(new Move(piece.pos, new Position(temp.getIndex())));
                        }
                    }
                }

                ArrayList<Position> targets = piece.generateEndPositions();

                targets.forEach(pos->{

                    char c = pieceMap[pos.getIndex()]; // piece at the index of this position

                    if (piece.type == Type.KNIGHT) {
                        // cant jump to friendly squares
                        // if target is empty or enemy piece, or if not pinned
                        // if knight is pinned then it has no moves
                        if ((c == '.' || !sameColour(c)) && !piece.isPinned){
                            legalMoves.add(new Move(piece.pos, pos));
                        }
                    }

                    else if (piece.type == Type.PAWN) {
                        // pawn pins info
                        // - vertical -> can move in the direction of attacker
                        // - diagonal -> if 1 square away, can take, otherwise no moves
                        // - horizontal -> no moves

                        // Idea to restrict movement based on pin direction. Not currently implemented

                        // 0 = Any, 1 = vertical, 2 = diagonal, 3 = none
                        int movementType = 0;
                        if (piece.isPinned) {
                            // determine direction of pin
                            int colDiff = piece.pinningPiece.getCol() - piece.pos.getCol();
                            int rowDiff = piece.pinningPiece.getRow() - piece.pos.getRow();
                            if (colDiff == 0) { // vertical
                                movementType = 1;
                            } else if (Math.abs(colDiff) == Math.abs(rowDiff) && Math.abs(colDiff) == 1) {
                                // diagonal
                                movementType = 2;
                            } else {
                                movementType = 3;
                            }
                        }

                        // target is different than current column 
                        if (pos.getCol() != piece.pos.getCol()) {
                            if (c != '.' && !sameColour(c)) {
                                legalMoves.add(new Move(piece.pos, pos));
                            }
                        }
                        // row differs by 1 
                        else if ((Math.abs(pos.getRow() - piece.pos.getRow()) == 1)) {
                            if (c == '.') {
                                legalMoves.add(new Move(piece.pos, pos));
                            }
                        }
                        // row differs by 2
                        else {
                            int offSet = piece.isWhite() ? 8 : -8;
                            int index = piece.pos.getIndex() + offSet;
                            // check if square directly above/below and 2 squares directly/below aren't blocked
                            if (pieceMap[index] == '.') {
                                if (pieceMap[index+offSet] == '.') {
                                    legalMoves.add(new Move(piece.pos, pos));
                                }
                            }
                        }

                    }
                    
                    if (piece.type == Type.KING) {
                        // only move king to a location if location is safe
                        if (!threatMap[pos.getIndex()]) {
                            legalMoves.add(new Move(piece.pos, pos));
                        }
                    }

                    // sliding pieces
                    else {
                        // sliding pins info
                        // - vertical -> can move in the direction of attacker if possible
                        // - diagonal -> can move in direction of attacker if possible
                        // - horizontal -> can move in direction of attacker if possible

                        // Idea to restrict movement based on pin direction. Not currently implemented

                        // 0 = Any, 1 = vertical, 2 = horizontal, 3 = diagonal
                        int movementType = 0;
                        if (piece.isPinned) {
                            // determine direction of pin
                            int colDiff = piece.pinningPiece.getCol() - piece.pos.getCol();
                            int rowDiff = piece.pinningPiece.getRow() - piece.pos.getRow();
                            if (colDiff == 0) { // vertical
                                movementType = 1;
                            } else if (rowDiff == 0) { // horizontal
                                movementType = 2;
                            } else { // diagonal
                                movementType = 3;
                            }
                        }

                        // if current endpoint matches direction...
                        int[] direction = pos.getDirection(piece.pos);
                        // if (Math.abs(direction[0]) == 1 && direction[1]==0 && (movementType == 0 || movementType == 1)
                        //     || Math.abs(direction[0])==0 && Math.abs(direction[1])==1 && (movementType == 0 || movementType == 2)
                        //     || Math.abs(direction[0])==Math.abs(direction[1]) && (movementType == 0 || movementType == 3)) {
                        
                            int numSquares = pos.numSquaresBetween(piece.pos);

                            Position nearestPiece = checkPiecesInBetween(piece.pos, pos, direction[0], direction[1], numSquares);

                            int col,row;
                            Position tempPos;

                            if (nearestPiece.getCol() == -1) { // no pieces
                                // all squares between piece.pos and pos should be added
                                col = pos.getCol();
                                row = pos.getRow();
                                
                                for (int i = 0; i < numSquares; ++i) {
                                    tempPos = new Position(col,row);
                                    legalMoves.add(new Move(piece.pos, tempPos));
                                    col += direction[0];
                                    row += direction[1];
                                }
                            }

                            else { // at least 1 piece
                                // start from location of nearest piece
                                col = nearestPiece.getCol();
                                row = nearestPiece.getRow();
                                tempPos = new Position(col,row);

                                // if nearest piece is friendly
                                if (sameColour(pieceMap[tempPos.getIndex()])) {
                                    col += direction[0];
                                    row += direction[1];
                                }

                                int d = nearestPiece.numSquaresBetween(piece.pos);

                                for (int i = 0; i < d; ++i) {
                                    tempPos = new Position(col,row);
                                    legalMoves.add(new Move(piece.pos, tempPos));
                                    col += direction[0];
                                    row += direction[1];
                                }
                            }
                        //}
                    }
                });
            }
        });
    }

    /**
     * Helper function for generateThreatMap and generateLegalMoves. p1 and p2 must be in the same rank, file, or diagonal
     * Side effects: can set certain pieces as pinned pieces
     * @param p1 position of current piece
     * @param p2 position of a square on the edge of the board. Must be distinct from p1
     * @return position of piece closest to current piece
     */
    public Position checkPiecesInBetween(Position p1, Position p2, int colDir, int rowDir, int numSquares) {

        // start looking from p2 in the direction of p1

        // flags
        boolean potentialPin = false;
        int numPieces = 0;

        // counter
        int col = p2.getCol();
        int row = p2.getRow();

        // temp info
        Position tempPos;
        Position lastPiecePos = new Position(-1,-1); // default: out of bounds
        char tempPiece;

        for (int i = 0; i < numSquares; ++i) {

            tempPos = new Position(col,row);
            tempPiece = pieceMap[tempPos.getIndex()];

            // if square is not empty
            if (tempPiece != '.') {

                //if piece is same colour as current turn
                if (sameColour(tempPiece)) {
                    numPieces++;

                    // if piece is current turn's king
                    if(String.valueOf(tempPiece).toUpperCase().equals("K")) {
                        // if piece on p1 is not of current turn => piece is pinned
                        if (isWhite(p1) != this.turn) potentialPin = true;

                        // note that any "checks" will be determined based on threatMap later
                    }

                    // any other friendly piece
                    else {
                        lastPiecePos = tempPos;
                    }
                }

                // piece is opposite colour
                else {
                    lastPiecePos = tempPos;
                }
            }

            // if square is empty, do nothing
            else {
                ;
            }

            col += colDir;
            row += rowDir;
        }

        if (potentialPin && numPieces == 2) {
            // lastPiecePos needs to be set as pinned
            for (int i = 0; i < pieces.size(); ++i) {
                ChessPiece p = pieces.get(i);
                if (p.pos.equals(lastPiecePos)) {
                    p.isPinned = true;
                    p.pinningPiece = p1;
                }
            }
        }

        // return location of last tempPiece
        return lastPiecePos;
    }

    public void printPieces() {
        pieces.forEach(p->System.out.println(p.toString()));
    }

    /**
     * Creates a map of squares that our king can absolutely not go to
     */
    public void generateThreatMap() {
        // need to generate threats from opposite coloured pieces

        // for each piece...
        pieces.forEach(piece->{

            // piece is same colour =? skip
            if (piece.isWhite() == this.turn) {
                ;
            }

            // piece is opposite colour => create threat map
            else {
                ArrayList<Position> attacked = piece.generateEndPositions();
                attacked.forEach(pos->{

                    // counters and temp variables
                    int col, row; // these represent starting col,row from where we start checking
                    Position tempPos; // a variable to temporarily hold/create a position
                    
                    if (piece.type == Type.KNIGHT) {
                        // knight can jump over pieces
                        // if square is unoccupied => threat
                        // if square has friendly piece => can't go there anyway (still threatened)
                        // if square hss enemy piece => defended by knight (threatened)
                        // therefore all squares attacked by knight are threatened
                        threatMap[pos.getIndex()] = true;
                    }

                    else if (piece.type == Type.PAWN) {
                        // if landing square has same column as starting square, ignore
                        // only immediate "upper" diagonal squares are threatened

                        // if columns are different
                        if (pos.getCol() != piece.pos.getCol()) {
                            threatMap[pos.getIndex()] = true;
                        }
                    }

                    // Sliding pieces - // If there is a piece between end position and current position
                    else {
                        int[] direction = pos.getDirection(piece.pos);
                        int numSquares = pos.numSquaresBetween(piece.pos);

                        Position nearestPiece = checkPiecesInBetween(piece.pos, pos, direction[0], direction[1], numSquares);

                        if (nearestPiece.getCol() == -1) { // no pieces
                            // all squares between piece.pos and pos should be marked as threat
                            col = pos.getCol();
                            row = pos.getRow();
                            
                            for (int i = 0; i < numSquares; ++i) {
                                tempPos = new Position(col,row);
                                threatMap[tempPos.getIndex()] = true;
                                col += direction[0];
                                row += direction[1];
                            }
                        }

                        else { // at least 1 piece
                            // start from location of nearest piece
                            col = nearestPiece.getCol();
                            row = nearestPiece.getRow();
                            int d = nearestPiece.numSquaresBetween(piece.pos);

                            for (int i = 0; i < d; ++i) {
                                tempPos = new Position(col,row);
                                int index = tempPos.getIndex();
                                threatMap[index] = true;
                                col += direction[0];
                                row += direction[1];
                            }
                        }
                    }

                });

            }
        });
    }
    
    /**
     * 
     * @param row 
     */
    private void updateRow(int row) {
                
        String newRow = "";
        char c;
        int emptySquareCount = 0;
        
        for (int i = 0; i < 8; ++i) {
            c = pieceMap[row*8 + i];
            
            // If square is empty
            if (c == '.') {
                emptySquareCount++;
            }
            
            // If square has a piece
            else {
                if (emptySquareCount > 0) {
                    newRow += emptySquareCount;
                }
                emptySquareCount = 0;
                newRow += c;
            }
        }
        if (emptySquareCount > 0) {
            newRow += emptySquareCount;
        }
        
        // update FEN - probably a better way to do this. This seems inefficient
        
        String[] fenArray = this.fen.split(" "); // split FEN into [board, turn, castling, en passant, halfmove clock, fullmove number)
        String[] fenArrayBoard = fenArray[0].split("/"); // further split boardFEN into rows
        
        //System.out.println(newRow);
        
        // replace the old row with the new row, then join the boardFEN
        fenArrayBoard[7-row] = newRow;
        List<String> fenBoardList = Arrays.asList(fenArrayBoard);
        String fenBoardString = String.join("/", fenBoardList);
        
        // replace the old boardFEN with new boardFEN and then join all FEN strings
        fenArray[0] = fenBoardString;
        fenArray[1] = turn ? "w" : "b";
        List<String> fenList = Arrays.asList(fenArray);
        this.fen = String.join(" ", fenList);
    }

    /*
    public String getContiniousStats() {
        sf.getOutput(0);
        return
    } */
    
    public void setMaxAnalysisTime(int n) {
        this.maxAnalysisTime = n;
    }
    
    public void setNumThreads(int n) {
        this.numThreads = n;
    }
    
    public void getStats() {
        sf.getOutput(0);
        sf.analyzeBoard(fen, maxAnalysisTime, numThreads);
        //sf.printAnalysis();
        String bestMove = humanReadableMove(new Move(sf.getBestMove()));

        String eval = sf.getEvalScore();
        float numEval = Float.parseFloat(eval);
        numEval = this.turn ? numEval : -1*numEval;
        eval = Float.toString(numEval);

        String bestLine = sf.getBestLine();
        this.stats = new String[] {bestMove, eval, bestLine};
    }
    
    public String getBestMove() {
        return stats[0];
    }
    
    public String getEval() {
        return stats[1];
    }
    
    public void startEngine() {
        sf.startEngine();
    }

    public void stopEngine() {
        sf.stopEngine();
    }
}