package org.eclipse.epsilon.cbp.comparison;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.epsilon.cbp.event.CancelEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CBPComparisonUtil {

	public static String getEventString(ChangeEvent<?> changeEvent)
			throws ParserConfigurationException, TransformerException {
		String eventString = "";
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		Document document = documentBuilder.newDocument();
		Element e = null;

		if (changeEvent instanceof StartNewSessionEvent) {
			StartNewSessionEvent s = (StartNewSessionEvent) changeEvent;
			e = document.createElement("session");
			e.setAttribute("id", s.getSessionId());
			e.setAttribute("time", s.getTime());
		}
		if (changeEvent instanceof CancelEvent) {
			CancelEvent c = ((CancelEvent) changeEvent);
			e = document.createElement("cancel");
			e.setAttribute("offset", String.valueOf(c.getLineToCancelOffset()));
		}
		if (e != null)
			document.appendChild(e);

		DOMSource source = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		eventString = writer.toString();

		return eventString;
	}
}
