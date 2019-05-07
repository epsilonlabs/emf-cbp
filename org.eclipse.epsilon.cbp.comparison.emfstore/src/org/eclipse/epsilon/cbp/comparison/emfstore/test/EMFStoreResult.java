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

public class EMFStoreResult {

	private long emfsPreparationTime = 0;
	private long emfsPreparationMemory = 0;
	private long emfsConflictTime = 0;
	private long emfsConflictMemory = 0;
	private long emfsComparisonTime = 0;
	private long emfsComparisonMemory = 0;
	private int emfsConflictCount = 0;

	public long getEmfsPreparationTime() {
		return emfsPreparationTime;
	}

	public long getEmfsPreparationMemory() {
		return emfsPreparationMemory;
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

	public void setEmfsPreparationTime(long emfsPreparationTime) {
		this.emfsPreparationTime = emfsPreparationTime;
	}

	public void setEmfsPreparationMemory(long emfsPreparationMemory) {
		this.emfsPreparationMemory = emfsPreparationMemory;
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

}