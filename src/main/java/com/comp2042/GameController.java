package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private SimpleBoard simpleBoard;

    private final GuiController viewGuiController;
    private final HighScoreManager highScoreManager;

    public GameController(GuiController c, GameMode mode) {
        viewGuiController = c;
        highScoreManager = HighScoreManager.getInstance();

        if (board instanceof SimpleBoard) {
            simpleBoard = (SimpleBoard) board;
            simpleBoard.setGameMode(mode);
        }

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());

        // Display current high scores
        viewGuiController.displayHighScores(mode, highScoreManager);
    }

    public SimpleBoard getSimpleBoard() {
        return simpleBoard;
    }

    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
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

        boolean isNewRecord = highScoreManager.isSprintNewRecord(completionTime);
        if (isNewRecord) {
            highScoreManager.setSprintBestTime(completionTime);
        }

        viewGuiController.showSprintComplete(completionTime, isNewRecord, highScoreManager);
    }

    private void endBlitzMode() {
        int finalScore = board.getScore().scoreProperty().get();

        boolean isNewRecord = highScoreManager.isBlitzNewRecord(finalScore);
        if (isNewRecord) {
            highScoreManager.setBlitzHighScore(finalScore);
        }

        viewGuiController.showBlitzComplete(finalScore, isNewRecord, highScoreManager);
    }

    private void handleGameOver() {
        // Check for Pitfall high scores on game over
        if (simpleBoard != null && simpleBoard.getGameMode() == GameMode.PITFALL) {
            int finalScore = simpleBoard.getScore().scoreProperty().get();
            int finalLevel = simpleBoard.getCurrentLevel();

            boolean newLevelRecord = highScoreManager.isPitfallNewLevelRecord(finalLevel);
            boolean newScoreRecord = highScoreManager.isPitfallNewScoreRecord(finalScore);

            if (newLevelRecord) {
                highScoreManager.setPitfallHighLevel(finalLevel);
            }
            if (newScoreRecord) {
                highScoreManager.setPitfallHighScore(finalScore);
            }

            viewGuiController.showPitfallGameOver(finalLevel, finalScore,
                    newLevelRecord || newScoreRecord, highScoreManager);
        } else {
            viewGuiController.gameOver();
        }
    }

    @Override
    public DownData onDropEvent(MoveEvent event) {
        ClearRow clearRow = null;
        while(board.moveBrickDown()){
            // Loop to Keep Dropping until the Bottom
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
            board.getScore().add(1);
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateLeftEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateRightEvent(MoveEvent event) {
        board.rotateRightBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdCurrentBrick();  // Run the hold function
        return board.getViewData();  // Return updated view
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    @Override
    public ViewData getCurrentViewData() {
        return board.getViewData();
    }
}