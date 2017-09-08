package org.eclipse.epsilon.cbp.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.test.node.Node;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
//import org.eclipse.epsilon.cbp.thrift.CBPThriftResourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NodeCompareTests extends ComparePerformanceTests {

	private static final int MODIFIER_MAX_OPERATION_COUNT = 50000;
	private static final int INITIAL_MAX_OPERATION_COUNT = 0;
	private static final int INITIAL_NODE_COUNT = 10;
	private static Integer nameIndex = 0;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml", new CBPXMLResourceFactory() }
				// ,
				// { "cbpthrift", null/*new CBPThriftResourceFactory()*/ }
		});
	}

	public NodeCompareTests(String extension, Resource.Factory factory) {
		super(extension, factory);
	}

	@Test
	public void simpleTest() throws Exception {
		StringBuilder initialCode = new StringBuilder();
		initialCode.append("var n1 = new Node;\n");
		initialCode.append("n1.name = \"n1\";\n");
		initialCode.append("var n2 = new Node;\n");
		initialCode.append("n2.name = \"n2\";\n");
		initialCode.append("delete n1;\n");

		StringBuilder modifierCode = new StringBuilder();
		modifierCode.append("var n1 = new Node;\n");
		modifierCode.append("n1.name = \"n1\";\n");
		// modifierCode.append("var n5 = M.allContents.selectOne(node |
		// node.name == \"n5\");\n");
		// modifierCode.append("delete n5;\n");
		// modifierCode.append("var n3 = M.allContents.selectOne(node |
		// node.name == \"n3\");\n");
		// modifierCode.append("delete n3;\n");

		this.createInitialMode(initialCode.toString());
		this.debug(modifierCode.toString(), 0);
	}

	@Test
	public void testRandomModelComparison() throws Exception {
		System.out.print("numOps\tNodes\tCBPSize\tEMFSize\tCBPComp\tEMFComp\n");

		StringBuilder modelCode = new StringBuilder();
		StringBuilder operationCode = new StringBuilder();
		StringBuilder initialCode = new StringBuilder();

		// Random operation
		Map<String, Integer> eventProbabilityMap = new HashMap<>();
		eventProbabilityMap.put("CREATE", 1);
		eventProbabilityMap.put("SET_ATTRIBUTE", 1);
		eventProbabilityMap.put("ADD_REFERENCE_VAL", 1);
		eventProbabilityMap.put("DELETE", 2);

		List<String> operations = new ArrayList<>();
		for (Entry<String, Integer> entry : eventProbabilityMap.entrySet()) {
			for (int k = 0; k < entry.getValue(); k++) {
				operations.add(entry.getKey());
			}
		}

		// Create initial model
		for (; nameIndex < INITIAL_NODE_COUNT; nameIndex++) {
			modelCode.append("var n = new Node;\n");
			modelCode.append(String.format("n.name = \"e%1$s\";\n", nameIndex));
		}
		// // random operations
		for (int iOpNum = 1; iOpNum < INITIAL_MAX_OPERATION_COUNT; iOpNum++) {
			String initRandOps = "";
			initRandOps = nextRandomOperation(operations);
			modelCode.append(initRandOps);
		}

		StringBuilder isCircularCode = new StringBuilder();
		isCircularCode.append("//-----------------------\n");
		isCircularCode.append("operation isCircular(targetObject, valueObject): Boolean {\n");
		isCircularCode.append("	for (child in valueObject.valNodes){\n");
		isCircularCode.append("		if (child == targetObject){\n");
		isCircularCode.append("			return true;\n");
		isCircularCode.append("		}else{\n");
		isCircularCode.append("			return isCircular(\n");
		isCircularCode.append("			targetObject, child);\n");
		isCircularCode.append("		}\n");
		isCircularCode.append("	}\n");
		isCircularCode.append("	return false;\n");
		isCircularCode.append("}\n");

		StringBuilder deleteObjectCode = new StringBuilder();
		deleteObjectCode.append("operation deleteObject(object){\n");
		deleteObjectCode.append("	var i : Integer = object.valNodes.size()-1;\n");
		deleteObjectCode.append("	while (i >= 0){\n");
		deleteObjectCode.append("		var x = object.valNodes.get(i);\n");
		deleteObjectCode.append("		deleteObject(x);\n");
		deleteObjectCode.append("		i -= 1;\n");
		deleteObjectCode.append("	}\n");
		deleteObjectCode.append("	delete object;\n");
		deleteObjectCode.append("}\n");

		initialCode.append(modelCode.toString());
		initialCode.append(isCircularCode.toString());
		initialCode.append(deleteObjectCode.toString());

		this.createInitialMode(initialCode.toString());

		// modifier operations to modify the random model

		StringBuilder modifierCode = new StringBuilder();
		for (int j = 0; j < MODIFIER_MAX_OPERATION_COUNT; j+=100) {
			modifierCode.setLength(0);
			for (int i = 0; i < j; i++) {
				String modRandOps = "";
				modRandOps = nextRandomOperation(operations);
				operationCode.append(modRandOps);
				modifierCode.setLength(0);
			}
			modifierCode.append(operationCode.toString());
			modifierCode.append(isCircularCode.toString());
			modifierCode.append(deleteObjectCode.toString());
			this.run(modifierCode.toString(), j);
		}

	}

	@Override
	public EPackage getEPackage() {
		return NodePackage.eINSTANCE;
	}

	@Override
	public Class<?> getNodeClass() {
		return Node.class;
	}

	public String nextRandomOperation(List<String> operations) {
		StringBuilder eolCode = new StringBuilder();
		String operation = operations.get(ThreadLocalRandom.current().nextInt(operations.size()));
		if (operation.equals("CREATE")) {
			eolCode.append(String.format("var e%1$s = new Node;\n", nameIndex));
			eolCode.append(String.format("e%1$s.name = \"e%1$s\";\n", nameIndex));
			nameIndex = nameIndex + 1;
		} else if (operation.equals("SET_ATTRIBUTE")) {
			String value = this.randomString(4);
			String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
			StringBuilder code = new StringBuilder();
			code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
			code.append("if (M.owns(%1$s)){\n");
			code.append("    %1$s.name = \"%2$s\";\n");
			code.append("}\n");
			eolCode.append(String.format(code.toString(), target, value));
		} else if (operation.equals("ADD_REFERENCE_VAL")) {
			String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
			String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
			StringBuilder code = new StringBuilder();
			code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
			code.append("var %2$s = M.allContents.selectOne(node | node.name == \"%2$s\");\n");
			code.append("if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s and isCircular(%1$s, %2$s) == false){\n");
			code.append("    %2$s.parent = %1$s;\n");
			code.append("    %1$s.valNodes.add(%2$s);\n");
			code.append("}\n");
			eolCode.append(String.format(code.toString(), target, value));
		} else if (operation.equals("DELETE")) {
			String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
			StringBuilder code = new StringBuilder();
			code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
			code.append("if (M.owns(%1$s)){\n");
			code.append("    deleteObject(%1$s);\n");
			code.append("}\n");
			eolCode.append(String.format(code.toString(), target));
		}

		return eolCode.toString();
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
