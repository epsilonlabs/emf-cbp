package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.League;
import org.eclipse.emf.emfstore.bowling.Player;
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
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESRemoteProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionSpecImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.ESConflict;
import org.eclipse.emf.emfstore.server.ESConflictSet;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.exceptions.ESUpdateRequiredException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESGlobalProjectId;
import org.eclipse.emf.emfstore.server.model.ESLocalProjectId;
import org.eclipse.emf.emfstore.server.model.ESOperation;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class EMFStoreTest {

	@Test
	public void testUMLModification() {
		System.out.println("Client starting...");
		try {
			final ESServer localServer = ESServer.FACTORY.createAndStartLocalServer();
			runCode(localServer);
			localServer.getLastUsersession().logout();
			ESServer.FACTORY.stopLocalServer();
		} catch (final ESServerStartFailedException e) {
			System.out.println("Server start failed!");
			e.printStackTrace();
		} catch (final ESException e) {
			System.out.println("Connection to Server failed!");
			e.printStackTrace();
		}

		assertEquals(true, true);
	}

	public void runCode(ESServer server) throws ESException {
		System.out.println("Client starting...");
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

		final ESLocalProject leftProject = workspace.createLocalProject("LeftProject");
		for (final ESLocalProject existingLocalProject : workspace.getLocalProjects()) {
			if (existingLocalProject != leftProject) {
				try {
					existingLocalProject.delete(new ESSystemOutProgressMonitor());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	
		final ESUsersession usersession = server.login("super", "super");

		final ESRemoteProject remoteDemoProject = leftProject.shareProject(usersession,
				new ESSystemOutProgressMonitor());
		for (final ESRemoteProject existingRemoteProject : server.getRemoteProjects(usersession)) {
			if (!existingRemoteProject.getGlobalProjectId().equals(remoteDemoProject.getGlobalProjectId())) {
				existingRemoteProject.delete(usersession, new NullProgressMonitor());
			}
		}

		final ESLocalProject rightProject = leftProject.getRemoteProject().checkout("RightProject", usersession,
				new ESSystemOutProgressMonitor());
	

		//create root version
		UMLFactory factory = UMLFactory.eINSTANCE;
		Package leftPackage01 = factory.createPackage();
		leftProject.getModelElements().add(leftPackage01);
		leftPackage01.setName("1");
		
		Package leftPackage02 = factory.createPackage();
		leftProject.getModelElements().add(leftPackage02);
		leftPackage02.setName("2");
		
		Package leftPackage03 = factory.createPackage();
		leftProject.getModelElements().add(leftPackage03);
		leftPackage03.setName("3");
		
		leftPackage02.getPackagedElements().add(leftPackage03);
		leftPackage01.getPackagedElements().add(leftPackage02);
		
		leftProject.commit("Root", null, new ESSystemOutProgressMonitor());
		
		//setup the right version
		leftProject.update(new ESSystemOutProgressMonitor());
		rightProject.update(new ESSystemOutProgressMonitor());

		Package rightPackage01 = (Package) rightProject.getModelElements().get(0);
		if (leftPackage01.getName().equals(rightPackage01.getName())) {
			System.out.println("The names of the packages are same.");
		}

		rightPackage01 = (Package) rightProject.getModelElement(leftProject.getModelElementId(leftPackage01));
		rightPackage01.setName("A");
		
		Package rightPackage02 = (Package) rightProject.getModelElement(leftProject.getModelElementId(leftPackage02));
		EcoreUtil.delete(rightPackage02, true);

		rightProject.commit(new ESSystemOutProgressMonitor());
		
		//setup the left version
		leftPackage03 = (Package) leftProject.getModelElement(leftProject.getModelElementId(leftPackage03));
		leftPackage03.setName("C");
		
		Package leftPackage04 = factory.createPackage();
		leftProject.getModelElements().add(leftPackage04);
		leftPackage04.setName("D");
		
		//commit the left version to raise a conflicts
		try {
			leftProject.commit(new ESSystemOutProgressMonitor());
		} catch (final ESUpdateRequiredException e) {
			leftProject.update(ESVersionSpec.FACTORY.createHEAD(), new ESUpdateCallback() {
				public void noChangesOnServer() {
				}

				
				
				public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changes,
					ESModelElementIdToEObjectMapping idToEObjectMapping) {
	
					
					
					for ( ESChangePackage change :changes) {
						Iterator<ESOperation> iterator = change.operations().iterable().iterator();
						while(iterator.hasNext()) {
							ESOperation operation = iterator.next();
							System.out.println(operation);
						}
					}
					return true;
				}

				public boolean conflictOccurred(ESConflictSet changeConflictSet, IProgressMonitor monitor) {
					
					Iterator<ESConflict> iterator  = changeConflictSet.getConflicts().iterator();
					while(iterator.hasNext()) {
						ESConflict conflict = iterator.next();
//						System.out.println(conflicts.toString());
						
						
						 
						ESGlobalProjectId projectId = leftProject.getGlobalProjectId();
						for (ProjectSpace projectSpace :  ((ESWorkspaceImpl) workspace).toInternalAPI().getProjectSpaces()) {
							System.out.println(projectSpace.getProjectId().getId() + " vs " + projectId.getId());
							if (projectSpace.getProjectId().getId().equals(projectId.getId())){
								AbstractChangePackage changePackage = projectSpace.getLocalChangePackage();
								 ESCloseableIterable<AbstractOperation> list = changePackage.operations();
								Iterator<AbstractOperation> iterator1 = changePackage.operations().iterable().iterator();
								while(iterator1.hasNext()) {
									AbstractOperation operation = iterator1.next();
									System.out.println(operation);
								}
							}
						}
						
//						conflicts.resolveConflict(conflicts.getLocalOperations(), conflicts.getRemoteOperations());
						conflict.resolveConflict(conflict.getLocalOperations(), conflict.getRemoteOperations());
						
						System.out.println("After Resolve Conflict!");
						projectId = leftProject.getGlobalProjectId();
						for (ProjectSpace projectSpace :  ((ESWorkspaceImpl) workspace).toInternalAPI().getProjectSpaces()) {
//							System.out.println(projectSpace.getProjectId().getId() + " vs " + projectId.getId());
							if (projectSpace.getProjectId().getId().equals(projectId.getId())){
								AbstractChangePackage changePackage = projectSpace.getLocalChangePackage();
								 ESCloseableIterable<AbstractOperation> list = changePackage.operations();
								Iterator<AbstractOperation> iterator1 = changePackage.operations().iterable().iterator();
								while(iterator1.hasNext()) {
									AbstractOperation operation = iterator1.next();
									System.out.println(operation);
								}
							}
						}
						
						System.out.println();
					}
					return true;
				}
			}, new ESSystemOutProgressMonitor());
			
			
//			ProjectSpace x = ((ESLocalProjectImpl)  leftProject).toInternalAPI();
			
//			x.getChanges(sourceVersion, targetVersion);
			
			leftProject.commit(new ESSystemOutProgressMonitor());
			System.out.println();
			System.out.println("-------------------LEFT");
			ESGlobalProjectId projectId = leftProject.getGlobalProjectId();
			for (ProjectSpace projectSpace :  ((ESWorkspaceImpl) workspace).toInternalAPI().getProjectSpaces()) {
				if (projectSpace.getProjectId().getId().equals(projectId.getId())){
					AbstractChangePackage changePackage = projectSpace.getLocalChangePackage(false);
					Iterator<AbstractOperation> iterator1 = changePackage.operations().iterable().iterator();
					while(iterator1.hasNext()) {
						AbstractOperation operation = iterator1.next();
						System.out.println(operation);
					}
				}
			}
			
			System.out.println("-------------------RIGHT");
//			rightProject.update(new ESSystemOutProgressMonitor());
//			
			
//			((ESServerImpl) server).toInternalAPI().
			
			ESRemoteProjectImpl prj = (ESRemoteProjectImpl) server.getRemoteProjects(usersession).get(0);
			
			
			ESGlobalProjectId projectId2 = prj.getGlobalProjectId();
			for (ProjectSpace projectSpace :  ((ESWorkspaceImpl) workspace).toInternalAPI().getProjectSpaces()) {
				
				System.out.println(projectSpace.getProjectId().getId() + " vs " + projectId2.getId());
				
				if (projectSpace.getProjectId().getId().equals(projectId2.getId())){
					AbstractChangePackage changePackage = projectSpace.getLocalChangePackage(false);
					Iterator<AbstractOperation> iterator1 = changePackage.operations().iterable().iterator();
					while(iterator1.hasNext()) {
						AbstractOperation operation = iterator1.next();
						System.out.println(operation);
					}
				}
			}
		}

	}

	@Test
	public void testEMFStoreDemo() {
		System.out.println("Client starting...");

		try {
			// Create a client representation for a local server and start a
			// local server.
			final ESServer localServer = ESServer.FACTORY.createAndStartLocalServer();
			// Run a client on the local server that shows the basic features of
			// the EMFstore
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

		assertEquals(true, true);
	}

	public static void runClient(ESServer server) throws ESException {
		System.out.println("Client starting...");

		// The workspace is the core controller to access local and remote
		// projects.
		// A project is a container for models and their elements (EObjects).
		// To get started, we obtain the current workspace of the client.
		final ESWorkspace workspace = ESWorkspaceProvider.INSTANCE.getWorkspace();

		// The workspace stores all available servers that have been configured.
		// We add the local server that has
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

		// The workspace also contains a list of local projects that have either
		// been created locally or checked out
		// from a server.
		// We create a new local project. The project new created is not yet
		// shared with the server.
		final ESLocalProject demoProject = workspace.createLocalProject("DemoProject");

		// We delete all projects from the local workspace other than the one
		// just created.
		for (final ESLocalProject existingLocalProject : workspace.getLocalProjects()) {
			if (existingLocalProject != demoProject) {
				try {
					existingLocalProject.delete(new ESSystemOutProgressMonitor());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		// Next, we create a user session by logging in to the local EMFStore
		// server with default super user
		// credentials.
		final ESUsersession usersession = server.login("super", "super");

		// Now we can share the created local project to our server.
		final ESRemoteProject remoteDemoProject = demoProject.shareProject(usersession,
				new ESSystemOutProgressMonitor());

		// We also retrieve a list of existing (and accessible) remote projects
		// on the server.
		// Remote projects represent a project that is currently available on
		// the server.
		// We delete all remote projects to clean up remaining projects from
		// previous launches.
		for (final ESRemoteProject existingRemoteProject : server.getRemoteProjects(usersession)) {
			if (!existingRemoteProject.getGlobalProjectId().equals(remoteDemoProject.getGlobalProjectId())) {
				existingRemoteProject.delete(usersession, new NullProgressMonitor());
			}
		}

		// Now we are all set: we have a client workspace with one server
		// configured and exactly one project shared to a
		// server with only this one project.

		// We check out a second, independent copy of the project (simulating a
		// second client).
		final ESLocalProject demoProjectCopy = demoProject.getRemoteProject().checkout("DemoProject Copy", usersession,
				new ESSystemOutProgressMonitor());

		// We start working now with the local project and later we will
		// synchronize it with the copy of the project we
		// just checked out.
		// We create some EObjects and add them to the project, that is, to
		// project�s containment tree. Everything
		// that
		// is
		// in the project�s containment tree (spanning tree on containment
		// references) is considered part of the
		// project. We will use an example model about bowling.

		// First we add a league and set the league name.
		final League league = BowlingFactory.eINSTANCE.createLeague();
		league.setName("Suprbowling League");

		// Next we add the league to the root of the project. The project has a
		// containment feature called model
		// element that holds all root elements of a project. This list is
		// comparable to the content list in EMF
		// Resources that
		// you can retrieve with getContents(). Adding something to the list
		// will add it to the project.
		demoProject.getModelElements().add(league);

		// Then we create two players.
		final Player player1 = BowlingFactory.eINSTANCE.createPlayer();
		player1.setName("Maximilian");
		final Player player2 = BowlingFactory.eINSTANCE.createPlayer();
		player2.setName("Ottgar");

		// Finally, we add the players to the league. Since the league is
		// already part of the project and League.players
		// is a containment feature, the players also become part of the
		// project.
		league.getPlayers().add(player1);
		league.getPlayers().add(player2);

		// To synchronize the local changes of the client with the server, we
		// will commit the project.
		demoProject.commit("My message", null, new ESSystemOutProgressMonitor());
		// The server is now up-to-date, but we still need to synchronize the
		// copy of the project we checked out
		// earlier.
		demoProjectCopy.update(new ESSystemOutProgressMonitor());

		// We will now retrieve the copy of the league from the copy of the
		// project and assert its name and player count
		// are equal with the name of the project�s league.
		League leagueCopy = (League) demoProjectCopy.getModelElements().get(0);
		if (league.getName().equals(leagueCopy.getName())
				&& league.getPlayers().size() == leagueCopy.getPlayers().size()) {
			System.out.println("Leagues names and player count are equal.");
		}

		// Of course, we can also change something in the project copy and
		// synchronize it back to the project.
		// We change the league name to correct the type and then commit and
		// update accordingly.
		// This time, we use the IDs assigned to every EObject of a project to
		// identify the copy of league in the
		// project�s copy.
		leagueCopy = (League) demoProjectCopy.getModelElement(demoProject.getModelElementId(league));
		league.setName("Superbowling League");
		demoProject.commit(new ESSystemOutProgressMonitor());
		demoProjectCopy.update(new ESSystemOutProgressMonitor());

		if (league.getName().equals(leagueCopy.getName())
				&& league.getPlayers().size() == leagueCopy.getPlayers().size()) {
			System.out.println("Leagues names and player count are still equal.");
		}
	}

}
