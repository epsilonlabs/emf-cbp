package org.eclipse.epsilon.cbp.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

public abstract class CBPResource extends ResourceImpl
{
	public CBPResource(URI uri)
	{
		super(uri);
	}
	
	public CBPResource()
	{
	}
}
