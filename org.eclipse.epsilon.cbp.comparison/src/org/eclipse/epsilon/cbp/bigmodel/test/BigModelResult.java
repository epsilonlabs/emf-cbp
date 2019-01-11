package org.eclipse.epsilon.cbp.bigmodel.test;

public class BigModelResult {

    private int number = 0;
    private long stateLoadTime = 0;
    private long stateLoadMemory = 0;
    private long stateMatchTime = 0;
    private long stateMatchMemory = 0;
    private long stateDiffTime = 0;
    private long stateDiffMemory = 0;
    private long stateComparisonTime = 0;
    private long stateComparisonMemory = 0;
    private int leftElementCount = 0;
    private int rightElementCount = 0;
    private int stateDiffCount = 0;

    private long changeLoadTime = 0;
    private long changeLoadMemory = 0;
    private long changeTreeTime = 0;
    private long changeTreeMemory = 0;
    private long changeDiffTime = 0;
    private long changeDiffMemory = 0;
    private long changeComparisonTime = 0;
    private long changeComparisonMemory = 0;
    private int leftEventCount = 0;
    private int rightEventCount = 0;
    private int changeDiffCount = 0;
    private int affectedObjectCount = 0;

    public int getAffectedObjectCount() {
	return affectedObjectCount;
    }

    public void setAffectedObjectCount(int affectedObjectCount) {
	this.affectedObjectCount = affectedObjectCount;
    }

    public int getNumber() {
	return number;
    }

    public void setNumber(int number) {
	this.number = number;
    }

    public long getStateLoadTime() {
	return stateLoadTime;
    }

    public void setStateLoadTime(long stateLoadTime) {
	this.stateLoadTime = stateLoadTime;
    }

    public long getStateLoadMemory() {
	return stateLoadMemory;
    }

    public void setStateLoadMemory(long stateLoadMemory) {
	this.stateLoadMemory = stateLoadMemory;
    }

    public long getStateMatchTime() {
	return stateMatchTime;
    }

    public void setStateMatchTime(long stateMatchTime) {
	this.stateMatchTime = stateMatchTime;
    }

    public long getStateMatchMemory() {
	return stateMatchMemory;
    }

    public void setStateMatchMemory(long stateMatchMemory) {
	this.stateMatchMemory = stateMatchMemory;
    }

    public long getStateDiffTime() {
	return stateDiffTime;
    }

    public void setStateDiffTime(long stateDiffTime) {
	this.stateDiffTime = stateDiffTime;
    }

    public long getStateDiffMemory() {
	return stateDiffMemory;
    }

    public void setStateDiffMemory(long stateDiffMemory) {
	this.stateDiffMemory = stateDiffMemory;
    }

    public int getLeftElementCount() {
	return leftElementCount;
    }

    public void setLeftElementCount(int leftElementCount) {
	this.leftElementCount = leftElementCount;
    }

    public int getRightElementCount() {
	return rightElementCount;
    }

    public void setRightElementCount(int rightElementCount) {
	this.rightElementCount = rightElementCount;
    }

    public int getStateDiffCount() {
	return stateDiffCount;
    }

    public void setStateDiffCount(int stateDiffCount) {
	this.stateDiffCount = stateDiffCount;
    }

    public long getChangeLoadTime() {
	return changeLoadTime;
    }

    public void setChangeLoadTime(long changeLoadTime) {
	this.changeLoadTime = changeLoadTime;
    }

    public long getChangeLoadMemory() {
	return changeLoadMemory;
    }

    public void setChangeLoadMemory(long changeLoadMemory) {
	this.changeLoadMemory = changeLoadMemory;
    }

    public long getChangeTreeTime() {
	return changeTreeTime;
    }

    public void setChangeTreeTime(long changeTreeTime) {
	this.changeTreeTime = changeTreeTime;
    }

    public long getChangeTreeMemory() {
	return changeTreeMemory;
    }

    public void setChangeTreeMemory(long changeTreeMemory) {
	this.changeTreeMemory = changeTreeMemory;
    }

    public long getChangeDiffTime() {
	return changeDiffTime;
    }

    public void setChangeDiffTime(long changeDiffTime) {
	this.changeDiffTime = changeDiffTime;
    }

    public long getChangeDiffMemory() {
	return changeDiffMemory;
    }

    public void setChangeDiffMemory(long changeDiffMemory) {
	this.changeDiffMemory = changeDiffMemory;
    }

    public int getLeftEventCount() {
	return leftEventCount;
    }

    public void setLeftEventCount(int leftEventCount) {
	this.leftEventCount = leftEventCount;
    }

    public int getRightEventCount() {
	return rightEventCount;
    }

    public void setRightEventCount(int rightEventCount) {
	this.rightEventCount = rightEventCount;
    }

    public int getChangeDiffCount() {
	return changeDiffCount;
    }

    public void setChangeDiffCount(int changeDiffCount) {
	this.changeDiffCount = changeDiffCount;
    }

    public long getStateComparisonTime() {
	return stateComparisonTime;
    }

    public long getStateComparisonMemory() {
	return stateComparisonMemory;
    }

    public long getChangeComparisonTime() {
	return changeComparisonTime;
    }

    public long getChangeComparisonMemory() {
	return changeComparisonMemory;
    }

    public void setStateComparisonTime(long stateComparisonTime) {
	this.stateComparisonTime = stateComparisonTime;
    }

    public void setStateComparisonMemory(long stateComparisonMemory) {
	this.stateComparisonMemory = stateComparisonMemory;
    }

    public void setChangeComparisonTime(long changeComparisonTime) {
	this.changeComparisonTime = changeComparisonTime;
    }

    public void setChangeComparisonMemory(long changeComparisonMemory) {
	this.changeComparisonMemory = changeComparisonMemory;
    }

}