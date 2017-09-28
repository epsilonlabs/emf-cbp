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
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConferenceEquivalenceTests extends LoadingEquivalenceTests {

	int objectTotalCount = 0;
	Map<String, Integer> objectCounters = new HashMap<>();
	StringBuilder code = new StringBuilder();
	private List<String> codeList = new ArrayList<>();;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public ConferenceEquivalenceTests(String extension) {
		super(extension);
	}

	@Test
	public void unsetAttributeTest() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		eolCode.append("var p = new Person;\n");
		eolCode.append("p.fullName = \"Old Name!\";\n");
		eolCode.append("p.fullName = null;\n");
		eolCode.append("p.fullName = \"New Name!\";\n");
		eolCode.append("p.fullName = null;\n");
		System.out.println(eolCode);
		run(eolCode.toString(), true);

	}

	@Test
	public void setAttributeTest() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		eolCode.append("var p = new Person;\n");
		eolCode.append("p.fullName = \"Old Name!\";\n");
		eolCode.append("p.fullName = null;\n");
		eolCode.append("p.fullName = \"New Name!\";\n");
		System.out.println(eolCode);
		run(eolCode.toString(), true);
	}

	@Test
	public void testFromFile() throws Exception {
		String eolCode = new String(Files.readAllBytes(Paths.get("data/conference.eol")));
		run(eolCode, true);

	}

	@Test
	public void testRandomModel() throws Exception {

		objectCounters.put("Conference", 1);
		objectCounters.put("Person", 1);
		objectCounters.put("Day", 1);
		objectCounters.put("Room", 1);
		objectCounters.put("Break", 1);
		objectCounters.put("Track", 1);
		objectCounters.put("Talk", 1);

		Map<String, Integer> objTypes = new HashMap<>();
		objTypes.put("Conference", 1);
		objTypes.put("Person", 120);
		objTypes.put("Day", 3);
		objTypes.put("Room", 3);
		objTypes.put("Break", 9);
		objTypes.put("Track", 6);
		objTypes.put("Talk", 36);

		List<String> objectSeeds = new ArrayList<>();
		for (Entry<String, Integer> entry : objTypes.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				objectSeeds.add(entry.getKey());
			}
		}

		// initial
		codeList.add(createObjectCode("Conference", "c"));

		// create objects
		for (int i = 0; i < 300; i++) {
			createObjects(codeList, objectSeeds);
		}
		// delete objects
		for (int i = 0; i < 100; i++) {
			deleteObjects();
		}

		String eolCode = String.join("\n", codeList);
		System.out.println(eolCode);
		System.out.println("Running...");
		run(eolCode, true);
	}

	private void deleteObjects() {
		if (objectTotalCount > 0) {
			code.setLength(0);
			int index = ThreadLocalRandom.current().nextInt(objectTotalCount);
			StringBuilder temp = new StringBuilder();
			temp.append("var c = M.allContents.selectOne(item | item.index == \"%1$s\");\n");
			temp.append("if (c <> null and M.owns(c)){\n");
			temp.append("    delete c;\n");
			temp.append("}\n");
			code.append(String.format(temp.toString(), index));
			codeList.add(code.toString());
		}
	}

	private void createObjects(List<String> codeList, List<String> objectSeeds) {
		code.setLength(0);
		int index = ThreadLocalRandom.current().nextInt(objectSeeds.size());
		String objectType = objectSeeds.get(index);
		if (objectType.equals("Conference")) {
			code.append(createObjectCode(objectType, "c"));
		} else if (objectType.equals("Person")) {
			if (objectCounters.get("Conference") > 0) {
				code.append(createObjectCode(objectType, "p"));
				int number = ThreadLocalRandom.current().nextInt(objectCounters.get("Conference"));
				StringBuilder temp = new StringBuilder();
				String name = "c" + number;
				temp.append("var c = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (c <>  null){\n");
				temp.append("    c.participants.add(x);\n");
				temp.append("}\n");
				code.append(String.format(temp.toString(), name));
			}
		} else if (objectType.equals("Day")) {
			if (objectCounters.get("Conference") > 0) {
				code.append(createObjectCode(objectType, "d"));
				int number = ThreadLocalRandom.current().nextInt(objectCounters.get("Conference"));
				StringBuilder temp = new StringBuilder();
				String name = "c" + number;
				temp.append("var c = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (c <>  null){\n");
				temp.append("    c.days.add(x);\n");
				temp.append("}\n");
				code.append(String.format(temp.toString(), name));
			}
		} else if (objectType.equals("Room")) {
			if (objectCounters.get("Conference") > 0) {
				code.append(createObjectCode(objectType, "r"));
				int number = ThreadLocalRandom.current().nextInt(objectCounters.get("Conference"));
				StringBuilder temp = new StringBuilder();
				String name = "c" + number;
				temp.append("var c = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (c <> null){\n");
				temp.append("    c.rooms.add(x);\n");
				temp.append("}\n");
				code.append(String.format(temp.toString(), name));
			}
		} else if (objectType.equals("Break")) {
			if (objectCounters.get("Day") > 0) {
				code.append(createObjectCode(objectType, "b"));
				StringBuilder temp = new StringBuilder();

				int dayNumber = ThreadLocalRandom.current().nextInt(objectCounters.get("Day"));
				String dayName = "d" + dayNumber;
				temp.append("var d = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (d <> null){\n");
				temp.append("    d.slots.add(x);\n");
				temp.append("}\n");

				int roomNumber = ThreadLocalRandom.current().nextInt(objectCounters.get("Room"));
				String roomName = "r" + roomNumber;
				if (objectCounters.get("Room") > 0) {
					temp.append("var r = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
					temp.append("if (r <> null){\n");
					temp.append("    x.room = r;\n");
					temp.append("}\n");
				}

				code.append(String.format(temp.toString(), dayName, roomName));
			}
		} else if (objectType.equals("Track")) {
			if (objectCounters.get("Day") > 0) {
				code.append(createObjectCode(objectType, "tr"));
				StringBuilder temp = new StringBuilder();

				int dayNumber = ThreadLocalRandom.current().nextInt(objectCounters.get("Day"));
				String dayName = "d" + dayNumber;
				temp.append("var d = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (d <> null){\n");
				temp.append("    d.slots.add(x);\n");
				temp.append("}\n");

				int roomNumber = ThreadLocalRandom.current().nextInt(objectCounters.get("Room"));
				String roomName = "r" + roomNumber;
				if (objectCounters.get("Room") > 0) {
					temp.append("var r = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
					temp.append("if (r <> null){\n");
					temp.append("    x.room = r;\n");
					temp.append("}\n");
				}

				code.append(String.format(temp.toString(), dayName, roomName));
			}
		} else if (objectType.equals("Talk")) {
			if (objectCounters.get("Track") > 0) {
				code.append(createObjectCode(objectType, "ta"));
				StringBuilder temp = new StringBuilder();

				int trackNumber = ThreadLocalRandom.current().nextInt(objectCounters.get("Track"));
				String trackKey = "tr" + trackNumber;
				temp.append("var tr = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (tr <> null){\n");
				temp.append("    tr.talks.add(x);\n");
				temp.append("}\n");

				int personNumber1 = ThreadLocalRandom.current().nextInt(objectCounters.get("Person"));
				String personKey1 = "p" + personNumber1;
				int personNumber2 = ThreadLocalRandom.current().nextInt(objectCounters.get("Person"));
				String personKey2 = "p" + personNumber2;
				if (objectCounters.get("Person") > 0) {
					temp.append("var p1 = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
					temp.append("if (p1 <> null){\n");
					temp.append("    x.speaker = p1;\n");
					temp.append("}\n");
					temp.append("var p2 = M.allContents.selectOne(item | item.key == \"%3$s\");\n");
					temp.append("if (p2 <> null){\n");
					temp.append("    x.discussant = p2;\n");
					temp.append("}\n");
				}

				code.append(String.format(temp.toString(), trackKey, personKey1, personKey2));
			}
		}
		codeList.add(code.toString());
	}

	@Override
	public EPackage getEPackage() {
		return ConferencePackage.eINSTANCE;
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

	public String createObjectCode(String className, String initial) {
		String code = new String();
		StringBuilder temp = new StringBuilder();
		int counter = objectCounters.get(className);
		String key = initial + counter;
		temp.append("var x = new %3$s;\n");
		temp.append("x.key = \"%1$s\";\n");
		temp.append("x.index = \"%2$s\";\n");
		code = String.format(temp.toString(), key, objectTotalCount, className);
		counter += 1;
		objectCounters.put(className, counter);
		objectTotalCount += 1;
		return code;
	}

}
