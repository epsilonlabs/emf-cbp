package org.eclipse.epsilon.cbp.state2change;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.uml2.common.util.CacheAdapter;
import org.eclipse.uml2.uml.UMLPackage;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("restriction")
public class State2ChangeConverter {

    private static final int SINGLE_FILE = 1;
    private static final int MULTIPLE_FILES = 2;
    protected int convertionType = MULTIPLE_FILES;

    protected URI rootXmiFileUri;
    protected URI commonXmiURI = URI.createURI("umlmodel.xmi");
    protected URI commonCbpxmlURI = URI.createURI("umlmodel.cbpxml");

    protected IBatchMerger batchMerger;
    protected EMFCompare comparator;

    protected List<ResourceSet> resourceSetList = new ArrayList<>();

    protected Resource cbpResource;
    protected Resource cbpStateResource;
    protected ResourceSet cbpResourceSet;
    protected ResourceSet xmiResourceSet;
    protected ResourceSet diffResourceSet;
    protected ResourceSet cbpTestResourceSet;
    protected ResourceSet cbpStateResourceSet;

    protected Map<Object, Object> saveOptions;
    protected String cbpStatePath;

    private File sourceDirectory;

    public State2ChangeConverter(File xmiDirectory) throws FileNotFoundException {
	this.sourceDirectory = xmiDirectory;
    }

    public boolean generateFromSingleFile(Resource cbpResource, Resource xmiResource, File diffDirectory) throws Exception {
	convertionType = SINGLE_FILE;

	UMLPackage.eINSTANCE.eClass();
	saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
	saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

	this.cbpResource = cbpResource;

	diffResourceSet = new ResourceSetImpl();
	diffResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	resourceSetList.add(diffResourceSet);

	Task task = new Task(xmiResource, diffDirectory);
	task.setDaemon(true);
	task.setName(xmiResource.getURI().lastSegment());
	task.start();
	task.join();

	return true;
    }

    public boolean generateFromMultipleFiles(File cbpFile, File diffDirectory) throws Exception {
	convertionType = MULTIPLE_FILES;

	UMLPackage.eINSTANCE.eClass();
	// JavaPackage.eINSTANCE.eClass();
	saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
	saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

	// -----
	cbpResourceSet = new ResourceSetImpl();
	cbpResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml", new CBPXMLResourceFactory());

	cbpResource = new CBPXMLResourceImpl();
	((CBPResource) cbpResource).setIdType(IdType.UUID);
	URI cbpUri = URI.createFileURI(cbpFile.getAbsolutePath());
	File file = new File(cbpUri.toFileString());
//	if (file.exists()) {
//	    file.delete();
//	}
	file.createNewFile();

	cbpResource.setURI(cbpUri);
	cbpResourceSet.getResources().add(cbpResource);
	// cbpResource.load(null);

	xmiResourceSet = new ResourceSetImpl();
	xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	resourceSetList.add(xmiResourceSet);

	diffResourceSet = new ResourceSetImpl();
	diffResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	resourceSetList.add(diffResourceSet);

	cbpStateResourceSet = new ResourceSetImpl();
	cbpStateResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	resourceSetList.add(cbpStateResourceSet);

	// cbpStatePath = "D:/TEMP/BigModel/cbp-state/";
	// cbpStateResource =
	// cbpStateResourceSet.createResource(URI.createURI("cbp-state.xmi"));

	File[] sourceFiles = sourceDirectory.listFiles();
	System.out.println("Converting " + sourceFiles.length + " file(s) to CBP");
	for (int i = 0; i < sourceFiles.length; i++) {
	    Task task = new Task(cbpFile, diffDirectory, sourceFiles[i]);
	    task.setDaemon(true);
	    task.setName(sourceFiles[i].getName());
	    task.start();
	    task.join();
	}
	System.out.println("Finished!");
	return true;
    }

    public class Task extends Thread {

	protected File cbpFile;
	protected File diffDirectory;
	protected File xmiFile;
	protected Resource xmiResource;

	public Task(Resource xmiResource, File diffDirectory) throws IOException {
	    super();
	    this.xmiResource = xmiResource;
	    this.diffDirectory = diffDirectory;
	}

	public Task(File cbpFile, File diffDirectory, File sourceFile) throws IOException {
	    super();
	    this.cbpFile = cbpFile;
	    this.diffDirectory = diffDirectory;
	    this.xmiFile = sourceFile;

	}

