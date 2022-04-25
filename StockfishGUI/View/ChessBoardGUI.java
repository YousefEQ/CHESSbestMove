/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Move;
import Model.Position;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.BorderUIResource;

/**
 *
 * @author zamil
 */
public class ChessBoardGUI extends JFrame {

    char[] pieceMap;
    final int DIM = 512; // Dmensiton of the JFrame Window
    
    // input fields
    JTextField fenInput;
    JTextField numThreads;
    JTextField moveTime; 

    // output fields
    JLabel stockfishEval;
    JLabel bestMove;
    JTextArea bestLine;
    
    // interact
    JButton evalButton;

    /**
     * Constructor
     * @param pieceMap an array of characters containing the pieces in the proper indices 
     */
    public ChessBoardGUI(char[] pieceMap) {
        this.pieceMap = pieceMap;

        /*
        final Image[] imgs = getPieceSprites();
        if (imgs == null) {}
 
        //initComponents();
        this.setSize(DIM+16, DIM+39);
        JPanel pn = createChessboard(imgs);
        fenInput = new JTextField();
        
        this.add(pn);
        //JLabel fen = new JLabel("FEN:");
        //JTextField fenIn = new JTextField(100);
        //fen.setLabelFor(fenIn); */

        initializeFrame();
 
    }

