package com.comp2042.controllers;

import com.comp2042.events.InputEventListener;
import com.comp2042.data.ClearRow;
import com.comp2042.data.DownData;
import com.comp2042.data.HighScore;
import com.comp2042.data.ViewData;
import com.comp2042.events.EventSource;
import com.comp2042.events.GameMode;
import com.comp2042.events.MoveEvent;
import com.comp2042.model.Board;
import com.comp2042.model.SimpleBoard;

import static com.comp2042.constant.GameConstants.BOARD_HEIGHT;
import static com.comp2042.constant.GameConstants.BOARD_WIDTH;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(BOARD_WIDTH, BOARD_HEIGHT);
    private SimpleBoard simpleBoard;

    private final GuiController viewGuiController;
    private final HighScore highScore;

    // Track lock delay
    private boolean lockDelayActive = false;

    public GameController(GuiController c, GameMode mode) {
        viewGuiController = c;
        highScore = HighScore.getInstance();

        if (board instanceof SimpleBoard) {
            simpleBoard = (SimpleBoard) board;
            simpleBoard.setGameMode(mode);
        }

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());

        // Display current high scores
        if (mode == GameMode.ZEN) {
            viewGuiController.displayHighScores(mode, highScore);
        }
    }

    public SimpleBoard getSimpleBoard() {
        return simpleBoard;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            if (!lockDelayActive) {
                // First time hitting bottom: Activate delay and do NOT merge yet
                lockDelayActive = true;
                return new DownData(null, board.getViewData());
            }

            // If we are here, lockDelayActive is true (2nd tick at bottom), so we lock.
            lockDelayActive = false; // Reset flag
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }

            // Check game mode completion
            if (simpleBoard != null) {
                checkGameModeCompletion();
            }

            if (board.createNewBrick()) {
                handleGameOver();
            }
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }

        // Check time limit for BLITZ
        if (simpleBoard != null && simpleBoard.getGameMode() == GameMode.BLITZ) {
            long startTime = simpleBoard.getGameStartTime();
            if (startTime > 0) {  // Only check if game has actually started
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= 180000) { // 3 minutes
                    endBlitzMode();
                }
            }
        }

        return new DownData(clearRow, board.getViewData());
    }

    private void checkGameModeCompletion() {
        if (simpleBoard == null) return;

        switch (simpleBoard.getGameMode()) {
            case SPRINT:
                if (simpleBoard.getLinesCleared() >= 40) {
                    endSprintMode();
                }
                break;
            case PITFALL:
                // Pitfall ends on game over, check high scores then
                break;
        }
    }

    private void endSprintMode() {
        long completionTime = System.currentTimeMillis() - simpleBoard.getGameStartTime();
        simpleBoard.setCompletionTime(completionTime);

        boolean isNewRecord = highScore.isSprintNewRecord(completionTime);
        if (isNewRecord) {
            highScore.setSprintBestTime(completionTime);
        }

        viewGuiController.showSprintComplete(completionTime, isNewRecord, highScore);
    }

    private void endBlitzMode() {
        int finalScore = board.getScore().scoreProperty().get();

        boolean isNewRecord = highScore.isBlitzNewRecord(finalScore);
        if (isNewRecord) {
            highScore.setBlitzHighScore(finalScore);
        }

        viewGuiController.showBlitzComplete(finalScore, isNewRecord, highScore);
    }

    private void handleGameOver() {
        // Check for Pitfall high scores on game over
        if (simpleBoard != null && simpleBoard.getGameMode() == GameMode.PITFALL) {
            int finalScore = simpleBoard.getScore().scoreProperty().get();
            int finalLevel = simpleBoard.getCurrentLevel();

            boolean newLevelRecord = highScore.isPitfallNewLevelRecord(finalLevel);
            boolean newScoreRecord = highScore.isPitfallNewScoreRecord(finalScore);

            if (newLevelRecord) {
                highScore.setPitfallHighLevel(finalLevel);
            }
            if (newScoreRecord) {
                highScore.setPitfallHighScore(finalScore);
            }

            viewGuiController.showPitfallGameOver(finalLevel, finalScore,
                    newLevelRecord || newScoreRecord, highScore);
        } else {
            viewGuiController.gameOver();
        }
    }

    @Override
    public DownData onDropEvent(MoveEvent event) {
        lockDelayActive = false;
        ClearRow clearRow = null;
        // Count the rows dropped
        int droppedRows = 0;
        while(board.moveBrickDown()){
            droppedRows++;
        }
        board.mergeBrickToBackground();
        clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        // Check game mode completion
        if (simpleBoard != null) {
            checkGameModeCompletion();
        }

        if (board.createNewBrick()) {
            handleGameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        if (event.getEventSource() == EventSource.USER) {
            board.getScore().add(droppedRows);
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        lockDelayActive = false;
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        lockDelayActive = false;
        return board.getViewData();
    }

    @Override
    public ViewData onRotateLeftEvent(MoveEvent event) {
        board.rotateLeftBrick();
        lockDelayActive = false;
        return board.getViewData();
    }

    @Override
    public ViewData onRotateRightEvent(MoveEvent event) {
        board.rotateRightBrick();
        lockDelayActive = false;
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdCurrentBrick();  // Run the hold function
        lockDelayActive = false;
        return board.getViewData();  // Return updated view
    }

    @Override
    public void createNewGame() {
        board.newGame();
        lockDelayActive = false;
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    @Override
    public ViewData getCurrentViewData() {
        return board.getViewData();
    }
}