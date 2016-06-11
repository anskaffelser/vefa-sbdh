package no.difi.vefa.sbdh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.*;
import java.io.InputStream;
import java.io.OutputStream;

public class StaxPayloadExtractor {

    private static Logger logger = LoggerFactory.getLogger(StaxPayloadExtractor.class);

    private static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

    public static void extract(InputStream inputStream, OutputStream outputStream) {
        try {
            XMLStreamReader source = xmlInputFactory.createXMLStreamReader(inputStream);
            XMLStreamWriter target = xmlOutputFactory.createXMLStreamWriter(outputStream, source.getEncoding());

            boolean payload = false;

            do {
                switch (source.getEventType()) {
                    case XMLStreamReader.START_DOCUMENT:
                        logger.debug("START_DOCUMENT");
                        target.writeStartDocument(source.getEncoding(), source.getVersion());
                        break;

                    case XMLStreamConstants.END_DOCUMENT:
                        logger.debug("END_DOCUMENT");
                        target.writeEndDocument();
                        break;

                    case XMLStreamConstants.START_ELEMENT:
                        payload = !source.getNamespaceURI().equals("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader");

                        if (payload) {
                            logger.debug("START_ELEMENT");
                            target.writeStartElement(source.getPrefix(), source.getLocalName(), source.getNamespaceURI());

                            for (int i = 0; i < source.getAttributeCount(); i++)
                                target.writeAttribute(source.getAttributeLocalName(i), source.getAttributeValue(i));
                            for (int i = 0; i < source.getNamespaceCount(); i++)
                                target.writeNamespace(source.getNamespacePrefix(i), source.getNamespaceURI(i));
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        payload = !source.getNamespaceURI().equals("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader");

                        if (payload) {
                            logger.debug("END_ELEMENT");
                            target.writeEndElement();
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (payload) {
                            logger.debug("CHARACTERS");
                            target.writeCharacters(source.getText());
                        }
                        break;

                    case XMLStreamConstants.CDATA:
                        if (payload) {
                            logger.debug("CDATA");
                            target.writeCData(source.getText());
                        }
                        break;
                }

                target.flush();
            } while (source.hasNext() && source.next() > 0);
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
