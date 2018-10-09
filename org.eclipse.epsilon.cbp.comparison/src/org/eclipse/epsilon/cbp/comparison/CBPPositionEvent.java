package org.eclipse.epsilon.cbp.comparison;

public class CBPPositionEvent {
    CBPPositionEventType eventType = CBPPositionEventType.UNDEFINED;
    int position = -1;
    int from = -1;

    public CBPPositionEvent(CBPPositionEventType eventType, int position) {
        this.eventType = eventType;
        this.position = position;
    }

    public CBPPositionEvent(CBPPositionEventType eventType, int to, int from) {
        this.eventType = eventType;
        this.position = to;
        this.from = from;
    }

    public CBPPositionEventType getEventType() {
        return eventType;
    }

    public void setEventType(CBPPositionEventType eventType) {
        this.eventType = eventType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTo() {
        return position;
    }

    public void setTo(int to) {
        this.position = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
}