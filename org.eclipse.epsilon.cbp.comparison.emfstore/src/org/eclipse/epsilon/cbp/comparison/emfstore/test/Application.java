/*******************************************************************************
 * Copyright 2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Florian Pirchner
 * Maximilian Koegel
 ******************************************************************************/
package org.eclipse.epsilon.cbp.comparison.emfstore.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.ESWorkspace;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.exceptions.ESServerNotFoundException;
import org.eclipse.emf.emfstore.client.exceptions.ESServerStartFailedException;
import org.eclipse.emf.emfstore.common.ESSystemOutProgressMonitor;
import org.eclipse.emf.emfstore.common.model.ESModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOperationImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeMoveOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceMoveOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnsetType;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.CompositeOperationImpl;
import org.eclipse.emf.emfstore.server.ESConflict;
import org.eclipse.emf.emfstore.server.ESConflictSet;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.exceptions.ESUpdateRequiredException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESOperation;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.epsilon.cbp.bigmodel.ModifiedEMFCompare;
import org.eclipse.epsilon.cbp.bigmodel.ModifiedEMFCompare.ModifiedBuilder;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.emfstore.CBP2EMFStoreAdapter;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.comparison.util.CBPComparisonUtil;
import org.eclipse.epsilon.cbp.conflict.test.EObjectComparator;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;

import com.google.common.io.Files;

/**
 * An application that runs the demo.<br>
 * Run a client and local server that demo the basic features of EMFStore.
 */
public class Application implements IApplication {

	static boolean isConflict = false;
	static DecimalFormat df = new DecimalFormat("###.###");
	static private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
	static private Random random = new Random();
	static EPackage ePackage = null;

	static CBP2EMFStoreAdapter originalAdapater = null;
	static CBP2EMFStoreAdapter leftAdapater = null;
	static CBP2EMFStoreAdapter rightAdapater = null;

	static Map<Object, Object> options = new HashMap();

	static enum ChangeType {
		CHANGE, ADD, MOVE, DELETE;
	}

	// static Map<String, String> id2esIdMap = new HashMap<String, String>();
	// static Map<String, String> esId2IdMap = new HashMap<String, String>();
	// static Map<String, EObject> id2EObjectMap = new HashMap<String, EObject>();
	// static Map<EObject, String> eObject2IdMap = new HashMap<EObject, String>();
	// static File xmiFile = new File("D:\\TEMP\\CONFLICTS\\temp\\emfstore-target.xmi");
	// static XMIResource xmiResource = (XMIResource) new XMIResourceFactoryImpl()
	// .createResource(URI.createFileURI(xmiFile.getAbsolutePath()));

