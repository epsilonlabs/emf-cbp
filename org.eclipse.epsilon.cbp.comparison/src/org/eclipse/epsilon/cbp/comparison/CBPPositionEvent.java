package org.eclipse.epsilon.cbp.comparison;

public class CBPPositionEvent {
    CBPPositionEventType eventType = CBPPositionEventType.UNDEFINED;
    int position = -1;
    int from = -1;
    Object value = null;

    public CBPPositionEvent(CBPPositionEventType eventType, int position, Object value) {
        this.eventType = eventType;
        this.position = position;
        this.value = value;
    }

    public CBPPositionEvent(CBPPositionEventType eventType, int to, int from, Object value) {
        this.eventType = eventType;
        this.position = to;
        this.from = from;
        this.value = value;
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    
}