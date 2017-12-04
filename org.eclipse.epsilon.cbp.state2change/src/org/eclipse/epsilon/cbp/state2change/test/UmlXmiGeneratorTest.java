package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;

import org.eclipse.epsilon.cbp.state2change.UmlXmiGenerator;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.junit.Test;

public class UmlXmiGeneratorTest {
	
	@Test
	public void headlessXmiGenerationTest() throws DiscoveryException{
		
		File gitCommitsDirectory = new File("./test.data/projects/".replace("/", File.separator));
		File targetXmiDirectory = new File("./test.data/xmi/".replace("/", File.separator));
		
		UmlXmiGenerator generator = new UmlXmiGenerator();
		generator.generateXmi(gitCommitsDirectory, targetXmiDirectory);
		
		assertNotEquals(targetXmiDirectory.listFiles().length, 0);	
	}
	
	
}
