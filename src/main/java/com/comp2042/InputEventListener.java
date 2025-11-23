package com.comp2042;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateLeftEvent(MoveEvent event);

    ViewData onRotateRightEvent(MoveEvent event);

    DownData onDropEvent (MoveEvent event);

    ViewData onHoldEvent (MoveEvent event);

    ViewData getCurrentViewData();

    void createNewGame();
}
