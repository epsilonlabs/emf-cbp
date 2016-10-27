package org.eclipse.epsilon.cbp.impl;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;

public class CBPBinaryResourceImpl extends CBPResource 
{
	//class name
	private final String classname = this.getClass().getSimpleName();
	
	//change log
	private final Changelog changelog = new Changelog();
 
	//persistence manager
	private final PersistenceManager persistenceManager;
	
	//event adapter
    private final EventAdapter eventAdapter;
 
    //epackage elements map
    private final ModelElementIDMap ePackageElementsNamesMap;
	
	public CBPBinaryResourceImpl(URI uri, EPackage ePackage)
	{
		super(uri);
		
		eventAdapter = new EventAdapter(changelog);
		
		this.eAdapters().add(eventAdapter); 
		
		ePackageElementsNamesMap = populateEPackageElementNamesMap(ePackage);
		
		persistenceManager = new PersistenceManager(changelog,this, 
				ePackageElementsNamesMap);
	}
	
	public CBPBinaryResourceImpl(EPackage ePackage)
	{
		eventAdapter = new EventAdapter(changelog);
		
		this.eAdapters().add(eventAdapter); 
		
		ePackageElementsNamesMap = populateEPackageElementNamesMap(ePackage);
		
		persistenceManager = new PersistenceManager(changelog,this, 
				ePackageElementsNamesMap);
	}
	
	@Override
	public void save(Map<?, ?> options)
	{
		persistenceManager.save(options);
	}
	
	@Override
	public void load(Map<?, ?> options)throws IOException
	{
		eventAdapter.setEnabled(false);
		
		System.out.println(classname+": Load called!");
		
		try {
			persistenceManager.load(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		eventAdapter.setEnabled(true);
	}
		
	public Changelog getChangelog()
	{
		return this.changelog;
	}
	
	
}
