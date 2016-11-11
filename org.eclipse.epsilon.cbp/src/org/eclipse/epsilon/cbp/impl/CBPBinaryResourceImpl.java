package org.eclipse.epsilon.cbp.impl;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.context.CBPContext;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.event.EventAdapter;

public class CBPBinaryResourceImpl extends CBPResource 
{
	//context
	protected CBPContext context;
 
	//persistence manager
	private final PersistenceManager persistenceManager;
	
	//event adapter
    private final EventAdapter eventAdapter;
 
	public CBPBinaryResourceImpl(URI uri, EPackage ePackage)
	{
		super(uri);
		
		eventAdapter = new EventAdapter(context.getChangelog());
		
		this.eAdapters().add(eventAdapter); 
		
		context.populateEPackageElementNamesMap(ePackage);
		
		persistenceManager = new PersistenceManager(context,this);
	}
	
	public CBPBinaryResourceImpl(EPackage ePackage)
	{
		eventAdapter = new EventAdapter(context.getChangelog());
		
		this.eAdapters().add(eventAdapter); 
		
		context.populateEPackageElementNamesMap(ePackage);
		
		persistenceManager = new PersistenceManager(context,this);
	}
	
	@Override
	public void save(Map<?, ?> options) throws IOException
	{
		persistenceManager.save(options);
	}
	
	@Override
	public void load(Map<?, ?> options)throws IOException
	{
		eventAdapter.setEnabled(false);
		
		System.out.println("Load called on CBPBinaryResourceImpl");
		
		try {
			persistenceManager.load(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		eventAdapter.setEnabled(true);
	}
}
