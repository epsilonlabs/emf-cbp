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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.emf.compare.Conflict;
import org.eclipse.emf.compare.ConflictKind;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
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
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
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
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.emfstore.CBP2EMFStoreAdapter;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodeFactory;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
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
			// manualChanges(localServer);
			// runClient(localServer);
			// runClient2(localServer);
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

		originalAdapater = new CBP2EMFStoreAdapter(originalProject, originalProject, originalProject);
		leftAdapater = new CBP2EMFStoreAdapter(leftProject, originalProject, rightProject);
		rightAdapater = new CBP2EMFStoreAdapter(rightProject, originalProject, leftProject);

		// final File cbpOriginalFile = new File("D:\\TEMP\\FASE\\Debug\\origin.cbpxml");
		// final File cbpLeftFile = new File("D:\\TEMP\\FASE\\Debug\\left.cbpxml");
		// final File cbpRightFile = new File("D:\\TEMP\\FASE\\Debug\\right.cbpxml");

		final File cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.cbpxml");
		final File cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.cbpxml");
		final File cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.cbpxml");
		final File emfsTargetXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-target.xmi");
		final File emfsLeftXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-left.xmi");
		final File emfsRightXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-right.xmi");
		final File emfsOriginalXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-origin.xmi");

		XMIResource emfsTargetResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsTargetXmiFile.getAbsolutePath()));
		XMIResource emfsLeftResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsLeftXmiFile.getAbsolutePath()));
		XMIResource emfsRightResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsRightXmiFile.getAbsolutePath()));
		XMIResource emfsOriginalResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsOriginalXmiFile.getAbsolutePath()));

		long skip = cbpOriginalFile.length();

		try {
			System.out.println("LOADING ORIGINAL MODEL");
			originalAdapater.load(cbpOriginalFile);
			originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());
			rightProject.update(new ESSystemOutProgressMonitor());
			leftProject.update(new ESSystemOutProgressMonitor());
			exportToXMI(originalAdapater, emfsOriginalResource);

			// process right file first
			System.out.println("LOADING RIGHT MODEL");
			String rightText = readFiles(cbpRightFile, skip);
			InputStream rightStream = new ByteArrayInputStream(rightText.getBytes());
			rightAdapater.load(rightStream);
			rightStream.close();
			rightProject.commit("RIGHT", null, new ESSystemOutProgressMonitor());
			exportToXMI(rightAdapater, emfsRightResource);

			// process left
			System.out.println("LOADING LEFT MODEL");
			String leftText = readFiles(cbpLeftFile, skip);
			InputStream leftStream = new ByteArrayInputStream(leftText.getBytes());
			leftAdapater.load(leftStream);
			leftStream.close();
			exportToXMI(leftAdapater, emfsLeftResource);

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

						EMFStoreResult result = new EMFStoreResult();
						printEMFStoreConflicts(changeConflictSet, result);

						for (ESConflict conflict : changeConflictSet.getConflicts()) {
							conflict.resolveConflict(conflict.getLocalOperations(), conflict.getRemoteOperations());
						}
						return true;
					}

				}, new ESSystemOutProgressMonitor());
			}

			leftProject.commit("LEFT", null, new ESSystemOutProgressMonitor());
			// rightProject.update(new ESSystemOutProgressMonitor());

			exportToXMI(leftAdapater, emfsTargetResource);

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

	/**
	 * Run an EMFStore Client connecting to the given server.
	 *
	 * @param server the server
	 * @throws ESException if the server connection fails
	 */
	public static void manualChanges(ESServer server) throws ESException {
		System.out.println("Client starting...");

		EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
		// EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
		// EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
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

		originalAdapater = new CBP2EMFStoreAdapter(originalProject, originalProject, originalProject);
		leftAdapater = new CBP2EMFStoreAdapter(leftProject, originalProject, rightProject);
		rightAdapater = new CBP2EMFStoreAdapter(rightProject, originalProject, leftProject);
		// final File cbpOriginalFile = new File("D:\\TEMP\\FASE\\Debug\\origin.cbpxml");
		// final File cbpLeftFile = new File("D:\\TEMP\\FASE\\Debug\\left.cbpxml");
		// final File cbpRightFile = new File("D:\\TEMP\\FASE\\Debug\\right.cbpxml");

		final File cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.cbpxml");
		final File cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.cbpxml");
		final File cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.cbpxml");
		final File emfsTargetXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-target.xmi");
		final File emfsLeftXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-left.xmi");
		final File emfsRightXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-right.xmi");
		final File emfsOriginalXmiFile = new File("D:\\TEMP\\CONFLICTS\\debug\\emfstore-origin.xmi");

		XMIResource emfsTargetResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsTargetXmiFile.getAbsolutePath()));
		XMIResource emfsLeftResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsLeftXmiFile.getAbsolutePath()));
		XMIResource emfsRightResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsRightXmiFile.getAbsolutePath()));
		XMIResource emfsOriginalResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsOriginalXmiFile.getAbsolutePath()));

		long skip = cbpOriginalFile.length();

		try {
			System.out.println("LOADING ORIGINAL MODEL");

			NodeFactory factory = NodeFactory.eINSTANCE;
			Node rootNode = factory.createNode();
			rootNode.setName("ROOT");
			originalProject.getModelElements().add(rootNode);

			Node nodeA = factory.createNode();
			nodeA.setName("A");
			Node nodeB = factory.createNode();
			nodeB.setName("B");
			Node nodeC = factory.createNode();
			nodeC.setName("C");
			Node nodeD = factory.createNode();
			nodeD.setName("D");
			rootNode.getValNodes().add(nodeA);
			rootNode.getValNodes().add(nodeB);
			rootNode.getValNodes().add(nodeC);
			rootNode.getValNodes().add(nodeD);

			ESModelElementId rootId = originalProject.getModelElementId(rootNode);
			ESModelElementId aId = originalProject.getModelElementId(nodeA);
			ESModelElementId bId = originalProject.getModelElementId(nodeB);
			ESModelElementId cId = originalProject.getModelElementId(nodeC);
			ESModelElementId dId = originalProject.getModelElementId(nodeD);
			nodeD.setParent(nodeA);
			nodeB.setRefNode(nodeA);

			originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());

			rightProject.update(new ESSystemOutProgressMonitor());
			leftProject.update(new ESSystemOutProgressMonitor());
			// exportToXMI(originalAdapater, emfsOriginalResource);

			// process right file first
			System.out.println("LOADING RIGHT MODEL");
			nodeD = (Node) rightProject.getModelElement(dId);
			nodeA = (Node) rightProject.getModelElement(aId);
			// nodeD.setRefNode(null);
			EcoreUtil.delete(nodeD);
			EcoreUtil.delete(nodeB);
			rightProject.commit("RIGHT", null, new ESSystemOutProgressMonitor());
			// exportToXMI(rightAdapater, emfsRightResource);

			// process left
			System.out.println("LOADING LEFT MODEL");
			nodeD = (Node) leftProject.getModelElement(dId);
			nodeA = (Node) leftProject.getModelElement(aId);
			// nodeC = (Node) leftProject.getModelElement(cId);
			EcoreUtil.delete(nodeD);
			EcoreUtil.delete(nodeA);
			// EcoreUtil.delete(nodeB);
			// EcoreUtil.delete(nodeC);

			// exportToXMI(leftAdapater, emfsLeftResource);

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

						EMFStoreResult result = new EMFStoreResult();
						printEMFStoreConflicts(changeConflictSet, result);

						for (ESConflict conflict : changeConflictSet.getConflicts()) {
							conflict.resolveConflict(conflict.getLocalOperations(), conflict.getRemoteOperations());
						}
						return true;
					}

				}, new ESSystemOutProgressMonitor());
			}

			leftProject.commit("LEFT", null, new ESSystemOutProgressMonitor());
			// rightProject.update(new ESSystemOutProgressMonitor());

			// exportToXMI(leftAdapater, emfsTargetResource);

			System.out.println();
			System.out.println();
			System.out.println("SUCCESS!!");
		} catch (final FactoryConfigurationError ex) {
			ex.printStackTrace();
		}
		// catch (final IOException ex) {
		// ex.printStackTrace();
		// }
	}

	@SuppressWarnings("restriction")
	public static void runClient2(ESServer server) throws ESException, FactoryConfigurationError, XMLStreamException {
		System.out.println("Client starting...");

		EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(), JavaPackage.eINSTANCE);
		final ESWorkspace workspace = ESWorkspaceProvider.INSTANCE.getWorkspace();
		workspace.addServer(server);
		for (final ESServer existingServer : workspace.getServers()) {
			if (existingServer != server) {
				try {
					workspace.removeServer(existingServer);
				} catch (final ESServerNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		for (final ESLocalProject existingLocalProject : workspace.getLocalProjects()) {
			try {
				existingLocalProject.delete(new ESSystemOutProgressMonitor());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		ESLocalProject originalProject = workspace.createLocalProject("OriginalProject");
		final ESUsersession usersession = server.login("super", "super");
		for (final ESRemoteProject existingRemoteProject : server.getRemoteProjects(usersession)) {
			existingRemoteProject.delete(usersession, new NullProgressMonitor());
		}
		originalProject.shareProject(usersession,
			new ESSystemOutProgressMonitor());
		ESLocalProject leftProject = originalProject.getRemoteProject().checkout("LeftProject",
			usersession, new ESSystemOutProgressMonitor());
		ESLocalProject rightProject = originalProject.getRemoteProject().checkout("RightProject",
			usersession, new ESSystemOutProgressMonitor());

		originalAdapater = new CBP2EMFStoreAdapter(originalProject, originalProject, originalProject);
		leftAdapater = new CBP2EMFStoreAdapter(leftProject, originalProject, rightProject);
		rightAdapater = new CBP2EMFStoreAdapter(rightProject, originalProject, leftProject);

		final File xmiOriginalFile = new File("D:\\TEMP\\CONFLICTS\\performance\\origin.xmi");
		final File xmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\performance\\left.xmi");
		final File xmiRightFile = new File("D:\\TEMP\\CONFLICTS\\performance\\right.xmi");
		final File cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\performance\\origin.cbpxml");
		final File cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\performance\\left.cbpxml");
		final File cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\performance\\right.cbpxml");
		final File emfsLeftFile = new File("D:\\TEMP\\CONFLICTS\\performance\\emfstore-left.xmi");
		final File emfsRightFile = new File("D:\\TEMP\\CONFLICTS\\performance\\emfstore-right.xmi");
		final File emfsOriginalFile = new File("D:\\TEMP\\CONFLICTS\\performance\\emfstore-origin.xmi");

		XMIResource emfsLeftResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsLeftFile.getAbsolutePath()));
		XMIResource emfsRightResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsRightFile.getAbsolutePath()));
		XMIResource emfsOriginalResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsOriginalFile.getAbsolutePath()));

		try {
			// Load Original XMI
			XMIResource xmiOrigin = (XMIResource) new XMIResourceFactoryImpl()
				.createResource(URI.createFileURI(xmiOriginalFile.getAbsolutePath()));
			xmiOrigin.load(options);

			// Create Original CBP

			if (cbpOriginalFile.exists()) {
				cbpOriginalFile.delete();
			}
			if (cbpLeftFile.exists()) {
				cbpLeftFile.delete();
			}
			if (cbpRightFile.exists()) {
				cbpRightFile.delete();
			}

			CBPXMLResourceFactory cbpFactory = new CBPXMLResourceFactory();
			CBPXMLResourceImpl originCbp = (CBPXMLResourceImpl) cbpFactory
				.createResource(URI.createFileURI(cbpOriginalFile.getAbsolutePath()));
			originCbp.setIdType(IdType.NUMERIC, "O-");
			System.out.println("Creating origin cbp");
			originCbp.startNewSession("ORIGIN");
			originCbp.getContents().addAll(xmiOrigin.getContents());
			originCbp.save(null);
			saveXmiWithID(originCbp, xmiOriginalFile);
			originCbp.unload();
			xmiOrigin.unload();

			System.out.println("Copying origin cbp to left and right cbps");
			Files.copy(cbpOriginalFile, cbpLeftFile);
			Files.copy(cbpOriginalFile, cbpRightFile);

			// long previRightFileSize = cbpOriginalFile.length();
			// long prevLeftFileSize = cbpOriginalFile.length();
			long previRightFileSize = cbpRightFile.length();
			long prevLeftFileSize = cbpLeftFile.length();

			ePackage = getEPackageFromFiles(cbpLeftFile, cbpRightFile);

			CBPXMLResourceImpl leftCbp = (CBPXMLResourceImpl) cbpFactory
				.createResource(URI.createFileURI(cbpLeftFile.getAbsolutePath()));
			CBPXMLResourceImpl rightCbp = (CBPXMLResourceImpl) cbpFactory
				.createResource(URI.createFileURI(cbpRightFile.getAbsolutePath()));
			leftCbp.setIdType(IdType.NUMERIC, "L-");
			rightCbp.setIdType(IdType.NUMERIC, "R-");

			int number = 1;

			System.out.println("Loading " + cbpLeftFile.getName() + "...");
			leftCbp.load(null);
			leftCbp.startNewSession("LEFT-" + number);
			System.out.println("Loading " + cbpRightFile.getName() + "...");
			rightCbp.load(null);
			rightCbp.startNewSession("RIGHT-" + number);

			// Create Original EMF STORE
			System.out.println("LOADING ORIGINAL MODEL");
			originalAdapater.load(cbpOriginalFile);
			originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());
			rightProject.update(new ESSystemOutProgressMonitor());
			leftProject.update(new ESSystemOutProgressMonitor());
			exportToXMI(originalAdapater, emfsOriginalResource);

			// Set Configuration for Modification
			List<ChangeType> seeds = new ArrayList<ChangeType>();
			setProbability(seeds, 0, ChangeType.CHANGE);
			setProbability(seeds, 0, ChangeType.ADD);
			setProbability(seeds, 1, ChangeType.DELETE);
			setProbability(seeds, 0, ChangeType.MOVE);

			List<EObject> leftEObjectList = identifyAllEObjects(leftCbp);
			List<EObject> rightEObjectList = identifyAllEObjects(rightCbp);

			int modificationCount = 4400;
			for (int i = 1; i <= modificationCount; i++) {
				System.out.print("Change " + i + ":");
				// leftCbp.startNewSession("LEFT-" + i);
				startModification(leftCbp, leftEObjectList, seeds);
				leftCbp.save(null);
				// rightCbp.startNewSession("RIGHT-" + i);
				startModification(rightCbp, rightEObjectList, seeds);
				rightCbp.save(null);
				System.out.println();

				// if (i % 10 == 0) {
				if (i % 1650 == 0) {

					number++;

					// BigModelResult result = new BigModelResult();

					// ------------CBP
					System.out.println("Reload from the Cbps ...");
					CBPXMLResourceImpl leftCbp2 = (CBPXMLResourceImpl) cbpFactory
						.createResource(URI.createFileURI(cbpLeftFile.getAbsolutePath()));
					CBPXMLResourceImpl rightCbp2 = (CBPXMLResourceImpl) cbpFactory
						.createResource(URI.createFileURI(cbpRightFile.getAbsolutePath()));
					leftCbp2.load(null);
					rightCbp2.load(null);
					System.out.println("Saving changes to Xmis ...");
					saveXmiWithID(leftCbp2, xmiLeftFile);
					saveXmiWithID(rightCbp2, xmiRightFile);
					leftCbp2.unload();
					rightCbp2.unload();

					System.out.println("\nCBP:");
					CBPComparisonImpl changeComparison = new CBPComparisonImpl();
					changeComparison.setDiffEMFCompareFile(
						new File(cbpOriginalFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
					changeComparison.setObjectTreeFile(
						new File(cbpOriginalFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
					// changeComparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
					changeComparison.compare(cbpLeftFile, cbpRightFile, cbpOriginalFile);

					// ------------EMF STORE
					System.out.println("\nEMF STORE:");

					// originalProject.shareProject(usersession,
					// new ESSystemOutProgressMonitor());
					//
					// rightProject.delete(new ESSystemOutProgressMonitor());
					// rightProject = originalProject.getRemoteProject().checkout("RightProject",
					// usersession, new ESSystemOutProgressMonitor());
					// rightProject.update(new ESSystemOutProgressMonitor());
					// rightAdapater = new CBP2EMFStoreAdapter(rightProject);
					//
					// leftProject.delete(new ESSystemOutProgressMonitor());
					// leftProject = originalProject.getRemoteProject().checkout("LeftProject",
					// usersession, new ESSystemOutProgressMonitor());
					// leftProject.update(new ESSystemOutProgressMonitor());
					// leftAdapater = new CBP2EMFStoreAdapter(leftProject);

					// process right file first
					System.out.println("LOADING RIGHT MODEL");
					String rightText = readFiles(cbpRightFile, previRightFileSize);
					InputStream rightStream = new ByteArrayInputStream(rightText.getBytes());
					rightAdapater.load(rightStream);
					rightStream.close();
					rightProject.commit("RIGHT-" + number, null, new ESSystemOutProgressMonitor());

					// process left
					System.out.println("LOADING LEFT MODEL");
					String leftText = readFiles(cbpLeftFile, prevLeftFileSize);
					InputStream leftStream = new ByteArrayInputStream(leftText.getBytes());
					leftAdapater.load(leftStream);
					leftStream.close();

					final EMFStoreResult emfsResult = new EMFStoreResult();

					try {
						try {
							// leftProject.update(new ESSystemOutProgressMonitor());
							leftProject.commit("LEFT-" + number, null, new ESSystemOutProgressMonitor());
						} catch (final ESUpdateRequiredException e) {

							leftProject.update(ESVersionSpec.FACTORY.createHEAD(), new ESUpdateCallback() {
								public void noChangesOnServer() {
								}

								public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
									ESModelElementIdToEObjectMapping idToEObjectMapping) {
									// ESCloseableIterable<AbstractOperation> localChanges = ((ESLocalProjectImpl)
									// project)
									// .toInternalAPI().getLocalChangePackage().operations();
									// List<AbstractOperation> localOperations = new ArrayList<AbstractOperation>();
									// Iterator<AbstractOperation> localIterator = localChanges.iterable().iterator();
									// while (localIterator.hasNext()) {
									// AbstractOperation op = localIterator.next();
									// localOperations.add(op);
									// }
									// emfsResult.setLeftOperations(localOperations);
									//
									// List<AbstractOperation> remoteOperations = new ArrayList<AbstractOperation>();
									// for (ESChangePackage change : changes) {
									// ESCloseableIterable<ESOperation> remoteChanges = change.operations();
									// Iterator<ESOperation> remoteIterator = remoteChanges.iterable().iterator();
									// while (remoteIterator.hasNext()) {
									// AbstractOperation op = ((ESOperationImpl) remoteIterator.next())
									// .toInternalAPI();
									// remoteOperations.add(op);
									// }
									// }
									// emfsResult.setRightOperations(remoteOperations);

									System.console();
									return true;
								}

								public boolean conflictOccurred(ESConflictSet changeConflictSet,
									IProgressMonitor monitor) {
									System.out
										.println(
											"\nEMFStore Conflict size = " + changeConflictSet.getConflicts().size());
									System.out.println();
									emfsResult.setChangeConflictSet(changeConflictSet);
									emfsResult.setEmfsConflictCount(changeConflictSet.getConflicts().size());
									try {
										printEMFStoreConflicts(changeConflictSet, emfsResult);
									} catch (Exception e) {
										e.printStackTrace();
									}
									System.out
										.println(
											"EMFStore Conflict size = " + changeConflictSet.getConflicts().size());
									return false;
								}

							}, new ESSystemOutProgressMonitor());
						}
					} catch (Exception e) {
						System.console();
					}

					exportToXMI(leftAdapater, emfsLeftResource);
					exportToXMI(rightAdapater, emfsRightResource);

					previRightFileSize = cbpRightFile.length();
					prevLeftFileSize = cbpLeftFile.length();

					System.out
						.println();
					System.out
						.println(changeComparison.getConflicts().size() + " VS " + emfsResult.getEmfsConflictCount());
					// if (changeComparison.getConflicts().size() != emfsResult.getEmfsConflictCount() &&
					// changeComparison.getConflicts().size() >= 1 && emfsResult.getEmfsConflictCount() >= 1) {
					getSplittedDetectedCBPConflicts(changeComparison, emfsResult);
					getSplittedDetectedEMFSConflicts(changeComparison, emfsResult);
					getUndetectedCBPConflicts(changeComparison, emfsResult);
					getUndetectedEMFStoreConflicts(changeComparison, emfsResult);

					// Set<String> leftCbpEvents = new HashSet<String>(changeComparison.getLeftEventStrings());
					// leftCbpEvents.removeAll(emfsResult.getLeftEventStrings());
					// Set<String> rightCbpEvents = new HashSet<String>(changeComparison.getRightEventStrings());
					// rightCbpEvents.removeAll(emfsResult.getRightEventStrings());

					// List<AbstractOperation> lefts = emfsResult.getLeftOperations();
					// List<AbstractOperation> rights = emfsResult.getRightOperations();
					// Set<String> leftEmfsEvents = new HashSet<String>(emfsResult.getLeftEventStrings());
					// leftEmfsEvents.removeAll(changeComparison.getLeftEventStrings());
					// Set<String> rightEmfsEvents = new HashSet<String>(emfsResult.getRightEventStrings());
					// rightEmfsEvents.removeAll(changeComparison.getRightEventStrings());

					System.console();
					// }

					leftCbp.startNewSession("LEFT-" + number);
					rightCbp.startNewSession("RIGHT-" + number);
				}
			}

			System.out.println();
			System.out.println("SUCCESS!!");
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param changeComparison
	 * @param emfsResult
	 */
	private static void getSplittedDetectedEMFSConflicts(CBPComparisonImpl changeComparison,
		final EMFStoreResult emfsResult) {

		System.out.println("\nSplitted Detected EMFS Conflicts:");
		System.out.println("right:");
		for (Entry<Integer, Set<String>> esEntry : emfsResult.getRightEventStrings().entrySet()) {
			int esConfId = esEntry.getKey();
			Set<String> esEvents = esEntry.getValue();
			Set<Integer> counter = new HashSet<Integer>();

			// System.out.println(esConfId);
			for (String esString : esEvents) {

				if (esString.equals("")) {
					continue;
				}
				// System.out.println("+---ES: " + esString);

				for (Entry<Integer, Set<String>> cbEntry : changeComparison.getRightEventStrings()
					.entrySet()) {
					int cbConfId = cbEntry.getKey();
					Set<String> cbEvents = cbEntry.getValue();
					// System.out.println("+---+--- " + cbConfId);
					for (String cbString : cbEvents) {
						// System.out.println("+---+---+--- " + cbString);
						if (esString.trim().equals(cbString.trim())) {
							counter.add(cbConfId);
							// System.out.println(esString + ": " + esConfId + " => " + cbConfId);
						}
					}
				}
			}

			if (counter.size() > 1) {
				System.out.print(esConfId + " : ");
				for (int c : counter) {
					System.out.print(c + " ");
				}
				System.out.println();
				// System.out.println(": " + esString + "");
			}
		}
		System.out.println("left:");
		for (Entry<Integer, Set<String>> esEntry : emfsResult.getLeftEventStrings().entrySet()) {
			int esConfId = esEntry.getKey();
			Set<String> esEvents = esEntry.getValue();
			Set<Integer> counter = new HashSet<Integer>();

			// System.out.println(esConfId);
			for (String esString : esEvents) {

				if (esString.equals("")) {
					continue;
				}
				// System.out.println("+---ES: " + esString);

				for (Entry<Integer, Set<String>> cbEntry : changeComparison.getLeftEventStrings()
					.entrySet()) {
					int cbConfId = cbEntry.getKey();
					Set<String> cbEvents = cbEntry.getValue();
					// System.out.println("+---+--- " + cbConfId);
					for (String cbString : cbEvents) {
						// System.out.println("+---+---+--- " + cbString);
						if (esString.trim().equals(cbString.trim())) {
							counter.add(cbConfId);
							// System.out.println(esString + ": " + esConfId + " => " + cbConfId);
						}
					}
				}
			}

			if (counter.size() > 1) {
				System.out.print(esConfId + " : ");
				for (int c : counter) {
					System.out.print(c + " ");
				}
				System.out.println();
				// System.out.println(": " + esString + "");
			}
		}
	}

	/**
	 * @param changeComparison
	 * @param emfsResult
	 */
	private static void getUndetectedCBPConflicts(CBPComparisonImpl changeComparison,
		final EMFStoreResult emfsResult) {
		Set<Integer> undetectedConflicts = new HashSet<Integer>();
		System.out.println("\nUndetected CBP Conflicts:");
		System.out.println("left:");
		for (Entry<Integer, Set<String>> cbEntry : changeComparison.getLeftEventStrings().entrySet()) {
			int cbConfId = cbEntry.getKey();
			Set<String> cbEvents = cbEntry.getValue();
			Set<Integer> counter = new HashSet<Integer>();
			// System.out.println(cbConfId);
			for (String cbEvent : cbEvents) {
				if (cbEvent.equals("")) {
					continue;
				}

				// System.out.println("+-- " + cbEvent);
				for (Entry<Integer, Set<String>> esEntry : emfsResult.getLeftEventStrings().entrySet()) {
					int esConfId = esEntry.getKey();
					Set<String> esEvents = esEntry.getValue();

					// System.out.println("+--+-- " + esConfId);
					for (String esEvent : esEvents) {
						if (esEvent.equals("")) {
							continue;
						}
						// System.out.println("+--+--+-- " + esEvent);
						if (cbEvent.trim().equals(esEvent.trim())) {
							counter.add(esConfId);
						}
					}
				}
			}

			if (counter.size() == 0) {
				undetectedConflicts.add(cbConfId);
			}
		}
		System.out.println("Undetected CBP conflicts: " + undetectedConflicts.size());
		for (int x : undetectedConflicts) {
			System.out.print(x + " ");
		}
		System.out.println();
	}

	/**
	 * @param changeComparison
	 * @param emfsResult
	 */
	private static void getSplittedDetectedCBPConflicts(CBPComparisonImpl changeComparison,
		final EMFStoreResult emfsResult) {

		System.out.println("\nSplitted Detected CBP Conflicts:");
		System.out.println("right:");
		for (Entry<Integer, Set<String>> esEntry : changeComparison.getRightEventStrings().entrySet()) {
			int esConfId = esEntry.getKey();
			Set<String> esEvents = esEntry.getValue();
			Set<Integer> counter = new HashSet<Integer>();

			// System.out.println(esConfId);
			for (String esString : esEvents) {

				if (esString.equals("")) {
					continue;
				}
				// System.out.println("+---ES: " + esString);

				for (Entry<Integer, Set<String>> cbEntry : emfsResult.getRightEventStrings()
					.entrySet()) {
					int cbConfId = cbEntry.getKey();
					Set<String> cbEvents = cbEntry.getValue();
					// System.out.println("+---+--- " + cbConfId);
					for (String cbString : cbEvents) {
						// System.out.println("+---+---+--- " + cbString);
						if (esString.trim().equals(cbString.trim())) {
							counter.add(cbConfId);
							// System.out.println(esString + ": " + esConfId + " => " + cbConfId);
						}
					}
				}
			}

			if (counter.size() > 1) {
				System.out.print(esConfId + " : ");
				for (int c : counter) {
					System.out.print(c + " ");
				}
				System.out.println();
				// System.out.println(": " + esString + "");
			}
		}
		System.out.println("left:");
		for (Entry<Integer, Set<String>> esEntry : changeComparison.getLeftEventStrings().entrySet()) {
			int esConfId = esEntry.getKey();
			Set<String> esEvents = esEntry.getValue();
			Set<Integer> counter = new HashSet<Integer>();

			// System.out.println(esConfId);
			for (String esString : esEvents) {

				if (esString.equals("")) {
					continue;
				}
				// System.out.println("+---ES: " + esString);

				for (Entry<Integer, Set<String>> cbEntry : emfsResult.getLeftEventStrings()
					.entrySet()) {
					int cbConfId = cbEntry.getKey();
					Set<String> cbEvents = cbEntry.getValue();
					// System.out.println("+---+--- " + cbConfId);
					for (String cbString : cbEvents) {
						// System.out.println("+---+---+--- " + cbString);
						if (esString.trim().equals(cbString.trim())) {
							counter.add(cbConfId);
							// System.out.println(esString + ": " + esConfId + " => " + cbConfId);
						}
					}
				}
			}

			if (counter.size() > 1) {
				System.out.print(esConfId + " : ");
				for (int c : counter) {
					System.out.print(c + " ");
				}
				System.out.println();
				// System.out.println(": " + esString + "");
			}
		}
	}

	/**
	 * @param changeComparison
	 * @param emfsResult
	 */
	private static void getUndetectedEMFStoreConflicts(CBPComparisonImpl changeComparison,
		final EMFStoreResult emfsResult) {
		Set<Integer> undetectedConflicts = new HashSet<Integer>();
		System.out.println("\nUndetected EMF Store Conflicts:");
		System.out.println("left:");
		for (Entry<Integer, Set<String>> entry1 : emfsResult.getLeftEventStrings().entrySet()) {
			int confId1 = entry1.getKey();
			Set<String> events1 = entry1.getValue();
			Set<Integer> counter = new HashSet<Integer>();
			// System.out.println(cbConfId);
			for (String event1 : events1) {
				if (event1.equals("")) {
					continue;
				}

				// System.out.println("+-- " + cbEvent);
				for (Entry<Integer, Set<String>> esEntry2 : changeComparison.getLeftEventStrings().entrySet()) {
					int confId2 = esEntry2.getKey();
					Set<String> events2 = esEntry2.getValue();

					// System.out.println("+--+-- " + esConfId);
					for (String event2 : events2) {
						if (event2.equals("")) {
							continue;
						}
						// System.out.println("+--+--+-- " + esEvent);
						if (event1.trim().equals(event2.trim())) {
							counter.add(confId2);
						}
					}
				}
			}

			if (counter.size() == 0) {
				undetectedConflicts.add(confId1);
			}
		}
		System.out.println("Undetected EMF Store conflicts: " + undetectedConflicts.size());
		for (int x : undetectedConflicts) {
			System.out.print(x + " ");
		}
		System.out.println();
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

		originalAdapater = new CBP2EMFStoreAdapter(originalProject, originalProject, originalProject);
		leftAdapater = new CBP2EMFStoreAdapter(leftProject, originalProject, rightProject);
		rightAdapater = new CBP2EMFStoreAdapter(rightProject, originalProject, leftProject);

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
			"num,levc,revc,aoc,clt,clm,cdc,ctt,ctm,cdt,cdm,cxc,cxt,cxm,cct,ccm,lelc,relc,slt,slm,sdc,smt,smm,sdt,sdm,sxc,sxt,sxm,sct,scm,exc,ept,epm,ext,exm,ect,ecm,srx");
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

		File emfsXmiRightFile = new File("D:\\TEMP\\CONFLICTS\\performance\\emfstore-right.xmi");
		File emfsXmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\performance\\emfstore-left.xmi");
		XMIResource emfsXmiRightResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsXmiRightFile.getAbsolutePath()));
		XMIResource emfsXmiLeftResource = (XMIResource) new XMIResourceFactoryImpl()
			.createResource(URI.createFileURI(emfsXmiLeftFile.getAbsolutePath()));

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
		setProbability(seeds, 1, ChangeType.CHANGE);
		setProbability(seeds, 0, ChangeType.ADD);
		setProbability(seeds, 0, ChangeType.DELETE);
		setProbability(seeds, 0, ChangeType.MOVE);

		System.out.println("Loading " + leftCbpFile.getName() + "...");
		leftCbp.load(null);
		leftCbp.startNewSession("LEFT");
		System.out.println("Loading " + rightCbpFile.getName() + "...");
		rightCbp.load(null);
		rightCbp.startNewSession("RIGHT");

		List<EObject> leftEObjectList = identifyAllEObjects(leftCbp);
		List<EObject> rightEObjectList = identifyAllEObjects(rightCbp);

		// ----------------EMF STORE
		originalAdapater.load(originCbpFile, true);
		originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());
		leftProject.update(new ESSystemOutProgressMonitor());
		rightProject.update(new ESSystemOutProgressMonitor());

		long prevLeftCbpSize = leftCbpFile.length();
		long prevRightCbpSize = rightCbpFile.length();
		// -----

		List<BigModelResult> results = new ArrayList<BigModelResult>();
		int modificationCount = 4400;
		int number = 0;
		for (int i = 1; i <= modificationCount; i++) {
			System.out.print("Change " + i + ":");

			// leftCbp.startNewSession("LEFT-" + i);
			startModification(leftCbp, leftEObjectList, seeds);
			leftCbp.save(null);
			// rightCbp.startNewSession("RIGHT-" + i);
			startModification(rightCbp, rightEObjectList, seeds);
			rightCbp.save(null);
			System.out.println();

			// do comparison
			if (i % 200 == 0) {
				// if (i % 20 == 0) {

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
				CBPComparisonImpl changeComparison = new CBPComparisonImpl();
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

				result.setChangeConflictCount(changeComparison.getConflictCount());
				writer.print(result.getChangeConflictCount());
				writer.print(",");
				result.setChangeConflictTime(changeComparison.getConflictTime());
				writer.print(result.getChangeConflictTime());
				writer.print(",");
				result.setChangeConflictMemory(changeComparison.getConflictMemory());
				writer.print(result.getChangeConflictMemory());
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
				originXmi.load(options);
				leftXmi.load(options);
				rightXmi.load(options);
				endTime = System.nanoTime();
				System.gc();
				endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

				ModifiedEMFCompare stateComparison = doEMFComparison(leftXmi, rightXmi, originXmi);

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

				result.setStateConflictCount(stateComparison.getConflicts().size());
				writer.print(result.getStateConflictCount());
				writer.print(",");
				result.setStateConflictTime(stateComparison.getConflictTime());
				writer.print(result.getStateConflictTime());
				writer.print(",");
				result.setStateConflictMemory(stateComparison.getConflictMemory());
				writer.print(result.getStateConflictMemory());
				writer.print(",");

				result.setStateComparisonTime(stateComparison.getComparisonTime());
				writer.print(result.getStateComparisonTime());
				writer.print(",");
				result.setStateComparisonMemory(stateComparison.getComparisonMemory());
				writer.print(result.getStateComparisonMemory());
				writer.print(",");

				if (stateComparison.getConflicts().size() != changeComparison.getConflictCount()) {
					System.console();
				}

				// ----------------EMF STORE
				// leftProject.update(new ESSystemOutProgressMonitor());

				System.out.println("\nEMF STORE:");

				// usersession.refresh();

				EMFStoreResult emfResult = doEMFStoreConflictDetection(rightProject, leftProject, leftCbpFile,
					rightCbpFile, prevLeftCbpSize,
					prevRightCbpSize);

				// ------------
				result.setEmfsConflictCount(emfResult.getEmfsConflictCount());
				writer.print(result.getEmfsConflictCount());
				writer.print(",");
				result.setEmfsPreparationTime(emfResult.getEmfsPreparationTime());
				writer.print(result.getEmfsPreparationTime());
				writer.print(",");
				result.setEmfsPreparationMemory(emfResult.getEmfsPreparationMemory());
				writer.print(result.getEmfsPreparationMemory());
				writer.print(",");
				result.setEmfsConflictTime(emfResult.getEmfsConflictTime());
				writer.print(result.getEmfsConflictTime());
				writer.print(",");
				result.setEmfsConflictMemory(emfResult.getEmfsConflictMemory());
				writer.print(result.getEmfsConflictMemory());
				writer.print(",");

				result.setEmfsComparisonTime(emfResult.getEmfsComparisonTime());
				writer.print(result.getEmfsComparisonTime());
				writer.print(",");
				result.setEmfsComparisonMemory(emfResult.getEmfsComparisonMemory());
				writer.print(result.getEmfsComparisonMemory());
				writer.print(",");

				result.setStateRealConflictCount(stateComparison.getRealConflicts().size());
				writer.print(result.getStateRealConflictCount());
				writer.println();

				writer.flush();

				// ------------
				results.add(result);

				originXmi.unload();
				leftXmi.unload();
				rightXmi.unload();

				System.out.println();

				// prevLeftCbpSize = leftCbpFile.length();
				// prevRightCbpSize = rightCbpFile.length();
				try {
					exportToXMI(leftAdapater, emfsXmiLeftResource);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					exportToXMI(rightAdapater, emfsXmiRightResource);
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println(result.getChangeConflictCount() + " vs " + result.getEmfsConflictCount());
				if (result.getChangeConflictCount() - result.getEmfsConflictCount() >= 2) {
					// System.out.println(result.getChangeConflictCount() + " vs " + result.getEmfsConflictCount());

					// changeComparison.getLeftEventStrings().removeAll(emfResult.getLeftEventStrings());
					// Set<String> leftSet = changeComparison.getLeftEventStrings();
					// changeComparison.getRightEventStrings().removeAll(emfResult.getRightEventStrings());
					// Set<String> rightSet = changeComparison.getRightEventStrings();

					System.console();
				}
				System.console();
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

	/**
	 * @param rightProject
	 * @param leftProject
	 * @param leftCbpFile
	 * @param rightCbpFile
	 * @param prevLeftCbpSize
	 * @param prevRightCbpSize
	 * @throws IOException
	 * @throws FactoryConfigurationError
	 * @throws ESUpdateRequiredException
	 * @throws ESException
	 */
	private static EMFStoreResult doEMFStoreConflictDetection(ESLocalProject rightProject,
		ESLocalProject leftProject,
		File leftCbpFile, File rightCbpFile, long prevLeftCbpSize, long prevRightCbpSize)
		throws IOException, FactoryConfigurationError, ESUpdateRequiredException, ESException {

		final EMFStoreResult result = new EMFStoreResult();

		// process right file first
		System.out.println("loading right model");
		String rightText = readFiles(rightCbpFile, prevRightCbpSize);
		InputStream rightStream = new ByteArrayInputStream(rightText.getBytes());
		rightAdapater.load(rightStream);
		rightStream.close();
		rightProject.commit("RIGHT", null, new ESSystemOutProgressMonitor());

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

			try {

				System.gc();

				leftProject.update(ESVersionSpec.FACTORY.createHEAD(), new ESUpdateCallback() {

					long emfsPrepStartMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					long emfsPrepStartTime = System.nanoTime();

					long emfsConflictStartMemory = 0;
					long emfsConflictStartTime = 0;

					public void noChangesOnServer() {
						// do nothing if there are no changes on the server (in this example we know
						// there are changes anyway)
					}

					public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
						ESModelElementIdToEObjectMapping idToEObjectMapping) {

						long emfsPrepEndTime = System.nanoTime();
						// System.gc();
						long emfsPrepEndMemory = Runtime.getRuntime().totalMemory()
							- Runtime.getRuntime().freeMemory();
						System.out
							.println(
								"\nEMFS preparation time = "
									+ df.format((emfsPrepEndTime - emfsPrepStartTime) / 1000000.0)
									+ " ms ");
						System.out.println(
							"EMFS preparation memory = "
								+ df.format((emfsPrepEndMemory - emfsPrepStartMemory) / 1000000.0)
								+ " MBs");
						result.setEmfsPreparationTime(emfsPrepEndTime - emfsPrepStartTime);
						result.setEmfsPreparationMemory(emfsPrepEndMemory - emfsPrepStartMemory);

						System.gc();
						emfsConflictStartMemory = Runtime.getRuntime().totalMemory()
							- Runtime.getRuntime().freeMemory();
						emfsConflictStartTime = System.nanoTime();

						return true;
					}

					@SuppressWarnings("restriction")
					public boolean conflictOccurred(ESConflictSet changeConflictSet, IProgressMonitor monitor) {

						long emfsConflictEndTime = System.nanoTime();
						// System.gc();
						long emfsConflictEndMemory = Runtime.getRuntime().totalMemory()
							- Runtime.getRuntime().freeMemory();
						System.out
							.println(
								"EMFS conflict time = "
									+ df.format((emfsConflictEndTime - emfsConflictStartTime) / 1000000.0)
									+ " ms ");
						System.out.println(
							"EMFS conflict memory = "
								+ df.format((emfsConflictEndMemory - emfsConflictStartMemory) / 1000000.0)
								+ " MBs");

						result.setEmfsConflictTime(emfsConflictEndTime - emfsConflictStartTime);
						result.setEmfsConflictMemory(emfsConflictEndMemory - emfsConflictStartMemory);
						result.setEmfsComparisonTime(result.getEmfsPreparationTime() + result.getEmfsConflictTime());
						result.setEmfsComparisonMemory(
							result.getEmfsPreparationMemory() + result.getEmfsConflictMemory());

						System.out
							.println(
								"EMFS conflict time = "
									+ df.format(result.getEmfsComparisonTime() / 1000000.0)
									+ " ms ");
						System.out.println(
							"EMFS conflict memory = "
								+ df.format(result.getEmfsComparisonMemory() / 1000000.0)
								+ " MBs");

						printEMFStoreConflicts(changeConflictSet, result);

						result.setChangeConflictSet(changeConflictSet);
						System.out
							.println("\nEMFStore Conflict size = " + changeConflictSet.getConflicts().size());
						result.setEmfsConflictCount(changeConflictSet.getConflicts().size());

						// for (ESConflict conflict : changeConflictSet.getConflicts()) {
						// conflict.resolveConflict(conflict.getLocalOperations(),
						// conflict.getRemoteOperations());
						// }
						return false;
					}
				}, new ESSystemOutProgressMonitor());
			} catch (Exception exe) {
			}

			// if (isConflict == false) {
			// long emfsEndTime = System.nanoTime();
			// System.gc();
			// long emfsEndMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			// System.out
			// .println(
			// "\nEMFS comparison time = " + df.format((emfsEndTime - emfsStartTime) / 1000000.0)
			// + " ms ");
			// System.out.println(
			// "EFMS comparison memory = " + df.format((emfsEndMemory - emfsStartMemory) / 1000000.0)
			// + " MBs");
			// result.setEmfsConflictCount(0);
			// }
		}

		return result;
	}

	public static void printEMFStoreConflicts(ESConflictSet changeConflictSet) {
		printEMFStoreConflicts(changeConflictSet, null);
	}

	@SuppressWarnings("restriction")
	public static void printEMFStoreConflicts(ESConflictSet changeConflictSet, EMFStoreResult result) {
		if (result == null) {
			return;
		}
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

				String[] localStrings = localString.trim().split("@#");
				String[] remoteStrings = remoteString.trim().split("@#");
				int max = localStrings.length;
				if (max < remoteStrings.length) {
					max = remoteStrings.length;
				}

				for (int i = 0; i < max; i++) {
					localString = "";
					remoteString = "";
					if (i < localStrings.length) {
						localString = localStrings[i].trim();
					}
					if (i < remoteStrings.length) {
						remoteString = remoteStrings[i].trim();
					}
					System.out.println(count + ": " + localString + " <-> " + remoteString);

					Set<String> levents = result.getLeftEventStrings().get(count);
					if (levents == null) {
						levents = new LinkedHashSet<String>();
						levents.add(localString.trim());
						result.getLeftEventStrings().put(count, levents);
					} else {
						levents.add(localString.trim());
					}

					Set<String> revents = result.getRightEventStrings().get(count);
					if (revents == null) {
						revents = new LinkedHashSet<String>();
						revents.add(remoteString.trim());
						result.getRightEventStrings().put(count, revents);
					} else {
						revents.add(remoteString.trim());
					}

				}
				System.console();
			}
			// if (printed) {
			count++;
			// printed = false;
			// }
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
					result += "@#" + operationToString(adapter, op);
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
			// if (oldValue instanceof String) {
			// oldValue = "\"" + oldValue + "\"";
			// }
			Object newValue = ((AttributeOperation) operation).getNewValue();
			// if (newValue instanceof String) {
			// newValue = "\"" + newValue + "\"";
			// }
			if (newValue == null) {
				result = "UNSET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
			} else {
				result = "SET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
			}
		} else if (operation instanceof AttributeOperation
			&& ((AttributeOperation) operation).getUnset() == UnsetType.IS_UNSET) {
			String feature = ((AttributeOperation) operation).getFeatureName();
			Object oldValue = ((AttributeOperation) operation).getOldValue();
			// if (oldValue instanceof String) {
			// oldValue = "\"" + oldValue + "\"";
			// }
			Object newValue = ((AttributeOperation) operation).getNewValue();
			// if (newValue instanceof String) {
			// newValue = "\"" + newValue + "\"";
			// }
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
			String esId = null;
			if (((MultiReferenceOperation) operation).getReferencedModelElements().size() != 0) {
				esId = ((MultiReferenceOperation) operation).getReferencedModelElements().get(0).getId();
			}
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

	private static ModifiedEMFCompare doEMFComparison(XMIResource left, XMIResource right, XMIResource origin)
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
		printConflicts(left, right, origin, comparison.getConflicts());

		System.out.println("Matching Time = " + comparator.getMatchTime() / 1000000.0 + " ms");
		System.out.println("Diffing Time = " + comparator.getDiffTime() / 1000000.0 + " ms");
		System.out.println("Conflict Time = " + comparator.getConflictTime() / 1000000.0 + " ms");
		System.out.println("Comparison Time = " + comparator.getComparisonTime() / 1000000.0 + " ms");
		System.out.println("State-based Diff Size = " + diffs.size());
		System.out.println("State-based Conflict Size = " + comparison.getConflicts().size());

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
									try {
										values.add(pos, eObject2);
									} catch (Exception e) {
									} finally {
										leftCbp.endCompositeEvent();
									}
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
								try {
									eObject1.eSet(eReference, eObject2);
								} catch (Exception e) {
								} finally {
									leftCbp.endCompositeEvent();
								}
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
	 * @param cbpResource
	 */
	private static void deleteModification(List<EObject> eObjectList, CBPXMLResourceImpl cbpResource) {
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
					String id = cbpResource.getURIFragment(eObject);
					if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
						try {
							cbpResource.startCompositeEvent();
							EcoreUtil.delete(eObject);
						} catch (Exception e) {
						} finally {
							cbpResource.endCompositeEvent();
						}
						// eObjectList.remove(eObject);
						continue;
					}
				}
				if (eObject.eResource() == null) {
					try {
						cbpResource.startCompositeEvent();
						EcoreUtil.delete(eObject);
					} catch (Exception e) {
					} finally {
						cbpResource.endCompositeEvent();
					}
					// eObjectList.remove(eObject);
					continue;
				}

				if (!eObject.eContents().isEmpty()) {
					continue;
				}

				if (((EReference) eObject.eContainingFeature()).isContainment()) {
					removeObjectFromEObjectList(eObject, eObjectList);
					try {
						cbpResource.startCompositeEvent();
						EcoreUtil.delete(eObject);
					} catch (Exception e) {
					} finally {
						cbpResource.endCompositeEvent();
					}
					// eObjectList.remove(eObject);
					found = true;
				} else {
					try {
						cbpResource.startCompositeEvent();
						EcoreUtil.delete(eObject);
					} catch (Exception e) {
					} finally {
						cbpResource.endCompositeEvent();
					}
					if (eObject.eContainer() == null) {
						try {
							cbpResource.startCompositeEvent();
							EcoreUtil.delete(eObject);
						} catch (Exception e) {
						} finally {
							cbpResource.endCompositeEvent();
						}
						// eObjectList.remove(eObject);
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

	private static void printConflicts(XMIResource left, XMIResource right, XMIResource origin,
		EList<Conflict> conflicts)
		throws FileNotFoundException, IOException {
		System.out.println("\nEMF COMPARE CONFLICTS:");
		int conflictCount = 0;
		for (Conflict conflict : conflicts) {
			if (conflict.getKind() == ConflictKind.REAL || conflict.getKind() == ConflictKind.PSEUDO) {

				conflictCount++;

				EList<Diff> leftDiffs = conflict.getLeftDifferences();
				EList<Diff> rightDiffs = conflict.getRightDifferences();
				boolean foundConflict = false;

				Iterator<Diff> leftIterator = leftDiffs.iterator();
				Iterator<Diff> rightIterator = rightDiffs.iterator();
				while (leftIterator.hasNext() || rightIterator.hasNext()) {
					Diff leftDiff = null;
					if (leftIterator.hasNext()) {
						leftDiff = leftIterator.next();
					}
					Diff rightDiff = null;
					if (rightIterator.hasNext()) {
						rightDiff = rightIterator.next();
					}

					String leftString = "";
					String rightString = "";
					if (leftDiff instanceof AttributeChange || leftDiff instanceof ReferenceChange
						|| leftDiff instanceof ResourceAttachmentChange) {
						leftString = diffToString(left, origin, leftDiff);
					}
					if (rightDiff instanceof AttributeChange || rightDiff instanceof ReferenceChange
						|| rightDiff instanceof ResourceAttachmentChange) {
						rightString = diffToString(right, origin, rightDiff);
					}
					// if (leftString.equals(rightString)) {
					// continue;
					// }
					foundConflict = true;

					System.out.println(conflictCount + ": " + leftString + " <-> " + rightString);
					System.console();
				}
			}
		}
		System.out.println("Conflict count:" + conflictCount);
		if (conflictCount > 0) {
			System.console();
		}
	}

	@SuppressWarnings("unchecked")
	private static String diffToString(XMIResource leftModel, XMIResource rightModel, Diff diff)
		throws FileNotFoundException, IOException {

		String result = "";
		String leftTarget = null;
		String rightTarget = null;
		EObject eLeftTarget = null;
		EObject eRightTarget = null;

		if (diff.getSource() == DifferenceSource.LEFT) {
			eLeftTarget = diff.getMatch().getLeft();
			eRightTarget = diff.getMatch().getOrigin();
		} else {
			eLeftTarget = diff.getMatch().getRight();
			eRightTarget = diff.getMatch().getOrigin();
		}

		if (eLeftTarget != null) {
			leftTarget = leftModel.getURIFragment(eLeftTarget);
		}
		if (eRightTarget != null) {
			rightTarget = rightModel.getURIFragment(eRightTarget);
		}

		if (diff.getKind() == DifferenceKind.ADD) {
			if (diff instanceof AttributeChange) {
				EAttribute eFeature = ((AttributeChange) diff).getAttribute();
				String featureName = eFeature.getName();
				Object value = ((AttributeChange) diff).getValue();
				if (value instanceof String) {
					value = "\"" + value + "\"";
				}
				int index = 0;
				if (eFeature.isMany()) {
					List<Object> list = (List<Object>) eLeftTarget.eGet(eFeature);
					index = list.indexOf(value);
				}
				result = "ADD " + value + " TO " + leftTarget + "." + featureName + " AT " + index;
			} else if (diff instanceof ReferenceChange) {
				EReference eFeature = ((ReferenceChange) diff).getReference();
				String featureName = eFeature.getName();
				EObject eValue = ((ReferenceChange) diff).getValue();
				String value = leftModel.getID(eValue);
				int index = 0;
				if (eFeature.isMany()) {
					List<Object> list = (List<Object>) eLeftTarget.eGet(eFeature);
					index = list.indexOf(eValue);
					result = "ADD " + value + " TO " + leftTarget + "." + featureName + " AT " + index;
				} else {
					result = "SET " + leftTarget + "." + featureName + " FROM " + null + " TO " + value;
				}
			} else if (diff instanceof ResourceAttachmentChange) {
				String featureName = "resource";
				String value = leftModel.getID(eLeftTarget);
				int index = leftModel.getContents().indexOf(eLeftTarget);
				result = "ADD " + value + " TO " + featureName + " AT " + index;
			}
		} else if (diff.getKind() == DifferenceKind.CHANGE) {
			if (diff instanceof AttributeChange) {
				EAttribute eFeature = ((AttributeChange) diff).getAttribute();
				String featureName = eFeature.getName();
				Object rightValue = eRightTarget.eGet(eFeature);
				Object leftValue = eLeftTarget.eGet(eFeature);
				if (rightValue instanceof String) {
					rightValue = "\"" + rightValue + "\"";
				}
				if (leftValue instanceof String) {
					leftValue = "\"" + leftValue + "\"";
				}
				result = "SET " + leftTarget + "." + featureName + " FROM " + rightValue + " TO " + leftValue;
			} else if (diff instanceof ReferenceChange) {
				EReference eFeature = ((ReferenceChange) diff).getReference();
				String featureName = eFeature.getName();
				EObject eOldValue = null;
				String oldValue = null;
				if (eRightTarget != null) {
					eOldValue = (EObject) eRightTarget.eGet(eFeature);
					oldValue = rightModel.getID(eOldValue);
				} else {
					System.console();
				}
				EObject eValue = null;
				String value = null;
				if (eLeftTarget != null) {
					eValue = (EObject) eLeftTarget.eGet(eFeature);
					value = leftModel.getID(eValue);
				}
				result = "SET " + leftTarget + "." + featureName + " FROM " + oldValue + " TO " + value;
			}
		} else if (diff.getKind() == DifferenceKind.MOVE) {
			if (diff instanceof AttributeChange) {
				EAttribute eFeature = ((AttributeChange) diff).getAttribute();
				String featureName = eFeature.getName();
				Object value = ((AttributeChange) diff).getValue();
				String oldPosition = "";
				String position = "";
				EList<EObject> list1 = (EList<EObject>) eRightTarget.eGet(eFeature);
				oldPosition = "" + list1.indexOf(value);
				EList<EObject> list2 = (EList<EObject>) eLeftTarget.eGet(eFeature);
				position = "" + list2.indexOf(value);
				result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO "
					+ position;
			} else if (diff instanceof ReferenceChange) {
				EReference eFeature = ((ReferenceChange) diff).getReference();
				if (eFeature.isContainment()) {
					String featureName = eFeature.getName();
					EObject eValue = ((ReferenceChange) diff).getValue();
					String value = leftModel.getID(eValue);
					EObject eOldValue = rightModel.getEObject(value);
					EReference eOldFeature = (EReference) eOldValue.eContainingFeature();
					String eOldFeatureName = null;
					if (eOldFeature != null) {
						eOldFeatureName = eOldFeature.getName();
					}
					eRightTarget = eOldValue.eContainer();
					rightTarget = rightModel.getID(eRightTarget);
					String oldPosition = "";
					if (eOldFeature != null && eOldFeature.isMany()) {
						EList<EObject> list = (EList<EObject>) eRightTarget.eGet(eOldFeature);
						oldPosition = "" + list.indexOf(eOldValue);
					}
					String position = "";
					if (eFeature.isMany()) {
						EList<EObject> list = (EList<EObject>) eLeftTarget.eGet(eFeature);
						position = "" + list.indexOf(eValue);
					}
					// cross container
					if (!leftTarget.equals(rightTarget) || !featureName.equals(eOldFeatureName)) {
						// from other container/feature
						if (eRightTarget != null) {
							if (eOldFeature.isMany()) {
								oldPosition = "." + oldPosition;
							}
							if (eFeature.isMany()) {
								position = "." + position;
							}
							result = "MOVE " + value + " FROM " + rightTarget + "." + eOldFeatureName + oldPosition
								+ " TO " + leftTarget + "." + featureName + position;
						}
						// from resource
						else {
							eOldFeatureName = "resource";
							oldPosition = "" + rightModel.getContents().indexOf(eValue);
							if (eFeature.isMany()) {
								position = "." + position;
							}
							result = "MOVE " + value + " FROM " + eOldFeatureName + oldPosition + " TO " + leftTarget
								+ "." + featureName + position;
						}
					}
					// within container
					else {
						result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition
							+ " TO " + position;
					}
				} else {
					String featureName = eFeature.getName();
					EObject eValue = ((ReferenceChange) diff).getValue();
					String value = leftModel.getID(eValue);
					Object eOldValue = rightModel.getEObject(value);
					String oldPosition = "";
					String position = "";
					EList<EObject> list1 = (EList<EObject>) eRightTarget.eGet(eFeature);
					oldPosition = "" + list1.indexOf(eOldValue);
					EList<EObject> list2 = (EList<EObject>) eLeftTarget.eGet(eFeature);
					position = "" + list2.indexOf(eValue);
					result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO "
						+ position;
				}
			}

			else if (diff instanceof ResourceAttachmentChange) {
				String featureName = "resource";
				EObject eValue = eLeftTarget;
				String value = null;
				EObject eOldValue = null;
				if (eValue != null) {
					value = leftModel.getID(eValue);
					eOldValue = rightModel.getEObject(value);
				} else {
					eOldValue = diff.getMatch().getRight();
					value = rightModel.getID(eValue);
				}

				// from container/feature
				if (eOldValue.eContainer() != null) {
					eRightTarget = eOldValue.eContainer();
					rightTarget = rightModel.getID(eRightTarget);
					EReference eOldFeature = (EReference) eOldValue.eContainingFeature();
					String eOldFeatureName = eOldFeature.getName();

					String oldPosition = "";
					if (eOldFeature.isMany()) {
						EList<Object> list = (EList<Object>) eRightTarget.eGet(eOldFeature);
						oldPosition = "." + list.indexOf(eOldValue);
					}
					String position = "." + leftModel.getContents().indexOf(eValue);
					result = "MOVE " + value + " FROM " + rightTarget + "." + eOldFeatureName + oldPosition + " TO "
						+ featureName + position;
				}
				// within resource / root level
				else {
					String oldPosition = "" + rightModel.getContents().indexOf(eOldValue);
					String position = "" + leftModel.getContents().indexOf(eValue);
					result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO "
						+ position;
				}
			}
		} else if (diff.getKind() == DifferenceKind.DELETE) {
			if (diff instanceof AttributeChange) {
				EAttribute eFeature = ((AttributeChange) diff).getAttribute();
				String featureName = eFeature.getName();
				Object value = ((AttributeChange) diff).getValue();
				if (value instanceof String) {
					value = "\"" + value + "\"";
				}
				int index = 0;
				if (eFeature.isMany()) {
					List<Object> list = (List<Object>) eRightTarget.eGet(eFeature);
					index = list.indexOf(value);
				}
				result = "DELETE " + value + " FROM " + rightTarget + "." + featureName + " AT " + index;
			} else if (diff instanceof ReferenceChange) {
				EReference eFeature = ((ReferenceChange) diff).getReference();
				String featureName = eFeature.getName();
				EObject eValue = ((ReferenceChange) diff).getValue();
				String value = rightModel.getID(eValue);
				int index = 0;
				if (eFeature.isMany()) {
					List<Object> list = (List<Object>) eRightTarget.eGet(eFeature);
					index = list.indexOf(eValue);
				}
				result = "DELETE " + value + " FROM " + rightTarget + "." + featureName + " AT " + index;
			} else if (diff instanceof ResourceAttachmentChange) {
				String featureName = "resource";
				String value = rightModel.getID(eRightTarget);
				int index = rightModel.getContents().indexOf(eRightTarget);
				result = "DELETE " + value + " FROM " + featureName + " AT " + index;
			}
		}

		return result;

	}

}