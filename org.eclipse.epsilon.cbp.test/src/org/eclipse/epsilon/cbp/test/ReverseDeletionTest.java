package org.eclipse.epsilon.cbp.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.test.node.Node;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.junit.Test;
import org.junit.internal.JUnitSystem;

public class ReverseDeletionTest {

	@Test
	public void testReverseDeletion() throws Exception {
		try {
			NodePackage nodePackage = NodePackage.eINSTANCE;

			StringBuilder sb = new StringBuilder();
			String s = System.lineSeparator();
			sb.append("var n1 : new Node;" + s);
			sb.append("n1.name = \"n1\";" + s);
//			sb.append("n1.name = null;" + s);
			
			sb.append("var n2 : new Node;" + s);
			sb.append("n2.name = \"n2\";" + s);
			sb.append("n2.listValues.add(11);" + s);
			sb.append("n2.listValues.add(12);" + s);
			sb.append("var n3 : new Node;" + s);
			sb.append("n3.name = \"n3\";" + s);
			sb.append("var n4 : new Node;" + s);
			sb.append("n4.name = \"n4\";" + s);
			sb.append("n2.valNodes.add(n3);" + s);
			sb.append("n1.valNodes.add(n2);" + s);
			sb.append("n4.refNodes.add(n3);" + s);
			sb.append("n4.refNodes.add(n1);" + s);
			sb.append("n4.refNodes.add(n2);" + s);
			sb.append("n4.refNode = n3;" + s);
			sb.append("n1.refNodes.add(n4);" + s);
			sb.append("n1.refNode = n4;" + s);
//			sb.append("n4.refNode.println();" + s);			
//			sb.append("delete n1;" + s);
			

			EolModule module = new EolModule();
			module.parse(sb.toString());

			CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory())
					.createResource(URI.createURI("dummy.cbpxml"));
			cbpResource.setIdType(IdType.NUMERIC);
			InMemoryEmfModel model = new InMemoryEmfModel("M", cbpResource, nodePackage);
			module.getContext().getModelRepository().addModel(model);
			module.execute();
			
//			TreeIterator<EObject> iterator = cbpResource.getAllContents();
//			while(iterator.hasNext()) {
//				Node node = (Node) iterator.next();
//				if (node.getName().equals("n4")) {
//					System.out.println(node.getRefNode());
//					break;
//				}
//			}
			
			TreeIterator<EObject> iterator = cbpResource.getAllContents();
			while(iterator.hasNext()) {
				Node node = (Node) iterator.next();
				if (node.getName() != null && node.getName().equals("n1")) {
					cbpResource.deleteElement(node);
//					EcoreUtil.delete(node, true);
					break;
				}
			}
			
			iterator = cbpResource.getAllContents();
			while(iterator.hasNext()) {
				Node node = (Node) iterator.next();
				if (node.getName().equals("n4")) {
					node.setName("n4-b");
//					System.out.println(node.getRefNode());
					break;
				}
			}
						

			StringOutputStream outputStream = new StringOutputStream();
//			Map<Object, Object> options = new HashMap<>();
//			options.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, false);
//			cbpResource.save(outputStream, options);
			cbpResource.save(outputStream, null);
			System.out.println(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		

	}

}
