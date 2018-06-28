package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.event.CompositeEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.event.ReconcileEvent;
import org.eclipse.epsilon.cbp.comparison.event.SessionEvent;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.CancelEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;
import org.eclipse.epsilon.cbp.event.EStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.FromPositionEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CBPComparison {

	private List<CompositeEvent> leftCompositeEvents = new ArrayList<>();
	private List<CompositeEvent> rightCompositeEvents = new ArrayList<>();
	private List<ComparisonEvent> mergedEvents = new ArrayList<>();
	private List<SessionEvent> leftSessionEvents = new ArrayList<>();
	private List<SessionEvent> rightSessionEvents = new ArrayList<>();
	private List<ComparisonEvent> leftComparisonEvents = new ArrayList<>();
	private List<ComparisonEvent> rightComparisonEvents = new ArrayList<>();
	private List<Line> leftLines = new ArrayList<>();
	private List<Line> rightLines = new ArrayList<>();
	private List<ComparisonLine> comparisonLines = new ArrayList<>();
	private File leftCbpFile;
	private File rightCbpFile;
	private CBPResource leftResource;
	private CBPResource rightResource;
	private List<ConflictedEventPair> conflictedEventsList = new ArrayList<>();
	private SessionEvent reconciliationSessionEvent;

	public CBPComparison() {

	}

	public CBPComparison(File leftCbpFile, File rightCbpFile) throws IOException, XMLStreamException {
		this.leftCbpFile = leftCbpFile;
		this.rightCbpFile = rightCbpFile;

	}

	public void compare() throws IOException, XMLStreamException, TransformerConfigurationException, ParserConfigurationException {
		// leftResource = (CBPResource) (new CBPXMLResourceFactory())
		// .createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
		// System.out.println("Loading " + leftResource.getURI().lastSegment() +
		// "...");
		// leftResource.load(null);

		// rightResource = (CBPResource) (new CBPXMLResourceFactory())
		// .createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
		// System.out.println("Loading " + rightResource.getURI().lastSegment()
		// + "...");
		// rightResource.load(null);

		RandomAccessFile leftRaf = new RandomAccessFile(leftCbpFile, "rw");
		RandomAccessFile rightRaf = new RandomAccessFile(rightCbpFile, "rw");

		FileChannel leftChannel = leftRaf.getChannel();
		FileChannel rightChannel = rightRaf.getChannel();

		ByteBuffer leftBuffer = ByteBuffer.allocate(1);
		ByteBuffer rightBuffer = ByteBuffer.allocate(1);

		int leftInt = -1;
		int rightInt = -1;
		CharBuffer leftCharBuffer;
		CharBuffer rightCharBuffer;
		int lineCount = 1;
		long position = -1;
		char leftChar;
		char rightChar;

		while (true) {
			leftInt = leftChannel.read(leftBuffer);
			rightInt = rightChannel.read(rightBuffer);

			leftBuffer.flip();
			leftCharBuffer = Charset.defaultCharset().decode(leftBuffer);
			leftChar = leftCharBuffer.get();
			leftBuffer.clear();

			rightBuffer.flip();
			rightCharBuffer = Charset.defaultCharset().decode(rightBuffer);
			rightChar = rightCharBuffer.get();
			rightBuffer.clear();

			if (leftChar != rightChar || leftInt == -1 && rightInt == -1) {
				System.out.println();
				break;
			} else if (leftChar == (char) 13) {
				lineCount += 1;
				position = leftChannel.position();
			}
			System.out.print(leftChar);
		}

		leftChannel.position(position);
		rightChannel.position(position);

		System.out.println();
		createComparisonEvents(leftChannel, leftComparisonEvents, leftSessionEvents, leftCompositeEvents);
		System.out.println();
		createComparisonEvents(rightChannel, rightComparisonEvents, rightSessionEvents, rightCompositeEvents);

		int leftLineCount = lineCount;
		int rightLineCount = lineCount;

		System.out.println();
		System.out.println("COMPARISON:");
		for (SessionEvent leftSessionEvent : leftSessionEvents) {
			for (SessionEvent rightSessionEvent : rightSessionEvents) {
				if (!leftSessionEvent.getSessionId().equals(rightSessionEvent.getSessionId())) {

					for (ComparisonEvent leftEvent : leftSessionEvent.getComparisonEvents()) {
						for (ComparisonEvent rightEvent : rightSessionEvent.getComparisonEvents()) {

							System.out.println(leftEvent.getEventString() + " vs " + rightEvent.getEventString());

							if (leftEvent.getEventType().equals(SetEAttributeEvent.class)
									&& rightEvent.getEventType().equals(SetEAttributeEvent.class)
									&& leftEvent.getTargetId().equals(rightEvent.getTargetId())
									&& leftEvent.getFeatureName().equals(rightEvent.getFeatureName())
									&& leftEvent.getValue() != null && rightEvent.getValue() != null
									&& !leftEvent.getValue().equals(rightEvent.getValue())) {
								ConflictedEventPair conflictedEvents = new ConflictedEventPair(leftLineCount, leftEvent,
										rightLineCount, rightEvent, ConflictedEventPair.TYPE_DIFFERENT_VALUES);
								conflictedEventsList.add(conflictedEvents);
								leftEvent.setIsConflicted(true);
								leftEvent.setConflictedEventPair(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.setConflictedEventPair(conflictedEvents);
							} else if (leftEvent.getEventType().equals(EReferenceEvent.class)
									&& rightEvent.getEventType().equals(EReferenceEvent.class)
									&& leftEvent.getTargetId().equals(rightEvent.getTargetId())
									&& leftEvent.getFeatureName().equals(rightEvent.getFeatureName())
									&& leftEvent.getValue() != null && rightEvent.getValue() != null
									&& !leftEvent.getValueId().equals(rightEvent.getValueId())) {
								ConflictedEventPair conflictedEvents = new ConflictedEventPair(leftLineCount, leftEvent,
										rightLineCount, rightEvent, ConflictedEventPair.TYPE_DIFFERENT_VALUES);
								conflictedEventsList.add(conflictedEvents);
								leftEvent.setIsConflicted(true);
								leftEvent.setConflictedEventPair(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.setConflictedEventPair(conflictedEvents);
							} else if (((leftEvent.getEventType().equals(SetEAttributeEvent.class)
									|| leftEvent.getEventType().equals(SetEReferenceEvent.class)
									|| leftEvent.getEventType().equals(AddToEAttributeEvent.class)
									|| leftEvent.getEventType().equals(AddToEReferenceEvent.class)
									|| leftEvent.getEventType().equals(RemoveFromEAttributeEvent.class)
									|| leftEvent.getEventType().equals(RemoveFromEReferenceEvent.class)
									|| leftEvent.getEventType().equals(MoveWithinEAttributeEvent.class)
									|| leftEvent.getEventType().equals(MoveWithinEReferenceEvent.class)
									|| leftEvent.getEventType().equals(AddToResourceEvent.class)
									|| leftEvent.getEventType().equals(RemoveFromResourceEvent.class))
									&& rightEvent.getEventType().equals(DeleteEObjectEvent.class)
									&& (rightEvent.getValueId().equals(leftEvent.getTargetId())
											|| rightEvent.getValueId().equals(leftEvent.getValueId())))
									|| (rightEvent.getEventType().equals(SetEAttributeEvent.class)
											|| rightEvent.getEventType().equals(SetEReferenceEvent.class)
											|| rightEvent.getEventType().equals(AddToEAttributeEvent.class)
											|| rightEvent.getEventType().equals(AddToEReferenceEvent.class)
											|| rightEvent.getEventType().equals(RemoveFromEAttributeEvent.class)
											|| rightEvent.getEventType().equals(RemoveFromEReferenceEvent.class)
											|| rightEvent.getEventType().equals(MoveWithinEAttributeEvent.class)
											|| rightEvent.getEventType().equals(MoveWithinEReferenceEvent.class)
											|| rightEvent.getEventType().equals(AddToResourceEvent.class)
											|| rightEvent.getEventType().equals(RemoveFromResourceEvent.class))
											&& leftEvent.getEventType().equals(DeleteEObjectEvent.class)
											&& (leftEvent.getValueId().equals(rightEvent.getTargetId())
													|| leftEvent.getValueId().equals(rightEvent.getValueId()))) {
								ConflictedEventPair conflictedEvents = new ConflictedEventPair(leftLineCount, leftEvent,
										rightLineCount, rightEvent, ConflictedEventPair.TYPE_INAPPLICABLE);
								conflictedEventsList.add(conflictedEvents);
								leftEvent.setIsConflicted(true);
								leftEvent.setConflictedEventPair(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.setConflictedEventPair(conflictedEvents);
							}
						}
					}
				}
				rightLineCount += rightSessionEvent.getComparisonEvents().size();
			}
			leftLineCount += leftSessionEvent.getComparisonEvents().size();
		}

		leftChannel.close();
		leftRaf.close();
		rightChannel.close();
		rightRaf.close();

	}

	/**
	 * @param fileChannel
	 * @param byteBuffer
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws ParserConfigurationException 
	 * @throws TransformerConfigurationException 
	 */
	private void createComparisonEvents(FileChannel fileChannel, List<ComparisonEvent> comparisonEvents,
			List<SessionEvent> sessionEvents, List<CompositeEvent> compositeEvents)
			throws IOException, XMLStreamException, TransformerConfigurationException, ParserConfigurationException {
		SessionEvent previousSessionEvent = null;
		CompositeEvent previousCompositeEvent = null;
		String previousCompositeId = null;
		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
		CharBuffer charBuffer;
		char c;
		StringBuilder stringBuilder = new StringBuilder();
		while (fileChannel.read(byteBuffer) != -1) {
			byteBuffer.flip();
			charBuffer = Charset.defaultCharset().decode(byteBuffer);
			c = charBuffer.get();
			byteBuffer.clear();
			if (c == (char) 13) {
				String lineString = stringBuilder.toString().trim();
				System.out.println(lineString);
				ComparisonEvent comparisonEvent = createComparisonEvent(lineString, leftResource);

				if (comparisonEvent.getComposite() != null
						&& !comparisonEvent.getComposite().equals(previousCompositeId)) {
					CompositeEvent compositeEvent = new CompositeEvent(comparisonEvent.getComposite());
					compositeEvent.getComparisonEvents().add(comparisonEvent);
					compositeEvents.add(compositeEvent);
					previousCompositeId = comparisonEvent.getComposite();
					previousCompositeEvent = compositeEvent;
				} else if (comparisonEvent.getComposite() != null) {
					previousCompositeEvent.getComparisonEvents().add(comparisonEvent);
					previousCompositeId = comparisonEvent.getComposite();
				}

				if (comparisonEvent.getEventType().equals(StartNewSessionEvent.class)) {
					SessionEvent sessionEvent = new SessionEvent(comparisonEvent);
					StartNewSessionEvent event = (StartNewSessionEvent) comparisonEvent.getChangeEvent();
					sessionEvent.setSessionId(event.getSessionId());
					sessionEvent.setStringTime(event.getTime());
					sessionEvent.getComparisonEvents().add(comparisonEvent);
					sessionEvents.add(sessionEvent);
					previousSessionEvent = sessionEvent;
				} else {
					previousSessionEvent.getComparisonEvents().add(comparisonEvent);
				}

				comparisonEvents.add(comparisonEvent);
				stringBuilder.setLength(0);
			} else {
				stringBuilder.append(c);
			}
		}
	}

	public void updateLeftWithAllLeftSolutions(boolean createNewFile)
			throws FileNotFoundException, IOException, TransformerException, ParserConfigurationException {

		File targetFile = null;
		if (createNewFile) {
			String path = leftCbpFile.getParentFile().getAbsolutePath();
			String fileName = leftCbpFile.getName();
			int pos = fileName.lastIndexOf(".");
			String extension = "";
			if (pos > -1) {
				extension = fileName.substring(pos, fileName.length());
			}
			fileName = fileName.substring(0, pos);
			fileName = (new StringBuilder()).append(fileName).append("-merged").append(extension).toString();
			targetFile = new File(path + File.separator + fileName);
			Files.copy(new FileInputStream(leftCbpFile), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			targetFile = leftCbpFile;
		}

		//put right comparison events to merged events
		for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
			mergedEvents.add(comparisonEvent);
		}
		
		//put left comparison events to merged events
		Iterator<ComparisonEvent> iterator = leftComparisonEvents.iterator();
		while(iterator.hasNext()) {
			ComparisonEvent comparisonEvent = iterator.next();
			if (comparisonEvent.isConflicted()) {
				ConflictedEventPair conflictedEventPair = comparisonEvent.getConflictedEventPair();
				
			}
			
			mergedEvents.add(comparisonEvent);
		}
		
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("");
		for (ComparisonEvent comparisonEvent : mergedEvents) {
			sb.append(comparisonEvent.getEventString());
			sb.append(System.lineSeparator());
		}

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile, true));
		bos.write(sb.toString().getBytes());
		bos.flush();
		bos.close();

	}
//	public void updateLeftWithAllLeftSolutions(boolean createNewFile)
//			throws FileNotFoundException, IOException, TransformerException, ParserConfigurationException {
//
//		File targetFile = null;
//		if (createNewFile) {
//			String path = leftCbpFile.getParentFile().getAbsolutePath();
//			String fileName = leftCbpFile.getName();
//			int pos = fileName.lastIndexOf(".");
//			String extension = "";
//			if (pos > -1) {
//				extension = fileName.substring(pos, fileName.length());
//			}
//			fileName = fileName.substring(0, pos);
//			fileName = (new StringBuilder()).append(fileName).append("-merged").append(extension).toString();
//			targetFile = new File(path + File.separator + fileName);
//			Files.copy(new FileInputStream(leftCbpFile), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//		} else {
//			targetFile = leftCbpFile;
//		}
//
//		// create session event to group solution event
//		StartNewSessionEvent startNewSessionEvent = new StartNewSessionEvent(EcoreUtil.generateUUID());
//		String eventString = createEventString((ChangeEvent<?>) startNewSessionEvent);
//		ComparisonEvent sessionComparisonEvent = new ComparisonEvent(startNewSessionEvent.getClass(),
//				startNewSessionEvent, null, null, null, -1, eventString, null, null, null);
//		reconciliationSessionEvent = new SessionEvent(sessionComparisonEvent);
//		reconciliationSessionEvent.getComparisonEvents().add(sessionComparisonEvent);
//
//		for (ComparisonEvent rightEvent : rightComparisonEvents) {
//			if (rightEvent.isConflicted()) {
//				if (rightEvent.getConflictedEvents().getType() == ConflictedEvents.TYPE_DIFFERENT_VALUES) {
//					fillReconciliationSessionEvent(rightEvent);
//				} else if (rightEvent.getConflictedEvents().getType() == ConflictedEvents.TYPE_INAPPLICABLE) {
//					fillReconciliationSessionEvent(rightEvent);
//				}
//			} else {
//
//			}
//		}
//
//		int indexReconcileEvent = 0;
//		int sizeReconcileEvents = reconciliationSessionEvent.getComparisonEvents().size() + leftComparisonEvents.size();
//		int indexCancelledEvent = 0;
//		for (ComparisonEvent comparisonEvent : reconciliationSessionEvent.getComparisonEvents()) {
//			if (comparisonEvent instanceof ReconcileEvent) {
//				ReconcileEvent reconcileEvent = (ReconcileEvent) comparisonEvent;
//				ComparisonEvent cancelledEvent = reconcileEvent.getCancelledEvent();
//				indexCancelledEvent = rightComparisonEvents.indexOf(cancelledEvent);
//				int offset = sizeReconcileEvents - indexReconcileEvent + indexCancelledEvent;
//
//				CancelEvent cancelEvent = (CancelEvent) reconcileEvent.getChangeEvent();
//				cancelEvent.setLineToCancelOffset(offset);
//				String temp = createEventString(cancelEvent);
//				reconcileEvent.setEventString(temp);
//			}
//			mergedEvents.add(comparisonEvent);
//			indexReconcileEvent += 1;
//		}
//		for (ComparisonEvent comparisonEvent : leftComparisonEvents) {
//			mergedEvents.add(comparisonEvent);
//		}
//		for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
//			mergedEvents.add(comparisonEvent);
//		}
//
//		StringBuilder sb = new StringBuilder();
//		sb.append("");
//		for (ComparisonEvent comparisonEvent : mergedEvents) {
//			sb.append(comparisonEvent.getEventString());
//			sb.append(System.lineSeparator());
//		}
//
//		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile, true));
//		bos.write(sb.toString().getBytes());
//		bos.flush();
//		bos.close();
//
//	}

	/**
	 * @param reconciliationSessionEvent
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private void fillReconciliationSessionEvent(ComparisonEvent cancelledComparisonEvent)
			throws ParserConfigurationException, TransformerException {
		CancelEvent cancelEvent = new CancelEvent();
		String cancelEventString = "";
		ComparisonEvent cancelingComparisonEvent = new ComparisonEvent(cancelEvent.getClass(), cancelEvent, null, null,
				null, -1, cancelEventString, null, null, null);
		ReconcileEvent reconcileEvent = new ReconcileEvent(cancelingComparisonEvent, cancelledComparisonEvent);
		reconciliationSessionEvent.getComparisonEvents().add(reconcileEvent);
	}

	public List<ConflictedEventPair> getConflictedEventPairs() {
		return this.conflictedEventsList;
	}

	public List<Line> getLeftLines() {
		return leftLines;
	}

	public List<Line> getRightLines() {
		return rightLines;
	}

	public List<ComparisonLine> getComparisonLines() {
		return comparisonLines;
	}

	private ComparisonEvent createComparisonEvent(String eventString, Resource mainResource) throws XMLStreamException {
		ComparisonEvent comparisonEvent = null;

		ByteArrayInputStream headerStream = new ByteArrayInputStream(new byte[0]);
		ByteArrayInputStream contentStream = new ByteArrayInputStream(eventString.getBytes());
		InputStream stream = new SequenceInputStream(headerStream, contentStream);

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream);

		ChangeEvent<?> changeEvent = null;
		boolean ignore = false;

		while (xmlReader.hasNext()) {
			XMLEvent xmlEvent = xmlReader.nextEvent();
			if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
				StartElement e = xmlEvent.asStartElement();
				String name = e.getName().getLocalPart();

				if (name.equals("m")) {
					continue;
				}

				if (!name.equals("value")) {
					if (ignore == false) {
						switch (name) {
						case "session": {
							String sessionId = e.getAttributeByName(new QName("id")).getValue();
							String time = e.getAttributeByName(new QName("time")).getValue();
							changeEvent = new StartNewSessionEvent(sessionId, time);
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, null, null, null,
									-1, eventString, null, null, null);
						}
							break;
						case "register": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							changeEvent = new RegisterEPackageEvent(ePackage, null);
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, null, null, null,
									-1, eventString, null, null, null);
						}
							break;
						case "create": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							changeEvent = new CreateEObjectEvent(eClass, (CBPResource) mainResource, id);
							EObject eObject = getEObject(id, mainResource);
							changeEvent.setValues(eObject);

							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, null,
									changeEvent.getValue(), null, -1, eventString, null, id, null);
						}
							break;
						case "add-to-resource":
							changeEvent = new AddToResourceEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "remove-from-resource":
							changeEvent = new RemoveFromResourceEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "add-to-ereference":
							changeEvent = new AddToEReferenceEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "remove-from-ereference":
							changeEvent = new RemoveFromEReferenceEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "set-eattribute":
							changeEvent = new SetEAttributeEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "set-ereference":
							changeEvent = new SetEReferenceEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "unset-eattribute":
							changeEvent = new UnsetEAttributeEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "unset-ereference":
							changeEvent = new UnsetEReferenceEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "add-to-eattribute":
							changeEvent = new AddToEAttributeEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "remove-from-eattribute":
							changeEvent = new RemoveFromEAttributeEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "move-in-eattribute":
							changeEvent = new MoveWithinEAttributeEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "move-in-ereference":
							changeEvent = new MoveWithinEReferenceEvent();
							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
							break;
						case "delete": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							if (mainResource != null) {
								changeEvent = new DeleteEObjectEvent(eClass, (CBPResource) mainResource, id);
							} else {
								changeEvent = new DeleteEObjectEvent();
							}
							EObject eObject = getEObject(id, mainResource);
							changeEvent.setValues(eObject);

							comparisonEvent = new ComparisonEvent(changeEvent.getClass(), changeEvent, null,
									changeEvent.getValue(), null, -1, eventString, null, id, null);
						}
							break;
						}

						if (changeEvent instanceof EStructuralFeatureEvent<?>) {
							String sTarget = e.getAttributeByName(new QName("target")).getValue();
							String sName = e.getAttributeByName(new QName("name")).getValue();
							EObject target = getEObject(sTarget, mainResource);
							if (target != null) {
								EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(sName);
								((EStructuralFeatureEvent<?>) changeEvent).setEStructuralFeature(eStructuralFeature);
								((EStructuralFeatureEvent<?>) changeEvent).setTarget(target);

								comparisonEvent.setFeature(eStructuralFeature);
							}
							if (sTarget != null) {
								comparisonEvent.setFeatureName(sName);
								comparisonEvent.setTarget(target);
								comparisonEvent.setTargetId(sTarget);
							}
						}

						if (changeEvent instanceof AddToEAttributeEvent || changeEvent instanceof AddToEReferenceEvent
								|| changeEvent instanceof AddToResourceEvent) {
							String sPosition = e.getAttributeByName(new QName("position")).getValue();
							changeEvent.setPosition(Integer.parseInt(sPosition));

							comparisonEvent.setPosition(changeEvent.getPosition());
						}
						if (changeEvent instanceof FromPositionEvent) {
							String sTo = e.getAttributeByName(new QName("to")).getValue();
							String sFrom = e.getAttributeByName(new QName("from")).getValue();
							changeEvent.setPosition(Integer.parseInt(sTo));
							((FromPositionEvent) changeEvent).setFromPosition(Integer.parseInt(sFrom));

							comparisonEvent.setFrom(((FromPositionEvent) changeEvent).getFromPosition());
							comparisonEvent.setTo(changeEvent.getPosition());
						}

						Attribute compositeAttribute = e.getAttributeByName(new QName("composite"));
						if (compositeAttribute != null) {
							String composite = compositeAttribute.getValue();
							changeEvent.setComposite(composite);

							comparisonEvent.setComposite(composite);
						}

					}

				} else if (name.equals("value")) {
					if (ignore == false) {
						if (changeEvent instanceof EObjectValuesEvent) {
							EObjectValuesEvent valuesEvent = (EObjectValuesEvent) changeEvent;
							String seobject = e.getAttributeByName(new QName("eobject")).getValue();
							EObject eob = resolveXRef(seobject, mainResource);
							valuesEvent.getValues().add(eob);

							comparisonEvent.setValueId(seobject);
							comparisonEvent.setValue(eob);
						} else if (changeEvent instanceof EAttributeEvent) {
							EAttributeEvent eAttributeEvent = (EAttributeEvent) changeEvent;
							String sliteral = e.getAttributeByName(new QName("literal")).getValue();

							if (eAttributeEvent.getEStructuralFeature() != null) {
								EDataType eDataType = ((EDataType) eAttributeEvent.getEStructuralFeature().getEType());
								Object value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,
										sliteral);
								eAttributeEvent.getValues().add(value);
							}

							comparisonEvent.setValue(sliteral);
						}
					}
				}
			}
			if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
				EndElement ee = xmlEvent.asEndElement();
				String name = ee.getName().getLocalPart();
				if (changeEvent != null && !name.equals("value") && !name.equals("m")) {
					if (ignore == false) {
						// comparisonEvent = event;
					} else {
						ignore = false;
					}
					// eventNumber += 1;
				}
			}
		}
		xmlReader.close();

		return comparisonEvent;
	}

	private EObject resolveXRef(final String sEObjectURI, Resource mainResource) {
		EObject eob = getEObject(sEObjectURI, mainResource);
		if (mainResource != null && eob == null) {
			URI uri = URI.createURI(sEObjectURI);

			String nsURI = uri.trimFragment().toString();
			ResourceSet resourceSet = mainResource.getResourceSet();
			EPackage pkg = null;
			if (resourceSet != null) {
				EPackage.Registry registry = resourceSet.getPackageRegistry();
				pkg = (EPackage) registry.get(nsURI);
			}
			if (pkg == null) {
				resourceSet = mainResource.getResourceSet();
				if (resourceSet != null) {
					EPackage.Registry registry = resourceSet.getPackageRegistry();
					pkg = (EPackage) registry.get(nsURI);
				}
			}
			if (pkg != null) {
				eob = pkg.eResource().getEObject(uri.fragment());
			}
		}
		return eob;
	}

	private EObject getEObject(String uriFragment, Resource mainResource) {
		EObject eObject = null;
		if (mainResource != null) {
			eObject = mainResource.getEObject(uriFragment);
		}
		return eObject;
	}

	
}
