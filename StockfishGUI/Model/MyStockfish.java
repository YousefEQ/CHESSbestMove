/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Runtime;
/**
 *
 * @author zamil
 */
public class MyStockfish {

    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    
    String analysisResults = "";
    String[] dump;

    private static final String PATH = "engine\\stockfish_14_win_x64_bmi2\\stockfish";
    
    /**
     * Starts Stockfish engine as a process and initializes it
     * 
     * @param None
     * @return True on success. False otherwise
     */
    public boolean startEngine() {
        try {
            engineProcess = Runtime.getRuntime().exec(PATH);
            processReader = new BufferedReader(new InputStreamReader(
                            engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(
                            engineProcess.getOutputStream());
            
            sendCommand("uci");
        } catch (Exception e) {
                return false;
        }
        return true;
    }
    
    /**
     * Takes in any valid UCI command and executes it
     * 
     * @param command
     */
    public void sendCommand(String command) {
        try {
                processWriter.write(command + "\n");
                processWriter.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    /**
     * This is generally called right after 'sendCommand' for getting the raw
     * output from Stockfish
     * 
     * @param waitTime
     *            Time in milliseconds for which the function waits before
     *            reading the output. Useful when a long running command is
     *            executed
     * @return Raw output from Stockfish
     */
    public String getOutput(int waitTime) {
        StringBuilder buffer = new StringBuilder();
        try {
            Thread.sleep(waitTime);
            sendCommand("stop");
            Thread.sleep(20);
            sendCommand("isready");
            Thread.sleep(20);
            String text;
            while (true) {
                text = processReader.readLine();
                if (text.equals("readyok")) {
                    break;
                }
                else
                    buffer.append(text).append("\n");
            }  
        } catch (IOException | InterruptedException e) {
        }
        return buffer.toString();
    }
    
    public void analyzeBoard(String fen, int waitTime, int numThreads) {
        numThreads = Math.min(numThreads, Runtime.getRuntime().availableProcessors());
        sendCommand("setoption name Threads value " + Integer.toString(numThreads));
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);
        this.analysisResults = getOutput(waitTime);
        dump = analysisResults.split("\n");
    }
    
    public void printAnalysis() {
        System.out.println(this.analysisResults);
    }
    
    public String getBestMove() {
        String bestMove = "";
        int i = dump.length - 1;
        if (dump[i].startsWith("bestmove ")) {
            bestMove = analysisResults.split("bestmove ")[1].split(" ")[0];
        }
        else {
            bestMove = dump[i].split(" pv ")[1].substring(0,4);
        }
        return bestMove;
    }
    
    public String getEvalScore() {
        float evalScore = 0.0f;
        String eval = "0.0";
        for (int i = dump.length - 1; i >= 0; i--) {
            //System.out.println(dump[i]);
            if (dump[i].startsWith("info depth ")) {
                String[] evalType = dump[i].split("score ")[1].split(" nodes")[0].split(" ");
                if (evalType[0].equals("cp")) {
                    evalScore = Float.parseFloat(evalType[1]);
                    evalScore/=100;
                    eval = String.valueOf(evalScore);
                }
                else {
                    eval = "M" + evalType[1];
                }
                break;
            } 
            
        }
        
        return eval;
    }

    public String getBestLine() {
        String bestLine = "";
        int i = dump.length-1;
        if (!dump[dump.length-1].startsWith("info depth")) i--;

        for (; i >= 0; i--) {

            if ((dump[i].split("upperbound nodes")).length > 1 || (dump[i].split("lowerbound nodes")).length > 1
                    || (dump[i].split("currmove")).length > 1) {
                continue;
            } else {
                System.out.println(dump[i]);
                bestLine = (dump[i].split(" pv "))[1];
                break;
            }
            
        }
        return bestLine;
    }
    
    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException e) {}
    }
    
}