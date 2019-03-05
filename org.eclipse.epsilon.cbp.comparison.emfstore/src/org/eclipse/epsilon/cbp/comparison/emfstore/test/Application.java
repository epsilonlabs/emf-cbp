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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.stream.FactoryConfigurationError;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMIResource;
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
import org.eclipse.emf.emfstore.server.ESConflict;
import org.eclipse.emf.emfstore.server.ESConflictSet;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.exceptions.ESUpdateRequiredException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESOperation;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.epsilon.cbp.comparison.emfstore.CBP2EMFStoreAdapter;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * An application that runs the demo.<br>
 * Run a client and local server that demo the basic features of EMFStore.
 */
public class Application implements IApplication {

	final static Map<String, String> id2esIdMap = new HashMap<String, String>();
	final static Map<String, String> esId2IdMap = new HashMap<String, String>();
	final static Map<String, EObject> id2EObjectMap = new HashMap<String, EObject>();
	final static Map<EObject, String> eObject2IdMap = new HashMap<EObject, String>();
	final static File xmiFile = new File("D:\\TEMP\\CONFLICTS\\temp\\emfstore-target.xmi");
	final static XMIResource xmiResource = (XMIResource) new XMIResourceFactoryImpl()
		.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));

	/**
	 * {@inheritDoc}
	 */
	public Object start(IApplicationContext context) {

		try {
			// Create a client representation for a local server and start a local server.
			final ESServer localServer = ESServer.FACTORY.createAndStartLocalServer();
			// Run a client on the local server that shows the basic features of the EMFstore
			runClient(localServer);
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

	/**
	 * Run an EMFStore Client connecting to the given server.
	 *
	 * @param server the server
	 * @throws ESException if the server connection fails
	 */
	public static void runClient(ESServer server) throws ESException {
		System.out.println("Client starting...");

		EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
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

		CBP2EMFStoreAdapter originalAdapater = new CBP2EMFStoreAdapter(originalProject, id2EObjectMap,
			eObject2IdMap, id2esIdMap, esId2IdMap);
		CBP2EMFStoreAdapter leftAdapater = new CBP2EMFStoreAdapter(leftProject, id2EObjectMap, eObject2IdMap,
			id2esIdMap, esId2IdMap);
		CBP2EMFStoreAdapter rightAdapater = new CBP2EMFStoreAdapter(rightProject, id2EObjectMap, eObject2IdMap,
			id2esIdMap, esId2IdMap);

		// final File cbpOriginalFile = new File("D:\\TEMP\\FASE\\Debug\\origin.cbpxml");
		// final File cbpLeftFile = new File("D:\\TEMP\\FASE\\Debug\\left.cbpxml");
		// final File cbpRightFile = new File("D:\\TEMP\\FASE\\Debug\\right.cbpxml");

		final File cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\temp\\origin.cbpxml");
		final File cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\temp\\left.cbpxml");
		final File cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\temp\\right.cbpxml");

		long skip = cbpOriginalFile.length();

		try {
			originalAdapater.load(cbpOriginalFile);
			originalProject.commit("ORIGIN", null, new ESSystemOutProgressMonitor());
			rightProject.update(new ESSystemOutProgressMonitor());
			leftProject.update(new ESSystemOutProgressMonitor());

			// EObject originalObject = id2EObjectMap.get("O-0");
			// ESModelElementId originalEsId = originalProject.getModelElementId(originalObject);

			// process right file first
			String rightText = readFiles(cbpRightFile, skip);
			InputStream rightStream = new ByteArrayInputStream(rightText.getBytes());
			rightAdapater.load(rightStream);
			rightStream.close();
			rightProject.commit("RIGHT", null, new ESSystemOutProgressMonitor());
			// EObject rightObject = rightProject.getModelElement(originalEsId);
			// ESModelElementId rightEsId = rightProject.getModelElementId(rightObject);

			// process left
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
								String localString = operationToString(localOperation);
								String remoteString = operationToString(remoteOperation);
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

			exportToXMI(leftProject);

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
	 * @param project
	 * @throws IOException
	 */
	private static void exportToXMI(ESLocalProject project) throws IOException {
		Iterator<EObject> iterator2 = project.getAllModelElements().iterator();
		while (iterator2.hasNext()) {
			EObject obj2 = iterator2.next();
			String esId = ((ESModelElementIdImpl) project.getModelElementId(obj2)).getId();
			String id = esId2IdMap.get(esId);
			eObject2IdMap.put(obj2, id);
			System.console();
		}

		xmiResource.getContents().addAll(project.getModelElements());
		TreeIterator<EObject> iterator1 = xmiResource.getAllContents();
		while (iterator1.hasNext()) {
			EObject obj1 = iterator1.next();
			String id = eObject2IdMap.get(obj1);
			xmiResource.setID(obj1, id);
		}
		xmiResource.save(null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
	}

	protected static String readFiles(File file, long skip) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

		bufferedReader.skip(skip);
		bufferedReader.mark(0);
		bufferedReader.reset();
		String text = bufferedReader.lines().collect(Collectors.joining());
		bufferedReader.close();
		return text;
	}

	@SuppressWarnings("restriction")
	private static String operationToString(AbstractOperation operation) {
		if (operation == null) {
			return "";
		}

		String result = null;
		String target = esId2IdMap
			.get(operation.getModelElementId().getId());
		// CompositeOperationImpl, CreateDeleteOperationImpl, AttributeOperationImpl,
		// MultiAttributeMoveOperationImpl, MultiAttributeOperationImpl,
		// MultiAttributeSetOperationImpl, MultiReferenceMoveOperationImpl, ReferenceOperationImpl
		//
		// CompositeOperationImpl
		// MultiAttributeOperationImpl,
		// MultiAttributeSetOperationImpl
		if (operation instanceof CreateDeleteOperation
			&& ((CreateDeleteOperation) operation).isDelete()) {
			String className = ((CreateDeleteOperation) operation).getModelElement().eClass().getName();
			String esId = ((CreateDeleteOperation) operation).getModelElementId().getId();
			String value = esId2IdMap.get(esId);
			result = "DELETE " + value + " TYPE " + className;
		} else if (operation instanceof CreateDeleteOperation
			&& !((CreateDeleteOperation) operation).isDelete()) {
			String className = ((CreateDeleteOperation) operation).getModelElement().eClass().getName();
			String esId = ((CreateDeleteOperation) operation).getModelElementId().getId();
			String value = esId2IdMap.get(esId);
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
			String value = esId2IdMap.get(esId);
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
			String oldValue = esId2IdMap.get(esOldId);
			String esNewId = ((SingleReferenceOperation) operation).getNewValue().getId();
			String newValue = esId2IdMap.get(esNewId);
			result = "SET " + target + "." + feature + " FROM " + oldValue + " TO " + newValue;
		} else if (operation instanceof MultiReferenceOperation && ((MultiReferenceOperation) operation).isAdd()) {
			String feature = ((MultiReferenceOperation) operation).getFeatureName();
			String esId = ((MultiReferenceOperation) operation).getReferencedModelElements().get(0).getId();
			String value = esId2IdMap.get(esId);
			int index = ((MultiReferenceOperation) operation).getIndex();
			result = "ADD " + value + " TO " + target + "." + feature + " AT " + index;
		} else if (operation instanceof MultiReferenceOperation && !((MultiReferenceOperation) operation).isAdd()) {
			String feature = ((MultiReferenceOperation) operation).getFeatureName();
			String esId = ((MultiReferenceOperation) operation).getReferencedModelElements().get(0).getId();
			String value = esId2IdMap.get(esId);
			int index = ((MultiReferenceOperation) operation).getIndex();
			result = "REMOVE " + value + " FROM " + target + "." + feature + " AT " + index;
		} else {
			System.out.println(operation);
		}

		return result;
	}

}