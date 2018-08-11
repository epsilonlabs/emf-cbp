package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.event.CompositeEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.event.SessionEvent;
import org.eclipse.epsilon.cbp.comparison.merge.AllLeftResolutionStrategy;
import org.eclipse.epsilon.cbp.comparison.merge.AllRightResolutionStrategy;
import org.eclipse.epsilon.cbp.comparison.merge.ResolutionStrategy;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;
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

public class CBPComparison {

    
    	private String leftNsURI = null;
    	private String rightNsURI = null;
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
		char leftChar = ' ';
		char rightChar = ' ';

		boolean continueRead = true; 
		while (continueRead) {
			leftInt = leftChannel.read(leftBuffer);
			rightInt = rightChannel.read(rightBuffer);
			
			if (leftChar != rightChar || (leftInt == -1 || rightInt == -1)) {
				continueRead = false;
				break;
			} else if (leftChar == (char) 13) {
				lineCount += 1;
				position = leftChannel.position();
			}
			
			leftBuffer.flip();
			leftCharBuffer = Charset.defaultCharset().decode(leftBuffer);
			leftChar = leftCharBuffer.get();
			leftBuffer.clear();

			rightBuffer.flip();
			rightCharBuffer = Charset.defaultCharset().decode(rightBuffer);
			rightChar = rightCharBuffer.get();
			rightBuffer.clear();

			System.out.print(leftChar);
		}
		System.out.println();

		leftChannel.position(position);
		rightChannel.position(position);
		truncatePosition = position-1;

		System.out.println();
		leftNsURI = createComparisonEvents(leftChannel, leftComparisonEvents, leftSessionEvents, leftCompositeEvents);
		System.out.println();
		rightNsURI = createComparisonEvents(rightChannel, rightComparisonEvents, rightSessionEvents, rightCompositeEvents);

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

							// SET, UNSET, ATTRIBUTE
							if ((leftEvent.getEventType().equals(SetEAttributeEvent.class)
									|| leftEvent.getEventType().equals(UnsetEAttributeEvent.class))
									&& (rightEvent.getEventType().equals(SetEAttributeEvent.class)
											|| rightEvent.getEventType().equals(UnsetEAttributeEvent.class))
									&& leftEvent.getTargetId().equals(rightEvent.getTargetId())
									&& leftEvent.getFeatureName().equals(rightEvent.getFeatureName())
									&& (leftEvent.getValue() != null || rightEvent.getValue() != null)
									&& !leftEvent.getValue().equals(rightEvent.getValue())) {
								ConflictedEventPair conflictedEvents = new ConflictedEventPair(leftLineCount, leftEvent,
										rightLineCount, rightEvent, ConflictedEventPair.TYPE_DIFFERENT_STATES);
								conflictedEventsList.add(conflictedEvents);
								leftEvent.setIsConflicted(true);
								leftEvent.getConflictedEventPairList().add(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.getConflictedEventPairList().add(conflictedEvents);

								// SET, UNSET, REFERENCE
							} else if ((leftEvent.getEventType().equals(SetEReferenceEvent.class)
									|| leftEvent.getEventType().equals(UnsetEReferenceEvent.class))
									&& (rightEvent.getEventType().equals(SetEReferenceEvent.class)
											|| rightEvent.getEventType().equals(UnsetEReferenceEvent.class))
									&& leftEvent.getTargetId().equals(rightEvent.getTargetId())
									&& leftEvent.getFeatureName().equals(rightEvent.getFeatureName())
									&& (leftEvent.getValueId() != null || rightEvent.getValueId() != null)
									&& !leftEvent.getValueId().equals(rightEvent.getValueId())) {
								ConflictedEventPair conflictedEvents = new ConflictedEventPair(leftLineCount, leftEvent,
										rightLineCount, rightEvent, ConflictedEventPair.TYPE_DIFFERENT_STATES);
								conflictedEventsList.add(conflictedEvents);
								leftEvent.setIsConflicted(true);
								leftEvent.getConflictedEventPairList().add(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.getConflictedEventPairList().add(conflictedEvents);

								// REMOVE, ADD, MOVE REFERENCE
							} else if (((leftEvent.getEventType().equals(RemoveFromEReferenceEvent.class)
									&& (rightEvent.getEventType().equals(AddToEReferenceEvent.class)
											|| rightEvent.getEventType().equals(MoveWithinEReferenceEvent.class)))
									|| (rightEvent.getEventType().equals(RemoveFromEReferenceEvent.class)
											&& (leftEvent.getEventType().equals(AddToEReferenceEvent.class) || leftEvent
													.getEventType().equals(MoveWithinEReferenceEvent.class))))
									&& rightEvent.getFeatureName().equals(leftEvent.getFeatureName())
									&& rightEvent.getTargetId().equals(leftEvent.getTargetId())
									&& rightEvent.getValueId().equals(leftEvent.getValueId())

							) {
								ConflictedEventPair conflictedEvents = new ConflictedEventPair(leftLineCount, leftEvent,
										rightLineCount, rightEvent, ConflictedEventPair.TYPE_DIFFERENT_STATES);
								conflictedEventsList.add(conflictedEvents);
								leftEvent.setIsConflicted(true);
								leftEvent.getConflictedEventPairList().add(conflictedEvents);
								rightEvent.setIsConflicted(true);
								rightEvent.getConflictedEventPairList().add(conflictedEvents);

								// DELETE
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
	private String createComparisonEvents(FileChannel fileChannel, List<ComparisonEvent> comparisonEvents,
			List<SessionEvent> sessionEvents, Map<String, CompositeEvent> compositeEvents)
			throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {
	    	String nsURI = null;
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
				} else if (comparisonEvent.getEventType().equals(RegisterEPackageEvent.class)) {
				    	if (nsURI  == null && comparisonEvent.getPackageName() != null) {
				    	    nsURI = comparisonEvent.getPackageName();
				    	}
				} else {
					previousSessionEvent.getComparisonEvents().add(comparisonEvent);
				}

				comparisonEvents.add(comparisonEvent);
				stringBuilder.setLength(0);
			} else {
				stringBuilder.append(c);
			}
		}
		
		return nsURI;
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

		AllLeftResolutionStrategy resolveStrategy = new AllLeftResolutionStrategy(conflictedEventsList,
				leftCompositeEvents, rightCompositeEvents);
		File outputFile = merge(createNewFile, leftCbpFile, leftComparisonEvents, rightComparisonEvents,
				resolveStrategy);
		return outputFile;
	}

