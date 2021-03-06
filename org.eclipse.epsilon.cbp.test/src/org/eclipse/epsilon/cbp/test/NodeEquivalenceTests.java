package org.eclipse.epsilon.cbp.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NodeEquivalenceTests extends LoadingEquivalenceTests {

	private static final int ITERATION = 5000;
	private static final String ADD_ATTRIBUTE = "ADD_ATTRIBUTE";
	private static final String REMOVE_ATTRIBUTE = "REMOVE_ATTRIBUTE";
	private static final String MOVE_WITHIN_ATTRIBUTE = "MOVE_WITHIN_ATTRIBUTE";
	private static final String SET_REFERENCE = "SET_REFERENCE";
	private static final String UNSET_REFERENCE = "UNSET_REFERENCE";
	private static final String MOVE_WITHIN_REFERENCE = "MOVE_WITHIN_REFERENCE";
	private static final String DELETE = "DELETE";
	private static final String ADD_REFERENCE = "ADD_REFERENCE";
	private static final String UNSET_ATTRIBUTE = "UNSET_ATTRIBUTE";
	private static final String CREATE = "CREATE";
	private static final String SET_ATTRIBUTE = "SET_ATTRIBUTE";

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public NodeEquivalenceTests(String extension) {
		super(extension);
	}

	
	@Test
	public void unsetAttributeTest() throws Exception {
		String eolCode = new String();
		eolCode += "var node = new Node;\n";
		eolCode += "node.name = \"Old Name!\";\n";
		eolCode += "node.name = null;\n";
		eolCode += "node.name = \"New Name!\";\n";
		eolCode += "node.name = null;\n";
		System.out.println(eolCode);
		run(eolCode, true);

	}
	
	@Test
	public void setAttributeTest() throws Exception {
		String eolCode = new String();
		eolCode += "var node = new Node;\n";
		eolCode += "node.name = \"Old Name!\";\n";
		eolCode += "node.name = null;\n";
		eolCode += "node.name = \"New Name!\";\n";
		System.out.println(eolCode);
		run(eolCode, true);

	}

//	@Test
//	public void simpleTest() throws Exception {
//		String eolCode = new String(Files.readAllBytes(Paths.get("data/test.eol")));
//		run(eolCode, true);
//	}
//	
	@Test
	public void nodeTestFromFile() throws Exception {
		String eolCode = new String(Files.readAllBytes(Paths.get("data/node.eol")));
		run(eolCode, true);
	}

	@Test
	public void randomModelTest() throws Exception {
		StringBuilder codeBuilder = new StringBuilder();
		String eolCode = "";
		int nameIncrement = 0;
		List<String> objects = new ArrayList<>();
		List<String> deletedObjects = new ArrayList<>();
		Map<String, Integer> eventProbabilityMap = new HashMap<>();
		eventProbabilityMap.put(CREATE, 1);
		eventProbabilityMap.put(SET_ATTRIBUTE, 1);
		eventProbabilityMap.put(UNSET_ATTRIBUTE, 1);
		eventProbabilityMap.put(ADD_ATTRIBUTE, 1);
		eventProbabilityMap.put(REMOVE_ATTRIBUTE, 1);
		eventProbabilityMap.put(MOVE_WITHIN_ATTRIBUTE, 1);
		eventProbabilityMap.put(SET_REFERENCE, 1);
		eventProbabilityMap.put(UNSET_REFERENCE, 1);
		eventProbabilityMap.put(ADD_REFERENCE, 1);
		eventProbabilityMap.put(MOVE_WITHIN_REFERENCE, 1);
		eventProbabilityMap.put(DELETE, 1);

		List<String> operations = new ArrayList<>();
		for (Entry<String, Integer> entry : eventProbabilityMap.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				operations.add(entry.getKey());
			}
		}

		objects.add("e" + nameIncrement);
		codeBuilder.append(String.format("var %s = new Node;\n", objects.get(objects.size() - 1)));
		codeBuilder.append(
				String.format("%s.name = \"%s\";\n", objects.get(objects.size() - 1), objects.get(objects.size() - 1)));

		for (int i = 0; i < 50; i++) {
			nameIncrement += 1;
			objects.add("e" + nameIncrement);
			codeBuilder.append(String.format("var %s = new Node;\n", objects.get(objects.size() - 1)));
			codeBuilder.append(String.format("%s.name = \"%s\";\n", objects.get(objects.size() - 1),
					objects.get(objects.size() - 1)));
		}

		for (int i = 0; i < ITERATION; i++) {
			String operation = operations.get(ThreadLocalRandom.current().nextInt(operations.size()));

			if (operation.equals(CREATE)) {
				nameIncrement += 1;
				objects.add("e" + nameIncrement);
				codeBuilder.append(String.format("var %s = new Node;\n", objects.get(objects.size() - 1)));
				codeBuilder.append(String.format("%s.name = \"%s\";\n", objects.get(objects.size() - 1),
						objects.get(objects.size() - 1)));
			} else if (operation.equals(SET_ATTRIBUTE) && objects.size() > 0) {
				int index = ThreadLocalRandom.current().nextInt(objects.size());
				String object = objects.get(index);
				codeBuilder.append(String.format("if (containedByModel(%1$s)){\n" + " %1$s.name = \"%2$s\";\n" + "}\n",
						object, this.randomString(3)));
			} else if (operation.equals(UNSET_ATTRIBUTE) && objects.size() > 0) {
				int index = ThreadLocalRandom.current().nextInt(objects.size());
				String object = objects.get(index);
				codeBuilder.append(
						String.format("if (containedByModel(%1$s)){\n" + " %1$s.name = null;\n" + "}\n", object));
			} else if (operation.equals(ADD_ATTRIBUTE) && objects.size() > 0) {
				int index = ThreadLocalRandom.current().nextInt(objects.size());
				String object = objects.get(index);
				codeBuilder.append(String.format(
						"if (containedByModel(%1$s)){\n" + "    %1$s.values.addAll(Collection{0,1,2,3});\n" + "}\n",
						object));
			} else if (operation.equals(REMOVE_ATTRIBUTE) && objects.size() > 0) {
				int index = ThreadLocalRandom.current().nextInt(objects.size());
				int removedValue = ThreadLocalRandom.current().nextInt(4);
				String object = objects.get(index);
				codeBuilder.append(
						String.format("if (containedByModel(%1$s)){\n" + "    %1$s.values.remove(%2$s);\n" + "}\n",
								object, removedValue));
			} else if (operation.equals(MOVE_WITHIN_ATTRIBUTE) && objects.size() > 0) {
				int index = ThreadLocalRandom.current().nextInt(objects.size());
				String object = objects.get(index);
				int toPos = ThreadLocalRandom.current().nextInt(4);
				int fromPos = ThreadLocalRandom.current().nextInt(4);

				codeBuilder.append(String.format("if (containedByModel(%1$s) and %1$s.values.size() > 1){\n"
						+ "    var toPos = %2$s;\n" + "    var fromPos = %3$s;\n"
						+ "    if (toPos >= %1$s.values.size()) toPos = %1$s.values.size() -1;\n"
						+ "    if (fromPos >= %1$s.values.size()) fromPos = %1$s.values.size() -1;\n"
						+ "    %1$s.values.move(toPos, fromPos);\n" + "}\n", object, toPos, fromPos));
			} else if (operation.equals(SET_REFERENCE) && objects.size() > 0) {
				int targetIndex = ThreadLocalRandom.current().nextInt(objects.size());
				String targetObject = objects.get(targetIndex);
				int valueIndex = ThreadLocalRandom.current().nextInt(objects.size());
				String valueObject = objects.get(valueIndex);
				if (!targetObject.equals(valueObject)) {
					codeBuilder.append(String
							.format("if (containedByModel(%1$s) and containedByModel(%2$s) and not isCircular(%1$s, %2$s)){\n"
									+ "    %1$s.parent = %2$s;\n" + "}\n", targetObject, valueObject));
				}
			} else if (operation.equals(UNSET_REFERENCE) && objects.size() > 0) {
				int index = ThreadLocalRandom.current().nextInt(objects.size());
				String object = objects.get(index);
				codeBuilder.append(
						String.format("if (containedByModel(%1$s)){\n" + "    %1$s.parent = null;\n" + "}\n", object));
			} else if (operation.equals(ADD_REFERENCE) && objects.size() > 0) {
				int targetIndex = ThreadLocalRandom.current().nextInt(objects.size());
				String targetObject = objects.get(targetIndex);
				int valueIndex = ThreadLocalRandom.current().nextInt(objects.size());
				String valueObject = objects.get(valueIndex);
				if (!targetObject.equals(valueObject)) {
					codeBuilder.append(String
							.format("if (containedByModel(%1$s) and containedByModel(%2$s) and not isCircular(%1$s, %2$s)){\n"
									+ "    %2$s.parent =%1$s ;\n" 
									+ "    %1$s.valNodes.add(%2$s);\n" 
									+ "}\n", 
									targetObject, valueObject));
				}
			} else if (operation.equals(MOVE_WITHIN_REFERENCE) && objects.size() > 0) {
				int index = ThreadLocalRandom.current().nextInt(objects.size());
				String object = objects.get(index);
				int toPos = ThreadLocalRandom.current().nextInt(4);
				int fromPos = ThreadLocalRandom.current().nextInt(4);
				codeBuilder.append(String.format("if (containedByModel(%1$s) and %1$s.valNodes.size() > 1){\n"
						+ "    var toPos = %2$s;\n" + "    var fromPos = %3$s;\n"
						+ "    if (toPos >= %1$s.valNodes.size()) toPos = %1$s.valNodes.size() -1;\n"
						+ "    if (fromPos >= %1$s.valNodes.size()) fromPos = %1$s.valNodes.size() -1;\n"
						+ "    %1$s.valNodes.move(toPos, fromPos);\n" + "}\n", object, toPos, fromPos));
			} else if (operation.equals(DELETE) && objects.size() > 0) {
				int targetIndex = ThreadLocalRandom.current().nextInt(objects.size());
				String deletedObject = objects.get(targetIndex);
				deletedObjects.add(deletedObject);
				objects.remove(deletedObject);
				codeBuilder.append(String.format("if (containedByModel(%1$s)){\n" + "    RemoveRefToObject(%1$s)\n;"
						+ "    delete %1$s;\n" + "}\n", deletedObject));
			}
		}

		codeBuilder.append("\n");
		codeBuilder.append("\"Total Node: \".print();Node.all().size().println();\n");
		codeBuilder.append("\n");
		codeBuilder.append("operation isCircular(targetObject, valueObject): Boolean {" + "	var result = false;	"
				+ "	for (child in valueObject.valNodes){" + "		if (child == targetObject){"
				+ "			result = true;" + "			break;" + "		}else{" + "			result = isCircular("
				+ "			targetObject, child);" + "		}" + "	}" + "	return result;" + "}");
		codeBuilder.append("\n");
		codeBuilder.append("operation containedByModel(targetObject): Boolean {" + "	var result = false;"
				+ "	if (M.owns(targetObject)){" + "		result = true;"
				+ "	}" + "	return result;" + "}");
		codeBuilder.append("\n");
		codeBuilder.append("operation RemoveRefToObject(targetObject){" + "	for (child in targetObject.valNodes){"
				+ "		RemoveRefToObject(child);" + "	}" + "	for (object in M.allContents()){"
				+ "		if (object.parent == targetObject){" + "			object.parent = null;" + "		} " + "	}"
				+ "}");

		eolCode = codeBuilder.toString();
		System.out.println(eolCode);
		System.out.println("Running...");
		run(eolCode, true);

	}

	@Override
	public EPackage getEPackage() {
		return NodePackage.eINSTANCE;
	}

	public String randomString(int length) {
		String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String result = "";
		for (int i = 0; i < length; i++) {
			int index = ThreadLocalRandom.current().nextInt(alphabets.length());
			result = result + alphabets.charAt(index);
		}
		return result;
	}

}
