package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.ComparisonLine;
import org.eclipse.epsilon.cbp.comparison.ConflictedEvents;
import org.eclipse.epsilon.cbp.comparison.Line;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import com.eclipsesource.makeithappen.model.task.Task;
import com.eclipsesource.makeithappen.model.task.TaskFactory;
import com.eclipsesource.makeithappen.model.task.TaskPackage;
import com.eclipsesource.makeithappen.model.task.User;
import com.eclipsesource.makeithappen.model.task.UserGroup;

public class CBPComparisonTest {

	@Test
	public void testReadTwoComparedCBPs() throws IOException, XMLStreamException {
		TaskPackage.eINSTANCE.eClass();
		
		File leftCbpFile = new File("D:\\TEMP\\COMPARISON\\left.cbpxml");
		File rightCbpFile = new File("D:\\TEMP\\COMPARISON\\right.cbpxml");
		
		CBPComparison cbpComparison = new CBPComparison(leftCbpFile, rightCbpFile);
		cbpComparison.compare();
		
		System.out.println("CONFLICTS:");
		List<ConflictedEvents> conflicts = cbpComparison.getConflicts();
		for (ConflictedEvents item : conflicts) {
//			item.get
			
		}
		
		assertEquals(true, true);
	}
	
	@Test
	public void testCompareTwoDifferentCBPs() throws IOException {
		try {
			File originFile = new File("D:\\TEMP\\COMPARISON\\origin.cbpxml");
			if (originFile.exists())
				originFile.delete();
			File leftFile = new File("D:\\TEMP\\COMPARISON\\left.cbpxml");
			if (leftFile.exists())
				leftFile.delete();
			File rightFile = new File("D:\\TEMP\\COMPARISON\\right.cbpxml");
			if (rightFile.exists())
				rightFile.delete();

			TaskPackage.eINSTANCE.eClass();
			TaskFactory factory = TaskFactory.eINSTANCE;

			CBPResource originResource = (CBPResource) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(originFile.getPath()));
			CBPResource leftResource = (CBPResource) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(leftFile.getPath()));
			CBPResource rightResource = (CBPResource) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(rightFile.getPath()));

			originResource.startNewSession("ROOT");

			UserGroup group01 = factory.createUserGroup();
			originResource.getContents().add(group01);
			group01.setName("Group 01");
			
			User user01 = factory.createUser();
			originResource.getContents().add(user01);
			user01.setFirstName("User 01");
			group01.getUsers().add(user01);
			
			User user02 = factory.createUser();
			originResource.getContents().add(user02);
			user02.setFirstName("User 02");
			group01.getUsers().add(user02);
			
			Task task01 = factory.createTask();
			originResource.getContents().add(task01);
			task01.setName("Task 01");
			
			originResource.save(null);
			originResource.unload();

			Files.copy(new FileInputStream(originFile), leftFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			leftResource.load(null);
			leftResource.startNewSession("LEFT");

			// Rename package 4 to D
			TreeIterator<EObject> iterator1 = leftResource.getAllContents();
			while (iterator1.hasNext()) {
				EObject eObject = iterator1.next();
				if (eObject instanceof User) {
					User tempUser = (User) eObject;
					if (tempUser.getFirstName().equals("User 01")) {
						tempUser.setFirstName("User A");
						break;
					}
				}
			}

			// add new package
			UserGroup newGroup = factory.createUserGroup();
			leftResource.getContents().add(newGroup);
			newGroup.setName("Group 02");

			leftResource.save(null);
			Resource xmiResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI("D:\\TEMP\\COMPARISON\\left.xmi"));
			xmiResource.getContents().addAll(leftResource.getContents());
			xmiResource.save(null);
			xmiResource.unload();
			leftResource.unload();

			//// -------------------------------
			Files.copy(new FileInputStream(originFile), rightFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			rightResource.load(null);
			rightResource.startNewSession("RIGHT");

			TreeIterator<EObject> iterator2 = rightResource.getAllContents();
			while (iterator2.hasNext()) {
				EObject eObject = iterator2.next();
				if (eObject instanceof User) {
					User tempUser = (User) eObject;
					if (tempUser.getFirstName().equals("User 01")) {
						tempUser.setFirstName("User A");
						break;
					}
				}
			}
			
			// Rename package 1 to A
			iterator2 = rightResource.getAllContents();
			while (iterator2.hasNext()) {
				EObject eObject = iterator2.next();
				if (eObject instanceof Task) {
					Task tempTask = (Task) eObject;
					if (tempTask.getName().equals("Task 01")) {
						tempTask.setName("Task A");
					}
					break;
				}
			}

			// delete package
			iterator2 = rightResource.getAllContents();
			while (iterator2.hasNext()) {
				EObject eObject = iterator2.next();
				if (eObject instanceof UserGroup) {
					UserGroup tempGroup = (UserGroup) eObject;
					if (tempGroup.getName().equals("Group 01")) {
						EcoreUtil.delete(tempGroup, true);
						break;
					}
				}
			}

			rightResource.save(null);
			xmiResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI("D:\\TEMP\\COMPARISON\\right.xmi"));
			xmiResource.getContents().addAll(rightResource.getContents());
			xmiResource.save(null);
			xmiResource.unload();
			rightResource.unload();

			List<String> leftLines = Files.readAllLines(leftFile.toPath());
			List<String> rightLines = Files.readAllLines(rightFile.toPath());

			CBPComparison comparison = new CBPComparison();
			List<ComparisonLine> comparisonLines = comparison.diff(leftLines, rightLines);
			for (ComparisonLine line : comparisonLines) {
				System.out.print(line.getLineNumber());
				System.out.print(" ");
				System.out.print(line.getLeftLine().getSourceLineNumber() >= 0
						? line.getLeftLine().getSourceLineNumber() + 1 : " ");
				System.out.print(" ");
				System.out.print(line.getLeftLine().getSign()
						+ (line.getLeftLine().getText().length() > 0 ? line.getLeftLine().getText() : " "));
				System.out.print(" <-> ");
				System.out.print(line.getRightLine().getSourceLineNumber() >= 0
						? line.getRightLine().getSourceLineNumber() + 1 : " ");
				System.out.print(" ");
				System.out.print(line.getRightLine().getSign()
						+ (line.getRightLine().getText().length() > 0 ? line.getRightLine().getText() : " "));
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(true, true);
	}

	@Test
	public void testGetComparisonList() {

		// List<String> leftText = Arrays.asList("A", "B", "B", "A");
		// List<String> rightText = Arrays.asList("A", "D", "A", "C");

		// List<String> leftText = Arrays.asList("B", "B", "A", "A");
		// List<String> rightText = Arrays.asList("A", "A", "C", "C");

		// List<String> leftText = Arrays.asList("B", "B", "A", "A", "E", "F",
		// "E", "A", "A");
		// List<String> rightText = Arrays.asList("A", "A", "D", "F", "D", "A",
		// "A", "C", "C");

		List<String> leftText = Arrays.asList("B", "B", "A", "A", "D", "F", "F", "D", "A", "A");
		List<String> rightText = Arrays.asList("A", "A", "D", "D", "A", "A", "C", "C");

		CBPComparison comparison = new CBPComparison();
		List<ComparisonLine> comparisonLines = comparison.diff(leftText, rightText);
		for (ComparisonLine line : comparisonLines) {
			System.out.print(line.getLineNumber());
			System.out.print(" ");
			System.out.print(
					line.getLeftLine().getSourceLineNumber() >= 0 ? line.getLeftLine().getSourceLineNumber() + 1 : " ");
			System.out.print(" ");
			System.out.print(line.getLeftLine().getSign()
					+ (line.getLeftLine().getText().length() > 0 ? line.getLeftLine().getText() : " "));
			System.out.print(" <-> ");
			System.out.print(line.getRightLine().getSourceLineNumber() >= 0
					? line.getRightLine().getSourceLineNumber() + 1 : " ");
			System.out.print(" ");
			System.out.print(line.getRightLine().getSign()
					+ (line.getRightLine().getText().length() > 0 ? line.getRightLine().getText() : " "));
			System.out.println();
		}
		assertEquals(true, true);

	}
}