	@SuppressWarnings("restriction")
	@Override
	public void run() {
	    if (State2ChangeConverter.this.convertionType == State2ChangeConverter.SINGLE_FILE) {
		convertSingleFile();
	    } else if (State2ChangeConverter.this.convertionType == State2ChangeConverter.MULTIPLE_FILES) {
		try {
		    convertMultipleFiles();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

	/***
	 * 
	 */
	private void convertSingleFile() {
	    System.gc();
	    System.out.println("Converting file " + xmiResource.getURI().lastSegment() + " to CBP");

	    IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
	    BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(), Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
	    postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
	    Builder builder = EMFCompare.builder();
	    builder.setPostProcessorRegistry(postProcessorRegistry);
	    comparator = builder.build();

	    IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
	    UMLMerger umlMerger = new UMLMerger();
	    umlMerger.setRanking(11);
	    registry.add(umlMerger);
	    batchMerger = new BatchMerger(registry);

	    // register all objects to be removed later to save UML cache
	    // memory
	    List<EObject> list = new ArrayList<EObject>();
	    // TreeIterator<EObject> iterator = cbpResource.getAllContents();
	    // while (iterator.hasNext()) {
	    // EObject eObject = iterator.next();
	    // list.add(eObject);
	    // }
	    TreeIterator<EObject> iterator = xmiResource.getAllContents();
	    while (iterator.hasNext()) {
		EObject eObject = iterator.next();
		list.add(eObject);
	    }

	    // initialise UML comparison
	    IComparisonScope2 scope = createComparisonScope(cbpResource, xmiResource);
	    System.out.println("Compare ...");
	    System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    Comparison comparison = comparator.compare(scope);
	    System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    EList<Diff> diffs = comparison.getDifferences();
	    System.out.println("Diffs: " + diffs.size());

	    CBPXMLResourceImpl cbpImpl = ((CBPXMLResourceImpl) cbpResource);
	    cbpImpl.startNewSession(xmiResource.getURI().lastSegment());

	    int prevDiffs = diffs.size() + 1;
	    while (diffs.size() > 0 && prevDiffs > diffs.size()) {
		prevDiffs = diffs.size();

		// copy all right to left
		// List<ChangeEvent<?>> x = cbpImpl.getChangeEvents();
		System.out.println("Merge ...");
		System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
		System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));

		// after merging, to check if there are no more differences
		scope = createComparisonScope(cbpResource, xmiResource);
		System.out.println("Re-compare again for validation ...");
		System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		comparison = comparator.compare(scope);
		System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		diffs = comparison.getDifferences();
		System.out.println("Diffs: " + diffs.size());
	    }

	    try {
		// save
		cbpResource.save(null);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    System.out.println();

	    // clearCacheAdapter();
	    // ((CBPResource) cbpResource).getModelHistory().clear();
	    // ((CBPResource) cbpResource).getIgnoreSet().clear();
	    for (EObject eObject : list) {
		CacheAdapter.getInstance().getInverseReferences(eObject).clear();
		CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
		eObject.eAdapters().clear();
		EcoreUtil.remove(eObject);
	    }
	    list.clear();
	    clearCacheAdapter(xmiResource);
	    // clearCacheAdapter(cbpResource);
	}

	/**
	 * @throws IOException *
	 * 
	 */
	private void convertMultipleFiles() throws IOException {
	    System.gc();
	    System.out.println("Converting file " + xmiFile.getName() + " to CBP");
	    Resource x = cbpResource;
	    try {
		System.out.println("Load model from CBP ...");
//		cbpResource.unload();
		cbpResource.load(null);
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }

	    IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
	    BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(), Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
	    postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
	    Builder builder = EMFCompare.builder();
	    builder.setPostProcessorRegistry(postProcessorRegistry);
	    comparator = builder.build();

	    IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
	    UMLMerger umlMerger = new UMLMerger();
	    umlMerger.setRanking(11);
	    registry.add(umlMerger);
	    batchMerger = new BatchMerger(registry);

	    String xmiFilePath = xmiFile.getAbsolutePath();
	    URI xmiFileUri = URI.createFileURI(xmiFilePath);
	    Resource xmiResource = (new XMIResourceFactoryImpl()).createResource(xmiFileUri);
	    System.out.println("Loading XMI ...");
	    xmiResource.load(saveOptions);
	    xmiResource.setURI(commonXmiURI);
	    xmiResourceSet.getResources().clear();
	    xmiResourceSet.getResources().add(xmiResource);

	    // initialise UML comparison
	    IComparisonScope2 scope = createComparisonScope(cbpResource, xmiResource);

//	     Logger.getRootLogger().setLevel(Level.OFF);
	    List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
	    loggers.add(LogManager.getRootLogger());
	    for (Logger logger : loggers) {
		logger.setLevel(Level.OFF);
	    }

	    System.out.println("Start comparison ...");
	    Comparison comparison = comparator.compare(scope);
	    EList<Diff> diffs = comparison.getDifferences();

	    // // persist diffs
	    // Resource comparisonResource =
	    // diffResourceSet.createResource(URI.createURI("diffs.xmi"));
	    // comparisonResource.getContents().addAll(EcoreUtil.copyAll(comparison.getDifferences()));
	    // String diffFilePath = diffDirectory.getPath() + File.separator +
	    // "Diff-" + xmiFile.getName();
	    // File diffFile = new File(diffFilePath);
	    // FileOutputStream diffFileOutputStream;
	    // try {
	    // diffFileOutputStream = new FileOutputStream(diffFile);
	    // comparisonResource.save(diffFileOutputStream, saveOptions);
	    // diffFileOutputStream.flush();
	    // diffFileOutputStream.close();
	    // } catch (IOException e) {
	    // e.printStackTrace();
	    // }

//	    // copy all right to left
//	    List<EObject> list = new ArrayList<EObject>();
//	    TreeIterator<EObject> iterator = cbpResource.getAllContents();
//	    while (iterator.hasNext()) {
//		EObject eObject = iterator.next();
//		list.add(eObject);
//	    }
	    CBPXMLResourceImpl cbpImpl = ((CBPXMLResourceImpl) cbpResource);
	    cbpImpl.startNewSession(xmiFile.getName());
	    System.out.println("Start merging to left :  " + diffs.size() + " diffs");
	    batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
	    try {
		cbpResource.save(null);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    // //---test by reload
	    // ((CBPResource) cbpResource).getModelHistory().clear();
	    // ((CBPResource) cbpResource).getIgnoreSet().clear();
	    // clearCacheAdapter(cbpResource);
	    try {
		cbpResource.unload();
		cbpResource.load(null);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    // cbpStateResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
	    // URI cbpStateFileUri = URI.createFileURI(cbpStatePath +
	    // "Cbp-state-" + xmiFile.getName());
	    // cbpStateResource.setURI(cbpStateFileUri);
	    // try {
	    // cbpStateResource.save(null);
	    // } catch (IOException e) {
	    // e.printStackTrace();
	    // }

	    // after merging, to check if there are no more differences
	    scope = createComparisonScope(cbpResource, xmiResource);
	    System.out.println("Re-do comparison for validation ...");
	    comparison = comparator.compare(scope);
	    diffs = comparison.getDifferences();
	    System.out.println("Left: " + diffs.size() + " diffs");
	    // for (Diff diff : diffs) {
	    // System.out.println(diff);
	    // }
	    if (diffs.size() > 0) {
		System.out.println("There are still differences between the CBP and the XMI.");

		System.out.println("Merging to left again  ...");
		batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
		try {
		    cbpResource.save(null);
		} catch (IOException e) {
		    e.printStackTrace();
		}

		try {
		    System.out.println("Re-load from the CBP again  ...");
		    cbpResource.unload();
		    cbpResource.load(null);
		} catch (IOException e) {
		    e.printStackTrace();
		}

		scope = createComparisonScope(cbpResource, xmiResource);
		System.out.println("Re-do comparison for validation again ...");
		comparison = comparator.compare(scope);
		diffs = comparison.getDifferences();
		System.out.println(" Size After second comparison and merging " + diffs.size());

		if (diffs.size() > 0) {
		    System.out.println();
		    return;
		}
	    }
	   
//	    clearCacheAdapter();
//	    ((CBPResource) cbpResource).getModelHistory().clear();
//	    ((CBPResource) cbpResource).getIgnoreSet().clear();
//	    for (EObject eObject : list) {
//		CacheAdapter.getInstance().getInverseReferences(eObject).clear();
//		CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
//		eObject.eAdapters().clear();
//		EcoreUtil.remove(eObject);
//	    }
//	    list.clear();
//	    clearCacheAdapter(cbpResource);
	}
    }

    private IComparisonScope2 createComparisonScope(Resource cbpResource, Resource xmiResource) {
	IComparisonScope2 scope = new DefaultComparisonScope(cbpResource, xmiResource, null);
	Set<ResourceSet> resourceSets = ImmutableSet.of(cbpResource.getResourceSet(), xmiResource.getResourceSet());
	com.google.common.collect.ImmutableSet.Builder<URI> uriBuilder = ImmutableSet.builder();
	for (ResourceSet set : resourceSets) {
	    for (Resource resource : set.getResources()) {
		uriBuilder.add(resource.getURI());
	    }
	}
	Set<URI> setUri = uriBuilder.build();
	scope.getAllInvolvedResourceURIs().addAll(setUri);
	return scope;
    }

    private void clearCacheAdapter() {
	for (ResourceSet resourceSet : resourceSetList) {
	    for (Resource resource : resourceSet.getResources()) {
		TreeIterator<EObject> iterator = resource.getAllContents();
		List<EObject> list = new ArrayList<>();
		while (iterator.hasNext()) {
		    EObject item = iterator.next();
		    list.add(item);
		}
		for (EObject eObject : list) {
		    CacheAdapter.getInstance().getInverseReferences(eObject).clear();
		    CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
		    eObject.eAdapters().clear();
		    EcoreUtil.remove(eObject);

		}
		list.clear();
		CacheAdapter.getInstance().clear(resource);
		CacheAdapter.getInstance().clear();

		resource.getContents().clear();
		resource.unload();
	    }
	    resourceSet.getResources().clear();
	}
    }

    private void clearCacheAdapter(Resource resource) {
	TreeIterator<EObject> iterator = resource.getAllContents();
	List<EObject> list = new ArrayList<>();
	while (iterator.hasNext()) {
	    EObject item = iterator.next();
	    list.add(item);
	}
	for (EObject eObject : list) {
	    CacheAdapter.getInstance().getInverseReferences(eObject).clear();
	    CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
	    eObject.eAdapters().clear();
	    EcoreUtil.remove(eObject);

	}
	list.clear();
	CacheAdapter.getInstance().clear(resource);
	CacheAdapter.getInstance().clear();
	resource.getContents().clear();
	resource.unload();
    }
}
