package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import CBP.AddToResource;
import CBP.CBPFactory;
import CBP.CBPPackage;
import CBP.Create;
import CBP.Register;
import CBP.Session;
import CBP.SetEAttribute;
import CBP.Value;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.junit.Test;

public class CBPEventMetamodelTest {

	@Test
	public void testConvertRawCBPtoCBPMetamodel() {
		try {
			File target = new File("D:\\TEMP\\ASE\\_temp.cbpxml");
			
			CBPPackage.eINSTANCE.eClass();
			CBPFactory factory = CBPFactory.eINSTANCE;
			Session session = factory.createSession();
			session.setId("BPMN2-0000001-1b47f8942c6849ba1c2c61eb3e2253217361dbbc.xmi");
			session.setTime("20180129093255161GMT");
			Register register = factory.createRegister();
			register.setEpackage("http://www.eclipse.org/uml2/5.0.0/UML");
			Create create = factory.createCreate();
			create.setEclass("Model");
			create.setEpackage("http://www.eclipse.org/uml2/5.0.0/UML");
			create.setId("0");
			Value value1 = factory.createValue();
			value1.setLiteral("org.eclipse.mdt.bpmn2.editor");
			SetEAttribute setEAttribute = factory.createSetEAttribute();
			setEAttribute.setName("name");
			setEAttribute.setTarget("0");
			setEAttribute.setValue(value1);
			Value value2 = factory.createValue();
			value2.setEobject("0");
			AddToResource addToResource = factory.createAddToResource();
			addToResource.setPosition("0");
			addToResource.setValue(value2);

			Resource resource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(target.getAbsolutePath()));
			resource.getContents().add(session);
			resource.getContents().add(register);
			resource.getContents().add(create);
			resource.getContents().add(setEAttribute);
			resource.getContents().add(addToResource);

			resource.save(null);
			resource.unload();
			
			resource.load(null);

			System.out.println("Finished!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(true, true);
	}

	@Test
	public void testCBPEventMetamodel() {
		try {
			CBPPackage.eINSTANCE.eClass();

			File source = new File("D:\\TEMP\\ASE\\bpmn2.192.cbpxml");
			File target = new File("D:\\TEMP\\ASE\\bpmn2.192.comparison.cbpxml");
			if (target.exists()) {
				target.delete();
			}

			Map<String, String> map = new HashMap<>();
			map.put("session", "cbp:Session");
			map.put("register", "cbp:Register");
			map.put("create", "cbp:Create");
			map.put("delete", "cbp:Delete");
			map.put("add-to-resource", "cbp:AddToResource");
			map.put("remove-from-resource", "cbp:RemoveFromResource");
			map.put("set-eattribute", "cbp:SetEAttribute");
			map.put("unset-eattribute", "cbp:UnsetEAttribute");
			map.put("set-ereference", "cbp:SetEReference");
			map.put("unset-ereference", "cbp:UnsetEReference");
			map.put("add-to-eattribute", "cbp:AddToEAttribute");
			map.put("remove-from-eattribute", "cbp:RemoveFromEAttribute");
			map.put("add-to-ereference", "cbp:AddToEReference");
			map.put("remove-from-ereference", "cbp:RemoveFromEReference");
			map.put("move-in-eattribute", "cbp:MoveInEAttribute");
			map.put("move-in-ereference", "cbp:MoveInEReference");
			

			String startString = "<?xml version=\"1.0\" encoding=\"ASCII\"?>" + System.lineSeparator()
					+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:cbp=\"https://github.com/epsilonlabs/emf-cbp/1.0\">"
					+ System.lineSeparator();
			String endString = "</xmi:XMI>";
			
			FileOutputStream fos = new FileOutputStream(target, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			FileReader fr = new FileReader(source);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			int lineCount = 1;
			
			fos.write(startString.getBytes());
			while ((line = br.readLine()) != null) {
				if (lineCount % 100000 == 0) {
					System.out.println(lineCount);
				}

				for (Entry<String, String> entry : map.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					line = line.replace(key, value);
				}
				line = line + System.lineSeparator();
				bos.write(line.getBytes());
				bos.flush();
				fos.flush();
				lineCount += 1;
			}
			fos.write(endString.getBytes());
			
			br.close();
			fr.close();
			bos.close();
			fos.close();
			
			Resource resource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(target.getAbsolutePath()));
			resource.load(null);
			
//			StringOutputStream outputStream = new StringOutputStream();
//			resource.save(outputStream, null);
//
//			System.out.println(outputStream.toString());
//			outputStream.close();

			System.out.println("Finished!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(true, true);
	}

}