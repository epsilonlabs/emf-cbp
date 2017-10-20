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
public class NodeAppendTests extends AppendPerformanceTests {

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 { "cbpxml", new CBPXMLResourceFactory() }
//                 ,
//                 { "cbpthrift", null/*new CBPThriftResourceFactory()*/ }  
           });
    }

	public NodeAppendTests(String extension, Resource.Factory factory) {
		super(extension, factory);
	}

	@Test
	public void testCreates() throws Exception {
		List<String> list = new ArrayList<>();
		for (int i : new int[50]){
			list.add("new EClass;");
		}
		debug(list.toArray(new String[list.size()]));
	}
	
	@Test
	public void testCreatesAndDelete() throws Exception {
		debug("var c1 = new EClass;", "delete EClass.all;", "var c2 = new EClass;");
	}
	
	@Test
	public void testAppendRandomModel() throws Exception {
		List<String> list = new ArrayList<>();
		int nameIndex = 0;
		int iteration = 1000000;

		// Random operation
		Map<String, Integer> eventProbabilityMap = new HashMap<>();
		eventProbabilityMap.put("CREATE", 10);
		eventProbabilityMap.put("SET_ATTRIBUTE", 1);
		eventProbabilityMap.put("UNSET_ATTRIBUTE", 1);
		eventProbabilityMap.put("ADD_ATTRIBUTE", 1);
		eventProbabilityMap.put("MOVE_ATTRIBUTE", 1);
		eventProbabilityMap.put("REMOVE_ATTRIBUTE", 1);
//		eventProbabilityMap.put("ADD_REFERENCE_REF", 1);
//		eventProbabilityMap.put("MOVE_REFERENCE_REF", 1);
		eventProbabilityMap.put("ADD_REFERENCE_VAL", 1);
		eventProbabilityMap.put("MOVE_REFERENCE_VAL", 1);
		eventProbabilityMap.put("DELETE", 1);
//		eventProbabilityMap.put("DUMMY", 1);

		List<String> operations = new ArrayList<>();
		for (Entry<String, Integer> entry : eventProbabilityMap.entrySet()) {
			for (int k = 0; k < entry.getValue(); k++) {
				operations.add(entry.getKey());
			}
		}
		
		StringBuilder eolCode = new StringBuilder();
		
		// initial
		eolCode.append(String.format("var root = new Node;\n", nameIndex));
		eolCode.append(String.format("root.name = \"root\";\n"));
		list.add(eolCode.toString());
		for (int i = 0; i < 1; i++) {
			eolCode.setLength(0);
			eolCode.append(String.format("var node = new Node;\n", nameIndex));
			eolCode.append(String.format("node.name = \"e%1$s\";\n", nameIndex));
			eolCode.append(String.format("Node.allInstances().first().valNodes.add(node);\n",nameIndex));
			list.add(eolCode.toString());
			nameIndex += 1;
		}
		
		for (int i : new int[iteration]) {
			eolCode.setLength(0);
			String operation = operations.get(ThreadLocalRandom.current().nextInt(operations.size()));
			if (operation.equals("CREATE")) {
				eolCode.append(String.format("var node = new Node;\n", nameIndex));
				eolCode.append(String.format("node.name = \"e%1$s\";\n", nameIndex));
				eolCode.append(String.format("Node.allInstances().first().valNodes.add(node);\n", nameIndex));
				nameIndex += 1;
			} else if (operation.equals("SET_ATTRIBUTE")) {
				String value = this.randomString(4);
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(%1$s)){\n");
				code.append("    %1$s.defName = \"%2$s\";\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, value));
			} 
			else if (operation.equals("UNSET_ATTRIBUTE")) {
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(%1$s)){\n");
				code.append("    %1$s.defName = null;\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target));
			} else if (operation.equals("ADD_ATTRIBUTE")) {
				int value = ThreadLocalRandom.current().nextInt(3);
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(%1$s)){\n");
				code.append("    %1$s.values.add(%2$s);\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, value));
			} else if (operation.equals("REMOVE_ATTRIBUTE")) {
				int value = ThreadLocalRandom.current().nextInt(3);
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(%1$s)){\n");
				code.append("    %1$s.values.remove(%2$s);\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, value));
			} else if (operation.equals("ADD_REFERENCE_VAL")) {
				String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				StringBuilder code = new StringBuilder();
				
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("var %2$s = M.allContents.selectOne(node | node.name == \"%2$s\");\n");
				code.append("if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s ");
//				code.append("    and isCircular(%1$s, %2$s) == false){\n");
				code.append("    ){\n");
				code.append("    if (%1$s.deep + 1 > 300){\n");
				code.append("        var x = %1$s.parent;\n");
				code.append("        %2$s.deep = x.deep + 1;\n");
				code.append("        %2$s.parent = x;\n");
				code.append("        x.valNodes.add(%2$s);\n");
				code.append("    }else{\n");
				code.append("        %2$s.deep = %1$s.deep + 1;\n");
				code.append("        %2$s.parent = %1$s;\n");
				code.append("    	 %1$s.valNodes.add(%2$s);\n");
				code.append("    }\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, value));
				
//				eolCode.append("operation isCircular(targetObject, valueObject): Boolean {\n");
//				eolCode.append("	for (child in valueObject.valNodes){\n");
//				eolCode.append("		if (child == targetObject){\n");
//				eolCode.append("			return true;\n");
//				eolCode.append("		}else{\n");
//				eolCode.append("			return isCircular(\n");
//				eolCode.append("			targetObject, child);\n");
//				eolCode.append("		}\n");
//				eolCode.append("	}\n");
//				eolCode.append("	return false;\n");
//				eolCode.append("}\n");
			} else if (operation.equals("ADD_REFERENCE_REF")) {
				String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("var %2$s = M.allContents.selectOne(node | node.name == \"%2$s\");\n");
				code.append("if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s){\n");
				code.append("    if (%1$s.deep + 1 > 300){\n");
				code.append("        var x = %1$s.parent;\n");
				code.append("        %2$s.deep = x.deep + 1;\n");
				code.append("        %2$s.parent = x;\n");
				code.append("        x.refNodes.add(%2$s);\n");
				code.append("    }else{\n");
				code.append("        %2$s.deep = %1$s.deep + 1;\n");
				code.append("        %2$s.parent = %1$s;\n");
				code.append("    	 %1$s.refNodes.add(%2$s);\n");
				code.append("    }");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, value));
			} else if (operation.equals("MOVE_ATTRIBUTE")) {
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				int toIndex = ThreadLocalRandom.current().nextInt(3);
				int fromIndex = ThreadLocalRandom.current().nextInt(3);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(%1$s) and %1$s.values.size() > 1){\n");
				code.append("    var toIndex = %2$s;\n");
				code.append("    var fromIndex = %3$s;\n");
				code.append("    if (toIndex >= %1$s.values.size()){\n");
				code.append("        toIndex = %1$s.values.size() - 1;\n");
				code.append("    }\n");
				code.append("    if (fromIndex >= %1$s.values.size()){\n");
				code.append("        fromIndex = %1$s.values.size() - 1;\n");
				code.append("    }\n");
				code.append("    %1$s.values.move(toIndex, fromIndex);\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, toIndex, fromIndex));
			} else if (operation.equals("MOVE_REFERENCE_REF")) {
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				int toIndex = ThreadLocalRandom.current().nextInt(3);
				int fromIndex = ThreadLocalRandom.current().nextInt(3);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(%1$s) and %1$s.refNodes.size() > 1){\n");
				code.append("    var toIndex = %2$s;\n");
				code.append("    var fromIndex = %3$s;\n");
				code.append("    if (toIndex >= %1$s.refNodes.size()){\n");
				code.append("        toIndex = %1$s.refNodes.size() - 1;\n");
				code.append("    }\n");
				code.append("    if (fromIndex >= %1$s.refNodes.size()){\n");
				code.append("        fromIndex = %1$s.refNodes.size() - 1;\n");
				code.append("    }\n");
				code.append("    %1$s.refNodes.move(toIndex, fromIndex);\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, toIndex, fromIndex));
			} else if (operation.equals("MOVE_REFERENCE_VAL")) {
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				int toIndex = ThreadLocalRandom.current().nextInt(3);
				int fromIndex = ThreadLocalRandom.current().nextInt(3);
				StringBuilder code = new StringBuilder();
				code.append("var %1$s = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(%1$s) and %1$s.valNodes.size() > 1){\n");
				code.append("    var toIndex = %2$s;\n");
				code.append("    var fromIndex = %3$s;\n");
				code.append("    if (toIndex >= %1$s.valNodes.size()){\n");
				code.append("        toIndex = %1$s.valNodes.size() - 1;\n");
				code.append("    }\n");
				code.append("    if (fromIndex >= %1$s.valNodes.size()){\n");
				code.append("        fromIndex = %1$s.valNodes.size() - 1;\n");
				code.append("    }\n");
				code.append("    %1$s.valNodes.move(toIndex, fromIndex);\n");
				code.append("}\n");
				eolCode.append(String.format(code.toString(), target, toIndex, fromIndex));
			} else if (operation.equals("DELETE")) {
				String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
				StringBuilder code = new StringBuilder();
				code.append("var node = M.allContents.selectOne(node | node.name == \"%1$s\");\n");
				code.append("if (M.owns(node)){\n");
				code.append("    delete node;\n");
				code.append("}\n\n");
				eolCode.append(String.format(code.toString(), target));
				
//				eolCode.append("operation deleteObject(object){\n");
//				eolCode.append("	var i : Integer = object.valNodes.size()-1;\n");
//				eolCode.append("	while (i >= 0){\n");
//				eolCode.append("		var x = object.valNodes.get(i);\n");
//				eolCode.append("		deleteObject(x);\n");
//				eolCode.append("		i -= 1;\n");
//				eolCode.append("	}\n");
//				eolCode.append("	delete object;\n");
//				eolCode.append("}\n");
			}
			
			list.add(eolCode.toString());
		}
		run(list.toArray(new String[list.size()]));
//		debug(list.toArray(new String[list.size()]));
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
