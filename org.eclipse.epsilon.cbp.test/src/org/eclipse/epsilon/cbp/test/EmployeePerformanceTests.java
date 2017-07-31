package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.employee.EmployeePackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EmployeePerformanceTests extends LoadingPerformanceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public EmployeePerformanceTests(String extension) {
		super(extension);
	}

	@Test
	public void testDeepTreePerformance() throws Exception {
		String eolCode = "";
		System.out.println("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 20; i <= 400; i += 20) {
			System.out.print(String.valueOf(i) + "\t");
			String code = "";
			code += "var eRoot = new Employee;";
			code += "eRoot.name = \"0\";";
			code += "for(i in Sequence{1..%1$d}){";
			code += "    var employee = new Employee;";
			code += "    employee.name = i.asString();";
			code += "    eRoot.manages.add(employee);";
			code += "	   eRoot = employee;";
			code += "}";
			eolCode = String.format(code, i);
			run(eolCode, true);
		}

		assertEquals(true, true);
	}

	@Test
	public void testRandomModelPerformance() throws Exception {
		String eolCode = "";
		System.out.println("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 0; i <= 4000; i += 200) {
			eolCode = "";
			System.out.print(String.valueOf(i) + "\t");

			int nameIndex = 0;
			for (nameIndex = 0; nameIndex < i; nameIndex++) {
				eolCode += String.format("var e%1$s = new Employee;\n", nameIndex);
				eolCode += String.format("e%1$s.name = \"e%1$s\";\n", nameIndex);
			}

			// Random operation
			Map<String, Integer> eventProbabilityMap = new HashMap<>();
			eventProbabilityMap.put("CREATE", 1);
			eventProbabilityMap.put("SET_ATTRIBUTE", 1);
			eventProbabilityMap.put("UNSET_ATTRIBUTE", 1);
			eventProbabilityMap.put("ADD_ATTRIBUTE", 1);
			eventProbabilityMap.put("REMOVE_ATTRIBUTE", 1);
			eventProbabilityMap.put("ADD_REFERENCE_VAL", 1);
			eventProbabilityMap.put("ADD_REFERENCE_REF", 1);
			eventProbabilityMap.put("DELETE", 1);
			
			List<String> operations = new ArrayList<>();
			for (Entry<String, Integer> entry : eventProbabilityMap.entrySet()) {
				for (int k = 0; k < entry.getValue(); k++) {
					operations.add(entry.getKey());
				}
			}

			for (int k = 0; k < nameIndex; k++) {
				String operation = operations.get(ThreadLocalRandom.current().nextInt(operations.size()));
				if (operation.equals("CREATE")) {
					eolCode += String.format("var e%1$s = new Employee;\n", nameIndex);
					eolCode += String.format("e%1$s.name = \"e%1$s\";\n", nameIndex);
					nameIndex += 1;
				} 
				else if (operation.equals("SET_ATTRIBUTE")) {
					String value = this.randomString(4);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.name = \"%2$s\";\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} 
				else if (operation.equals("UNSET_ATTRIBUTE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.name = null;\n";
					code += "}\n";
					eolCode += String.format(code, target);
				}
				else if (operation.equals("ADD_ATTRIBUTE")) {
					int value = ThreadLocalRandom.current().nextInt(3);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.accounts.add(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				}
				else if (operation.equals("REMOVE_ATTRIBUTE")) {
					int value = ThreadLocalRandom.current().nextInt(3);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    %1$s.accounts.remove(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				}			
				else if (operation.equals("ADD_REFERENCE_VAL")) {
					String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s and isCircular(%1$s, %2$s) == false){\n";
					code += "    %1$s.manages.add(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} else if (operation.equals("ADD_REFERENCE_REF")) {
					String value = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s) and M.owns(%2$s) and %1$s <> %2$s){\n";
					code += "    %1$s.refManages.add(%2$s);\n";
					code += "}\n";
					eolCode += String.format(code, target, value);
				} else if (operation.equals("DELETE")) {
					String target = "e" + ThreadLocalRandom.current().nextInt(nameIndex);
					String code = "";
					code += "if (M.owns(%1$s)){\n";
					code += "    delete %1$s;\n";
					code += "}\n";
					eolCode += String.format(code, target);
				}
			}
			// eolCode += "\"Total Employee:
			// \".print();Employee.all().size().println();\n";
			// eolCode += "operation containedByModel(targetObject): Boolean
			// {\n"
			// + " var result = false;\n"
			// + " if (M.allContents().selectOne(object | object ==
			// targetObject) <> null){\n"
			// + " result = true;\n"
			// + " }\n"
			// + " return result;\n"
			// + "}\n";
			eolCode += "operation isCircular(targetObject, valueObject): Boolean {" + "	var result = false;\n"
					+ "	for (child in valueObject.manages){\n" + "		if (child == targetObject){\n"
					+ "			result = true;\n" + "			break;\n" + "		}else{\n"
					+ "			result = isCircular(\n" + "			targetObject, child);\n" + "		}\n" + "	}\n"
					+ "}\n";

			run(eolCode, true);
			// System.out.println(eolCode);
		}

		assertEquals(true, true);
	}

	@Test
	public void testCreateObjectPerformance() throws Exception {
		String eolCode = "";
		System.out.println("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP");
		for (int i = 500; i <= 10000; i += 500) {
			System.out.print(String.valueOf(i) + "\t");
			String code = "";
			code += "var eRoot = new Employee;";
			code += "for(i in Sequence{1..%1$d}){";
			code += "    var employee = new Employee;";
			code += "    employee.name = i.asString();";
			code += "}";
			eolCode = String.format(code, i);
			run(eolCode, true);
		}

		assertEquals(true, true);
	}

	@Override
	public EPackage getEPackage() {
		return EmployeePackage.eINSTANCE;
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
