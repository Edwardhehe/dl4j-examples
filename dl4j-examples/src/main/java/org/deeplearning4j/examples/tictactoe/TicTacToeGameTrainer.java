package org.deeplearning4j.examples.tictactoe;

import org.datavec.api.util.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;

/**
 * <b>Developed by KIT Solutions Pvt. Ltd.</b> (www.kitsol.com) on 24-Aug-16.
 * This program is used for training.(Update the move reward based on the win or loose of the game).
 * Here both player are being played automatically and update probability in AllMoveWithReward.txt.
 * AllMoveWithReward.txt file can be any file containing TicTacToe data generated by running TicTacToeData.java or any other old file
 * updated by this program when run earlier.
 */
public class TicTacToeGameTrainer {

    public static void main(String[] args) {

        // selects file contains TicTacToe data generated by running TicTacToeData.java
        String filePath = "";
        try {
            filePath = new ClassPathResource("TicTacToe").getFile().getAbsolutePath() + File.separator + "AllMoveWithReward.txt";
        } catch (Exception e) {
            System.out.println("FilePathException" + e.toString());
        }

        // This is the object of TicTacToePlayer class which contains all methods to make move and find best move and update probability
        // of each move based on winning or losing or drawing the game.
        TicTacToePlayer player = new TicTacToePlayer();

        // need to set file path containing basic game data which will be updated during training.
        player.setFilePath(filePath);

        // thread to start loading of a file asynchronously.
        Thread aiLoad = new Thread(player);
        aiLoad.start();

        // need to set number of games after which you want to write data file with latest data.
        player.setUpdateLimit(1000);

        // This property is set to tell TicTacToePlayer to update probability in data file.
        // data file is not updated if you set this as false.
        // This property is false by default
        player.setAutoUpdate(true);

        // counter to monitor number of games played as training proceeds.
        int totalGameCounter = 0;

        // number of games that player 1 won
        int numberOfWinPlayer1 = 0;

        // number of games that player 2 won
        int numberOfWinPlayer2 = 0;

        // number of games to play during training.
        int playTotalGame = 10000;

        // number of games played as draw games.
        int draw = 0;

        // to control whether a move was of a first player or a second player to request a next board positions from TicTacToePlayer object.
        int tempMoveType = 0;

        // This is a variable to do the training for all 9 empty positions when training starts.
        int movePosition = 0;

        // sets a player number for first player
        // it can be 1 or 2, i.e. X or O.
        int firstPlayerNumber = 1; //1-For First Player and 2- for second Player

        if (firstPlayerNumber == 1) {
            tempMoveType = 2;
        } else if (firstPlayerNumber == 2) {
            tempMoveType = 1;
        }

        // Following logic uses TicTacToePlayer object to play each game and use it to update probability for
        // each won/loose or draw game.
        INDArray board;
        while (true) {
            try {
                Thread.sleep(10);
                int moveType = tempMoveType;
                // checks whether data file is fully loaded or not before moving further in training.
                if (player.isAILoad() == true) {

                    // if played games becomes greater than total number of games to play then exits the loop
                    if (totalGameCounter >= playTotalGame) {
                        break;
                    }

                    // create blank board.
                    board = Nd4j.zeros(1, 9);
                    // board.putScalar(new int[]{0,4}, firstPlayerNumber);

                    // puts X or O (1 or 2 respectively) depending on the first player number.
                    board.putScalar(new int[]{0, movePosition}, firstPlayerNumber);

                    // increases movePosition by 1 to play upto 9 positions of the board
                    movePosition++;

                    // if movePosition becomes greater than 8, then set it to 0 to restart from first position again.
                    if (movePosition > 8) {
                        movePosition = 0;
                    }

                    // increase total games played counter by 1.
                    totalGameCounter++;

                    // print board to console for logging purpose.
                    // we can comment this line if not required.
                    printBoard(board);

                    // below while loop plays actual game automatically.
                    while (true) {

                        // gets next best board move by passing current board state.
                        board = player.getNextBestMove(board, moveType);

                        // prints board.
                        printBoard(board);

                        // verifies current game decision (win or draw)
                        int gameState = player.getGameDecision();

                        // if gameState != 0, means game is finished with a decision
                        if (gameState != 0) {
                            if (gameState == 1) {           // player 1 won
                                numberOfWinPlayer1++;
                            } else if (gameState == 2) {  // player 2 won
                                numberOfWinPlayer2++;
                            } else {  // game is draw
                                draw++;
                            }
                            System.out.println("Total Game :" + String.valueOf(totalGameCounter));
                            System.out.println("   X Player:" + String.valueOf(numberOfWinPlayer1));
                            System.out.println("   O Player:" + String.valueOf(numberOfWinPlayer2));
                            System.out.println("   XXDrawOO:" + String.valueOf(draw));
                            // exit while loop as current game is finished
                            break;
                        }
                        // setting moveType to particular player to request next game board
                        if (moveType == 1) {
                            moveType = 2;
                        } else {
                            moveType = 1;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Prints the board on a console as given below
     * 0 0 1
     * 2 0 0
     * 0 0 1
     */
    public static void printBoard(INDArray board) {
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int position = (int) board.getDouble(k);
                System.out.print("  " + position);
                k++;
            }
            System.out.println("");
        }
        System.out.println("------------");
    }
}

