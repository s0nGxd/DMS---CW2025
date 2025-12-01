package com.comp2042.events;

import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;

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
