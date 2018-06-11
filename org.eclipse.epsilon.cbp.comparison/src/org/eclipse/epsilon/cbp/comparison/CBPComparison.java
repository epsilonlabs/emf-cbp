package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
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
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;

public class CBPComparison {

	private List<ComparisonEvent> leftChangeEvents = new ArrayList<>();
	private List<ComparisonEvent> rightChangeEvents = new ArrayList<>();
	private List<Line> leftLines = new ArrayList<>();
	private List<Line> rightLines = new ArrayList<>();
	private List<ComparisonLine> comparisonLines = new ArrayList<>();
	private File leftCbpFile;
	private File rightCbpFile;
	private CBPResource leftResource;
	private CBPResource rightResource;
	private List<ConflictedEvents> conflictedEventList = new ArrayList<>();

	public CBPComparison() {

	}

	public CBPComparison(File leftCbpFile, File rightCbpFile) throws IOException, XMLStreamException {
		this.leftCbpFile = leftCbpFile;
		this.rightCbpFile = rightCbpFile;

	}

	public void compare() throws IOException, XMLStreamException {
//		leftResource = (CBPResource) (new CBPXMLResourceFactory())
//				.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
//		System.out.println("Loading " + leftResource.getURI().lastSegment() + "...");
//		leftResource.load(null);

//		rightResource = (CBPResource) (new CBPXMLResourceFactory())
//				.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
//		System.out.println("Loading " + rightResource.getURI().lastSegment() + "...");
//		rightResource.load(null);

		FileReader leftFileReader = new FileReader(leftCbpFile);
		BufferedReader leftBufferedReader = new BufferedReader(leftFileReader);

		FileReader rightFileReader = new FileReader(rightCbpFile);
		BufferedReader rightBufferedReader = new BufferedReader(rightFileReader);

		int leftInt = -1;
		int rightInt = -1;
		boolean keepReading = true;

		leftBufferedReader.mark(0);
		rightBufferedReader.mark(0);
		int prevInt = -1;
		int lineCount = 0;
		while (keepReading) {
			leftInt = leftBufferedReader.read();
			rightInt = rightBufferedReader.read();
			if (leftInt != rightInt || leftInt == -1 && rightInt == -1) {
				System.out.println();
				break;
			} else if (prevInt == 13) {
				leftBufferedReader.mark(0);
				rightBufferedReader.mark(0);
				lineCount += 1;
			}
			System.out.print((char) leftInt);
			prevInt = leftInt;
		}

		if (leftInt > -1)
			leftBufferedReader.reset();
		if (rightInt > -1)
			rightBufferedReader.reset();

		System.out.println();

		System.out.println("LEFT:");
		String leftString = null;
		while (leftInt > -1 && (leftString = leftBufferedReader.readLine()) != null) {
			System.out.println(leftString);
			ChangeEvent<?> changeEvent = createEvent(leftString, leftResource, rightResource);
			ComparisonEvent comparisonEvent = createComparisonEvent(changeEvent, leftString);
			leftChangeEvents.add(comparisonEvent);
		}
		leftBufferedReader.close();

		System.out.println("RIGHT:");
		String rightString = null;
		while (rightInt > -1 && (rightString = rightBufferedReader.readLine()) != null) {
			System.out.println(rightString);
			ChangeEvent<?> changeEvent = createEvent(rightString, rightResource, leftResource);
			ComparisonEvent comparisonEvent = createComparisonEvent(changeEvent, rightString);
			rightChangeEvents.add(comparisonEvent);
		}
		rightBufferedReader.close();

		int leftLineCount = lineCount;
		int rightLineCount = lineCount;
		for (ComparisonEvent leftEvent : leftChangeEvents) {
			for (ComparisonEvent rightEvent : rightChangeEvents) {
							
				if (leftEvent.getEventType().equals(rightEvent.getEventType()) 
						&& leftEvent.getTarget() != null
						&& rightEvent.getTarget() != null
						&& leftResource.getEObjectId(leftEvent.getTarget()).equals(rightResource.getEObjectId(rightEvent.getTarget()))
//						&& leftEvent.getFeature() != null && rightEvent.getFeature() != null
//						&& leftEvent.getFeature().equals(rightEvent.getFeature()) && leftEvent.getValue() != null
//						&& rightEvent.getValue() != null && !leftEvent.getValue().equals(rightEvent.getValue())
						) {

					String x = leftResource.getEObjectId(leftEvent.getTarget());
					String y = rightResource.getEObjectId(rightEvent.getTarget());
					
					conflictedEventList.add(new ConflictedEvents(leftLineCount, leftEvent, rightLineCount, rightEvent,
							ConflictedEvents.TYPE_DIFFERENT_VALUES));
					System.out.println();
				}

				rightLineCount += 1;
			}

			leftLineCount += 1;
		}

	}