    private void initializeFrame() {
        this.setTitle("Stockfish GUI");
        this.setBounds(10,10,1200,650);
        this.setUndecorated(false);
        this.setLayout(new GridLayout());

        this.add(gameSide());
        this.add(informationSide());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel informationSide() {
        JPanel pn = new JPanel();
        pn.setLayout(new BoxLayout(pn, BoxLayout.PAGE_AXIS));

        pn.add(createStockfishEvalPanel());
        pn.add(createFenPanel());
        pn.add(createSuggestedLinePanel());

        return pn;
    }

    private JPanel createSuggestedLinePanel() {
        JPanel pn = new JPanel();
        //pn.setLayout(new BoxLayout(pn, BoxLayout.PAGE_AXIS));
        //pn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bestLineLabel = new JLabel("Suggested Line: ");
        bestLineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pn.add(bestLineLabel);
        
        bestLine = new JTextArea(2,50);
        bestLine.setEditable(false);
        bestLine.setLineWrap(true);
        bestLine.setWrapStyleWord(true);

        pn.add(bestLine);

        return pn;
    }

    private JPanel createStockfishEvalPanel() {
        Font font = new Font("Serif", Font.BOLD, 30);

        JPanel stockfishEvalPanel = new JPanel();

        stockfishEval = new JLabel("0.0");
        stockfishEval.setFont(font);

        bestMove = new JLabel("Best Move: ");
        bestMove.setFont(font);

        evalButton = new JButton("Evaluate");

        stockfishEvalPanel.add(stockfishEval);
        stockfishEvalPanel.add(bestMove);
        stockfishEvalPanel.add(evalButton);

        return stockfishEvalPanel;
    }

    private JPanel createFenPanel() {
        JPanel fenPanel = new JPanel();

        JLabel fenInputLabel = new JLabel("FEN: ");
        fenInput = new JTextField(50);

        fenPanel.add(fenInputLabel);
        fenPanel.add(fenInput);

        return fenPanel;
    }

    // private Component createSuggestedLinePanel() {
    //     return null;
    // }

    private JPanel gameSide() {
        JPanel pn = new JPanel(new BorderLayout());
        Image[] imgs = getPieceSprites();
        pn.add(createTopPanel(), BorderLayout.PAGE_START);
        pn.add(createChessboard(imgs), BorderLayout.CENTER);
        return pn;
    }
    
    private JPanel createTopPanel() {
        JPanel pn = new JPanel();
        
        JLabel numThreadsLabel = new JLabel("Thread Count: ");
        numThreads = new JTextField("1"); 

        JLabel moveTimeLabel = new JLabel("Move Time (s): ");
        moveTime = new JTextField("1");

        pn.add(numThreadsLabel);
        pn.add(numThreads);
        pn.add(moveTimeLabel);
        pn.add(moveTime);

        return pn;
    }

    /**
     * Creates the Chessboard
     * @return JPanel object that contains the chess board
     */
    private JPanel createChessboard(Image[] imgs) {
        
        return new JPanel(){
            @Override
            public void paint (Graphics g){
                boolean white = true;
                for(int y = 0;y < 8;y++){
                    for(int x = 0;x < 8;x++){
                        if(white){
                            g.setColor(new Color(235,235, 208));
                        }else{
                            g.setColor(new Color(119, 148, 85));
                        }
                        g.fillRect(x*64, y*64, 64, 64);
                        white=!white;
                    }
                    white=!white;
                }
                for (int i = 0; i < 64; ++i) {
                    char p = pieceMap[i];
                    int ind = -1;
                    
                    switch(p) {
                        case 'K', 'k' -> ind = 0;
                        case 'Q', 'q' -> ind = 1;           
                        case 'B', 'b' -> ind = 2;
                        case 'N', 'n' -> ind = 3;
                        case 'R', 'r' -> ind = 4;
                        case 'P', 'p' -> ind = 5;
                        default -> {
                        }
                    }
                    
                    if(Character.isLowerCase(p)){
                        ind += 6;
                    }
                    if (ind > -1) {
                        Position pos = new Position(i);
                        g.drawImage(imgs[ind], pos.getCol()*64, (7-pos.getRow())*64, this);
                    }
                    
                } 
            }
        };
    }
    
    public void addFENListener(ActionListener listenForFEN) {
        fenInput.addActionListener(listenForFEN);
    }
    public void addThreadListener(ActionListener listenForThread) {
        numThreads.addActionListener(listenForThread);
    }
    public void addMoveTimeListener(ActionListener listenForTime) {
        moveTime.addActionListener(listenForTime);
    }
    public void addEvalButtonListener(ActionListener listenForEval) {
        evalButton.addActionListener(listenForEval);
    }
    
    /**
     * Gets the FEN Input from JTextField fenInput. Called by the actionListener
     * @return String containing the FEN
     */
    public String getFEN() {
        return fenInput.getText();
    }
    public String getThreads() {
        return numThreads.getText();
    }
    public String getMoveTime() {
        return moveTime.getText();
    }
    
    /**
     * Gets the move when piece is dragged and dropped. This is an example function for now.
     * @return 
     */
    public Move getMove() {
        
        return new Move("e2", "e4");
    }
    
    /**
     * 
     * @return 
     */
    private Image[] getPieceSprites() {
        BufferedImage all;
        try {
            all = ImageIO.read(new File("Sprites\\Pieces\\Pieces.png"));
            Image imgs[] = new Image[12];
            int ind = 0;
            for (int y = 0; y < 400; y+=200) {
                for (int x = 0; x < 1200; x+=200) {
                    imgs[ind] = all.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                    ind++;
                }
            }
            return imgs;
        } catch (IOException ex) {
            System.out.println("Images could not be properly imported");
            System.exit(0);
            return null;
        }
    }
    
    public void update() {
        this.repaint();
    }
    
    // this function currenty serves no purpose because there is no mechanism to pull data
    //  from the Model
    public void updatePieceMap(char[] pieceMap) {
        this.pieceMap = pieceMap;
    }

    public void updateNumericalEvaluation(String evaluation) {
        stockfishEval.setText(evaluation);
    }

    public void updateBestMove(String move) {
        bestMove.setText("Best Move: " + move);
    }

    public void updateBestLine(String line) {
        bestLine.setText(line);
    }
    
    // In reality getMove() will pull data from an action listener, which will be in ChessBoardController
    /**
     * Returns the move made by the user while dragging and dropping the mouse to a location.
     * @return Move object
     */
    /*
    public Move getMove() {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter move: ");
        
        String moveStr = s.nextLine();
        
        Move m = new Move(moveStr.substring(0, 2), moveStr.substring(2));
  
        return m;
        
    } */
    
    // texture atlas
    
}
