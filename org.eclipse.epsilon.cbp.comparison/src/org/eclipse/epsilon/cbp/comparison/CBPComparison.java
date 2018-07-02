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
import java.util.Collections;
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.event.CompositeEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair.SolutionOptions;
import org.eclipse.epsilon.cbp.comparison.event.SessionEvent;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CBPComparison {

	private Map<String, CompositeEvent> leftCompositeEvents = new HashMap<>();
	private Map<String, CompositeEvent> rightCompositeEvents = new HashMap<>();
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
	private List<ConflictedEventPair> conflictedEventsList = new ArrayList<>();
	private SessionEvent beforeSessionEvent;
	private SessionEvent afterSessionEvent;
	private String compositeId = null;

	private long truncatePosition = -1;

	public CBPComparison() {

	}

	public CBPComparison(File leftCbpFile, File rightCbpFile) throws IOException, XMLStreamException {
		this.leftCbpFile = leftCbpFile;
		this.rightCbpFile = rightCbpFile;

	}

	public void compare() throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {
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
		truncatePosition = position;

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

							// System.out.println(leftEvent.getEventString() + "
							// vs " + rightEvent.getEventString());

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
								leftEvent.getConflictedEventPairList().add(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.getConflictedEventPairList().add(conflictedEvents);
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
								leftEvent.getConflictedEventPairList().add(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.getConflictedEventPairList().add(conflictedEvents);
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
								leftEvent.getConflictedEventPairList().add(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.getConflictedEventPairList().add(conflictedEvents);
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
	 * @throws TransformerException
	 */
	private void createComparisonEvents(FileChannel fileChannel, List<ComparisonEvent> comparisonEvents,
			List<SessionEvent> sessionEvents, Map<String, CompositeEvent> compositeEvents)
			throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {
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
				ComparisonEvent comparisonEvent = new ComparisonEvent(lineString);

				if (comparisonEvent.getCompositeId() != null
						&& !comparisonEvent.getCompositeId().equals(previousCompositeId)) {
					CompositeEvent compositeEvent = new CompositeEvent(comparisonEvent.getCompositeId());
					compositeEvent.getComparisonEvents().add(comparisonEvent);
					compositeEvents.put(compositeEvent.getCompositeId(), compositeEvent);
					previousCompositeId = comparisonEvent.getCompositeId();
					previousCompositeEvent = compositeEvent;
				} else if (comparisonEvent.getCompositeId() != null) {
					previousCompositeEvent.getComparisonEvents().add(comparisonEvent);
					previousCompositeId = comparisonEvent.getCompositeId();
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
		System.out.println();
	}

	/***
	 * Method to update left file with solutions from left side when conflicts
	 * occurred
	 * 
	 * @param createNewFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws XMLStreamException
	 */
	public File updateLeftWithAllLeftSolutions(boolean createNewFile) throws FileNotFoundException, IOException,
			TransformerException, ParserConfigurationException, XMLStreamException {
		beforeSessionEvent = createSessionEvent();
		afterSessionEvent = createSessionEvent();

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

		// put right comparison events to merged events
		mergedEvents.addAll(rightComparisonEvents);

		// resolve conflicts
		Iterator<ConflictedEventPair> iterator1 = conflictedEventsList.iterator();
		while (iterator1.hasNext()) {
			ConflictedEventPair conflictedEventPair = iterator1.next();
			if (conflictedEventPair.isResolved() == false) {
				resolve(conflictedEventPair, SolutionOptions.CHOOSE_LEFT);
			}
		}

		// Iterator<ComparisonEvent> iterator2 =
		// leftComparisonEvents.iterator();
		// while (iterator2.hasNext()) {
		// ComparisonEvent comparisonEvent = iterator2.next();
		// if (comparisonEvent.isConflicted()) {
		// ConflictedEventPair conflictedEventPair =
		// comparisonEvent.getConflictedEventPair();
		// resolve(conflictedEventPair, SolutionOptions.CHOOSE_LEFT);
		// }
		// }

		// add before session event to merged events
		if (beforeSessionEvent.getComparisonEvents().size() > 1) {
			mergedEvents.addAll(beforeSessionEvent.getComparisonEvents());
		}

		// add current left events to merged events
		mergedEvents.addAll(leftComparisonEvents);

		// add after session event to merged events
		if (afterSessionEvent.getComparisonEvents().size() > 1) {
			mergedEvents.addAll(afterSessionEvent.getComparisonEvents());
		}

		// concatenate string of new events
		StringBuilder sb = new StringBuilder();
		sb.append("");
		for (ComparisonEvent comparisonEvent : mergedEvents) {
			sb.append(comparisonEvent.getEventString());
			sb.append(System.lineSeparator());
		}
		System.out.println();
		System.out.println(sb.toString());

		// truncate target file
		RandomAccessFile leftRaf = new RandomAccessFile(targetFile, "rw");
		FileChannel leftChannel = leftRaf.getChannel();
		leftChannel.truncate(truncatePosition + 1);
		leftChannel.close();

		// append new text to target file
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile, true));
		bos.write(sb.toString().getBytes());
		bos.flush();
		bos.close();

		return targetFile;
	}

	public void resolve(ConflictedEventPair conflictedEventPair, SolutionOptions selectedSolution)
			throws ParserConfigurationException, TransformerException, XMLStreamException {

		if (selectedSolution == SolutionOptions.CHOOSE_LEFT) {
			if (conflictedEventPair.getType() == ConflictedEventPair.TYPE_DIFFERENT_VALUES) {
				conflictedEventPair.setResolved(true);
			} else if (conflictedEventPair.getType() == ConflictedEventPair.TYPE_INAPPLICABLE) {
				if (conflictedEventPair.getRightEvent().getCompositeId() != null) {
					if (conflictedEventPair.getRightEvent().getEventType() == DeleteEObjectEvent.class) {
						String compositeId = conflictedEventPair.getRightEvent().getCompositeId();
						CompositeEvent compositeEvent = rightCompositeEvents.get(compositeId);
						CompositeEvent reversedCompositeEvent = reverseCompositeEvent(compositeEvent);
						beforeSessionEvent.getComparisonEvents().addAll(reversedCompositeEvent.getComparisonEvents());
					}
				} else {

				}
				conflictedEventPair.setResolved(true);
			}
			
			//set other related conflict pairs to resolved
			if (conflictedEventPair.isResolved()) {
				for (ConflictedEventPair conflictPair : conflictedEventPair.getLeftEvent().getConflictedEventPairList()) {
					conflictPair.setResolved(true);
				}
				
			}
		}

		else if (selectedSolution == SolutionOptions.CHOOSE_RIGHT) {
			if (conflictedEventPair.getType() == ConflictedEventPair.TYPE_DIFFERENT_VALUES) {
				conflictedEventPair.setResolved(true);
			}
			//set other related conflict pairs to resolved 
			if (conflictedEventPair.isResolved()) {
				for (ConflictedEventPair conflictPair : conflictedEventPair.getLeftEvent().getConflictedEventPairList()) {
					conflictPair.setResolved(true);
				}
			}
		}
	}

	/**
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws TransformerConfigurationException
	 */
	private SessionEvent createSessionEvent()
			throws ParserConfigurationException, TransformerException, TransformerConfigurationException {

		StartNewSessionEvent startNewSessionEvent = new StartNewSessionEvent(EcoreUtil.generateUUID());
		ComparisonEvent comparisonEvent = new ComparisonEvent(startNewSessionEvent);
		SessionEvent sessionEvent = new SessionEvent(comparisonEvent);
		sessionEvent.setSessionId(sessionEvent.getSessionId());
		sessionEvent.setStringTime(sessionEvent.getStringTime());
		sessionEvent.getComparisonEvents().add(comparisonEvent);
		return sessionEvent;
	}

	/***
	 * Reverse the order of events that constitute a composite event as well
	 * transform them into their inverse events
	 * 
	 * @param compositeEvent
	 * @return
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws XMLStreamException
	 */
	public CompositeEvent reverseCompositeEvent(CompositeEvent compositeEvent)
			throws ParserConfigurationException, TransformerException, XMLStreamException {

		CompositeEvent newCompositeEvent = new CompositeEvent(EcoreUtil.generateUUID());
		for (ComparisonEvent comparisonEvent : compositeEvent.getComparisonEvents()) {
			String eventString = comparisonEvent.getEventString();
			ComparisonEvent newComparisonEvent = comparisonEvent.reverse(newCompositeEvent.getCompositeId());
//			newComparisonEvent.setCompositeId(newCompositeEvent.getCompositeId());
//			newComparisonEvent.getChangeEvent().setComposite(newCompositeEvent.getCompositeId());
			String reverseEventString = newComparisonEvent.getEventString();
			newCompositeEvent.getComparisonEvents().add(newComparisonEvent);
		}
		Collections.reverse(newCompositeEvent.getComparisonEvents());

		return newCompositeEvent;
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

}
