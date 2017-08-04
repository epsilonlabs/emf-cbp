package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.node.Node;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NodePerformanceTests extends LoadingPerformanceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public NodePerformanceTests(String extension) {
		super(extension);
	}

	@Test
	public void testDeepTreePerformance() throws Exception {
		String eolCode = "";
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 0; i <= 480; i += 20) {
			appendToOutputText(String.valueOf(i) + "\t");
			String code = "";
			code += "var eRoot = new Node;\n";
			code += "eRoot.name = \"0\";\n";
			code += "for(i in Sequence{1..%1$d}){\n";
			code += "    var node = new Node;\n";
			code += "    node.name = i.asString();\n";
			code += "    eRoot.valNodes.add(node);\n";
			code += "	   eRoot = node;\n";
			code += "}\n";
			eolCode = String.format(code, i);
			//System.out.println(eolCode);
			run(eolCode, true);
		}
		
		saveOutputText();
	    saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testRandomModelPerformance() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		appendLineToOutputText("Start: " + sdf.format(new Date())+ "\n");
		
		String eolCode = "";
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		//for (int i = 3200; i <= 3200; i += 200) {
	    for (int i = 0; i <= 6000; i += 200) {
			eolCode = "";
			appendToOutputText(String.valueOf(i) + "\t");

			int nameIndex = 0;
			for (nameIndex = 0; nameIndex < i; nameIndex++) {
				eolCode += String.format("var e%1$s = new Node;\n", nameIndex);
				eolCode += String.format("e%1$s.name = \"e%1$s\";\n", nameIndex);
			}

			// Random operation
			Map<String, Integer> eventProbabilityMap = new HashMap<>();
			eventProbabilityMap.put("CREATE", 1);
//			eventProbabilityMap.put("SET_ATTRIBUTE", 1);
//			eventProbabilityMap.put("UNSET_ATTRIBUTE", 1);
//			eventProbabilityMap.put("ADD_ATTRIBUTE", 3);
//			eventProbabilityMap.put("MOVE_ATTRIBUTE", 2);
//			eventProbabilityMap.put("REMOVE_ATTRIBUTE", 1);
//			eventProbabilityMap.put("ADD_REFERENCE_REF", 3);
//			eventProbabilityMap.put("MOVE_REFERENCE_REF", 2);
			eventProbabilityMap.put("ADD_REFERENCE_VAL", 3);
			eventProbabilityMap.put("MOVE_REFERENCE_VAL", 2);
			eventProbabilityMap.put("DELETE", 2);

			List<String> operations = new ArrayList<>();
			for (Entry<String, Integer> entry : eventProbabilityMap.entrySet()) {
				for (int k = 0; k < entry.getValue(); k++) {
					operations.add(entry.getKey());
				}
			}

			for (int k = 0; k < nameIndex; k++) {
				String operation = operations.get(ThreadLocalRandom.current().nextInt(operations.size()));
				if (operation.equals("CREATE")) {
					eolCode += String.format("var e%1$s = new Node;\n", nameIndex);
					eolCode += String.format("e%1$s.name = \"e%1$s\";\n", nameIndex);
					nameIndex += 1;
				} else if (operation.equals("SET_ATTRIBUTE")) {
					String value = this.randomString(4);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.name = \"%2$s\";\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} else if (operation.equals("UNSET_ATTRIBUTE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.name = null;\n";
					code += "}\n";
					eolCode += String.format(code, target);
				} else if (operation.equals("ADD_ATTRIBUTE")) {
					int value = ThreadLocalRandom.current().nextInt(3);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.values.add(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} else if (operation.equals("REMOVE_ATTRIBUTE")) {
					int value = ThreadLocalRandom.current().nextInt(3);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.values.remove(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} else if (operation.equals("ADD_REFERENCE_VAL")) {
					String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s and isCircular(%1$s, %2$s) == false){\n";
					code += "    %1$s.valNodes.add(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} else if (operation.equals("ADD_REFERENCE_REF")) {
					String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s){\n";
					code += "    %1$s.refNodes.add(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} else if (operation.equals("MOVE_ATTRIBUTE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					int toIndex = ThreadLocalRandom.current().nextInt(3);
					int fromIndex = ThreadLocalRandom.current().nextInt(3);
					String code = "";
					code += "if (M.owns(%1$s) and %1$s.values.size() > 1){\n";
					code += "    var toIndex = %2$s;\n";
					code += "    var fromIndex = %3$s;\n";
					code += "    if (toIndex >= %1$s.values.size()){\n";
					code += "        toIndex = %1$s.values.size() - 1;\n";
					code += "    }\n";
					code += "    if (fromIndex >= %1$s.values.size()){\n";
					code += "        fromIndex = %1$s.values.size() - 1;\n";
					code += "    }\n";
					code += "    %1$s.values.move(toIndex, fromIndex);\n";
					code += "}\n";
					eolCode += String.format(code, target, toIndex, fromIndex);
				} else if (operation.equals("MOVE_REFERENCE_REF")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					int toIndex = ThreadLocalRandom.current().nextInt(3);
					int fromIndex = ThreadLocalRandom.current().nextInt(3);
					String code = "";
					code += "if (M.owns(%1$s) and %1$s.refNodes.size() > 1){\n";
					code += "    var toIndex = %2$s;\n";
					code += "    var fromIndex = %3$s;\n";
					code += "    if (toIndex >= %1$s.refNodes.size()){\n";
					code += "        toIndex = %1$s.refNodes.size() - 1;\n";
					code += "    }\n";
					code += "    if (fromIndex >= %1$s.refNodes.size()){\n";
					code += "        fromIndex = %1$s.refNodes.size() - 1;\n";
					code += "    }\n";
					code += "    %1$s.refNodes.move(toIndex, fromIndex);\n";
					code += "}\n";
					eolCode += String.format(code, target, toIndex, fromIndex);
				} else if (operation.equals("MOVE_REFERENCE_VAL")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					int toIndex = ThreadLocalRandom.current().nextInt(3);
					int fromIndex = ThreadLocalRandom.current().nextInt(3);
					String code = "";
					code += "if (M.owns(%1$s) and %1$s.valNodes.size() > 1){\n";
					code += "    var toIndex = %2$s;\n";
					code += "    var fromIndex = %3$s;\n";
					code += "    if (toIndex >= %1$s.valNodes.size()){\n";
					code += "        toIndex = %1$s.valNodes.size() - 1;\n";
					code += "    }\n";
					code += "    if (fromIndex >= %1$s.valNodes.size()){\n";
					code += "        fromIndex = %1$s.valNodes.size() - 1;\n";
					code += "    }\n";
					code += "    %1$s.valNodes.move(toIndex, fromIndex);\n";
					code += "}\n";
					eolCode += String.format(code, target, toIndex, fromIndex);
				} else if (operation.equals("DELETE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					//code += "    RemoveRefToObject(%1$s)\n;";
					code += "    delete %1$s;\n";
					code += "}\n";
					eolCode += String.format(code, target);
				}
			}
			// eolCode += "\"Total Node:
			// \".print();Node.all().size().println();\n";
			// eolCode += "operation containedByModel(targetObject): Boolean
			// {\n"
			// + " var result = false;\n"
			// + " if (M.allContents().selectOne(object | object ==
			// targetObject) <> null){\n"
			// + " result = true;\n"
			// + " }\n"
			// + " return result;\n"
			// + "}\n";
			
			eolCode += "operation isCircular(targetObject, valueObject): Boolean {\n";
			eolCode += "	for (child in valueObject.valNodes){\n";
			eolCode += "		if (child == targetObject){\n";
			eolCode += "			return true;\n";
			eolCode += "		}else{\n";
			eolCode += "			return isCircular(\n";
			eolCode += "			targetObject, child);\n";
			eolCode += "		}\n";
			eolCode += "	}\n";
			eolCode += "	return false;\n";
			eolCode += "}\n";

			eolCode += "operation RemoveRefToObject(targetObject){\n";
			eolCode += "	for (child in targetObject.valNodes){\n";
			eolCode += "		RemoveRefToObject(child);\n";
			eolCode += "	}\n";
			eolCode += "	for (object in M.allContents()){\n";
			eolCode += "		if (object.refNode == targetObject){\n";
			eolCode += "			object.refNode = null;\n";
			eolCode += "		}\n";
			eolCode += "		object.refNodes.remove(targetObject);\n";
			eolCode += "	}\n";
			eolCode += "}\n";

			run(eolCode, true);
			// appendLineToOutputText(eolCode);
		}

	    appendLineToOutputText("\nEnd: " + sdf.format(new Date()));
	    saveOutputText();
	    saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testCreateObjectPerformance() throws Exception {
		String eolCode = "";
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 500; i <= 10000; i += 500) {
			appendToOutputText(String.valueOf(i) + "\t");
			String code = "";
			code += "var eRoot = new Node;";
			code += "for(i in Sequence{1..%1$d}){";
			code += "    var node = new Node;";
			code += "    node.name = i.asString();";
			code += "}";
			eolCode = String.format(code, i);
			run(eolCode, true);
		}
		saveOutputText();
	    saveErrorMessages();
		assertEquals(true, true);
	}

	@Override
	public EPackage getEPackage() {
		return NodePackage.eINSTANCE;
	}
	
	@Override
	public Class getNodeClass(){
		return Node.class;
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