	/***
	 * Method to update left file with solutions from right side when conflicts
	 * occurred
	 * 
	 * @param createNewFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws XMLStreamException
	 */
	public File updateLeftWithAllRightSolutions(boolean createNewFile) throws FileNotFoundException, IOException,
			TransformerException, ParserConfigurationException, XMLStreamException {

		AllRightResolutionStrategy resolveStrategy = new AllRightResolutionStrategy(conflictedEventsList,
				leftCompositeEvents, rightCompositeEvents);
		File outputFile = merge(createNewFile, leftCbpFile, leftComparisonEvents, rightComparisonEvents,
				resolveStrategy);
		return outputFile;
	}

	/**
	 * @param createNewFile
	 * @param sourceFile
	 * @param targetComparisonEvents
	 * @param inputComparisonEvents
	 * @param resolveStrategy
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws XMLStreamException
	 */
	protected File merge(boolean createNewFile, File sourceFile, List<ComparisonEvent> targetComparisonEvents,
			List<ComparisonEvent> inputComparisonEvents, ResolutionStrategy resolveStrategy) throws IOException,
			FileNotFoundException, ParserConfigurationException, TransformerException, XMLStreamException {

		beforeSessionEvent = createSessionEvent();
		afterSessionEvent = createSessionEvent();

		File targetFile;
		if (createNewFile) {
			String path = sourceFile.getParentFile().getAbsolutePath();
			String fileName = sourceFile.getName();
			int pos = fileName.lastIndexOf(".");
			String extension = "";
			if (pos > -1) {
				extension = fileName.substring(pos, fileName.length());
			}
			fileName = fileName.substring(0, pos);
			fileName = (new StringBuilder()).append(fileName).append("-merged").append(extension).toString();
			targetFile = new File(path + File.separator + fileName);
			Files.copy(new FileInputStream(sourceFile), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			targetFile = sourceFile;
		}

		// put right comparison events to merged events
		mergedEvents.addAll(inputComparisonEvents);

		// resolve conflicts
		resolveStrategy.execute(beforeSessionEvent, afterSessionEvent);

		// add before session event to merged events
		if (beforeSessionEvent.getComparisonEvents().size() > 1) {
			mergedEvents.addAll(beforeSessionEvent.getComparisonEvents());
		}

		// add current left events to merged events
		mergedEvents.addAll(targetComparisonEvents);

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
		// System.out.println();
		// System.out.println(sb.toString());

		// truncate target file
		RandomAccessFile leftRaf = new RandomAccessFile(targetFile, "rw");
		FileChannel targetFileChannel = leftRaf.getChannel();
		targetFileChannel.truncate(truncatePosition + 1);
		targetFileChannel.close();
		leftRaf.close();

		// append new text to target file
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile, true));
		bos.write(sb.toString().getBytes());
		bos.flush();
		bos.close();

		return targetFile;
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

	public List<ComparisonEvent> getMergedEvents() {
		return mergedEvents;
	}

	public List<ComparisonEvent> getLeftComparisonEvents() {
		return leftComparisonEvents;
	}

	public List<ComparisonEvent> getRightComparisonEvents() {
		return rightComparisonEvents;
	}

	public String getLeftNsURI() {
	    return leftNsURI;
	}

	public String getRightNsURI() {
	    return rightNsURI;
	}
	

}
