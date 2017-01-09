package org.eclipse.epsilon.effectivemetamodel.impl;

import java.util.ArrayList;


public class EffectiveType {

	protected String name;
	protected EffectiveMetamodel effectiveMetamodel;
	protected ArrayList<EffectiveFeature> attributes = new ArrayList<EffectiveFeature>();
	protected ArrayList<EffectiveFeature> references = new ArrayList<EffectiveFeature>();
	
	public EffectiveType(String name)
	{
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public EffectiveMetamodel getEffectiveMetamodel() {
		return effectiveMetamodel;
	}
	
	public void setEffectiveMetamodel(EffectiveMetamodel effectiveMetamodel) {
		this.effectiveMetamodel = effectiveMetamodel;
	}
	
	public ArrayList<EffectiveFeature> getAttributes() {
		return attributes;
	}
	
	public ArrayList<EffectiveFeature> getReferences() {
		return references;
	}
	
	public boolean containsAttribute(String attribute)
	{
		for(EffectiveFeature ef: attributes)
		{
			if (ef.getName().equals(attribute)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsReference(String reference)
	{
		for(EffectiveFeature ef: references)
		{
			if (ef.getName().equals(reference)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsFeature(String feature)
	{
		return containsAttribute(feature) || containsReference(feature);
	}
	
	public EffectiveFeature addToAttributes(String attribute)
	{
		for(EffectiveFeature ef: attributes)
		{
			if (ef.getName().equals(attribute)) {
				ef.setEffectiveType(this);
				ef.increaseUsage();
				return ef;
			}
		}
		
		EffectiveFeature attr = new EffectiveFeature(attribute);
		attributes.add(attr);
		attr.setEffectiveType(this);
		return attr;
	}
	
	public EffectiveFeature addToReferences(String reference)
	{
		for(EffectiveFeature ef: references)
		{
			if(ef.getName().equals(reference))
			{
				ef.increaseUsage();
				return ef;
			}
		}
		EffectiveFeature ref = new EffectiveFeature(reference);
		references.add(ref);
		ref.setEffectiveType(this);
		return ref;
	}
	
	public EffectiveFeature increaseAttributeUsage(String attribute)
	{
		for(EffectiveFeature ef: attributes)
		{
			if (ef.getName().equals(attribute)) {
				ef.increaseUsage();
				return ef;
			}
		}
		return null;
	}
	
	public EffectiveFeature increaseReferenceUsage(String reference)
	{
		for(EffectiveFeature ef: references)
		{
			if (ef.getName().equals(reference)) {
				ef.increaseUsage();
				return ef;
			}
		}
		return null;
	}
	
	public ArrayList<EffectiveFeature> getAllFeatures()
	{
		ArrayList<EffectiveFeature> result = new ArrayList<EffectiveFeature>();
		result.addAll(attributes);
		result.addAll(references);
		return result;
	}
}
