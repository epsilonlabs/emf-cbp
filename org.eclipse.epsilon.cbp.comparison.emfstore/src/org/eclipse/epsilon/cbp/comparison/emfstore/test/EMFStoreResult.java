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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESConflictSet;

public class EMFStoreResult {

	private long emfsPreparationTime = 0;
	private long emfsPreparationMemory = 0;
	private long emfsConflictTime = 0;
	private long emfsConflictMemory = 0;
	private long emfsComparisonTime = 0;
	private long emfsComparisonMemory = 0;
	private Map<Integer, Set<String>> leftEventStrings = new LinkedHashMap<Integer, Set<String>>();
	private Map<Integer, Set<String>> rightEventStrings = new LinkedHashMap<Integer, Set<String>>();
	private ESConflictSet changeConflictSet;
	private List<AbstractOperation> leftOperations = new ArrayList<AbstractOperation>();
	private List<AbstractOperation> rightOperations = new ArrayList<AbstractOperation>();

	public List<AbstractOperation> getLeftOperations() {
		return leftOperations;
	}

	public List<AbstractOperation> getRightOperations() {
		return rightOperations;
	}

	public void setLeftOperations(List<AbstractOperation> leftOperations) {
		this.leftOperations = leftOperations;
	}

	public void setRightOperations(List<AbstractOperation> rightOperations) {
		this.rightOperations = rightOperations;
	}

	public Map<Integer, Set<String>> getLeftEventStrings() {
		return leftEventStrings;
	}

	public Map<Integer, Set<String>> getRightEventStrings() {
		return rightEventStrings;
	}

	public void setLeftEventStrings(Map<Integer, Set<String>> leftEventStrings) {
		this.leftEventStrings = leftEventStrings;
	}

	public void setRightEventStrings(Map<Integer, Set<String>> rightEventStrings) {
		this.rightEventStrings = rightEventStrings;
	}

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
		if (changeConflictSet != null) {
			return changeConflictSet.getConflicts().size();
		} else {
			return 0;
		}
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

	/**
	 * @param changeConflictSet
	 */
	public void setChangeConflictSet(ESConflictSet changeConflictSet) {
		this.changeConflictSet = changeConflictSet;

	}

	public ESConflictSet getChangeConflictSet() {
		return changeConflictSet;
	}

}