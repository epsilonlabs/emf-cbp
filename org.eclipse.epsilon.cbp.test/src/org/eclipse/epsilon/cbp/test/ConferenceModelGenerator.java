package org.eclipse.epsilon.cbp.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ConferenceModelGenerator {

	private static int NUMBER_OF_OPERATION = 3000;
	private static int objectTotalCount = 0;
	private static Map<String, Integer> objectCounters = new HashMap<>();
	private static StringBuilder code = new StringBuilder();
	private static List<String> codeList = new ArrayList<>();
	private static Map<String, Integer> objTypes = new HashMap<String, Integer>();
	private static List<String> objectSeeds = new ArrayList<String>();
	private static Map<String, Integer> eventProbabilityMap = new HashMap<String, Integer>();
	private static List<String> opSeeds = new ArrayList<String>();

	public static void setNumberOfOperation(int numberOfOperaration) {
		ConferenceModelGenerator.NUMBER_OF_OPERATION = numberOfOperaration;
	}

	public static void initialise() {
		objectTotalCount = 0;
		code.setLength(0);
		codeList.clear();
		objTypes.clear();
		objectSeeds.clear();
		eventProbabilityMap.clear();
		opSeeds.clear();

		objectCounters.put("Conference", 0);
		objectCounters.put("Person", 0);
		objectCounters.put("Day", 0);
		objectCounters.put("Room", 0);
		objectCounters.put("Break", 0);
		objectCounters.put("Track", 0);
		objectCounters.put("Talk", 0);

		objTypes = new HashMap<>();
		objTypes.put("Conference", 0);
		objTypes.put("Person", 40);// 1230
		objTypes.put("Day", 1); // 3
		objTypes.put("Room", 3);// 3
		objTypes.put("Break", 6);// 9
		objTypes.put("Track", 8);// 12
		objTypes.put("Talk", 24);// 36

		objectSeeds = new ArrayList<>();
		for (Entry<String, Integer> entry : objTypes.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				objectSeeds.add(entry.getKey());
			}
		}

		eventProbabilityMap = new HashMap<>();
		eventProbabilityMap.put("CREATE", 10);
		// eventProbabilityMap.put("SET_ATTRIBUTE", 1);
		// eventProbabilityMap.put("UNSET_ATTRIBUTE", 1);
		// eventProbabilityMap.put("SET_REFERENCE", 1);
		// eventProbabilityMap.put("UNSET_REFERENCE", 1);
		// eventProbabilityMap.put("ADD_ATTRIBUTE", 1);
		// eventProbabilityMap.put("MOVE_ATTRIBUTE", 1);
		// eventProbabilityMap.put("REMOVE_ATTRIBUTE", 1);
		// eventProbabilityMap.put("ADD_REFERENCE_REF", 1);
		// eventProbabilityMap.put("MOVE_REFERENCE_REF", 1);
		eventProbabilityMap.put("ADD_REFERENCE_VAL", 1);
		// eventProbabilityMap.put("MOVE_REFERENCE_VAL", 15);
		 eventProbabilityMap.put("EDIT", 1);
		 eventProbabilityMap.put("DELETE", 1);

		opSeeds = new ArrayList<>();
		for (Entry<String, Integer> entry : eventProbabilityMap.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				opSeeds.add(entry.getKey());
			}
		}
	}

	public static String generateInitialCode() {
		StringBuilder eolCode = new StringBuilder();
		eolCode.append("var c = new Conference;\n");
		eolCode.append("var p = new Person;\n");
		eolCode.append("var d = new Day;\n");
		eolCode.append("var r = new Room;\n");
		eolCode.append("var b = new Break;\n");
		eolCode.append("var tr = new Track;\n");
		eolCode.append("var ta = new Talk;\n");
		eolCode.append("c.participants.add(p);\n");
		eolCode.append("c.days.add(d);\n");
		eolCode.append("c.rooms.add(r);\n");
		eolCode.append("d.slots.add(tr);\n");
		eolCode.append("d.slots.add(b);\n");
		eolCode.append("tr.talks.add(ta);\n");

		// eolCode.append(createObjectCode("Conference", "c"));
		// eolCode.append(createObjectCode("Person", "p"));
		// eolCode.append(createObjectCode("Day", "d"));
		// eolCode.append(createObjectCode("Room", "r"));
		// eolCode.append(createObjectCode("Break", "b"));
		// eolCode.append(createObjectCode("Track", "tr"));
		// eolCode.append(createObjectCode("Talk", "ta"));

		return eolCode.toString();
	}

	public static String generateRandomOperationCode() {
		String outputCode = null;
		int opIndex = ThreadLocalRandom.current().nextInt(opSeeds.size());
		String opType = opSeeds.get(opIndex);
		if (opType.equals("CREATE")) {
			outputCode = createObjects();
		} else if (opType.equals("SET_ATTRIBUTE")) {
			outputCode = setAttribute();
		} else if (opType.equals("UNSET_ATTRIBUTE")) {
			outputCode = unsetAttribute();
		} else if (opType.equals("ADD_REFERENCE_VAL")) {
			outputCode = addReference();
			// } else if (opType.equals("MOVE_REFERENCE_VAL")) {
			// outputCode = addReference();
		} else if (opType.equals("EDIT")) {
			outputCode = editObjects();
		} else if (opType.equals("DELETE")) {
			outputCode = deleteObjects();
		}
		return outputCode;
	}

	public static List<String> generateCompleteCode() throws Exception {
		// initial
		codeList.add(ConferenceModelGenerator.generateInitialCode());
		// create objects
		for (int i = 0; i < NUMBER_OF_OPERATION; i++) {
			String code = generateRandomOperationCode();
			if (code != null && code.length() > 0) {
				codeList.add(code);
			}
		}

		return codeList;
	}

	public static String deleteObjects() {
		code.setLength(0);
		if (objectTotalCount > 0) {
			int seed = objectCounters.get("Conference");
			seed = (seed == 0) ? 1 : seed;
			int index = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			temp.append("var c = M.allContents.selectOne(item | item.index == \"%1$s\");\n");
			temp.append("if (c <> null and M.owns(c)){\n");
			temp.append("	if(c.isKindOf(Conference)){\n");
			// temp.append(" if (c.key <> \"c0\" and c.key <> \"c1\"){\n");
			temp.append("			for (d in c.days){\n");
			temp.append("				for(s in d.slots){\n");
			temp.append("					if (s.isTypeOf(Track)){\n");
			temp.append("						delete s.talks;\n");
			temp.append("					}\n");
			temp.append("				}\n");
			temp.append("				delete d.slots;\n");
			temp.append("			}\n");
			temp.append("			delete c.days;\n");
			temp.append("			delete c.participants;\n");
			temp.append("			delete c.rooms;\n");
			temp.append("			delete c;\n");
			// temp.append(" }\n");
			temp.append("	}else if(c.isKindOf(Day)) {\n");
			temp.append("			for(s in c.slots){\n");
			temp.append("				if (s.isTypeOf(Track)){\n");
			temp.append("					delete s.talks;\n");
			temp.append("				}\n");
			temp.append("			}\n");
			temp.append("			delete c.slots;\n");
			temp.append("			delete c;\n");
			temp.append("		\n");
			temp.append("	}else{\n");
			temp.append("		delete c;\n");
			temp.append("	}\n");
			temp.append("}\n");
			code.append(String.format(temp.toString(), index));
		}
		return code.toString();
	}

	public static String addReference() {
		code.setLength(0);
		int index = ThreadLocalRandom.current().nextInt(objectSeeds.size());
		String objectType = objectSeeds.get(index);
		if (objectType.equals("Track") || objectType.equals("Break")) {
			int seed = objectCounters.get("Day");
			seed = (seed == 0) ? 1 : seed;
			int number = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			String keyDay = "d" + number;
			temp.append("var d = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (d ==  null){\n");
			temp.append("    d = Day.allInstances().last();\n");
			temp.append("}\n");

			if (objectType.equals("Track")) {
				seed = objectCounters.get("Track");
				seed = (seed == 0) ? 1 : seed;
				number = ThreadLocalRandom.current().nextInt(seed);
				String keyTrack = "t" + number;
				temp.append("var t = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
				temp.append("if (t ==  null){\n");
				temp.append("    t = Track.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (t <> null and t <>  null){\n");
				temp.append("	d.slots.add(t);\n");
				temp.append("}\n");
				code.append(String.format(temp.toString(), keyDay, keyTrack));
			}

			if (objectType.equals("Break")) {
				seed = objectCounters.get("Break");
				seed = (seed == 0) ? 1 : seed;
				number = ThreadLocalRandom.current().nextInt(seed);
				String keyBreak = "b" + number;
				temp.append("var b = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
				temp.append("if (b ==  null){\n");
				temp.append("    b = Break.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (b <> null and b <>  null){\n");
				temp.append("	d.slots.add(b);\n");
				temp.append("}\n");
				code.append(String.format(temp.toString(), keyDay, keyBreak));
			}
		} else if (objectType.equals("Talk")) {
			int seed = objectCounters.get("Track");
			seed = (seed == 0) ? 1 : seed;
			int number = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			String keyTrack = "tr" + number;
			temp.append("var tr = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (tr ==  null){\n");
			temp.append("    tr = Track.allInstances().last();\n");
			temp.append("}\n");

			if (objectType.equals("Talk")) {
				seed = objectCounters.get("Talk");
				seed = (seed == 0) ? 1 : seed;
				number = ThreadLocalRandom.current().nextInt(seed);
				String keyTalk = "t" + number;
				temp.append("var ta = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
				temp.append("if (ta ==  null){\n");
				temp.append("    ta = Talk.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (ta <> null and ta <>  null){\n");
				temp.append("	tr.talks.add(ta);\n");
				temp.append("}\n");
				code.append(String.format(temp.toString(), keyTrack, keyTalk));
			}
		}
		return code.toString();
	}

	public static String unsetAttribute() {
		code.setLength(0);
		int index = ThreadLocalRandom.current().nextInt(objectSeeds.size());
		String objectType = objectSeeds.get(index);
		if (objectType.equals("Person")) {
			int seed = objectCounters.get("Person");
			seed = (seed == 0) ? 1 : seed;
			int number = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			String key = "p" + number;
			temp.append("var p = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (p ==  null){\n");
			temp.append("    p = Person.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (p <>  null){\n");
			int opt = ThreadLocalRandom.current().nextInt(2);
			String newFullName = ConferenceModelGenerator.randomString(6);
			String newAffiliation = ConferenceModelGenerator.randomString(6);
			if (opt == 0) {
				temp.append("    p.fullName = null;\n");
			} else if (opt == 1) {
				temp.append("    p.affiliation = null;\n");
			}
			temp.append("}\n");
			code.append(String.format(temp.toString(), key, newFullName, newAffiliation));
		}
		return code.toString();
	}

	public static String setAttribute() {
		code.setLength(0);
		int index = ThreadLocalRandom.current().nextInt(objectSeeds.size());
		String objectType = objectSeeds.get(index);
		if (objectType.equals("Person")) {
			int seed = objectCounters.get("Person");
			seed = (seed == 0) ? 1 : seed;
			int number = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			String key = "p" + number;
			temp.append("var p = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (p ==  null){\n");
			temp.append("    p = Person.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (p <>  null){\n");
			int opt = ThreadLocalRandom.current().nextInt(4);
			String newFullName = ConferenceModelGenerator.randomString(6);
			String newAffiliation = ConferenceModelGenerator.randomString(6);
			if (opt == 0) {
				temp.append("    p.fullName = \"%2$s\";\n");
			} else if (opt == 1) {
				temp.append("    p.affiliation = \"%3$s\";\n");
			}
			temp.append("}\n");
			code.append(String.format(temp.toString(), key, newFullName, newAffiliation));
		}
		return code.toString();
	}

	public static String editObjects() {
		code.setLength(0);
		int index = ThreadLocalRandom.current().nextInt(objectSeeds.size());
		String objectType = objectSeeds.get(index);
		if (objectType.equals("Conference") || objectType.equals("Person")) {
			if (objectCounters.get("Person") > 0) {
				int seed = objectCounters.get("Person");
				seed = (seed == 0) ? 1 : seed;
				int number = ThreadLocalRandom.current().nextInt(seed);
				StringBuilder temp = new StringBuilder();
				String key = "p" + number;
				temp.append("var p = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (p ==  null){\n");
				temp.append("    p = Person.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (p <>  null){\n");
				int opt = ThreadLocalRandom.current().nextInt(4);
				String newFullName = ConferenceModelGenerator.randomString(6);
				String newAffiliation = ConferenceModelGenerator.randomString(6);
				if (opt == 0) {
					temp.append("    p.fullName = \"%2$s\";\n");
				} else if (opt == 1) {
					temp.append("    p.affiliation = \"%3$s\";\n");
				} else if (opt == 2) {
					temp.append("    p.fullName = null;\n");
				} else if (opt == 3) {
					temp.append("    p.affiliation = null;\n");
				}
				temp.append("}\n");
				code.append(String.format(temp.toString(), key, newFullName, newAffiliation));
			}
		} else if (objectType.equals("Day")) {
			if (objectCounters.get("Day") > 0) {
				int seed = objectCounters.get("Day");
				seed = (seed == 0) ? 1 : seed;
				int number = ThreadLocalRandom.current().nextInt(seed);
				StringBuilder temp = new StringBuilder();
				String key = "d" + number;
				temp.append("var d = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (d ==  null){\n");
				temp.append("    d = Day.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (d <>  null){\n");
				int opt = ThreadLocalRandom.current().nextInt(4);
				String newName = ConferenceModelGenerator.randomString(6);
				if (opt == 0) {
					temp.append("    d.name = \"%2$s\";\n");
				} else if (opt == 1) {
					temp.append("    d.name = null;\n");
				} else if (opt == 2) {
					temp.append("    if (d.slots.size() > 1){\n");
					temp.append("    	d.slots.move(0,d.slots.size()-1);\n");
					temp.append("    }\n");
				} else if (opt == 3) {
					temp.append("    var s = Slot.allInstances().first();\n");
					temp.append("    if (s <> null){\n");
					temp.append("    	d.slots.add(s);\n");
					temp.append("    }\n");
				}
				temp.append("}\n");
				code.append(String.format(temp.toString(), key, newName));
			}
		} else if (objectType.equals("Room")) {
			if (objectCounters.get("Room") > 0) {
				int seed = objectCounters.get("Room");
				seed = (seed == 0) ? 1 : seed;
				int number = ThreadLocalRandom.current().nextInt(seed);
				StringBuilder temp = new StringBuilder();
				String key = "r" + number;
				temp.append("var r = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (r == null){\n");
				temp.append(" r = Room.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (r <> null){\n");
				int opt = ThreadLocalRandom.current().nextInt(2);
				String newName = ConferenceModelGenerator.randomString(6);
				if (opt == 0) {
					temp.append(" r.name = \"%2$s\";\n");
				} else if (opt == 1) {
					temp.append(" r.name = null;\n");
				}
				temp.append("}\n");
				code.append(String.format(temp.toString(), key, newName));
			}
		} else if (objectType.equals("Break")) {
			if (objectCounters.get("Break") > 0) {
				int seed = objectCounters.get("Break");
				seed = (seed == 0) ? 1 : seed;
				int number = ThreadLocalRandom.current().nextInt(seed);
				StringBuilder temp = new StringBuilder();
				String key = "b" + number;
				temp.append("var b = M.allContents.selectOne(item | item.key ==\"%1$s\");\n");
				temp.append("if (b == null){\n");
				temp.append(" b = Break.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (b <> null){\n");
				int opt = ThreadLocalRandom.current().nextInt(3);
				String newReason = ConferenceModelGenerator.randomString(6);
				if (opt == 0) {
					temp.append(" b.reason = \"%2$s\";\n");
				} else if (opt == 1) {
					temp.append(" b.reason = null;\n");
				} else if (opt == 2) {
					temp.append(" var r = Room.allInstances().first();\n");
					temp.append(" if (r <> null){\n");
					temp.append(" b.room = r;\n");
					temp.append(" }\n");
				}
				temp.append("}\n");
				code.append(String.format(temp.toString(), key, newReason));
			}
		} else if (objectType.equals("Track")) {
			if (objectCounters.get("Track") > 0) {
				int seed = objectCounters.get("Track");
				seed = (seed == 0) ? 1 : seed;
				int number = ThreadLocalRandom.current().nextInt(seed);
				StringBuilder temp = new StringBuilder();
				String key = "tr" + number;
				temp.append("var tr = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (tr == null){\n");
				temp.append(" tr = Track.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (tr <> null){\n");
				int opt = ThreadLocalRandom.current().nextInt(5);
				String title = ConferenceModelGenerator.randomString(6);
				if (opt == 0) {
					temp.append(" tr.title = \"%2$s\";\n");
				} else if (opt == 1) {
					temp.append(" tr.title = null;\n");
				} else if (opt == 2) {
					temp.append(" if (tr.talks.size() > 1){\n");
					temp.append(" tr.talks.move(0,tr.talks.size()-1);\n");
					temp.append(" }\n");
				} else if (opt == 3) {
					temp.append(" var ta = Talk.allInstances().first();\n");
					temp.append(" if (ta <> null){\n");
					temp.append(" tr.talks.add(ta);\n");
					temp.append(" }\n");
				} else if (opt == 4) {
					temp.append(" var r = Room.allInstances().first();\n");
					temp.append(" if (r <> null){\n");
					temp.append(" tr.room = r;\n");
					temp.append(" }\n");
				}
				temp.append("}\n");
				code.append(String.format(temp.toString(), key, title));
			}
		} else if (objectType.equals("Talk")) {
			if (objectCounters.get("Talk") > 0) {
				int seed = objectCounters.get("Talk");
				seed = (seed == 0) ? 1 : seed;
				int number = ThreadLocalRandom.current().nextInt(seed);
				StringBuilder temp = new StringBuilder();
				String key = "ta" + number;
				temp.append("var ta = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
				temp.append("if (ta == null){\n");
				temp.append(" ta = Talk.allInstances().first();\n");
				temp.append("}\n");
				temp.append("if (ta <> null){\n");
				int opt = ThreadLocalRandom.current().nextInt(5);
				String newTitle = ConferenceModelGenerator.randomString(6);
				Integer newDuration = ThreadLocalRandom.current().nextInt(60);
				if (opt == 0) {
					temp.append(" ta.title = \"%2$s\";\n");
				} else if (opt == 1) {
					temp.append(" ta.title = null;\n");
				} else if (opt == 2) {
					temp.append(" ta.duration = %3$s;\n");
				} else if (opt == 3) {
					temp.append(" var p = Person.allInstances().first();\n");
					temp.append(" if (p <> null){\n");
					temp.append(" ta.speaker = p;\n");
					temp.append(" }\n");
				} else if (opt == 4) {
					temp.append(" var p = Person.allInstances().first();\n");
					temp.append(" if (p <> null){\n");
					temp.append(" ta.discussant = p;\n");
					temp.append(" }\n");
				}
				temp.append("}\n");
				code.append(String.format(temp.toString(), key, newTitle, newDuration));
			}
		} else {
			System.out.println();
		}
		return code.toString();
	}

	public static String createObjects2() {
		code.setLength(0);
		int index = ThreadLocalRandom.current().nextInt(objectSeeds.size());
		String objectType = objectSeeds.get(index);
		if (objectType.equals("Conference")) {
			code.append(createObjectCode(objectType, "c"));
		} else if (objectType.equals("Person")) {
			// if (objectCounters.get("Conference") > 0) {
			code.append(createObjectCode(objectType, "p"));
			// }
		} else {
			System.out.println();
		}

		return code.toString();
	}

	public static String createObjects() {
		code.setLength(0);
		int index = ThreadLocalRandom.current().nextInt(objectSeeds.size());
		String objectType = objectSeeds.get(index);
		if (objectType.equals("Conference")) {
			code.append(createObjectCode(objectType, "c"));
		} else if (objectType.equals("Person")) {

			code.append(createObjectCode(objectType, "p"));
			int seed = objectCounters.get("Conference");
			seed = (seed == 0) ? 1 : seed;
			int number = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			String key = "c" + number;
			String fullName = ConferenceModelGenerator.randomString(3);
			String affiliation = ConferenceModelGenerator.randomString(3);
			temp.append(" x.fullName = \"%2$s\";\n");
			temp.append(" x.affiliation = \"%3$s\";\n");
			temp.append("var c = M.allContents.selectOne(item | item.key== \"%1$s\");\n");
			temp.append("if (c == null){\n");
			temp.append(" c = Conference.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (c <> null){\n");
			temp.append(" c.participants.add(x);\n");
			temp.append("}\n");
			code.append(String.format(temp.toString(), key, fullName, affiliation));

		} else if (objectType.equals("Day")) {

			code.append(createObjectCode(objectType, "d"));
			int seed = objectCounters.get("Conference");
			seed = (seed == 0) ? 1 : seed;
			int number = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			String key = "c" + number;
			String name = ConferenceModelGenerator.randomString(3);
			temp.append("    x.name = \"%2$s\";\n");
			temp.append("var c = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (c ==  null){\n");
			temp.append("    c = Conference.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (c <>  null){\n");
			temp.append("    c.days.add(x);\n");
			temp.append("}\n");
			code.append(String.format(temp.toString(), key, name));

		} else if (objectType.equals("Room")) {

			code.append(createObjectCode(objectType, "r"));
			int seed = objectCounters.get("Conference");
			seed = (seed == 0) ? 1 : seed;
			int number = ThreadLocalRandom.current().nextInt(seed);
			StringBuilder temp = new StringBuilder();
			String key = "c" + number;
			String name = ConferenceModelGenerator.randomString(3);
			temp.append("    x.name = \"%2$s\";\n");
			temp.append("var c = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (c ==  null){\n");
			temp.append("    c = Conference.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (c <> null){\n");
			temp.append("    c.rooms.add(x);\n");
			temp.append("}\n");
			code.append(String.format(temp.toString(), key, name));

		} else if (objectType.equals("Break")) {

			code.append(createObjectCode(objectType, "b"));
			StringBuilder temp = new StringBuilder();

			int seed = objectCounters.get("Day");
			seed = (seed == 0) ? 1 : seed;
			int dayNumber = ThreadLocalRandom.current().nextInt(seed);
			String key = "d" + dayNumber;
			String reason = ConferenceModelGenerator.randomString(3);
			temp.append("    x.reason = \"%3$s\";\n");
			temp.append("var d = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (d ==  null){\n");
			temp.append("    d = Day.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (d <> null){\n");
			temp.append("    d.slots.add(x);\n");

			seed = objectCounters.get("Room");
			seed = (seed == 0) ? 1 : seed;
			int roomNumber = ThreadLocalRandom.current().nextInt(seed);
			String roomName = "r" + roomNumber;
			if (objectCounters.get("Room") > 0) {
				temp.append("	var r = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
				temp.append("	if (r ==  null){\n");
				temp.append("   	r = Room.allInstances().first();\n");
				temp.append("	}\n");
				temp.append("	if (r <> null){\n");
				temp.append("  		x.room = r;\n");
				temp.append("	}\n");
			}
			temp.append("}\n");

			code.append(String.format(temp.toString(), key, roomName, reason));

		} else if (objectType.equals("Track")) {

			code.append(createObjectCode(objectType, "tr"));
			StringBuilder temp = new StringBuilder();

			int seed = objectCounters.get("Day");
			seed = (seed == 0) ? 1 : seed;
			int dayNumber = ThreadLocalRandom.current().nextInt(seed);
			String key = "d" + dayNumber;
			String title = ConferenceModelGenerator.randomString(3);
			temp.append("    x.title = \"%3$s\";\n");
			temp.append("var d = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (d ==  null){\n");
			temp.append("    d = Day.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (d <> null){\n");
			temp.append("    d.slots.add(x);\n");

			seed = objectCounters.get("Room");
			seed = (seed == 0) ? 1 : seed;
			int roomNumber = ThreadLocalRandom.current().nextInt(seed);
			String roomName = "r" + roomNumber;
			if (objectCounters.get("Room") > 0) {
				temp.append("	var r = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
				temp.append("	if (r ==  null){\n");
				temp.append("   	r = Room.allInstances().first();\n");
				temp.append("	}\n");
				temp.append("	if (r <> null){\n");
				temp.append("  		x.room = r;\n");
				temp.append("	}\n");
			}
			temp.append("}\n");

			code.append(String.format(temp.toString(), key, roomName, title));

		} else if (objectType.equals("Talk")) {

			code.append(createObjectCode(objectType, "ta"));
			StringBuilder temp = new StringBuilder();

			int seed = objectCounters.get("Track");
			seed = (seed == 0) ? 1 : seed;
			int trackNumber = ThreadLocalRandom.current().nextInt(seed);
			String trackKey = "tr" + trackNumber;
			String title = ConferenceModelGenerator.randomString(3);
			int duration = ThreadLocalRandom.current().nextInt(20);
			temp.append("x.title = \"%4$s\";\n");
			temp.append("x.duration = %5$s;\n");
			temp.append("var tr = M.allContents.selectOne(item | item.key == \"%1$s\");\n");
			temp.append("if (tr ==  null){\n");
			temp.append("    tr = Track.allInstances().first();\n");
			temp.append("}\n");
			temp.append("if (tr <> null){\n");
			temp.append("    tr.talks.add(x);\n");

			seed = objectCounters.get("Person");
			seed = (seed == 0) ? 1 : seed;
			int personNumber1 = ThreadLocalRandom.current().nextInt(seed);
			String personKey1 = "p" + personNumber1;

			seed = objectCounters.get("Person");
			seed = (seed == 0) ? 1 : seed;
			int personNumber2 = ThreadLocalRandom.current().nextInt(seed);
			String personKey2 = "p" + personNumber2;
			if (objectCounters.get("Person") > 0) {
				temp.append("	var p1 = M.allContents.selectOne(item | item.key == \"%2$s\");\n");
				temp.append("	if (p1 ==  null){\n");
				temp.append(" 	   p1 = Person.allInstances().first();\n");
				temp.append("	}\n");
				temp.append("	if (p1 <> null){\n");
				temp.append("	    x.speaker = p1;\n");
				temp.append("	}\n");
				temp.append("	var p2 = M.allContents.selectOne(item | item.key == \"%3$s\");\n");
				temp.append("	if (p2 ==  null){\n");
				temp.append("	    p2 = Person.allInstances().last();\n");
				temp.append("	}\n");
				temp.append("	if (p2 <> null){\n");
				temp.append("	    x.discussant = p2;\n");
				temp.append("	}\n");
			}
			temp.append("}\n");

			code.append(String.format(temp.toString(), trackKey, personKey1, personKey2, title, duration));

		} else {
			System.out.println();
		}
		return code.toString();
	}

	private static String randomString(int length) {
		String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String result = "";
		for (int i = 0; i < length; i++) {
			int index = ThreadLocalRandom.current().nextInt(alphabets.length());
			result = result + alphabets.charAt(index);
		}
		return result;
	}

	private static String createObjectCode(String className, String initial) {
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
