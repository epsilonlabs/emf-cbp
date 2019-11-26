/*******************************************************************************
 * Copyright (c) 2011-2019 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ryano - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.cbp.comparison.emfstore.test;

public class BigModelResult {

	private int number = 0;
	private long stateLoadTime = 0;
	private long stateLoadMemory = 0;
	private long stateMatchTime = 0;
	private long stateMatchMemory = 0;
	private long stateDiffTime = 0;
	private long stateDiffMemory = 0;
	private long stateConflictTime = 0;
	private long stateConflictMemory = 0;
	private long stateComparisonTime = 0;
	private long stateComparisonMemory = 0;
	private int leftElementCount = 0;
	private int rightElementCount = 0;
	private int stateDiffCount = 0;
	private int stateConflictCount = 0;
	private int stateRealConflictCount = 0;

	private long changeLoadTime = 0;
	private long changeLoadMemory = 0;
	private long changeTreeTime = 0;
	private long changeTreeMemory = 0;
	private long changeDiffTime = 0;
	private long changeDiffMemory = 0;
	private long changeConflictTime = 0;
	private long changeConflictMemory = 0;
	private long changeComparisonTime = 0;
	private long changeComparisonMemory = 0;
	private long changeConflictCount = 0;
	private long changeRealConflictCount = 0;
	private int leftEventCount = 0;
	private int rightEventCount = 0;
	private int changeDiffCount = 0;
	private int affectedObjectCount = 0;

	private long emfsPreparationTime = 0;
	private long emfsPreparationMemory = 0;
	private long emfsConflictTime = 0;
	private long emfsConflictMemory = 0;
	private int emfsConflictCount = 0;
	private long emfsComparisonTime = 0;
	private long emfsComparisonMemory = 0;

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

	public long getStateConflictTime() {
		return stateConflictTime;
	}

	public long getStateConflictMemory() {
		return stateConflictMemory;
	}

	public long getChangeConflictTime() {
		return changeConflictTime;
	}

	public long getChangeConflictMemory() {
		return changeConflictMemory;
	}

	public void setStateConflictTime(long stateConflictTime) {
		this.stateConflictTime = stateConflictTime;
	}

	public void setStateConflictMemory(long stateConflictMemory) {
		this.stateConflictMemory = stateConflictMemory;
	}

	public void setChangeConflictTime(long changeConflictTime) {
		this.changeConflictTime = changeConflictTime;
	}

	public void setChangeConflictMemory(long changeConflictMemory) {
		this.changeConflictMemory = changeConflictMemory;
	}

	public int getStateConflictCount() {
		return stateConflictCount;
	}

	public long getChangeConflictCount() {
		return changeConflictCount;
	}

	public long getEmfsConflictTime() {
		return emfsConflictTime;
	}

	public long getEmfsConflictMemory() {
		return emfsConflictMemory;
	}

	public int getEmfsConflictCount() {
		return emfsConflictCount;
	}

	public void setStateConflictCount(int stateConflictCount) {
		this.stateConflictCount = stateConflictCount;
	}

	public void setChangeConflictCount(long changeConflictCount) {
		this.changeConflictCount = changeConflictCount;
	}

	public void setEmfsConflictTime(long emfsConflictTime) {
		this.emfsConflictTime = emfsConflictTime;
	}

	public void setEmfsConflictMemory(long emfsConflictMemory) {
		this.emfsConflictMemory = emfsConflictMemory;
	}

	public void setEmfsConflictCount(int emfsConflictCount) {
		this.emfsConflictCount = emfsConflictCount;
	}

	public long getEmfsComparisonTime() {
		return emfsComparisonTime;
	}

	public long getEmfsComparisonMemory() {
		return emfsComparisonMemory;
	}

	public void setEmfsComparisonTime(long emfsComparisonTime) {
		this.emfsComparisonTime = emfsComparisonTime;
	}

	public void setEmfsComparisonMemory(long emfsComparisonMemory) {
		this.emfsComparisonMemory = emfsComparisonMemory;
	}

	public long getEmfsPreparationTime() {
		return emfsPreparationTime;
	}

	public long getEmfsPreparationMemory() {
		return emfsPreparationMemory;
	}

	public void setEmfsPreparationTime(long emfsPreparationTime) {
		this.emfsPreparationTime = emfsPreparationTime;
	}

	public void setEmfsPreparationMemory(long emfsPreparationMemory) {
		this.emfsPreparationMemory = emfsPreparationMemory;
	}

	public int getStateRealConflictCount() {
		return stateRealConflictCount;
	}

	public void setStateRealConflictCount(int stateRealConflictCount) {
		this.stateRealConflictCount = stateRealConflictCount;
	}

	public long getChangeRealConflictCount() {
		return changeRealConflictCount;
	}

	public void setChangeRealConflictCount(long changeRealConflictCount) {
		this.changeRealConflictCount = changeRealConflictCount;
	}

}