package org.eclipse.epsilon.cbp.hybrid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.FromPositionEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.hybrid.event.xmi.CreateEObjectEvent;
import org.eclipse.epsilon.hybrid.event.xmi.DeleteEObjectEvent;

public class CBP2XMIResource extends HybridResource {

	protected File cbpFile;
	protected File targetDir;
	protected int prevSessionLine = 0;
	protected int nextSessionLine = 0;
	protected int sessionCount = 0;
	protected int persistedEvents = 0;
	protected boolean useID = false;
	private Map<Object, Object> xmiOptions;
	private Map<String, String> id2uuid = new HashMap<String, String>();

	public CBP2XMIResource(File cbpFile, File targetDir) {
		this(cbpFile, targetDir, false);
	}

	public CBP2XMIResource(File cbpFile, File targetDir, boolean useID) {
		super();
		this.useID = useID;
		this.cbpFile = cbpFile;
		this.targetDir = targetDir;
		if (this.targetDir.exists() == false)
			targetDir.mkdir();
		this.stateBasedResource = (new XMIResourceFactoryImpl()).createResource(URI.createURI("model.xmi"));
		this.hybridChangeEventAdapter = new HybridXMIChangeEventAdapter(this);
		this.xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
		this.xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
	}

	public void generateXMI() throws FactoryConfigurationError, IOException {
		FileInputStream fis = new FileInputStream(cbpFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		replayEvents(bis, false, this.targetDir, -1, null);
		bis.close();
		fis.close();
	}

	public void generateSessionXMI(String sessionName) {

	}

	public void generateAllSessionsXMIs() throws IOException {
		FileInputStream fis = new FileInputStream(cbpFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		replayEvents(bis, true, this.targetDir, -1, null);
		bis.close();
		fis.close();

	}

	public void generateAllSessionsXMIs(String sessionName) {

	}

	public void generateAllSessionsXMIs(int lineNumber) {

	}

	public void generateLineNumberXMI(int lineNumber) {

	}

	private void replayEvents(InputStream inputStream, boolean all, File targetDir, int targetLineNumber,
			String targetSession) throws FactoryConfigurationError, IOException {
		String errorMessage = null;
		int eventNumber = 0;
		prevSessionLine = 0;
		nextSessionLine = 0;
		LinkedList<String> fifo = new LinkedList<>();

		try {
			InputStream stream = new ByteArrayInputStream(new byte[0]);

			ByteArrayInputStream header = new ByteArrayInputStream(
					"<?xml version='1.0' encoding='ISO-8859-1' ?>".getBytes());
			ByteArrayInputStream begin = new ByteArrayInputStream("<m>".getBytes());
			ByteArrayInputStream end = new ByteArrayInputStream("</m>".getBytes());
			stream = new SequenceInputStream(stream, header);
			stream = new SequenceInputStream(stream, begin);
			stream = new SequenceInputStream(stream, inputStream);
			stream = new SequenceInputStream(stream, end);

			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream);

			ChangeEvent<?> event = null;

			while (xmlReader.hasNext()) {
				XMLEvent xmlEvent = xmlReader.nextEvent();
				if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
					StartElement e = xmlEvent.asStartElement();
					String name = e.getName().getLocalPart();

					if (name.equals("m")) {
						continue;
					}

					if (!name.equals("value")) {

						errorMessage = name;
						switch (name) {
						case "session": {
							String sessionId = e.getAttributeByName(new QName("id")).getValue();
							System.out.println("Processing " + sessionId + " ...");
							String time = e.getAttributeByName(new QName("time")).getValue();
							event = new StartNewSessionEvent(sessionId, time);
							fifo.add(sessionId);
						}
							break;
						case "register": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							errorMessage = errorMessage + ", package: " + packageName;
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							event = new RegisterEPackageEvent(ePackage, hybridChangeEventAdapter);
						}
							break;
						case "create": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							errorMessage = errorMessage + ", package: " + packageName + ", class: " + className
									+ ", id: " + id;
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new CreateEObjectEvent(eClass, this, id);

							id2uuid.put(id, EcoreUtil.generateUUID());

						}
							break;
						case "add-to-resource":
							event = new AddToResourceEvent();
							break;
						case "remove-from-resource":
							event = new RemoveFromResourceEvent();
							break;
						case "add-to-ereference":
							event = new AddToEReferenceEvent();
							break;
						case "remove-from-ereference":
							event = new RemoveFromEReferenceEvent();
							break;
						case "set-eattribute":
							event = new SetEAttributeEvent();
							break;
						case "set-ereference":
							event = new SetEReferenceEvent();
							break;
						case "unset-eattribute":
							event = new UnsetEAttributeEvent();
							break;
						case "unset-ereference":
							event = new UnsetEReferenceEvent();
							break;
						case "add-to-eattribute":
							event = new AddToEAttributeEvent();
							break;
						case "remove-from-eattribute":
							event = new RemoveFromEAttributeEvent();
							break;
						case "move-in-eattribute":
							event = new MoveWithinEAttributeEvent();
							break;
						case "move-in-ereference":
							event = new MoveWithinEReferenceEvent();
							break;
						case "delete": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							errorMessage = errorMessage + ", package: " + packageName + ", class: " + className
									+ ", id: " + id;
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new DeleteEObjectEvent(eClass, this, id);

						}
							break;
						}

						if (event instanceof EStructuralFeatureEvent<?>) {
							String sTarget = e.getAttributeByName(new QName("target")).getValue();
							String sName = e.getAttributeByName(new QName("name")).getValue();
							errorMessage = errorMessage + ", target: " + sTarget + ", name: " + sName;
							EObject target = getEObject(sTarget);
							EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(sName);
							((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
							((EStructuralFeatureEvent<?>) event).setTarget(target);
						} else if (event instanceof ResourceEvent) {
							((ResourceEvent) event).setResource(this);
						}

						if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent
								|| event instanceof AddToResourceEvent) {
							String sPosition = e.getAttributeByName(new QName("position")).getValue();
							errorMessage = errorMessage + ", target: " + sPosition;
							event.setPosition(Integer.parseInt(sPosition));
						}
						if (event instanceof FromPositionEvent) {
							String sTo = e.getAttributeByName(new QName("to")).getValue();
							errorMessage = errorMessage + ", to: " + sTo;
							String sFrom = e.getAttributeByName(new QName("from")).getValue();
							errorMessage = errorMessage + ", from: " + sFrom;
							event.setPosition(Integer.parseInt(sTo));
							((FromPositionEvent) event).setFromPosition(Integer.parseInt(sFrom));
						}

					} else if (name.equals("value")) {

						if (event instanceof EObjectValuesEvent) {
							EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
							String seobject = e.getAttributeByName(new QName("eobject")).getValue();
							errorMessage = errorMessage + ", value: " + seobject;
							EObject eob = resolveXRef(seobject);
							valuesEvent.getValues().add(eob);
						} else if (event instanceof EAttributeEvent) {
							EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
							String sliteral = e.getAttributeByName(new QName("literal")).getValue();
							errorMessage = errorMessage + ", value: " + sliteral;
							EDataType eDataType = ((EDataType) eAttributeEvent.getEStructuralFeature().getEType());
							Object value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,
									sliteral);
							eAttributeEvent.getValues().add(value);
						}

					}
				}
				if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
					EndElement ee = xmlEvent.asEndElement();
					String name = ee.getName().getLocalPart();
					if (event != null && !name.equals("value") && !name.equals("m")) {
						// System.out.println(eventNumber);

						event.replay();

						if (name.equals("session")) {
							sessionCount += 1;
							nextSessionLine = eventNumber;
						}

						if (all == true) {
							if (nextSessionLine != prevSessionLine) {
								String sessionName = fifo.getFirst();
								fifo.removeFirst();
								this.generateXMIFile(targetDir, sessionName);
								prevSessionLine = nextSessionLine;
							}

						} else if (all == false) {
							if (nextSessionLine != prevSessionLine) {
								String sessionName = fifo.getFirst();
								fifo.removeFirst();
								prevSessionLine = nextSessionLine;
							}
							if (eventNumber == targetLineNumber) {
								break;
							}
						}

						errorMessage = "";
						eventNumber += 1;
					}
				}
			}

			// after the end of file
			if (all == true) {
				String sessionName = fifo.getFirst();
				if (sessionName != null) {
					this.generateXMIFile(targetDir, sessionName);
				}
			} else if (all == false) {
				String sessionName = fifo.getFirst();
				this.generateXMIFile(targetDir, sessionName);
			}

			begin.close();
			end.close();
			inputStream.close();
			stream.close();
			xmlReader.close();

			System.out.println("Done!");
		} catch (

		Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: Event Number " + eventNumber + " : " + errorMessage);
			throw new IOException(
					"Error: Event Number " + eventNumber + " : " + errorMessage + "\n" + ex.toString() + "\n");
		}
	}

	private void generateXMIFile(File targetDir, String fileName) throws Exception {

		String targetPath = targetDir.getAbsolutePath() + File.separator + fileName;

		// set id of all objects
		if (useID == true) {
			TreeIterator<EObject> iterator = this.getAllContents();
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();
				String id = this.getURIFragment(eObject);
				id = id2uuid.get(id);
				((XMIResourceImpl) this.stateBasedResource).setID(eObject, id);
			}
		}

		FileOutputStream fos = new FileOutputStream(targetPath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		this.save(bos, xmiOptions);
		fos.close();
		bos.close();
	}

	@Override
	public void doSave(OutputStream out, Map<?, ?> options) throws IOException {
		stateBasedResource.save(out, options);
		out.flush();
	}
}
