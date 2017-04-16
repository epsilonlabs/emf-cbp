package org.eclipse.epsilon.cbp.test;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.thrift.CBPThriftResourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EcoreAppendTests extends AppendTests {

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 { "cbpxml", new CBPXMLResourceFactory() },
                 { "cbpthrift", new CBPThriftResourceFactory() }  
           });
    }

	public EcoreAppendTests(String extension, Resource.Factory factory) {
		super(extension, factory);
	}

	@Test
	public void testCreates() throws Exception {
		run("var c1 = new EClass;", "var c2 = new EClass;");
	}
	
	@Test
	public void testCreatesAndDelete() throws Exception {
		run("var c1 = new EClass;", "delete EClass.all;", "var c2 = new EClass;");
	}
	
	@Override
	public EPackage getEPackage() {
		return EcorePackage.eINSTANCE;
	}
	
}