	/**
	 * {@inheritDoc}
	 *
	 * @throws Exception
	 */
	public Object start(IApplicationContext context) throws Exception {

		try {
			// Logger.getRootLogger().setLevel(Level.OFF);

			options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
			options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			// Create a client representation for a local server and start a local server.
			final ESServer localServer = ESServer.FACTORY.createAndStartLocalServer();
			// Run a client on the local server that shows the basic features of the EMFstore
			// runClient(localServer);
			// runBatchClient(localServer);
			runPerformanceTest(localServer);
		} catch (final ESServerStartFailedException e) {
			System.out.println("Server start failed!");
			e.printStackTrace();
		} catch (final ESException e) {
			// If there is a problem with the connection to the server,
			// e.g., a network, a specific EMFStoreException will be thrown.
			System.out.println("Connection to Server failed!");
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	public static CBP2EMFStoreAdapter getOriginalAdapater() {
		return originalAdapater;
	}

	public static CBP2EMFStoreAdapter getLeftAdapater() {
		return leftAdapater;
	}

	public static CBP2EMFStoreAdapter getRightAdapater() {
		return rightAdapater;
	}

	/**
	 * Run an EMFStore Client connecting to the given server.
	 *
	 * @param server the server
	 * @throws ESException if the server connection fails
	 */
	public static void runClient(ESServer server) throws ESException {
		System.out.println("Client starting...");

		EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
		// EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
		// EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(), JavaPackage.eINSTANCE);

		// The workspace is the core controller to access local and remote projects.
		// A project is a container for models and their elements (EObjects).
		// To get started, we obtain the current workspace of the client.
		final ESWorkspace workspace = ESWorkspaceProvider.INSTANCE.getWorkspace();

		// The workspace stores all available servers that have been configured. We add the local server that has
		// already
		// been started on the workspace.
		workspace.addServer(server);
		// Next, we remove all other existing servers
		for (final ESServer existingServer : workspace.getServers()) {
			if (existingServer != server) {
				try {
					workspace.removeServer(existingServer);
				} catch (final ESServerNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		// The workspace also contains a list of local projects that have either been created locally or checked out
		// from a server.
		// We create a new local project. The project new created is not yet shared with the server.

		// We delete all projects from the local workspace other than the one just created.
		for (final ESLocalProject existingLocalProject : workspace.getLocalProjects()) {
			try {
				existingLocalProject.delete(new ESSystemOutProgressMonitor());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		ESLocalProject originalProject = workspace.createLocalProject("OriginalProject");

		// Next, we create a user session by logging in to the local EMFStore server with default super user
		// credentials.
		final ESUsersession usersession = server.login("super", "super");

		// We also retrieve a list of existing (and accessible) remote projects on the server.
		// Remote projects represent a project that is currently available on the server.
		// We delete all remote projects to clean up remaining projects from previous launches.
		for (final ESRemoteProject existingRemoteProject : server.getRemoteProjects(usersession)) {
			existingRemoteProject.delete(usersession, new NullProgressMonitor());
		}

		// Now we can share the created local project to our server.
		final ESRemoteProject remoteDemoProject = originalProject.shareProject(usersession,
			new ESSystemOutProgressMonitor());

		// Now we are all set: we have a client workspace with one server configured and exactly one project shared to a
		// server with only this one project.

		// We check out a second, independent copy of the project (simulating a second client).
		ESLocalProject leftProject = originalProject.getRemoteProject().checkout("LeftProject",
			usersession, new ESSystemOutProgressMonitor());
		ESLocalProject rightProject = originalProject.getRemoteProject().checkout("RightProject",
			usersession, new ESSystemOutProgressMonitor());

		originalAdapater = new CBP2EMFStoreAdapter(originalProject);
		leftAdapater = new CBP2EMFStoreAdapter(leftProject);
		rightAdapater = new CBP2EMFStoreAdapter(rightProject);

		// final File cbpOriginalFile = new File("D:\\TEMP\\FASE\\Debug\\origin.cbpxml");
		// final File cbpLeftFile = new File("D:\\TEMP\\FASE\\Debug\\left.cbpxml");
		// final File cbpRightFile = new File("D:\\TEMP\\FASE\\Debug\\right.cbpxml");

		final File cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.cbpxml");
		final File cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.cbpxml");
		final File cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.cbpxml");
		final File xmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-target.xmi");

		XMIResource xmiResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));

		long skip = cbpOriginalFile.length();

		try {
			System.out.println("LOADING ORIGINAL MODEL");
			originalAdapater.load(cbpOriginalFile);
			originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());
			rightProject.update(new ESSystemOutProgressMonitor());
			leftProject.update(new ESSystemOutProgressMonitor());

			// process right file first
			System.out.println("LOADING RIGHT MODEL");
			String rightText = readFiles(cbpRightFile, skip);
			InputStream rightStream = new ByteArrayInputStream(rightText.getBytes());
			rightAdapater.load(rightStream);
			rightStream.close();
			rightProject.commit("RIGHT", null, new ESSystemOutProgressMonitor());
			// EObject rightObject = rightProject.getModelElement(originalEsId);
			// ESModelElementId rightEsId = rightProject.getModelElementId(rightObject);

			// process left
			System.out.println("LOADING LEFT MODEL");
			String leftText = readFiles(cbpLeftFile, skip);
			InputStream leftStream = new ByteArrayInputStream(leftText.getBytes());
			leftAdapater.load(leftStream);
			leftStream.close();
			// EObject leftObject = leftProject.getModelElement(originalEsId);
			// ESModelElementId leftEsId = leftProject.getModelElementId(leftObject);

			// EObject x = originalProject.getModelElements().get(0);
			// EStructuralFeature f1 = x.eClass().getEStructuralFeature("name");
			// String v1 = (String) x.eGet(f1);
			//
			// EObject y = rightProject.getModelElements().get(0);
			// EStructuralFeature f2 = y.eClass().getEStructuralFeature("name");
			// String v2 = (String) y.eGet(f2);
			//
			// EObject z = leftProject.getModelElements().get(0);
			// EStructuralFeature f3 = z.eClass().getEStructuralFeature("name");
			// String v3 = (String) z.eGet(f3);

			try {
				// leftProject.update(new ESSystemOutProgressMonitor());
				leftProject.commit("LEFT", null, new ESSystemOutProgressMonitor());
			} catch (final ESUpdateRequiredException e) {

				leftProject.update(ESVersionSpec.FACTORY.createHEAD(), new ESUpdateCallback() {
					public void noChangesOnServer() {
						// do nothing if there are no changes on the server (in this example we know
						// there are changes anyway)
					}

					public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
						ESModelElementIdToEObjectMapping idToEObjectMapping) {
						// allow update to proceed, here we could also add some UI
						return true;
					}

					@SuppressWarnings("restriction")
					public boolean conflictOccurred(ESConflictSet changeConflictSet, IProgressMonitor monitor) {

						System.out.println("\n\nEMFStore Conflict size = " + changeConflictSet.getConflicts().size());
						System.out.println();
						int count = 0;
						for (ESConflict conflict : changeConflictSet.getConflicts()) {
							count++;
							Iterator<ESOperation> localIterator = conflict.getLocalOperations().iterator();
							Iterator<ESOperation> remoteIterator = conflict.getRemoteOperations().iterator();
							while (localIterator.hasNext() || remoteIterator.hasNext()) {
								AbstractOperation localOperation = null;
								AbstractOperation remoteOperation = null;
								if (localIterator.hasNext()) {
									localOperation = ((ESOperationImpl) localIterator.next())
										.toInternalAPI();
								}
								if (remoteIterator.hasNext()) {
									remoteOperation = ((ESOperationImpl) remoteIterator.next())
										.toInternalAPI();
								}
								String localString = operationToString(leftAdapater, localOperation);
								String remoteString = operationToString(leftAdapater, remoteOperation);
								System.out.println(count + ": " + localString + " <-> " + remoteString);
								System.console();
							}

							conflict.resolveConflict(conflict.getLocalOperations(), conflict.getRemoteOperations());
						}
						return true;
					}

				}, new ESSystemOutProgressMonitor());
			}

			leftProject.commit("LEFT", null, new ESSystemOutProgressMonitor());
			// rightProject.update(new ESSystemOutProgressMonitor());

			exportToXMI(leftAdapater, xmiResource);

			// System.out.println("\nCommit of merge result of demoProjectCopy");
			// demoProjectCopy.commit(new ESSystemOutProgressMonitor());
			//
			// // After having merged the two projects update local project 1
			// System.out.println("\nUpdate of demoProject");
			// demoProject.update(new NullProgressMonitor());
			//
			// // Finally we print the league and player names of both projects
			// System.out.println("\nLeague name in demoProject is now: " + league.getName());
			// System.out.println("\nLeague name in demoProjectCopy is now: " + leagueCopy.getName());
			// System.out.println("\nPlayer name in demoProject is now: " + league.getPlayers().get(0).getName());
			// System.out.println("\nPlayer name in demoProjectCopy is now: " +
			// leagueCopy.getPlayers().get(0).getName());
			System.out.println();
			System.out.println();
			System.out.println("SUCCESS!!");
		} catch (final FactoryConfigurationError ex) {
			ex.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void runPerformanceTest(ESServer server)
		throws ESException, IOException, FactoryConfigurationError, XMLStreamException {
		System.out.println("Client starting...");

		EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
		// EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(), JavaPackage.eINSTANCE);

		// The workspace is the core controller to access local and remote projects.
		// A project is a container for models and their elements (EObjects).
		// To get started, we obtain the current workspace of the client.
		final ESWorkspace workspace = ESWorkspaceProvider.INSTANCE.getWorkspace();

		// The workspace stores all available servers that have been configured. We add the local server that has
		// already
		// been started on the workspace.
		workspace.addServer(server);
		// Next, we remove all other existing servers
		for (final ESServer existingServer : workspace.getServers()) {
			if (existingServer != server) {
				try {
					workspace.removeServer(existingServer);
				} catch (final ESServerNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		// The workspace also contains a list of local projects that have either been created locally or checked out
		// from a server.
		// We create a new local project. The project new created is not yet shared with the server.

		// We delete all projects from the local workspace other than the one just created.
		for (final ESLocalProject existingLocalProject : workspace.getLocalProjects()) {
			try {
				existingLocalProject.delete(new ESSystemOutProgressMonitor());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		ESLocalProject originalProject = workspace.createLocalProject("OriginalProject");

		// Next, we create a user session by logging in to the local EMFStore server with default super user
		// credentials.
		final ESUsersession usersession = server.login("super", "super");

		// We also retrieve a list of existing (and accessible) remote projects on the server.
		// Remote projects represent a project that is currently available on the server.
		// We delete all remote projects to clean up remaining projects from previous launches.
		for (final ESRemoteProject existingRemoteProject : server.getRemoteProjects(usersession)) {
			existingRemoteProject.delete(usersession, new NullProgressMonitor());
		}

		// Now we can share the created local project to our server.
		final ESRemoteProject remoteDemoProject = originalProject.shareProject(usersession,
			new ESSystemOutProgressMonitor());

		ESLocalProject leftProject = originalProject.getRemoteProject().checkout("LeftProject",
			usersession, new ESSystemOutProgressMonitor());
		ESLocalProject rightProject = originalProject.getRemoteProject().checkout("RightProject",
			usersession, new ESSystemOutProgressMonitor());

		originalAdapater = new CBP2EMFStoreAdapter(originalProject);
		leftAdapater = new CBP2EMFStoreAdapter(leftProject);
		rightAdapater = new CBP2EMFStoreAdapter(rightProject);

		System.out.println("LOADING ORIGINAL MODEL");

		System.out.println("START: " + new Date().toString());

		// File outputFile = new File("D:\\TEMP\\FASE\\performance\\output.csv");
		File outputFile = new File("D:\\TEMP\\CONFLICTS\\performance\\output.csv");
		if (outputFile.exists()) {
			outputFile.delete();
		}
		PrintWriter writer = new PrintWriter(outputFile);

		// print header
		writer.println(
			"num,levc,revc,aoc,clt,clm,cdc,ctt,ctm,cdt,cdm,cct,ccm,lelc,relc,slt,slm,sdc,smt,smm,sdt,sdm,sct,scm");
		writer.flush();

		String originXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\origin.xmi";
		String leftXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\left.xmi";
		String rightXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\right.xmi";
		String originCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\origin.cbpxml";
		String leftCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\left.cbpxml";
		String rightCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\right.cbpxml";
		// String originXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\origin.xmi";
		// String leftXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\left.xmi";
		// String rightXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\right.xmi";
		// String originCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\origin.cbpxml";
		// String leftCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\left.cbpxml";
		// String rightCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\right.cbpxml";

		File leftXmiFile = new File(leftXmiPath);
		File rightXmiFile = new File(rightXmiPath);
		File originXmiFile = new File(originXmiPath);
		File originCbpFile = new File(originCbpPath);
		File leftCbpFile = new File(leftCbpPath);
		File rightCbpFile = new File(rightCbpPath);

		if (originCbpFile.exists()) {
			originCbpFile.delete();
		}
		if (leftCbpFile.exists()) {
			leftCbpFile.delete();
		}
		if (rightCbpFile.exists()) {
			rightCbpFile.delete();
		}

		XMIResource originXmi = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(originXmiPath));
		originXmi.load(options);

		CBPXMLResourceFactory cbpFactory = new CBPXMLResourceFactory();
		CBPXMLResourceImpl originCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(originCbpPath));
		originCbp.setIdType(IdType.NUMERIC, "O-");
		System.out.println("Creating origin cbp");
		originCbp.startNewSession("ORIGIN");
		originCbp.getContents().addAll(originXmi.getContents());
		originCbp.save(null);
		saveXmiWithID(originCbp, originXmiFile);
		originCbp.unload();
		originXmi.unload();

		System.out.println("Copying origin cbp to left and right cbps");
		Files.copy(originCbpFile, leftCbpFile);
		Files.copy(originCbpFile, rightCbpFile);

		ePackage = getEPackageFromFiles(leftCbpFile, rightCbpFile);

		CBPXMLResourceImpl leftCbp = (CBPXMLResourceImpl) cbpFactory
			.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
		CBPXMLResourceImpl rightCbp = (CBPXMLResourceImpl) cbpFactory
			.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
		leftCbp.setIdType(IdType.NUMERIC, "L-");
		rightCbp.setIdType(IdType.NUMERIC, "R-");

		// Start modifying the models
		List<ChangeType> seeds = new ArrayList<ChangeType>();
		setProbability(seeds, 30, ChangeType.CHANGE);
		setProbability(seeds, 1, ChangeType.ADD);
		setProbability(seeds, 1, ChangeType.DELETE);
		setProbability(seeds, 10, ChangeType.MOVE);

		System.out.println("Loading " + leftCbpFile.getName() + "...");
		leftCbp.load(null);
		leftCbp.startNewSession("LEFT");
		System.out.println("Loading " + rightCbpFile.getName() + "...");
		rightCbp.load(null);
		rightCbp.startNewSession("RIGHT");

		List<EObject> leftEObjectList = identifyAllEObjects(leftCbp);
		List<EObject> rightEObjectList = identifyAllEObjects(rightCbp);

		// ----------------EMF STORE
		originalAdapater.load(originCbpFile);
		originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());
		leftProject.update(new ESSystemOutProgressMonitor());

		long prevLeftCbpSize = leftCbpFile.length();
		long prevRightCbpSize = rightCbpFile.length();
		// -----

		List<BigModelResult> results = new ArrayList<BigModelResult>();
		int modificationCount = 1000;
		int number = 0;
		for (int i = 1; i <= modificationCount; i++) {
			System.out.print("Change " + i + ":");

			startModification(leftCbp, leftEObjectList, seeds);
			leftCbp.save(null);
			startModification(rightCbp, rightEObjectList, seeds);
			rightCbp.save(null);
			System.out.println();

			// do comparison
			if (i % 100 == 0) {
				// if (i % modificationCount == 0) {
				number++;

				BigModelResult result = new BigModelResult();

				System.out.println("Reload from the Cbps ...");
				CBPXMLResourceImpl leftCbp2 = (CBPXMLResourceImpl) cbpFactory
					.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
				CBPXMLResourceImpl rightCbp2 = (CBPXMLResourceImpl) cbpFactory
					.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
				leftCbp2.load(null);
				rightCbp2.load(null);
				System.out.println("Saving changes to Xmis ...");
				XMIResource leftXmi = saveXmiWithID(leftCbp2, leftXmiFile);
				XMIResource rightXmi = saveXmiWithID(rightCbp2, rightXmiFile);
				leftCbp2.unload();
				rightCbp2.unload();

				// ------------CBP
				System.out.println("\nCBP:");
				ICBPComparison changeComparison = new CBPComparisonImpl();
				changeComparison.setDiffEMFCompareFile(
					new File(originCbpFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
				changeComparison.setObjectTreeFile(
					new File(originCbpFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
				changeComparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
				changeComparison.compare(leftCbpFile, rightCbpFile, originCbpFile);

				// ---------------

				result.setNumber(number);
				writer.print(result.getNumber());
				writer.print(",");
				result.setLeftEventCount(changeComparison.getLeftEvents().size());
				writer.print(result.getLeftEventCount());
				writer.print(",");
				result.setRightEventCount(changeComparison.getRightEvents().size());
				writer.print(result.getRightEventCount());
				writer.print(",");
				result.setAffectedObjectCount(changeComparison.getObjectTree().size());
				writer.print(result.getAffectedObjectCount());
				writer.print(",");
				result.setChangeLoadTime(changeComparison.getLoadTime());
				writer.print(result.getChangeLoadTime());
				writer.print(",");
				result.setChangeLoadMemory(changeComparison.getLoadMemory());
				writer.print(result.getChangeLoadMemory());
				writer.print(",");
				result.setChangeDiffCount(changeComparison.getDiffCount());
				writer.print(result.getChangeDiffCount());
				writer.print(",");
				result.setChangeTreeTime(changeComparison.getObjectTreeConstructionTime());
				writer.print(result.getChangeTreeTime());
				writer.print(",");
				result.setChangeTreeMemory(changeComparison.getObjectTreeConstructionMemory());
				writer.print(result.getChangeTreeMemory());
				writer.print(",");
				result.setChangeDiffTime(changeComparison.getDiffTime());
				writer.print(result.getChangeDiffTime());
				writer.print(",");
				result.setChangeDiffMemory(changeComparison.getDiffMemory());
				writer.print(result.getChangeDiffMemory());
				writer.print(",");
				result.setChangeComparisonTime(changeComparison.getComparisonTime());
				writer.print(result.getChangeComparisonTime());
				writer.print(",");
				result.setChangeComparisonMemory(changeComparison.getComparisonMemory());
				writer.print(result.getChangeComparisonMemory());
				writer.print(",");

				// --------------EMF COMPARE
				System.out.println("\nEMF COMPARE:");

				leftXmi.unload();
				rightXmi.unload();

				long startTime = 0;
				long endTime = 0;
				long startMemory = 0;
				long endMemory = 0;

				System.out.println("Loading xmis ...");
				System.gc();
				startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				startTime = System.nanoTime();
				leftXmi.load(options);
				rightXmi.load(options);
				endTime = System.nanoTime();
				System.gc();
				endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

				ModifiedEMFCompare stateComparison = doEMFComparison(leftXmi, rightXmi, originXmi);

				// ----------------EMF STORE
				// leftProject.update(new ESSystemOutProgressMonitor());

				System.out.println("\nEMF STORE:");

				// process right file first
				System.out.println("laoding right model");
				String rightText = readFiles(rightCbpFile, prevRightCbpSize);
				InputStream rightStream = new ByteArrayInputStream(rightText.getBytes());
				originalAdapater.load(rightStream);
				rightStream.close();
				originalProject.commit("RIGHT", null, new ESSystemOutProgressMonitor());

				// process left
				System.out.println("loading left model");
				String leftText = readFiles(leftCbpFile, prevLeftCbpSize);
				InputStream leftStream = new ByteArrayInputStream(leftText.getBytes());
				leftAdapater.load(leftStream);
				leftStream.close();

				try {
					// leftProject.update(new ESSystemOutProgressMonitor());
					leftProject.commit("LEFT", null, new ESSystemOutProgressMonitor());
				} catch (final ESUpdateRequiredException e) {
					isConflict = false;

					System.gc();
					final long emfsStartMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					final long emfsStartTime = System.nanoTime();

					try {
						leftProject.update(ESVersionSpec.FACTORY.createHEAD(), new ESUpdateCallback() {
							public void noChangesOnServer() {
								// do nothing if there are no changes on the server (in this example we know
								// there are changes anyway)
							}

							public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
								ESModelElementIdToEObjectMapping idToEObjectMapping) {
								// allow update to proceed, here we could also add some UI
								return true;
							}

							@SuppressWarnings("restriction")
							public boolean conflictOccurred(ESConflictSet changeConflictSet, IProgressMonitor monitor) {

								isConflict = true;

								long emfsEndTime = System.nanoTime();
								// System.gc();
								long emfsEndMemory = Runtime.getRuntime().totalMemory()
									- Runtime.getRuntime().freeMemory();
								System.out
									.println(
										"\nEMFS comparison time = "
											+ df.format((emfsEndTime - emfsStartTime) / 1000000.0)
											+ " ms ");
								System.out.println(
									"EMFS comparison memory = "
										+ df.format((emfsEndMemory - emfsStartMemory) / 1000000.0)
										+ " MBs");

								System.out
									.println("\n\nEMFStore Conflict size = " + changeConflictSet.getConflicts().size());
								// System.out.println();
								// int count = 0;
								for (ESConflict conflict : changeConflictSet.getConflicts()) {
									// count++;
									// Iterator<ESOperation> localIterator = conflict.getLocalOperations().iterator();
									// Iterator<ESOperation> remoteIterator = conflict.getRemoteOperations().iterator();
									// while (localIterator.hasNext() || remoteIterator.hasNext()) {
									// AbstractOperation localOperation = null;
									// AbstractOperation remoteOperation = null;
									// if (localIterator.hasNext()) {
									// localOperation = ((ESOperationImpl) localIterator.next())
									// .toInternalAPI();
									// }
									// if (remoteIterator.hasNext()) {
									// remoteOperation = ((ESOperationImpl) remoteIterator.next())
									// .toInternalAPI();
									// }
									// String localString = operationToString(leftAdapater, localOperation);
									// String remoteString = operationToString(leftAdapater, remoteOperation);
									// System.out.println(count + ": " + localString + " <-> " + remoteString);
									// System.console();
									// }
									//
									conflict.resolveConflict(conflict.getLocalOperations(),
										conflict.getRemoteOperations());
								}
								return false;
							}
						}, new ESSystemOutProgressMonitor());
					} catch (Exception exe) {
					}

					if (isConflict == false) {
						long emfsEndTime = System.nanoTime();
						System.gc();
						long emfsEndMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
						System.out
							.println(
								"\nEMFS comparison time = " + df.format((emfsEndTime - emfsStartTime) / 1000000.0)
									+ " ms ");
						System.out.println(
							"EFMS comparison memory = " + df.format((emfsEndMemory - emfsStartMemory) / 1000000.0)
								+ " MBs");
					}
				}

				// -------------------
				result.setLeftElementCount(leftXmi.getEObjectToIDMap().size());
				writer.print(result.getLeftElementCount());
				writer.print(",");
				result.setRightElementCount(rightXmi.getEObjectToIDMap().size());
				writer.print(result.getRightElementCount());
				writer.print(",");
				result.setStateLoadTime(endTime - startTime);
				writer.print(result.getStateLoadTime());
				writer.print(",");
				result.setStateLoadMemory(endMemory - startMemory);
				writer.print(result.getStateLoadMemory());
				writer.print(",");
				result.setStateDiffCount(stateComparison.getDiffs().size());
				writer.print(result.getStateDiffCount());
				writer.print(",");
				result.setStateMatchTime(stateComparison.getMatchTime());
				writer.print(result.getStateMatchTime());
				writer.print(",");
				result.setStateMatchMemory(stateComparison.getMatchMemory());
				writer.print(result.getStateMatchMemory());
				writer.print(",");
				result.setStateDiffTime(stateComparison.getDiffTime());
				writer.print(result.getStateDiffTime());
				writer.print(",");
				result.setStateDiffMemory(stateComparison.getDiffMemory());
				writer.print(result.getStateDiffMemory());
				writer.print(",");
				result.setStateComparisonTime(stateComparison.getComparisonTime());
				writer.print(result.getStateComparisonTime());
				writer.print(",");
				result.setStateComparisonMemory(stateComparison.getComparisonMemory());
				writer.print(result.getStateComparisonMemory());
				writer.println();
				writer.flush();

				results.add(result);

				leftXmi.unload();
				rightXmi.unload();

				System.out.println();

				prevLeftCbpSize = leftCbpFile.length();
				prevRightCbpSize = rightCbpFile.length();
			}

		}
		writer.close();

		System.out.println("FINISHED! " + new Date().toString());

		// originalProject.getModelElements().add(root);

		// originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());

		System.out.println();
		System.out.println();
		System.out.println("SUCCESS!!");

	}

	public static void runBatchClient(ESServer server) throws Exception {
		System.out.println("Client starting...");

		EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
		// EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(), JavaPackage.eINSTANCE);

		// The workspace is the core controller to access local and remote projects.
		// A project is a container for models and their elements (EObjects).
		// To get started, we obtain the current workspace of the client.
		final ESWorkspace workspace = ESWorkspaceProvider.INSTANCE.getWorkspace();

		// The workspace stores all available servers that have been configured. We add the local server that has
		// already
		// been started on the workspace.
		workspace.addServer(server);
		// Next, we remove all other existing servers
		for (final ESServer existingServer : workspace.getServers()) {
			if (existingServer != server) {
				try {
					workspace.removeServer(existingServer);
				} catch (final ESServerNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		// The workspace also contains a list of local projects that have either been created locally or checked out
		// from a server.
		// We create a new local project. The project new created is not yet shared with the server.

		// We delete all projects from the local workspace other than the one just created.
		for (final ESLocalProject existingLocalProject : workspace.getLocalProjects()) {
			try {
				existingLocalProject.delete(new ESSystemOutProgressMonitor());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		ESLocalProject originalProject = workspace.createLocalProject("OriginalProject");

		// Next, we create a user session by logging in to the local EMFStore server with default super user
		// credentials.
		final ESUsersession usersession = server.login("super", "super");

		// We also retrieve a list of existing (and accessible) remote projects on the server.
		// Remote projects represent a project that is currently available on the server.
		// We delete all remote projects to clean up remaining projects from previous launches.
		for (final ESRemoteProject existingRemoteProject : server.getRemoteProjects(usersession)) {
			existingRemoteProject.delete(usersession, new NullProgressMonitor());
		}

		// Now we can share the created local project to our server.
		final ESRemoteProject remoteDemoProject = originalProject.shareProject(usersession,
			new ESSystemOutProgressMonitor());

		// Now we are all set: we have a client workspace with one server configured and exactly one project shared to a
		// server with only this one project.

		// We check out a second, independent copy of the project (simulating a second client).
		ESLocalProject leftProject = originalProject.getRemoteProject().checkout("LeftProject",
			usersession, new ESSystemOutProgressMonitor());
		ESLocalProject rightProject = originalProject.getRemoteProject().checkout("RightProject",
			usersession, new ESSystemOutProgressMonitor());

		originalAdapater = new CBP2EMFStoreAdapter(originalProject);
		leftAdapater = new CBP2EMFStoreAdapter(leftProject);
		rightAdapater = new CBP2EMFStoreAdapter(rightProject);

		File cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.cbpxml");
		File cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.cbpxml");
		File cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.cbpxml");
		File emfsXmiOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-origin.xmi");
		File emfsXmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-left.xmi");
		File emfsXmiRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-right.xmi");
		File emfstoreXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-target.xmi");
		File changeXmiFile = new File("D:\\TEMP\\CONFLICTS\\Debug\\change-target.xmi");

		// File cbpOriginalFile = new File("D:\\TEMP\\FASE\\Debug\\origin.cbpxml");
		// File cbpLeftFile = new File("D:\\TEMP\\FASE\\Debug\\left.cbpxml");
		// File cbpRightFile = new File("D:\\TEMP\\FASE\\Debug\\right.cbpxml");
		// File emfsXmiOriginalFile = new File("D:\\TEMP\\FASE\\Debug\\emfstore-origin.xmi");
		// File emfsXmiLeftFile = new File("D:\\TEMP\\FASE\\Debug\\emfstore-left.xmi");
		// File emfsXmiRightFile = new File("D:\\TEMP\\FASE\\Debug\\emfstore-right.xmi");
		// File xmiFile = new File("D:\\TEMP\\FASE\\Debug\\emfstore-target.xmi");

		XMIResource xmiResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfstoreXmiFile.getAbsolutePath()));

		XMIResource emfsXmiOriginalResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsXmiOriginalFile.getAbsolutePath()));
		XMIResource emfsXmiLeftResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsXmiLeftFile.getAbsolutePath()));
		XMIResource emfsXmiRightResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsXmiRightFile.getAbsolutePath()));

		long skip = cbpOriginalFile.length();

		try {
			System.out.println("LOADING ORIGINAL MODEL");
			originalAdapater.load(cbpOriginalFile);
			originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());
			exportToXMI(originalAdapater, emfsXmiOriginalResource);
			rightProject.update(new ESSystemOutProgressMonitor());
			leftProject.update(new ESSystemOutProgressMonitor());

			// exportToXMI(originalProject, emfsXmiOriginalResource);

			// process right file first
			System.out.println("\nLOADING RIGHT MODEL");
			String rightText = readFiles(cbpRightFile, skip);
			InputStream rightStream = new ByteArrayInputStream(rightText.getBytes());
			rightAdapater.load(rightStream);
			rightStream.close();
			exportToXMI(rightAdapater, emfsXmiRightResource);
			rightProject.commit("RIGHT", null, new ESSystemOutProgressMonitor());
			// EObject rightObject = rightProject.getModelElement(originalEsId);
			// ESModelElementId rightEsId = rightProject.getModelElementId(rightObject);

			// process left
			System.out.println("\nLOADING LEFT MODEL");
			String leftText = readFiles(cbpLeftFile, skip);
			InputStream leftStream = new ByteArrayInputStream(leftText.getBytes());
			leftAdapater.load(leftStream);
			leftStream.close();
			exportToXMI(leftAdapater, emfsXmiLeftResource);

			// EObject leftObject = sleftProject.getModelElement(originalEsId);
			// ESModelElementId leftEsId = leftProject.getModelElementId(leftObject);

			// EObject x = originalProject.getModelElements().get(0);
			// EStructuralFeature f1 = x.eClass().getEStructuralFeature("name");
			// String v1 = (String) x.eGet(f1);
			//
			// EObject y = rightProject.getModelElements().get(0);
			// EStructuralFeature f2 = y.eClass().getEStructuralFeature("name");
			// String v2 = (String) y.eGet(f2);
			//
			// EObject z = leftProject.getModelElements().get(0);
			// EStructuralFeature f3 = z.eClass().getEStructuralFeature("name");
			// String v3 = (String) z.eGet(f3);

			try {
				// leftProject.update(new ESSystemOutProgressMonitor());
				leftProject.commit("LEFT", null, new ESSystemOutProgressMonitor());
			} catch (final ESUpdateRequiredException e) {

				leftProject.update(ESVersionSpec.FACTORY.createHEAD(), new ESUpdateCallback() {
					public void noChangesOnServer() {
						// do nothing if there are no changes on the server (in this example we know
						// there are changes anyway)
					}

					public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
						ESModelElementIdToEObjectMapping idToEObjectMapping) {
						// allow update to proceed, here we could also add some UI
						return true;
					}

					@SuppressWarnings("restriction")
					public boolean conflictOccurred(ESConflictSet changeConflictSet, IProgressMonitor monitor) {

						System.out.println("\n\nEMFStore Conflict size = " + changeConflictSet.getConflicts().size());
						System.out.println();
						int count = 1;
						for (ESConflict conflict : changeConflictSet.getConflicts()) {
							// count++;
							Iterator<ESOperation> localIterator = conflict.getLocalOperations().iterator();
							Iterator<ESOperation> remoteIterator = conflict.getRemoteOperations().iterator();
							boolean printed = false;
							while (localIterator.hasNext() || remoteIterator.hasNext()) {
								AbstractOperation localOperation = null;
								AbstractOperation remoteOperation = null;
								if (localIterator.hasNext()) {
									localOperation = ((ESOperationImpl) localIterator.next())
										.toInternalAPI();
								}
								if (remoteIterator.hasNext()) {
									remoteOperation = ((ESOperationImpl) remoteIterator.next())
										.toInternalAPI();
								}
								String localString = operationToString(leftAdapater, localOperation);
								String remoteString = operationToString(leftAdapater, remoteOperation);
								if (localString == remoteString || localString.equals(remoteString)) {
									// count = count - 1;
									// continue;
								}
								printed = true;
								System.out.println(count + ": " + localString + " <-> " + remoteString);
								System.console();
							}
							if (printed) {
								count++;
								printed = false;
							}
							conflict.resolveConflict(conflict.getLocalOperations(), conflict.getRemoteOperations());
						}
						return true;
					}

				}, new ESSystemOutProgressMonitor());
			}

			leftProject.commit("LEFT", null, new ESSystemOutProgressMonitor());
			// rightProject.update(new ESSystemOutProgressMonitor());

			exportToXMI(leftAdapater, xmiResource);
			xmiResource.unload();

			XMIResource changeXmiResource = (XMIResource) new XMIResourceFactoryImpl()
				.createResource(URI.createFileURI(changeXmiFile.getAbsolutePath()));
			changeXmiResource.load(null);

			XMIResource emfstoreXmiResource = (XMIResource) new XMIResourceFactoryImpl()
				.createResource(URI.createFileURI(emfstoreXmiFile.getAbsolutePath()));
			emfstoreXmiResource.load(null);

			// System.out.println("SORTING CBP XMI ...");
			// sortResourceElements(changeXmiResource);
			// changeXmiResource.save(options);
			System.out.println("SORTING EMFS XMI ...");
			CBPComparisonUtil.sort(emfstoreXmiResource);
			// sortResourceElements(emfstoreXmiResource);
			emfstoreXmiResource.save(options);
			compareCBPvsXMITargets(changeXmiResource, emfstoreXmiResource);

			System.out.println("SUCCESS!!");
		} catch (final FactoryConfigurationError ex) {
			ex.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param project
	 * @param xmiResource
	 * @throws IOException
	 */
	private static void exportToXMI(CBP2EMFStoreAdapter adapater, XMIResource xmiResource) throws IOException {
		ESLocalProject project = adapater.getLocalProject();
		Iterator<EObject> iterator2 = project.getAllModelElements().iterator();
		int x = 0;
		Set<EObject> withoutIdItems = new HashSet<EObject>();
		while (iterator2.hasNext()) {

			EObject obj2 = iterator2.next();
			String esId = ((ESModelElementIdImpl) project.getModelElementId(obj2)).getId();
			String id = adapater.getEsId2IdMap().get(esId);

			if (id == null || id.equals("") || id.contains("/")) {
				id = Application.getOriginalAdapater().getEsId2IdMap().get(esId);
			}
			if (id == null) {
				id = adapater.geteObject2IdMap().get(obj2);
			}
			if (id == null) {
				id = Application.getOriginalAdapater().geteObject2IdMap().get(obj2);
			}

			if (id == null || id.equals("") || id.contains("/")) {
				id = Application.getRightAdapater().getEsId2IdMap().get(esId);
			}
			if (id == null) {
				id = Application.getRightAdapater().geteObject2IdMap().get(obj2);
			}

			if (id == null || id.trim().equals("") || id.contains("/")) {
				id = "X-" + x;
				withoutIdItems.add(obj2);
			}
			adapater.geteObject2IdMap().put(obj2, id);
			System.console();
			x++;
		}
		for (EObject obj : withoutIdItems) {
			EcoreUtil.delete(obj);
		}
		withoutIdItems.clear();

		EObject modelElements = project.getModelElements().get(0);
		// Collection<EObject> modelElements = project.getModelElements().get(0).eResource().getContents();
		xmiResource.getContents().add(modelElements);
		TreeIterator<EObject> iterator1 = xmiResource.getAllContents();
		int z = 0;
		while (iterator1.hasNext()) {
			EObject obj1 = iterator1.next();
			String id = adapater.geteObject2IdMap().get(obj1);
			if (id == null) {
				id = Application.getOriginalAdapater().geteObject2IdMap().get(obj1);
			}
			if (id == null) {
				id = "Z-" + z;
			}

			xmiResource.setID(obj1, id);
			z++;
		}
		xmiResource.save(null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
	}

	protected static String readFiles(File file, long skip) throws IOException {

		FileInputStream fis = new FileInputStream(file);
		fis.skip(skip);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader bufferedReader = new BufferedReader(isr);
		// bufferedReader.skip(skip);
		bufferedReader.mark(0);
		bufferedReader.reset();
		String text = bufferedReader.lines().collect(Collectors.joining());
		bufferedReader.close();
		return text;
	}

	@SuppressWarnings("restriction")
	private static String operationToString(CBP2EMFStoreAdapter adapter, AbstractOperation operation) {
		if (operation == null) {
			return "";
		}

		String result = null;
		String targetEsId = operation.getModelElementId().getId();
		String target = adapter.getEsId2IdMap()
			.get(targetEsId);
		target = getId(targetEsId);
		// CompositeOperationImpl, CreateDeleteOperationImpl, AttributeOperationImpl,
		// MultiAttributeMoveOperationImpl, MultiAttributeOperationImpl,
		// MultiAttributeSetOperationImpl, MultiReferenceMoveOperationImpl, ReferenceOperationImpl
		if (operation instanceof CompositeOperationImpl) {
			int count = 0;
			result = "";
			for (AbstractOperation op : operation.getLeafOperations()) {
				count++;
				if (count > 1) {
					result += System.lineSeparator() + operationToString(adapter, op);
				} else {
					result += operationToString(adapter, op);
				}
			}
		} else if (operation instanceof CreateDeleteOperation
			&& ((CreateDeleteOperation) operation).isDelete()) {
			String className = ((CreateDeleteOperation) operation).getModelElement().eClass().getName();
			String esId = ((CreateDeleteOperation) operation).getModelElementId().getId();
			String value = getId(esId);
			result = "DELETE " + value + " TYPE " + className;
		} else if (operation instanceof CreateDeleteOperation
			&& !((CreateDeleteOperation) operation).isDelete()) {
			String className = ((CreateDeleteOperation) operation).getModelElement().eClass().getName();
			String esId = ((CreateDeleteOperation) operation).getModelElementId().getId();
			String value = getId(esId);
			result = "CREATE " + value + " TYPE " + className;
		} else if (operation instanceof AttributeOperation
			&& (((AttributeOperation) operation).getUnset() == UnsetType.NONE
				|| ((AttributeOperation) operation).getUnset() == UnsetType.WAS_UNSET)) {
			String feature = ((AttributeOperation) operation).getFeatureName();
			Object oldValue = ((AttributeOperation) operation).getOldValue();
			if (oldValue instanceof String) {
				oldValue = "\"" + oldValue + "\"";
			}
			Object newValue = ((AttributeOperation) operation).getNewValue();
			if (newValue instanceof String) {
				newValue = "\"" + newValue + "\"";
			}
			if (newValue == null) {
				result = "UNSET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
			} else {
				result = "SET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
			}
		} else if (operation instanceof AttributeOperation
			&& ((AttributeOperation) operation).getUnset() == UnsetType.IS_UNSET) {
			String feature = ((AttributeOperation) operation).getFeatureName();
			Object oldValue = ((AttributeOperation) operation).getOldValue();
			if (oldValue instanceof String) {
				oldValue = "\"" + oldValue + "\"";
			}
			Object newValue = ((AttributeOperation) operation).getNewValue();
			if (newValue instanceof String) {
				newValue = "\"" + newValue + "\"";
			}
			result = "UNSET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
		} else if (operation instanceof MultiAttributeOperation && ((MultiAttributeOperation) operation).isAdd()) {
			String feature = ((MultiAttributeOperation) operation).getFeatureName();
			Object value = ((MultiAttributeOperation) operation).getReferencedValues().get(0);
			int index = ((MultiAttributeOperation) operation).getIndexes().get(0);
			result = "ADD " + value + " TO " + target + "." + feature + " AT " + index;
		} else if (operation instanceof MultiAttributeOperation && !((MultiAttributeOperation) operation).isAdd()) {
			String feature = ((MultiAttributeOperation) operation).getFeatureName();
			Object value = ((MultiAttributeOperation) operation).getReferencedValues().get(0);
			int index = ((MultiAttributeOperation) operation).getIndexes().get(0);
			result = "REMOVE " + value + " FROM " + target + "." + feature + " AT " + index;
		} else if (operation instanceof MultiAttributeMoveOperation) {
			String feature = ((MultiAttributeMoveOperation) operation).getFeatureName();
			Object value = ((MultiAttributeMoveOperation) operation).getReferencedValue();
			int oldIndex = ((MultiAttributeMoveOperation) operation).getOldIndex();
			int newIndex = ((MultiAttributeMoveOperation) operation).getNewIndex();
			result = "MOVE " + value + " IN " + target + "." + feature + " FROM " + oldIndex
				+ " TO "
				+ newIndex;
		} else if (operation instanceof MultiReferenceMoveOperation) {
			String feature = ((MultiReferenceMoveOperation) operation).getFeatureName();
			String esId = ((MultiReferenceMoveOperation) operation)
				.getReferencedModelElementId().getId();
			String value = getId(esId);
			int oldIndex = ((MultiReferenceMoveOperation) operation).getOldIndex();
			int newIndex = ((MultiReferenceMoveOperation) operation).getNewIndex();
			result = "MOVE " + value + " IN " + target + "." + feature + " FROM " + oldIndex
				+ " TO " + newIndex;
		} else if (operation instanceof SingleReferenceOperation) {
			String feature = ((SingleReferenceOperation) operation).getFeatureName();
			String esOldId = null;
			if (((SingleReferenceOperation) operation).getOldValue() != null) {
				esOldId = ((SingleReferenceOperation) operation).getOldValue().getId();
			}
			String oldValue = getId(esOldId);
			String esNewId = null;
			if (((SingleReferenceOperation) operation).getNewValue() != null) {
				esNewId = ((SingleReferenceOperation) operation).getNewValue().getId();
			}
			String newValue = getId(esNewId);
			if (newValue != null) {
				result = "SET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
			} else {
				result = "UNSET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
			}
		} else if (operation instanceof MultiReferenceOperation && ((MultiReferenceOperation) operation).isAdd()) {
			String feature = ((MultiReferenceOperation) operation).getFeatureName();
			String esId = ((MultiReferenceOperation) operation).getReferencedModelElements().get(0).getId();
			String value = getId(esId);
			int index = ((MultiReferenceOperation) operation).getIndex();
			result = "ADD " + value + " TO " + target + "." + feature + " AT " + index;
		} else if (operation instanceof MultiReferenceOperation && !((MultiReferenceOperation) operation).isAdd()) {
			String feature = ((MultiReferenceOperation) operation).getFeatureName();
			String esId = ((MultiReferenceOperation) operation).getReferencedModelElements().get(0).getId();
			String value = getId(esId);
			int index = ((MultiReferenceOperation) operation).getIndex();
			result = "REMOVE " + value + " FROM " + target + "." + feature + " AT " + index;
		} else {
			System.out.println(operation);
		}

		return result;
	}

	/**
	 * @param targetId
	 * @param target
	 * @return
	 */
	private static String getId(String targetId) {
		String target = originalAdapater.getEsId2IdMap().get(targetId);
		if (target == null) {
			target = leftAdapater.getEsId2IdMap().get(targetId);
			if (target == null) {
				target = rightAdapater.getEsId2IdMap().get(targetId);
			}

		}
		return target;
	}

	@SuppressWarnings("unused")
	private void doThreeWayComparison(XMIResource leftXmi, XMIResource rightXmi, XMIResource originXmi)
		throws FileNotFoundException, IOException, Exception {
		IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
		matchEngineFactory.setRanking(100);
		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
		matchEngineRegistry.add(matchEngineFactory);

		IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
		BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
			Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
		postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);

		Builder builder = EMFCompare.builder();
		builder.setPostProcessorRegistry(postProcessorRegistry);
		builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
		EMFCompare comparator = builder.build();

		IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, originXmi);
		Comparison emfComparison = comparator.compare(scope);
		EList<Diff> evalDiffs = emfComparison.getDifferences();

		printEMFCompareDiffs(leftXmi, rightXmi, evalDiffs);
	}

	static private List<String> printEMFCompareDiffs(Resource left, Resource right, EList<Diff> diffs)
		throws FileNotFoundException, IOException {
		Set<String> set = new HashSet<String>();
		for (Diff diff : diffs) {
			String feature = null;
			String id = null;
			String value = null;

			if (diff.getKind() == DifferenceKind.MOVE) {
				continue;
			}

			if (diff.getMatch().getLeft() != null) {
				id = left.getURIFragment(diff.getMatch().getLeft());
			} else {
				id = right.getURIFragment(diff.getMatch().getRight());
			}

			if (diff instanceof AttributeChange) {
				feature = ((AttributeChange) diff).getAttribute().getName();
				value = String.valueOf(((AttributeChange) diff).getValue());
			} else if (diff instanceof ReferenceChange) {
				feature = ((ReferenceChange) diff).getReference().getName();
				EObject eObject = ((ReferenceChange) diff).getValue();
				value = left.getURIFragment(eObject);
				if (value == null || "/-1".equals(value)) {
					value = right.getURIFragment(eObject);
				}
			} else if (diff instanceof MultiplicityElementChange) {
				continue;
			} else if (diff instanceof ResourceAttachmentChange) {
				feature = "resource";
				value = new String(id);
				id = new String(feature);
			} else if (diff instanceof AssociationChange) {
				continue;
			} else if (diff instanceof DirectedRelationshipChange) {
				continue;
			} else {
				System.out.println("UNHANDLED DIFF: " + diff.getClass().getName());
			}

			String x = id + "." + feature + "." + value + "." + diff.getKind();
			set.add(x.trim());
		}
		// System.out.println("Before Merge Diffs: " + diffs.size());

		List<String> list = new ArrayList<String>(set);
		// Collections.sort(list);

		// System.out.println("\nEXPORT FOR COMPARISON WITH CBP:");
		for (String item : list) {
			System.out.println(item);
		}
		System.out.println("Diffs Size: " + list.size());
		return list;
	}

	@SuppressWarnings("restriction")
	static private EList<Diff> compareCBPvsXMITargets(XMIResource leftXmi, XMIResource rightXmi)
		throws FileNotFoundException, IOException, Exception {
		System.out.println("\nComparing CBP vs EMFStore Targets");

		IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
		matchEngineFactory.setRanking(100);
		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
		matchEngineRegistry.add(matchEngineFactory);

		IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
		BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
			Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
		postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);

		Builder builder = EMFCompare.builder();
		builder.setPostProcessorRegistry(postProcessorRegistry);
		builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
		EMFCompare comparator = builder.build();

		IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, null);
		Comparison emfComparison = comparator.compare(scope);
		EList<Diff> evalDiffs = emfComparison.getDifferences();

		printEMFCompareDiffs(leftXmi, rightXmi, evalDiffs);

		return evalDiffs;
	}

	static public void sortResourceElements(Resource resource) {

		TreeIterator<EObject> iterator = resource.getAllContents();
		Map<EObject, String> eObject2IdMap = new HashMap();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			String id = resource.getURIFragment(eObject);
			eObject2IdMap.put(eObject, id);
		}

		iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			String id = resource.getURIFragment(eObject);
			// System.out.println("EObject: " + id);
			// System.console();
			for (EReference eReference : eObject.eClass().getEAllReferences()) {
				if (eReference.isMany() && eReference.isChangeable()) {
					EList<EObject> list = (EList<EObject>) eObject.eGet(eReference);
					if (list != null && list.size() > 0) {
						// ECollections.sort(list);
						ECollections.sort(list, new EObjectComparator());
					}
				}
			}
		}

		iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			String id = eObject2IdMap.get(eObject);
			((XMIResource) resource).setID(eObject, id);
		}
	}

	private static XMIResource saveXmiWithID(CBPXMLResourceImpl cbp, File xmiFile) throws IOException {
		XMIResource xmi = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
		xmi.getContents().addAll(EcoreUtil.copyAll(cbp.getContents()));
		TreeIterator<EObject> cbpIterator = cbp.getAllContents();
		TreeIterator<EObject> xmiIterator = xmi.getAllContents();

		while (cbpIterator.hasNext() && xmiIterator.hasNext()) {
			EObject cbpEObject = cbpIterator.next();
			String id = cbp.getURIFragment(cbpEObject);
			EObject xmiEObject = xmiIterator.next();
			xmi.setID(xmiEObject, id);
		}
		xmi.save(options);
		// xmi.unload();
		return xmi;
	}

	private static EPackage getEPackageFromFiles(File leftFile, File rightFile)
		throws IOException, FactoryConfigurationError, XMLStreamException {
		BufferedReader reader = new BufferedReader(new FileReader(leftFile));
		String line = null;
		String eventString = null;
		// try to read ePackage from left file
		while ((line = reader.readLine()) != null) {
			if (line.contains("epackage=")) {
				eventString = line;
				reader.close();
				break;
			}
		}

		// if line is still null then try from right file
		if (eventString == null) {
			reader = new BufferedReader(new FileReader(rightFile));
			while ((line = reader.readLine()) != null) {
				if (line.contains("epackage=")) {
					eventString = line;
					reader.close();
					break;
				}
			}
		}
		String nsUri = null;
		XMLEventReader xmlReader = xmlInputFactory
			.createXMLEventReader(new ByteArrayInputStream(eventString.getBytes()));
		while (xmlReader.hasNext()) {
			XMLEvent xmlEvent = xmlReader.nextEvent();
			if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
				StartElement e = xmlEvent.asStartElement();
				String name = e.getName().getLocalPart();
				if (name.equals("register") || name.equals("create")) {
					nsUri = e.getAttributeByName(new QName("epackage")).getValue();
					xmlReader.close();
					break;
				}
			}
		}

		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsUri);
		return ePackage;
	}

	private static ModifiedEMFCompare doEMFComparison(Resource left, Resource right, Resource origin)
		throws FileNotFoundException, IOException {
		IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
		matchEngineFactory.setRanking(100);
		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
		matchEngineRegistry.add(matchEngineFactory);

		IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
		BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
			Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
		postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);

		ModifiedBuilder builder = ModifiedEMFCompare.modifiedBuilder();
		builder.setPostProcessorRegistry(postProcessorRegistry);
		builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
		ModifiedEMFCompare comparator = (ModifiedEMFCompare) builder.build();

		// System.out.println("Compare " + cbp.getURI().lastSegment() + " and "
		// + xmi.getURI().lastSegment());
		IComparisonScope2 scope = new DefaultComparisonScope(left, right, origin);
		System.out.println("Start comparison ...");
		Comparison comparison = comparator.compare(scope);
		EList<Diff> diffs = comparison.getDifferences();

		System.out.println("Matching Time = " + comparator.getMatchTime() / 1000000.0 + " ms");
		System.out.println("Diffing Time = " + comparator.getDiffTime() / 1000000.0 + " ms");
		System.out.println("Comparison Time = " + comparator.getComparisonTime() / 1000000.0 + " ms");
		System.out.println("State-based Diffs Size = " + diffs.size());

		return comparator;
	}

	private static List<EObject> identifyAllEObjects(CBPXMLResourceImpl cbp) {
		List<EObject> eObjectList = new ArrayList<EObject>();
		// int objectCount = originXmi.getEObjectToIDMap().size();
		TreeIterator<EObject> iterator = cbp.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			// String id = originXmi.getURIFragment(eObject);
			EClass eClass = eObject.eClass();
			eObjectList.add(eObject);
		}
		return eObjectList;
	}

	private static void startModification(CBPXMLResourceImpl cbp, List<EObject> eObjectList, List<ChangeType> seeds) {
		ChangeType changeType = seeds.get(random.nextInt(seeds.size()));
		if (changeType == ChangeType.CHANGE) {
			System.out.print(" " + changeType);
			changeModification(eObjectList, cbp);
		} else if (changeType == ChangeType.ADD) {
			System.out.print(" " + changeType);
			addModification(eObjectList, cbp);
		} else if (changeType == ChangeType.DELETE) {
			System.out.print(" " + changeType);
			deleteModification(eObjectList, cbp);
		} else if (changeType == ChangeType.MOVE) {
			System.out.print(" " + changeType);
			moveModification(eObjectList, cbp);
		}
		System.out.print(" " + eObjectList.size());

	}

	private static void addModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
		// 0 for Attributes
		boolean found = false;
		while (!found) {
			if (random.nextInt(2) == 0) {
				int index1 = random.nextInt(eObjectList.size());
				EObject eObject1 = eObjectList.get(index1);
				if (eObject1 != null) {
					if (eObject1.eResource() == null) {
						eObjectList.remove(eObject1);
						continue;
					}
					String id = leftCbp.getURIFragment(eObject1);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						eObjectList.remove(eObject1);
						continue;
					}
				}
				EList<EAttribute> attributes1 = eObject1.eClass().getEAllAttributes();
				List<EAttribute> filteredEAttributes = new ArrayList<EAttribute>();
				for (EAttribute eAttribute : attributes1) {
					if (eAttribute.isMany() && eAttribute.isChangeable()) {
						filteredEAttributes.add(eAttribute);
					}
				}
				if (filteredEAttributes.size() > 0) {
					EAttribute eAttribute = filteredEAttributes.get(random.nextInt(filteredEAttributes.size()));
					EList<Object> values = (EList<Object>) eObject1.eGet(eAttribute);
					Object value = null;
					if (eAttribute.getEAttributeType().getName().equals("String")) {
						value = "Z" + EcoreUtil.generateUUID();
					} else if (eAttribute.getEAttributeType().getName().equals("Integer")) {
						value = 0;
					} else if (eAttribute.getEAttributeType().getName().equals("Boolean")) {
						value = true;
					} else if (eAttribute.getEAttributeType().getName().equals("UnlimitedNatural")) {
						value = 0;
					} else if (eAttribute.getEAttributeType().getName().equals("EBoolean")) {
						value = random.nextBoolean();
					} else if (eAttribute.getEAttributeType().getName().equals("EString")) {
						value = "Z" + EcoreUtil.generateUUID();
					} else if (eAttribute.getEAttributeType().getName().equals("EInt")) {
						value = random.nextInt(10);
					}

					if (value != null) {
						if (values.size() > 0) {
							int pos = random.nextInt(values.size() - 1);
							values.add(pos, value);
							found = true;
						} else {
							values.add(value);
							found = true;
						}
					}

				}
			}
			// 1 for References
			else {

				int index1 = random.nextInt(eObjectList.size());
				EObject eObject1 = eObjectList.get(index1);
				if (eObject1 != null) {
					if (eObject1.eResource() == null) {
						eObjectList.remove(eObject1);
						continue;
					}
					String id = leftCbp.getURIFragment(eObject1);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						eObjectList.remove(eObject1);
						continue;
					}
				}
				EList<EReference> references1 = eObject1.eClass().getEAllContainments();
				List<EReference> filteredEReferences = new ArrayList<EReference>();
				for (EReference eReference : references1) {
					if (eReference.isChangeable()) {
						filteredEReferences.add(eReference);
					}
				}
				if (filteredEReferences.size() > 0) {
					EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
					EFactory factory = ePackage.getEFactoryInstance();
					if (!eReference.getEReferenceType().isAbstract()) {
						EObject eObject2 = factory.create(eReference.getEReferenceType());
						// eObject2.eSet(eObject2.eClass().getEStructuralFeature("name"),
						// "Z" + EcoreUtil.generateUUID());
						if (eReference.isMany()) {
							EList<EObject> values = (EList<EObject>) eObject1.eGet(eReference);
							if (values.size() > 0) {
								values.add(random.nextInt(values.size()), eObject2);
								found = true;
							} else {
								try {
									values.add(eObject2);
									eObjectList.add(eObject2);
									found = true;
								} catch (Exception e) {
									// e.printStackTrace();
								}
							}
						} else {
							try {
								if (eObject1.eGet(eReference) != null) {
									continue;
								}
								eObject1.eSet(eReference, eObject2);
								eObjectList.add(eObject2);
								found = true;
							} catch (Exception e) {
								// e.printStackTrace();
							}
						}
					}

				}
			}
		}

	}

	private static void moveModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
		// 0 for Attributes
		boolean found = false;
		while (!found) {
			if (random.nextInt(2) == 0) {
				int index1 = random.nextInt(eObjectList.size());
				EObject eObject1 = eObjectList.get(index1);
				if (eObject1.eResource() == null) {
					eObjectList.remove(eObject1);
					continue;
				}
				if (eObject1 != null) {
					String id = leftCbp.getURIFragment(eObject1);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						eObjectList.remove(eObject1);
						continue;
					}
				}
				EList<EAttribute> attributes1 = eObject1.eClass().getEAllAttributes();
				List<EAttribute> filteredEAttributes = new ArrayList<EAttribute>();
				for (EAttribute eAttribute : attributes1) {
					if (eAttribute.isMany() && eAttribute.isChangeable()) {
						filteredEAttributes.add(eAttribute);
					}
				}
				if (filteredEAttributes.size() > 0) {
					EAttribute eAttribute = filteredEAttributes.get(random.nextInt(filteredEAttributes.size()));
					EList<Object> values = (EList<Object>) eObject1.eGet(eAttribute);
					if (values.size() > 1) {
						int from = random.nextInt(values.size() - 1);
						int to = random.nextInt(values.size() - 1);
						if (from == to && from != 0) {
							from--;
						}
						values.move(from, to);
						found = true;
					}

				}
			}
			// 1 for References
			else {
				// 0 move-within
				if (random.nextInt(2) == 0) {
					int index1 = random.nextInt(eObjectList.size());
					EObject eObject1 = eObjectList.get(index1);
					if (eObject1 != null) {
						if (eObject1.eResource() == null) {
							eObjectList.remove(eObject1);
							continue;
						}
						String id = leftCbp.getURIFragment(eObject1);
						if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
							eObjectList.remove(eObject1);
							continue;
						}
					}
					EList<EReference> references1 = eObject1.eClass().getEAllContainments();
					List<EReference> filteredEReferences = new ArrayList<EReference>();
					for (EReference eReference : references1) {
						if (eReference.isMany() && eReference.isChangeable()) {
							filteredEReferences.add(eReference);
						}
					}
					if (filteredEReferences.size() > 0) {
						EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
						EList<EObject> values = (EList<EObject>) eObject1.eGet(eReference);
						if (values.size() > 1) {
							int from = random.nextInt(values.size() - 1);
							int to = random.nextInt(values.size() - 1);
							if (from == to && from != 0) {
								from--;
							}
							values.move(from, to);
							found = true;
						}
					}
				}
				// 1 move-between
				else {
					int index1 = random.nextInt(eObjectList.size());
					EObject eObject1 = eObjectList.get(index1);

					if (eObject1 != null) {
						if (eObject1.eResource() == null) {
							eObjectList.remove(eObject1);
							continue;
						}
						String id = leftCbp.getURIFragment(eObject1);
						if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
							eObjectList.remove(eObject1);
							continue;
						}
					}
					int index2 = random.nextInt(eObjectList.size());
					EObject eObject2 = eObjectList.get(index2);

					if (eObject2 != null) {
						if (eObject2.eResource() == null) {
							eObjectList.remove(eObject2);
							continue;
						}
						String id = leftCbp.getURIFragment(eObject2);
						if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
							eObjectList.remove(eObject2);
							continue;
						}
					}
					EList<EReference> references1 = eObject1.eClass().getEAllContainments();
					List<EReference> filteredEReferences = new ArrayList<EReference>();
					for (EReference eReference : references1) {
						if (eReference.isChangeable() && eReference.getEOpposite() == null) {
							EClass referenceType = eReference.getEReferenceType();
							EClass eObjectType = eObject2.eClass();
							if (eObjectType.equals(referenceType)) {
								filteredEReferences.add(eReference);
							}
						}
					}
					if (filteredEReferences.size() > 0) {
						EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
						if (eReference.isMany()) {
							EList<EObject> values = (EList<EObject>) eObject1.eGet(eReference);
							if (values.size() > 0 && !values.contains(eObject2)) {
								try {
									int pos = random.nextInt(values.size());
									values.add(pos, eObject2);
									found = true;
								} catch (Exception e) {
								}
							} else {
								try {
									values.add(eObject2);
									found = true;
								} catch (Exception e) {
								}
							}
						} else {
							try {
								if (eObject1.eGet(eReference) != null) {
									continue;
								}
								eObject1.eSet(eReference, eObject2);
								found = true;
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}

	}

	private static void changeModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
		// 0 for Attributes
		boolean found = false;
		while (!found) {
			if (random.nextInt(2) == 0) {
				int index1 = random.nextInt(eObjectList.size());
				EObject eObject1 = eObjectList.get(index1);
				if (eObject1 != null) {
					String id = leftCbp.getURIFragment(eObject1);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						eObjectList.remove(eObject1);
						continue;
					}
					if (eObject1.eResource() == null) {
						eObjectList.remove(eObject1);
						continue;
					}

				}
				EList<EAttribute> attributes1 = eObject1.eClass().getEAllAttributes();
				List<EAttribute> filteredEAttributes = new ArrayList<EAttribute>();
				for (EAttribute eAttribute : attributes1) {
					if (!eAttribute.isMany() && eAttribute.isChangeable()) {
						filteredEAttributes.add(eAttribute);
					}
				}
				if (filteredEAttributes.size() > 0) {
					EAttribute eAttribute = filteredEAttributes.get(random.nextInt(filteredEAttributes.size()));
					if (eAttribute.getEAttributeType().getName().equals("String")) {
						eObject1.eSet(eAttribute, "Z" + EcoreUtil.generateUUID());
						found = true;
					} else if (eAttribute.getEAttributeType().getName().equals("Integer")) {
						eObject1.eSet(eAttribute, 0);
						found = true;
					} else if (eAttribute.getEAttributeType().getName().equals("Boolean")) {
						eObject1.eSet(eAttribute, true);
						found = true;
					} else if (eAttribute.getEAttributeType().getName().equals("UnlimitedNatural")) {
						eObject1.eSet(eAttribute, 0);
						found = true;
					} else if (eAttribute.getEAttributeType().getName().equals("EBoolean")) {
						eObject1.eSet(eAttribute, !(Boolean) eObject1.eGet(eAttribute));
						found = true;
					} else if (eAttribute.getEAttributeType().getName().equals("EString")) {
						eObject1.eSet(eAttribute, "Z" + EcoreUtil.generateUUID());
						found = true;
					} else if (eAttribute.getEAttributeType().getName().equals("EInt")) {
						eObject1.eSet(eAttribute, 1 + (Integer) eObject1.eGet(eAttribute));
						found = true;
					} else {
						continue;
						// System.out.println(eAttribute.getEAttributeType().getName());
					}
				}
			}
			// 1 for References
			else {
				int index1 = random.nextInt(eObjectList.size());
				EObject eObject1 = eObjectList.get(index1);
				if (eObject1 != null) {

					String id = leftCbp.getURIFragment(eObject1);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						eObjectList.remove(eObject1);
						continue;
					}
					if (eObject1.eResource() == null) {
						eObjectList.remove(eObject1);
						continue;
					}
				}
				int index2 = random.nextInt(eObjectList.size());
				EObject eObject2 = eObjectList.get(index2);
				if (eObject2 != null) {

					String id = leftCbp.getURIFragment(eObject2);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						eObjectList.remove(eObject2);
						continue;
					}
					if (eObject2.eResource() == null) {
						eObjectList.remove(eObject2);
						continue;
					}
				}
				EList<EReference> references1 = eObject1.eClass().getEAllReferences();
				List<EReference> filteredEReferences = new ArrayList<EReference>();
				for (EReference eReference : references1) {
					if (!eReference.isMany() && !eReference.isContainment() && eReference.isChangeable()
						&& eReference.getEOpposite() == null) {
						EClass referenceType = eReference.getEReferenceType();
						EClass eObjectType = eObject2.eClass();
						if (eObjectType.equals(referenceType)) {
							filteredEReferences.add(eReference);
						}
					}
				}
				if (filteredEReferences.size() > 0) {
					try {
						EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
						EObject oldEObject = (EObject) eObject1.eGet(eReference);
						eObject1.eSet(eReference, eObject2);

						if (oldEObject != null && oldEObject.eResource() == null) {
							eObjectList.remove(oldEObject);
						}

						// System.out.println( leftCbp.getURIFragment(eObject1)
						// + "." + eReference.getName() + "." +
						// leftCbp.getURIFragment(eObject2) + ".SET");
						// if (eReference.getName().equals("association")) {
						// System.out.println();
						// }
						found = true;
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * @param eObjectList
	 * @param leftCbp
	 */
	private static void deleteModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
		boolean found = false;

		while (!found) {
			// 0 for Attributes
			if (random.nextInt(2) == 0) {
				int index = random.nextInt(eObjectList.size());
				EObject eObject = eObjectList.get(index);
				EList<EAttribute> eAttributes = eObject.eClass().getEAllAttributes();
				if (eAttributes.size() > 0) {
					EAttribute eAttribute = eAttributes.get(random.nextInt(eAttributes.size()));
					if (eAttribute.isMany()) {
						EList<Object> values = (EList<Object>) eObject.eGet(eAttribute);
						if (values.size() > 0) {
							values.remove(random.nextInt(values.size()));
							found = true;
						}
					}
				}

			}
			// 1 for References
			else {
				int index = random.nextInt(eObjectList.size());
				EObject eObject = eObjectList.get(index);
				if (eObject != null) {
					String id = leftCbp.getURIFragment(eObject);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						eObjectList.remove(eObject);
						continue;
					}
				}
				if (eObject.eResource() == null) {
					eObjectList.remove(eObject);
					continue;
				}

				if (!eObject.eContents().isEmpty()) {
					continue;
				}

				if (((EReference) eObject.eContainingFeature()).isContainment()) {
					removeObjectFromEObjectList(eObject, eObjectList);
					EcoreUtil.remove(eObject);
					eObjectList.remove(eObject);
					found = true;
				} else {
					EcoreUtil.remove(eObject);
					if (eObject.eContainer() == null) {
						eObjectList.remove(eObject);
					}
					found = true;
				}
			}
		}
	}

	private static void removeObjectFromEObjectList(EObject eObject, List<EObject> eObjectList) {
		for (EReference eReference : eObject.eClass().getEAllContainments()) {
			if (eReference.isChangeable()) {
				if (eReference.isMany()) {
					EList<EObject> values = (EList<EObject>) eObject.eGet(eReference);
					for (EObject value : values) {
						removeObjectFromEObjectList(value, eObjectList);
						eObjectList.remove(value);
					}
				} else {
					EObject value = (EObject) eObject.eGet(eReference);
					if (value != null) {
						removeObjectFromEObjectList(value, eObjectList);
						eObjectList.remove(value);
					}
				}
			}
		}

	}

	private static void setProbability(List<ChangeType> seeds, int probability, ChangeType type) {
		for (int i = 0; i < probability; i++) {
			seeds.add(type);
		}
	}

}