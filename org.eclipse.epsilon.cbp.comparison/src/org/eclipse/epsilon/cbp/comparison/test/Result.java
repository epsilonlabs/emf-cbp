package org.eclipse.epsilon.cbp.comparison.test;

public class Result {

    private String leftFileName;
    private String rightFileName;
    private int leftElementCount = -1;
    private int rightElementCount = -1;
    private int leftEventCount = -1;
    private int rightEventCount = -1;    
    private int stateBasedDiffCount = -1;
    private int changeBasedDiffCount = -1;
    private long stateBasedLoadTime = -1;
    private long stateBasedComparisonTime = -1;
    private long changeBasedComparisonTime = -1;
    private long changeBasedLoadTime = -1;
    
    public String getLeftFileName() {
        return leftFileName;
    }
    public void setLeftFileName(String leftFileName) {
        this.leftFileName = leftFileName;
    }
    public String getRightFileName() {
        return rightFileName;
    }
    public void setRightFileName(String rightFileName) {
        this.rightFileName = rightFileName;
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
    public int getStateBasedDiffCount() {
        return stateBasedDiffCount;
    }
    public void setStateBasedDiffCount(int stateBasedDiffCount) {
        this.stateBasedDiffCount = stateBasedDiffCount;
    }
    public int getChangeBasedDiffCount() {
        return changeBasedDiffCount;
    }
    public void setChangeBasedDiffCount(int changeBasedDiffCount) {
        this.changeBasedDiffCount = changeBasedDiffCount;
    }
    public long getStateBasedComparisonTime() {
        return stateBasedComparisonTime;
    }
    public void setStateBasedComparisonTime(long stateBasedMergingTime) {
        this.stateBasedComparisonTime = stateBasedMergingTime;
    }

    public void setChangeBasedComparisonTime(long changeBasedMergingTime) {
        this.changeBasedComparisonTime = changeBasedMergingTime;
    }
    public long getStateBasedLoadTime() {
        return stateBasedLoadTime;
    }
    public long getChangeBasedLoadTime() {
        return changeBasedLoadTime;
    }
    public void setStateBasedLoadTime(long stateBasedLoadTime) {
        this.stateBasedLoadTime = stateBasedLoadTime;
    }
    public void setChangeBasedLoadTime(long changeBasedLoadTime) {
        this.changeBasedLoadTime = changeBasedLoadTime;
    }
    public long getChangeBasedComparisonTime() {
        return changeBasedComparisonTime;
    }
    
    
    
}
