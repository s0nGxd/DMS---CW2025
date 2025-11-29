package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    private SimpleBoard simpleBoard;

    // Get methods from simple board
    public SimpleBoard getSimpleBoard() {
        return simpleBoard;
    }

    public GameController(GuiController c, GameMode mode) {
        viewGuiController = c;
        if (board instanceof SimpleBoard) {
            simpleBoard = (SimpleBoard) board;
            simpleBoard.setGameMode(mode);
        }
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
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

            // Check win conditions
            if (checkGameModeConditions()) {
                viewGuiController.gameWon();
                return new DownData(clearRow, board.getViewData());
            }

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }

        // Check time limit for BLITZ
        if (board.getGameMode() == GameMode.BLITZ) {
            long elapsed = System.currentTimeMillis() - board.getGameStartTime();
            if (elapsed >= 180000) { // 3 minutes
                viewGuiController.gameOver();
            }
        }

        return new DownData(clearRow, board.getViewData());
    }

    private boolean checkGameModeConditions() {
        SimpleBoard simpleBoard = (SimpleBoard) board;
        switch (simpleBoard.getGameMode()) {
            case SPRINT:
                return simpleBoard.getLinesCleared() >= 40;
            case BLITZ:
                long elapsed = System.currentTimeMillis() - simpleBoard.getGameStartTime();
                return elapsed >= 180000; // 3 minutes
            default:
                return false;
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
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
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
