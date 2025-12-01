package com.comp2042.model;

import com.comp2042.data.ClearRow;
import com.comp2042.events.GameMode;
import com.comp2042.data.Score;
import com.comp2042.data.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean rotateRightBrick();

    boolean holdCurrentBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    void setGameMode(GameMode mode);

    GameMode getGameMode();

    int getLinesCleared();

    long getGameStartTime();

    int getCurrentLevel();

    int getFallSpeed();
}
