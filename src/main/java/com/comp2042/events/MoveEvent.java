package com.comp2042.events;

/**
 * Represents a single movement or action event in the game.
 * Encapsulates both the type of event and its source.
 */

public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent with the specified type and source.
     * @param eventType the type of event (e.g., LEFT, RIGHT, ROTATE)
     * @param eventSource the origin of the event (USER or THREAD)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of this event.
     * @return the EventType of this event
     */
    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}
