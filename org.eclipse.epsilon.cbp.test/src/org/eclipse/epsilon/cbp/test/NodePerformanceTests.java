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
	public void testRandomModelPerformance() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		appendLineToOutputText("Start: " + sdf.format(new Date()) + "\n");

		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP"
				+ "\tTAtSU\tTReSU\tTAtARM\tTReARM\tTiDel"

				+ "\tSetAtt\tUnsAtt\tAddAtt\tRemAtt\tMovAtt" + "\tSetRef\tUnsRef\tAddRef\tRemRef\tMovRef"
				+ "\tDelete\tAddRes\tRemRes\tPackage\tSession\tCreate"

				+ "\tiSetAtt\tiUnsAtt\tiAddAtt\tiRemAtt\tiMovAtt" + "\tiSetRef\tiUnsRef\tiAddRef\tiRemRef\tiMovRef"
				+ "\tiDelete\tiAddRes\tiRemRes\tiPackg\tiSessio\tiCreate"

				+ "\taSetAtt\taUnsAtt\taAddAtt\taRemAtt\taMovAtt" + "\taSetRef\taUnsRef\taAddRef\taRemRef\taMovRef"
				+ "\taDelete\taAddRes\taRemRes\taPackg\taSessio\taCreate");

		for (int i = 0; i <= 100000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");

			int nameIndex = 0;
			eolCode.append(String.format("var root = new Node;\n", nameIndex));
			for (nameIndex = 0; nameIndex < i; nameIndex++) {
				eolCode.append(String.format("var e%1$s = new Node;\n", nameIndex));
				eolCode.append(String.format("e%1$s.name = \"e%1$s\";\n", nameIndex));
				eolCode.append(String.format("root.valNodes.add(e%1$s);\n", nameIndex));
			}

			// Random operation
			Map<String, Integer> eventProbabilityMap = new HashMap<>();
			eventProbabilityMap.put("CREATE", 1);
			eventProbabilityMap.put("SET_ATTRIBUTE", 1);
			eventProbabilityMap.put("UNSET_ATTRIBUTE", 1);
			eventProbabilityMap.put("SET_REFERENCE", 1);
			eventProbabilityMap.put("UNSET_REFERENCE", 1);
			eventProbabilityMap.put("ADD_ATTRIBUTE", 10);
			eventProbabilityMap.put("MOVE_ATTRIBUTE", 5);
			eventProbabilityMap.put("REMOVE_ATTRIBUTE", 1);
			eventProbabilityMap.put("ADD_REFERENCE_REF", 10);
			eventProbabilityMap.put("MOVE_REFERENCE_REF", 5);
			eventProbabilityMap.put("ADD_REFERENCE_VAL", 10);
			eventProbabilityMap.put("MOVE_REFERENCE_VAL", 5);
			eventProbabilityMap.put("DELETE", 1);

			List<String> operations = new ArrayList<>();
			for (Entry<String, Integer> entry : eventProbabilityMap.entrySet()) {
				for (int k = 0; k < entry.getValue(); k++) {
					operations.add(entry.getKey());
				}
			}

			for (int k = 0; k < nameIndex * 1; k++) {
				String operation = operations.get(ThreadLocalRandom.current().nextInt(operations.size()));
				if (operation.equals("CREATE")) {
					eolCode.append(String.format("var e%1$s = new Node;\n", nameIndex));
					eolCode.append(String.format("e%1$s.name = \"e%1$s\";\n", nameIndex));
					eolCode.append(String.format("root.valNodes.add(e%1$s);\n", nameIndex));
					nameIndex += 1;
				} else if (operation.equals("SET_ATTRIBUTE")) {
					String value = this.randomString(4);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s)){\n");
					code.append("    %1$s.defName = \"%2$s\";\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, value));
				} else if (operation.equals("UNSET_ATTRIBUTE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s)){\n");
					code.append("    %1$s.defName = null;\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target));
				} else if (operation.equals("SET_REFERENCE")) {
					String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append(
							"if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s and isCircular(%1$s, %2$s) == false){\n");
					code.append("    %1$s.refNode = %2$s;\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, value));
				} else if (operation.equals("UNSET_REFERENCE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s)){\n");
					code.append("    %1$s.refNode = null;\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target));
				} else if (operation.equals("ADD_ATTRIBUTE")) {
					int value = ThreadLocalRandom.current().nextInt(3);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s)){\n");
					code.append("    %1$s.values.add(%2$s);\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, value));
				} else if (operation.equals("REMOVE_ATTRIBUTE")) {
					int value = ThreadLocalRandom.current().nextInt(3);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s)){\n");
					code.append("    %1$s.values.remove(%2$s);\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, value));
				}
				// else if (operation.equals("ADD_REFERENCE_VAL")) {
				// String value = "e" +
				// ThreadLocalRandom.current().nextInt(nameIndex);
				// String target = "e" +
				// ThreadLocalRandom.current().nextInt(nameIndex);
				// StringBuilder code = new StringBuilder();
				// code.append(
				// "if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s and
				// isCircular(%1$s, %2$s) == false){\n");
				// code.append(" %2$s.parent = %1$s;\n");
				// code.append(" %1$s.valNodes.add(%2$s);\n");
				// code.append("}\n");
				// eolCode.append(String.format(code.toString(), target,
				// value));
				// } else if (operation.equals("ADD_REFERENCE_REF")) {
				// String value = "e" +
				// ThreadLocalRandom.current().nextInt(nameIndex);
				// String target = "e" +
				// ThreadLocalRandom.current().nextInt(nameIndex);
				// StringBuilder code = new StringBuilder();
				// code.append("if (M.owns(%1$s) and M.owns(%2$s) and %1$s <>
				// %2$s){\n");
				// code.append(" %1$s.refNodes.add(%2$s);\n");
				// code.append("}\n");
				// eolCode.append(String.format(code.toString(), target,
				// value));
				// }
				else if (operation.equals("ADD_REFERENCE_VAL")) {
					String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append("var n1 = M.allContents.selectOne(item | item.name == \"%1$s\");");
					code.append("var n2 = M.allContents.selectOne(item | item.name == \"%2$s\");");
					code.append("if (M.owns(n1) and M.owns(n2) and n1 <> n2 ");
					code.append("    and isCircular(n1, n2) == false){\n");
					code.append("    if (n1.deep + 1 > 300){\n");
					code.append("        n2.deep = n1.parent.deep + 1;\n");
					code.append("        n2.parent = n1.parent;\n");
					code.append("        n1.parent.valNodes.add(n2);\n");
					code.append("    }else{\n");
					code.append("        n2.deep = n1.deep + 1;\n");
					code.append("        n2.parent = n1;\n");
					code.append("    	 n1.valNodes.add(n2);\n");
					code.append("    }");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, value));
				}
				// else if (operation.equals("ADD_REFERENCE_REF")) {
				// String value = "e" +
				// ThreadLocalRandom.current().nextInt(nameIndex);
				// String target = "e" +
				// ThreadLocalRandom.current().nextInt(nameIndex);
				// StringBuilder code = new StringBuilder();
				// code.append("if (M.owns(%1$s) and M.owns(%2$s) and %1$s <>
				// %2$s){\n");
				// code.append(" if (%1$s.deep + 1 > 300){\n");
				// code.append(" var x = %1$s.parent;\n");
				// code.append(" %2$s.deep = x.deep + 1;\n");
				// code.append(" %2$s.parent = x;\n");
				// code.append(" x.refNodes.add(%2$s);\n");
				// code.append(" }else{\n");
				// code.append(" %2$s.deep = %1$s.deep + 1;\n");
				// code.append(" %2$s.parent = %1$s;\n");
				// code.append(" %1$s.refNodes.add(%2$s);\n");
				// code.append(" }");
				// code.append("}\n");
				// eolCode.append(String.format(code.toString(), target,
				// value));
				// }
				else if (operation.equals("MOVE_ATTRIBUTE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					int toIndex = 0;
					int fromIndex = 1;
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s) and %1$s.values.size() > 1){\n");
					code.append("    var toIndex = %2$s;\n");
					code.append("    var fromIndex = %3$s;\n");
					// code.append(" if (toIndex >= %1$s.values.size()){\n");
					// code.append(" toIndex = %1$s.values.size() - 1;\n");
					// code.append(" }\n");
					// code.append(" if (fromIndex >= %1$s.values.size()){\n");
					// code.append(" fromIndex = %1$s.values.size() - 1;\n");
					// code.append(" }\n");
					code.append("    %1$s.values.move(toIndex, fromIndex);\n");
					code.append("    %1$s.values.move(toIndex, fromIndex);\n");
					code.append("    %1$s.values.move(toIndex, fromIndex);\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, toIndex, fromIndex));
				} else if (operation.equals("MOVE_REFERENCE_REF")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					int toIndex = 0;
					int fromIndex = 1;
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s) and %1$s.refNodes.size() > 1){\n");
					code.append("    var toIndex = %2$s;\n");
					code.append("    var fromIndex = %3$s;\n");
					// code.append(" if (toIndex >= %1$s.refNodes.size()){\n");
					// code.append(" toIndex = %1$s.refNodes.size() - 1;\n");
					// code.append(" }\n");
					// code.append(" if (fromIndex >=
					// %1$s.refNodes.size()){\n");
					// code.append(" fromIndex = %1$s.refNodes.size() - 1;\n");
					// code.append(" }\n");
					code.append("    %1$s.refNodes.move(toIndex, fromIndex);\n");
					code.append("    %1$s.refNodes.move(toIndex, fromIndex);\n");
					code.append("    %1$s.refNodes.move(toIndex, fromIndex);\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, toIndex, fromIndex));
				} else if (operation.equals("MOVE_REFERENCE_VAL")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					int toIndex = 0;
					int fromIndex = 1;
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s) and %1$s.valNodes.size() > 1){\n");
					code.append("    var toIndex = %2$s;\n");
					code.append("    var fromIndex = %3$s;\n");
					// code.append(" if (toIndex >= %1$s.valNodes.size()){\n");
					// code.append(" toIndex = %1$s.valNodes.size() - 1;\n");
					// code.append(" }\n");
					// code.append(" if (fromIndex >=
					// %1$s.valNodes.size()){\n");
					// code.append(" fromIndex = %1$s.valNodes.size() - 1;\n");
					// code.append(" }\n");
					code.append("    %1$s.valNodes.move(toIndex, fromIndex);\n");
					code.append("    %1$s.valNodes.move(toIndex, fromIndex);\n");
					code.append("    %1$s.valNodes.move(toIndex, fromIndex);\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target, toIndex, fromIndex));
				} else if (operation.equals("DELETE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					StringBuilder code = new StringBuilder();
					code.append("if (M.owns(%1$s)){\n");
					code.append("    deleteObject(%1$s);\n");
					code.append("}\n");
					eolCode.append(String.format(code.toString(), target));
				}
			}

			eolCode.append("operation isCircular(targetObject, valueObject): Boolean {\n");
			eolCode.append("	for (child in valueObject.valNodes){\n");
			eolCode.append("		if (child == targetObject){\n");
			eolCode.append("			return true;\n");
			eolCode.append("		}else{\n");
			eolCode.append("			return isCircular(\n");
			eolCode.append("			targetObject, child);\n");
			eolCode.append("		}\n");
			eolCode.append("	}\n");
			eolCode.append("	return false;\n");
			eolCode.append("}\n");

			eolCode.append("operation deleteObject(object){\n");
			eolCode.append("	var i : Integer = object.valNodes.size()-1;\n");
			eolCode.append("	while (i >= 0){\n");
			eolCode.append("		var x = object.valNodes.get(i);\n");
			eolCode.append("		deleteObject(x);\n");
			eolCode.append("		i -= 1;\n");
			eolCode.append("	}\n");
			eolCode.append("	delete object;\n");
			eolCode.append("}\n");

			run(eolCode.toString(), true);
			// appendLineToOutputText(eolCode);
		}

		appendLineToOutputText("\nEnd: " + sdf.format(new Date()) + "\n");
		saveOutputText();
		saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testCreateSmallNumberOfObjectsPerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 0; i < 100; i += 1) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("for(i in Sequence{0..%1$d}){\n");
			// code.append(" i.println();\n");
			code.append("   var node = new Node;\n");
			code.append("}\n");
			eolCode.append(String.format(code.toString(), i));
			// System.out.println(eolCode);
			run(eolCode.toString(), true);
		}
		// saveOutputText();
		// saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testCreateObjectPerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 500; i <= 10000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("var eRoot = new Node;\n");
			code.append("for(i in Sequence{1..%1$d}){\n");
			code.append("    var node = new Node;\n");
			code.append("    node.name = i.asString();\n");
			code.append("}\n");
			eolCode.append(String.format(code.toString(), i));
			run(eolCode.toString(), true);
		}
		saveOutputText();
		saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testSetUnsetPerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		long before = System.currentTimeMillis();
		for (int i = 0; i <= 10000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("var root = new Node;\n");
			code.append("for(i in Sequence{0..%1$d}){\n");
			code.append("    root.name = i.asString();\n");
			code.append("}\n");
			code.append("root.name = null;\n");
			// code.append("deleteObject(root);\n");
			eolCode.append(String.format(code.toString(), i));
			eolCode.append("operation deleteObject(object){\n");
			eolCode.append("	var i : Integer = object.valNodes.size()-1;\n");
			eolCode.append("	while (i >= 0){\n");
			eolCode.append("		var x = object.valNodes.get(i);\n");
			eolCode.append("		deleteObject(x);\n");
			eolCode.append("		i -= 1;\n");
			eolCode.append("	}\n");
			eolCode.append("	delete object;\n");
			eolCode.append("}\n");
			// System.out.println(eolCode);
			run(eolCode.toString(), true);
		}
		long after = System.currentTimeMillis();
		saveOutputText();
		// saveErrorMessages();
		System.out.println("Time: " + (after - before) / 1000);
		assertEquals(true, true);
	}

	@Test
	public void testCreateDeletePerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		long before = System.currentTimeMillis();
		for (int i = 0; i <= 10000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("var root = new Node;\n");
			code.append("for(i in Sequence{0..%1$d}){\n");
			code.append("    var node = new Node;\n");
			code.append("	 root.valNodes.add(node);\n");
			// code.append(" delete node;\n");
			code.append("}\n");
			code.append("root.valNodes.clear();\n");
			// code.append("deleteObject(root);\n");
			eolCode.append(String.format(code.toString(), i));
			eolCode.append("operation deleteObject(object){\n");
			eolCode.append("	var i : Integer = object.valNodes.size()-1;\n");
			eolCode.append("	while (i >= 0){\n");
			eolCode.append("		var x = object.valNodes.get(i);\n");
			eolCode.append("		deleteObject(x);\n");
			eolCode.append("		i -= 1;\n");
			eolCode.append("	}\n");
			eolCode.append("	delete object;\n");
			eolCode.append("}\n");
			// System.out.println(eolCode);
			run(eolCode.toString(), true);
		}
		long after = System.currentTimeMillis();
		saveOutputText();
		// saveErrorMessages();
		System.out.println("Time: " + (after - before) / 1000);
		assertEquals(true, true);
	}

	@Test
	public void testDeepTreeRemoveRootPerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		long before = System.currentTimeMillis();
		for (int i = 0; i <= 10000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("var a = new Node;\n");
			code.append("var b = new Node;\n");
			code.append("var c = new Node;\n");
			code.append("for(i in Sequence{0..%1$d}){\n");
			code.append("    a.valNodes.add(c);\n");
			code.append("	 b.valNodes.add(c);\n");
			code.append("}\n");
			// code.append("var root = new Node;\n");
			// code.append("for(i in Sequence{0..%1$d}){\n");
			// code.append(" var node = new Node;\n");
			// code.append(" root.valNodes.add(node);\n");
			// code.append("}\n");
			// code.append("deleteObject(root);\n");
			eolCode.append(String.format(code.toString(), i));
			eolCode.append("operation deleteObject(object){\n");
			eolCode.append("	var i : Integer = object.valNodes.size()-1;\n");
			eolCode.append("	while (i >= 0){\n");
			eolCode.append("		var x = object.valNodes.get(i);\n");
			eolCode.append("		deleteObject(x);\n");
			eolCode.append("		i -= 1;\n");
			eolCode.append("	}\n");
			eolCode.append("	delete object;\n");
			eolCode.append("}\n");
			// System.out.println(eolCode);
			run(eolCode.toString(), true);
		}
		long after = System.currentTimeMillis();
		saveOutputText();
		// saveErrorMessages();
		System.out.println("Time: " + (after - before) / 1000);
		assertEquals(true, true);
	}

	@Test
	public void testDeepTreePerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 0; i <= 480; i += 20) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("var eRoot = new Node;\n");
			code.append("eRoot.name = \"0\";\n");
			code.append("for(i in Sequence{1..%1$d}){\n");
			code.append("    var node = new Node;\n");
			code.append("    node.name = i.asString();\n");
			code.append("    eRoot.valNodes.add(node);\n");
			code.append("	   eRoot = node;\n");
			code.append("}\n");
			eolCode.append(String.format(code.toString(), i));
			// System.out.println(eolCode);
			run(eolCode.toString(), true);
		}

		saveOutputText();
		saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testRemoveResourceAddToOneRootPerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP"
				+ "\tTAtSU\tTReSU\tTAtARM\tTReARM\tTiDel"

				+ "\tSetAtt\tUnsAtt\tAddAtt\tRemAtt\tMovAtt" + "\tSetRef\tUnsRef\tAddRef\tRemRef\tMovRef"
				+ "\tDelete\tAddRes\tRemRes\tPackage\tSession\tCreate"

				+ "\tiSetAtt\tiUnsAtt\tiAddAtt\tiRemAtt\tiMovAtt" + "\tiSetRef\tiUnsRef\tiAddRef\tiRemRef\tiMovRef"
				+ "\tiDelete\tiAddRes\tiRemRes\tiPackg\tiSessio\tiCreate"

				+ "\taSetAtt\taUnsAtt\taAddAtt\taRemAtt\taMovAtt" + "\taSetRef\taUnsRef\taAddRef\taRemRef\taMovRef"
				+ "\taDelete\taAddRes\taRemRes\taPackg\taSessio\taCreate");
		for (int i = 0; i <= 100000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("var eRoot = new Node;\n");
			code.append("for(i in Sequence{1..%1$d}){\n");
			code.append("    var node = new Node;\n");
			code.append("    eRoot.valNodes.add(node);\n");
			code.append("}\n");
			code.append("for(i in Sequence{1..%1$d}){\n");
//			code.append("    delete eRoot.valNodes.last();\n");
			code.append("    delete Node.allInstances().last();\n");
			code.append("}\n");
			eolCode.append(String.format(code.toString(), i));
			// System.out.println(eolCode);
			run(eolCode.toString(), true);
		}

		saveOutputText();
		saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testRemoveResourceAddToRandomRootPerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP"
				+ "\tTAtSU\tTReSU\tTAtARM\tTReARM\tTiDel"

				+ "\tSetAtt\tUnsAtt\tAddAtt\tRemAtt\tMovAtt" + "\tSetRef\tUnsRef\tAddRef\tRemRef\tMovRef"
				+ "\tDelete\tAddRes\tRemRes\tPackage\tSession\tCreate"

				+ "\tiSetAtt\tiUnsAtt\tiAddAtt\tiRemAtt\tiMovAtt" + "\tiSetRef\tiUnsRef\tiAddRef\tiRemRef\tiMovRef"
				+ "\tiDelete\tiAddRes\tiRemRes\tiPackg\tiSessio\tiCreate"

				+ "\taSetAtt\taUnsAtt\taAddAtt\taRemAtt\taMovAtt" + "\taSetRef\taUnsRef\taAddRef\taRemRef\taMovRef"
				+ "\taDelete\taAddRes\taRemRes\taPackg\taSessio\taCreate");
		for (int i = 1; i <= 100000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("    var root = new Node;\n");
			code.append("for(i in Sequence{1..%1$d}){\n");
			code.append("    var node3 = new Node;\n");
			code.append("    root.valNodes.add(node3);\n");
			code.append("}\n");
			code.append("for(i in Sequence{1..%1$d}){\n");
			code.append("    delete Node.allInstances.last();\n");
			code.append("}\n");
//			code.append("var eRoot = new Node;\n");
//			code.append("    var node1 = new Node;\n");
//			code.append("    var node2 = new Node;\n");
//			code.append("for(i in Sequence{1..%1$d}){\n");
//			code.append("    var node3 = new Node;\n");
//			code.append("    node1.valNodes.add(node3);\n");
//			code.append("    node2.valNodes.add(node3);\n");
//			code.append("}\n");
//			code.append("eRoot.valNodes.clear();\n");
//			code.append("var eRoot = new Node;\n");
//			code.append("for(i in Sequence{1..%1$d}){\n");
//			code.append("    var node1 = new Node;\n");
//			code.append("    eRoot.valNodes.add(node1);\n");
//			code.append("    var node2 = new Node;\n");
//			code.append("    eRoot.valNodes.add(node2);\n");
//			code.append("    node2.valNodes.add(node1);\n");
//			code.append("}\n");
			eolCode.append(String.format(code.toString(), i));
			run(eolCode.toString(), true);
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
	public Class<?> getNodeClass() {
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