	private ComparisonEvent createComparisonEvent(ChangeEvent<?> changeEvent, String eventString) {
		Class<?> eventType = changeEvent.getClass();
		EObject target = null;
		Object value = null;
		EStructuralFeature feature = null;
		Integer position = -1;
		if (changeEvent instanceof StartNewSessionEvent) {
		} else if (changeEvent instanceof CreateEObjectEvent) {
			CreateEObjectEvent event = (CreateEObjectEvent) changeEvent;
			value = event.getValue();
		} else if (changeEvent instanceof DeleteEObjectEvent) {
			DeleteEObjectEvent event = (DeleteEObjectEvent) changeEvent;
			value = event.getValue();
		} else if (changeEvent instanceof AddToResourceEvent) {
			AddToResourceEvent event = (AddToResourceEvent) changeEvent;
			value = event.getValue();
		} else if (changeEvent instanceof RemoveFromResourceEvent) {
			RemoveFromResourceEvent event = (RemoveFromResourceEvent) changeEvent;
			value = event.getValue();
		} else if (changeEvent instanceof SetEAttributeEvent) {
			SetEAttributeEvent event = (SetEAttributeEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
		} else if (changeEvent instanceof SetEReferenceEvent) {
			SetEReferenceEvent event = (SetEReferenceEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
		} else if (changeEvent instanceof UnsetEAttributeEvent) {
			UnsetEAttributeEvent event = (UnsetEAttributeEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
		} else if (changeEvent instanceof UnsetEReferenceEvent) {
			UnsetEReferenceEvent event = (UnsetEReferenceEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
		} else if (changeEvent instanceof AddToEAttributeEvent) {
			AddToEAttributeEvent event = (AddToEAttributeEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
			position = event.getPosition();
		} else if (changeEvent instanceof AddToEAttributeEvent) {
			AddToEAttributeEvent event = (AddToEAttributeEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
			position = event.getPosition();
		} else if (changeEvent instanceof AddToEReferenceEvent) {
			AddToEReferenceEvent event = (AddToEReferenceEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
			position = event.getPosition();
		} else if (changeEvent instanceof RemoveFromEAttributeEvent) {
			RemoveFromEAttributeEvent event = (RemoveFromEAttributeEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
			position = event.getPosition();
		} else if (changeEvent instanceof RemoveFromEReferenceEvent) {
			RemoveFromEReferenceEvent event = (RemoveFromEReferenceEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
			position = event.getPosition();
		} else if (changeEvent instanceof MoveWithinEAttributeEvent) {
			MoveWithinEAttributeEvent event = (MoveWithinEAttributeEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
			position = event.getPosition();
		} else if (changeEvent instanceof MoveWithinEReferenceEvent) {
			MoveWithinEReferenceEvent event = (MoveWithinEReferenceEvent) changeEvent;
			target = event.getTarget();
			value = event.getValue();
			feature = event.getEStructuralFeature();
			position = event.getPosition();
		}

		return new ComparisonEvent(eventType, changeEvent, target, value, feature, position, eventString, null, null);
	}
	
	public List<ConflictedEvents> getConflicts() {
		return this.conflictedEventList;
	}

	protected void backtrack(List<String> leftList, List<String> rightList, List<int[]> VS) {
		final int N = leftList.size();
		final int M = rightList.size();
		final int MAX = N + M;
		int offset = (2 * (MAX + 1)) / 2;
		int xEnd = N;
		int yEnd = M;

		char currentSign = Line.SIGN_INITIAL;
		char prevSign = Line.SIGN_INITIAL;
		String text = "";
		int lineNumber = 1;

		for (int D = VS.size() - 1; D >= 0 && (xEnd > 0 || yEnd > 0); D--) {
			int[] V = VS.get(D);

			int k = xEnd - yEnd;

			int kPrev = 0; // previous k
			int xStart = 0;
			int yStart = 0;

			// lower < upper -> upward, else leftward
			if (k == -D || (k != D && V[k - 1 + offset] < V[k + 1 + offset])) {
				kPrev = k + 1;
				xStart = V[kPrev + offset];
				yStart = xStart - kPrev;
				if (yStart >= 0 && yStart < M) {
					currentSign = Line.SIGN_ADD;
					text = rightList.get(yStart);
				}
			} else {
				kPrev = k - 1;
				xStart = V[kPrev + offset];
				yStart = xStart - kPrev;
				if (xStart >= 0 && xStart < N) {
					currentSign = Line.SIGN_DELETE;
					text = leftList.get(xStart);
				}
			}

			while (xEnd > xStart && yEnd > yStart) {
				char equalSign = Line.SIGN_NEUTRAL;
				String equalText = leftList.get(xEnd - 1);
				System.out.print(" " + equalSign + equalText);

				xEnd = xEnd - 1;
				yEnd = yEnd - 1;

				addToComparisonList(equalSign, leftList, rightList, xEnd, yEnd);

			}

			if (D > 0) {
				if (currentSign != Line.SIGN_NEUTRAL) {
					System.out.print(" " + currentSign + text);
					addToComparisonList(currentSign, leftList, rightList, xStart, yStart);
				}
			}

			xEnd = xStart;
			yEnd = yStart;
		}

		equaliseNumberOfLines();
		fillComparisonLines();

		System.out.println();
	}

	/**
	 * 
	 */
	private void fillComparisonLines() {
		boolean isConflict = false;
		boolean isDifferent = false;

		for (int i = 0; i < leftLines.size(); i++) {

			Line leftLine = leftLines.get(i);
			Line rightLine = rightLines.get(i);

			if (leftLine.getSign() != rightLine.getSign()) {
				isDifferent = true;
				if (leftLine.getSign() != Line.SIGN_NEUTRAL && rightLine.getSign() != Line.SIGN_NEUTRAL) {
					isConflict = true;
				}
			}

			leftLine.setLineNumber(i + 1);
			rightLine.setLineNumber(i + 1);
			ComparisonLine comparisonLine = new ComparisonLine(i + 1, isDifferent, isConflict, leftLine, rightLine);
			comparisonLines.add(comparisonLine);
		}
	}

	/**
	 * @param currentSign
	 * @param prevSign
	 * @param eventString
	 * @param lineNumber
	 * @param leftSourceLineNumber
	 * @param rightSourceLineNumber
	 * @return
	 */
	private void addToComparisonList(char currentSign, List<String> leftText, List<String> rightText,
			int leftSourceLineNumber, int rightSourceLineNumber) {

		if (currentSign == Line.SIGN_DELETE) {
			Line leftLine = new Line(0, leftSourceLineNumber, currentSign, leftText.get(leftSourceLineNumber));
			leftLines.add(0, leftLine);
		} else if (currentSign == Line.SIGN_ADD) {
			Line rightLine = new Line(0, rightSourceLineNumber, currentSign, rightText.get(rightSourceLineNumber));
			rightLines.add(0, rightLine);
		} else if (currentSign == Line.SIGN_NEUTRAL) {
			if (leftLines.size() != rightLines.size()) {
				equaliseNumberOfLines();
			}
			Line leftLine = new Line(0, leftSourceLineNumber, currentSign, leftText.get(leftSourceLineNumber));
			leftLines.add(0, leftLine);
			Line rightLine = new Line(0, rightSourceLineNumber, currentSign, rightText.get(rightSourceLineNumber));
			rightLines.add(0, rightLine);
		}
	}

	/**
	 * 
	 */
	private void equaliseNumberOfLines() {
		List<Line> bottomLines;
		List<Line> topLines;
		if (leftLines.size() < rightLines.size()) {
			bottomLines = leftLines;
			topLines = rightLines;
		} else {
			bottomLines = rightLines;
			topLines = leftLines;
		}
		int min = bottomLines.size();
		int max = topLines.size();
		for (; min < max; min++) {
			Line line = new Line(0, -1, Line.SIGN_NEUTRAL, "");
			bottomLines.add(0, line);
		}
	}

	// /**
	// * @param currentSign
	// * @param prevSign
	// * @param text
	// * @param lineNumber
	// * @param leftSourceLineNumber
	// * @param rightSourceLineNumber
	// * @return
	// */
	// private int addToComparisonList(char currentSign, char prevSign, int
	// lineNumber, List<String> leftText,
	// List<String> rightText, int leftSourceLineNumber, int
	// rightSourceLineNumber) {
	//
	// if (prevSign == Line.SIGN_NEUTRAL && currentSign == Line.SIGN_NEUTRAL) {
	// Line leftLine = new Line(lineNumber + 1, leftSourceLineNumber,
	// currentSign,
	// leftText.get(leftSourceLineNumber));
	// Line rightLine = new Line(lineNumber + 1, rightSourceLineNumber,
	// currentSign,
	// rightText.get(rightSourceLineNumber));
	// ComparisonLine comparisonLine = new ComparisonLine(lineNumber + 1, false,
	// false, leftLine, rightLine);
	// comparisonLines.add(0, comparisonLine);
	// leftLines.add(0, leftLine);
	// rightLines.add(0, rightLine);
	// lineNumber += 1;
	// } else if ((prevSign == Line.SIGN_INITIAL || prevSign ==
	// Line.SIGN_NEUTRAL || prevSign == Line.SIGN_DELETE) && currentSign ==
	// Line.SIGN_DELETE) {
	// Line leftLine = new Line(0, leftSourceLineNumber, currentSign,
	// leftText.get(leftSourceLineNumber));
	// Line rightLine = new Line(0, -1, Line.SIGN_NEUTRAL, " ");
	// ComparisonLine comparisonLine = new ComparisonLine(0, false, false,
	// leftLine, rightLine);
	// comparisonLines.add(0, comparisonLine);
	// leftLines.add(0, leftLine);
	// rightLines.add(0, rightLine);
	// lineNumber += 1;
	// } else if ((prevSign == Line.SIGN_INITIAL || prevSign ==
	// Line.SIGN_NEUTRAL || prevSign == Line.SIGN_ADD) && currentSign ==
	// Line.SIGN_ADD) {
	// Line leftLine = new Line(0, -1, Line.SIGN_NEUTRAL, " ");
	// Line rightLine = new Line(0, rightSourceLineNumber, currentSign,
	// rightText.get(rightSourceLineNumber));
	// ComparisonLine comparisonLine = new ComparisonLine(0, false, false,
	// leftLine, rightLine);
	// comparisonLines.add(0, comparisonLine);
	// leftLines.add(0, leftLine);
	// rightLines.add(0, rightLine);
	// lineNumber += 1;
	// } else if (prevSign == Line.SIGN_ADD && currentSign == Line.SIGN_DELETE)
	// {
	// Line leftLine = new Line(0, leftSourceLineNumber, currentSign,
	// leftText.get(leftSourceLineNumber));
	// Line rightLine = new Line(0, rightSourceLineNumber, prevSign,
	// rightText.get(rightSourceLineNumber));
	// ComparisonLine comparisonLine = new ComparisonLine(0, false, false,
	// leftLine, rightLine);
	// comparisonLines.add(0, comparisonLine);
	// leftLines.add(0, leftLine);
	// rightLines.add(0, rightLine);
	// }
	// lineNumber += 1;
	// return lineNumber;
	// }

	public List<ComparisonLine> diff(List<String> leftList, List<String> rightList) {

		comparisonLines.clear();

		final int N = leftList.size();
		final int M = rightList.size();
		final int MAX = N + M;
		int[] V = new int[2 * (MAX + 1)];
		// offset is a middle index to shift the k to the middle of the array
		// since array index cannot be negative (less than 0)
		int offset = (2 * (MAX + 1)) / 2;
		V[1 + offset] = 0;
		int x = 0;
		int y = 0;
		int D = -1;

		List<int[]> VS = new ArrayList<>();

		for (D = 0; D <= MAX; D++) {
			VS.add(V.clone());
			for (int k = -D; k <= D; k += 2) {

				int lower = V[k - 1 + offset];
				int upper = V[k + 1 + offset];
				// lower < upper -> downward, else rightward
				if (k == -D || (k != D && lower < upper)) {
					x = V[k + 1 + offset];
				} else {
					x = V[k - 1 + offset] + 1;
				}
				y = x - k;

				while (x < N && y < M && leftList.get(x).equals(rightList.get(y))) {
					x = x + 1;
					y = y + 1;
				}

				V[k + offset] = x;

				if (x >= N && y >= M) {
					backtrack(leftList, rightList, VS);
					return comparisonLines;
				}
			}

		}
		return comparisonLines;
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

	private ChangeEvent<?> createEvent(String line, Resource mainResource, Resource otherResource)
			throws XMLStreamException {
		ChangeEvent<?> changeEvent = null;

		int eventNumber = 0;

		// ByteArrayInputStream headerStream = new ByteArrayInputStream(
		// "<?xml version='1.0' encoding='ISO-8859-1' ?>".getBytes());
		ByteArrayInputStream headerStream = new ByteArrayInputStream(new byte[0]);
		ByteArrayInputStream contentStream = new ByteArrayInputStream(line.getBytes());
		InputStream stream = new SequenceInputStream(headerStream, contentStream);

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream);

		ChangeEvent<?> event = null;
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
							event = new StartNewSessionEvent(sessionId, time);
						}
							break;
						case "register": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							event = new RegisterEPackageEvent(ePackage, null);
						}
							break;
						case "create": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new CreateEObjectEvent(eClass, (CBPResource) mainResource, id);
							EObject eObject = getEObject(id, mainResource, otherResource);
							event.setValues(eObject);
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
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new DeleteEObjectEvent(eClass, (CBPResource) mainResource, id);
							EObject eObject = getEObject(id, mainResource, otherResource);
							event.setValues(eObject);
						}
							break;
						}

						if (event instanceof EStructuralFeatureEvent<?>) {
							String sTarget = e.getAttributeByName(new QName("target")).getValue();
							String sName = e.getAttributeByName(new QName("name")).getValue();
							EObject target = getEObject(sTarget, mainResource, otherResource);
							if (target != null) {
								EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(sName);
								((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
								((EStructuralFeatureEvent<?>) event).setTarget(target);
							}
						} else if (event instanceof ResourceEvent) {
						}

						if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent
								|| event instanceof AddToResourceEvent) {
							String sPosition = e.getAttributeByName(new QName("position")).getValue();
							event.setPosition(Integer.parseInt(sPosition));
						}
						if (event instanceof FromPositionEvent) {
							String sTo = e.getAttributeByName(new QName("to")).getValue();
							String sFrom = e.getAttributeByName(new QName("from")).getValue();
							event.setPosition(Integer.parseInt(sTo));
							((FromPositionEvent) event).setFromPosition(Integer.parseInt(sFrom));
						}
					}

				} else if (name.equals("value")) {
					if (ignore == false) {
						if (event instanceof EObjectValuesEvent) {
							EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
							String seobject = e.getAttributeByName(new QName("eobject")).getValue();
							EObject eob = resolveXRef(seobject, mainResource, otherResource);
							valuesEvent.getValues().add(eob);
						} else if (event instanceof EAttributeEvent) {
							EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
							String sliteral = e.getAttributeByName(new QName("literal")).getValue();

							if (eAttributeEvent.getEStructuralFeature() != null) {
								EDataType eDataType = ((EDataType) eAttributeEvent.getEStructuralFeature().getEType());
								Object value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,
										sliteral);
								eAttributeEvent.getValues().add(value);
							}
						}
					}
				}
			}
			if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
				EndElement ee = xmlEvent.asEndElement();
				String name = ee.getName().getLocalPart();
				if (event != null && !name.equals("value") && !name.equals("m")) {
					if (ignore == false) {

						changeEvent = event;

					} else {
						ignore = false;
					}
					eventNumber += 1;
				}
			}
		}
		xmlReader.close();

		return changeEvent;
	}

	private EObject resolveXRef(final String sEObjectURI, Resource mainResource, Resource otherResource) {
		EObject eob = getEObject(sEObjectURI, mainResource, otherResource);
		if (eob == null) {
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

	private EObject getEObject(String uriFragment, Resource mainResource, Resource otherResource) {
		EObject eObject = null;
		EObject mainEObject = mainResource.getEObject(uriFragment);
		EObject otherEObject = otherResource.getEObject(uriFragment);

		if (mainEObject != null) {
			eObject = mainEObject;
		} else if (otherEObject != null) {
			eObject = otherEObject;
		}
		return eObject;
	}
}
