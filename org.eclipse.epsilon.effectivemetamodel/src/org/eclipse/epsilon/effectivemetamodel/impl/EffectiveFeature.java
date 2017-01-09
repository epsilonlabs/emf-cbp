package org.eclipse.epsilon.effectivemetamodel.impl;

public class EffectiveFeature {

	protected String name;
	protected int usage;
	protected EffectiveType effectiveType;
	
	public EffectiveFeature(String name)
	{
		this.name = name;
		usage = 1;
	}
	
	public EffectiveType getEffectiveType() {
		return effectiveType;
	}
	
	public void setEffectiveType(EffectiveType effectiveType) {
		this.effectiveType = effectiveType;
	}
	
	public String getName() {
		return name;
	}
	
	public int getUsage() {
		return usage;
	}
	
	public void increaseUsage()
	{
		usage++;
	}
}
