/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import View.ChessBoardGUI;
import Model.ChessBoard;
import Model.Move;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;
import java.util.Scanner;
/**
 *
 * @author zamil
 */
public class ChessBoardController {
    
    //static String fen;
    ChessBoardGUI gui; // view
    ChessBoard cb; // model
    
    boolean BoardEditMode = true; // If true, pieces can be dragged and dropped anywhere
    
    public ChessBoardController(ChessBoardGUI gui, ChessBoard cb) {
        this.gui = gui; // GUI: The View
        this.cb = cb; // ChessBoard: The Model
    
        // Add the FENListener class as an ActionListener for the
        //  fenInput text field
        this.gui.addFENListener(new FENListener());
        this.gui.addMoveTimeListener(new MoveTimeListener());
        this.gui.addThreadListener(new ThreadListener());
        this.gui.addEvalButtonListener(new EvalButtonListener());

        cb.startEngine();
    }
    

    public void Move(Move move) {
        cb.move(move.getStartSquare(), move.getTargetSquare());
        gui.repaint();

    }
    
    /**
     * Action listener for the fenInput JTexrField. Builds the pieceMap[] for
     * ChessBoard when invoked.
     */
    class FENListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            String fenInput = gui.getFEN();
            
            // Create a SwingWorker thread to build the pieceMap in the backghround
            //  and not pause the main program
            class InterpretFEN extends SwingWorker<Void, Object> {
                @Override
                protected Void doInBackground() throws Exception {
                    cb.setFen(fenInput);
                    return null;
                }          
            }
            
            // Run the thread
            (new InterpretFEN()).execute();
            
            gui.updatePieceMap(cb.pieceMap);
            gui.update();
        } 
    }

    class ThreadListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String numThreads = gui.getThreads();

            class InterpretThreads extends SwingWorker<Void, Object> {
                @Override
                protected Void doInBackground() throws Exception {
                    cb.setNumThreads(Integer.parseInt(numThreads));
                    return null;
                }
            }
            // Run the thread
            (new InterpretThreads()).execute();
        }
        
    }

    class MoveTimeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String moveTime = gui.getMoveTime();

            class InterpretMoveTime extends SwingWorker<Void, Object> {
                @Override
                protected Void doInBackground() throws Exception {
                    cb.setMaxAnalysisTime(Math.round(Float.parseFloat(moveTime)*1000));
                    return null;
                }
            }

            // Run the thread
            (new InterpretMoveTime()).execute();
        }
        
    }

    class EvalButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            class EvaluatePosition extends SwingWorker<Void, Object> {
                @Override
                protected Void doInBackground() throws Exception {
                    cb.getStats();
                    return null;
                }

                @Override
                protected void done() {
                    gui.updateBestMove(cb.stats[0]);
                    gui.updateNumericalEvaluation(cb.stats[1]);
                    gui.updateBestLine(cb.stats[2]);
                    gui.update();
                }
            }

            // Run the thread
            (new EvaluatePosition()).execute();
        }
        
    }


    
    // incomplete
    class MoveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. convert drag and drop coordinates to chessboard coordinates
            
            // 2. If BoardEditMode == true, move the piece in the GUI and update
            //      the FEN.
            
            // 3. If boardEditMode == false, ask the ChessBoard class
            //      if the move is legal in another SwingWorker thread.
            
            // 4. If the move is not legal, then put the piece back to where it was.
            
            // 5. If the move is legal, then move the piece in the GUI and update
            //      the FEN.

        }
        
    }
    
    // Main function needs to be moved to a top level class. Havent created it yet
    public static void main(String args[]) {

        ChessBoard cb = new ChessBoard();
        ChessBoardGUI gui = new ChessBoardGUI(cb.pieceMap);
        ChessBoardController controller = new ChessBoardController(gui, cb);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            //String fen = "1r1r4/5p2/pq1pkP1Q/4p3/6b1/1P6/P3N1PP/3R1R1K w - - 0 1"; // Winning
            //String fen = "8/2k5/1q6/8/8/8/8/K7 b - - 0 1"; // Mate 5

            controller.gui.setVisible(true);
            
            /*
            System.out.println("==== Stockfish GUI ====");
            ChessBoard cb = new ChessBoard();
            
            System.out.println(cb.toString());

            Scanner scanner = new Scanner(System.in);
            String command = "";
            System.out.println("Enter command:");
            boolean engineStarted = false;
            
            while (scanner.hasNextLine()) {
                command = scanner.nextLine();
                if (command.startsWith("set fen ")) {
                    String fen = (command.split("fen "))[1];
                    cb.setFen(fen);
                    System.out.println(cb.toString());
                }
                else if (command.startsWith("make move ")) {
                    String moveStr = (command.split("move "))[1];
                    Move m = new Move(moveStr);
                    cb.move(m.getStartSquare(), m.getTargetSquare());
                }
                else if (command.equals("get fen")) {
                    System.out.println(cb.getFen());
                }
                else if (command.equals("start engine")) {
                    if (engineStarted) {
                        System.out.println("Engine already running");
                    } else {
                        cb.startEngine();
                        System.out.println("Engine is ready");
                        engineStarted = true;
                    }
                }
                else if (command.startsWith("set time ")) {
                    if (!engineStarted) {
                        System.out.println("Engine not running");
                    }
                    String strTime = (command.split("time "))[1];
                    int time = Integer.parseInt(strTime);
                    cb.setMaxAnalysisTime(5000);
                    System.out.println("Analysis time set to " + time + "ms");
                }
                else if (command.startsWith("set threads ")) {
                    if (!engineStarted) {
                        System.out.println("Engine not running");
                    }
                    String strThreads = (command.split("threads "))[1];
                    int threads = Integer.parseInt(strThreads);
                    cb.setNumThreads(threads);
                    System.out.println("Number of allocated threads set to " + threads);
                }
                else if (command.equals("eval")) {
                    if (!engineStarted) {
                        System.out.println("Engine not running");
                    }
                    cb.getStats();
                    String bestMove = cb.stats[0];
                    String eval = cb.stats[1];
                    System.out.println(String.format("Best Move: %s, Evaluation: %s", bestMove, eval));
                }
                else if (command.equals("bestLine")) {
                    String bestLine = cb.stats[2];
                    System.out.println(bestLine);
                }

                else if (command.equals("legalMoves")) {
                    cb.generateLegalMoves();
                    (cb.legalMoves).forEach(move -> System.out.print(move + " "));
                    System.out.println();
                }
                else if (command.equals("exit")) {
                    System.out.println("Exiting program");
                    break;
                }
                else {
                    System.out.println("Not recognized");
                }

            }
            */
        });
    }
 
    
}
