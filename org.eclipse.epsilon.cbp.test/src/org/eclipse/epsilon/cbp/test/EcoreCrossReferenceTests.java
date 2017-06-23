package org.eclipse.epsilon.cbp.test;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.junit.Test;

public class EcoreCrossReferenceTests extends XmiResourceEquivalenceTests {
	
	public EcoreCrossReferenceTests(String extension) {
		super(extension);
	}

	@Test
	public void testExternalSupertype() throws Exception {
		run("var c1 : new M!EClass; var c2 : new X!EClass; /*c1.eResource().println(); c2.eResource().println();*/ c1.eSuperTypes.add(c2);");
	}
	
	@Override
	public EPackage getEPackage() {
		return EcorePackage.eINSTANCE;
	}
	
	@Override
	protected Collection<IModel> getExtraModels() {
		EmfModel model = new EmfModel();
		model.setMetamodelUri(EcorePackage.eINSTANCE.getNsURI());
		model.setModelFile("extra.ecore");
		model.setName("X");
		model.setReadOnLoad(false);
		model.setStoredOnDisposal(false);
		try { model.load(); } catch (EolModelLoadingException ex) { throw new RuntimeException(ex); }
		return Arrays.asList(model);
	}
	
}
