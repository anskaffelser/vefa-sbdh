package no.difi.vefa.sbdh;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.*;
import java.io.InputStream;
import java.io.OutputStream;

public class StaxExtractor extends SbdhContext {

    private static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

    public static StandardBusinessDocumentHeader extract(InputStream inputStream, OutputStream outputStream) {
        try {
            XMLStreamReader source = xmlInputFactory.createXMLStreamReader(inputStream);
            XMLStreamWriter target = xmlOutputFactory.createXMLStreamWriter(outputStream, source.getEncoding());

            boolean payload = false;
            StandardBusinessDocumentHeader header = null;

            do {
                switch (source.getEventType()) {
                    case XMLStreamReader.START_DOCUMENT:
                        target.writeStartDocument(source.getEncoding(), source.getVersion());
                        break;

                    case XMLStreamConstants.END_DOCUMENT:
                        target.writeEndDocument();
                        break;

                    case XMLStreamConstants.START_ELEMENT:
                        payload = !source.getNamespaceURI().equals(NS_SBDH);

                        if (payload) {
                            target.writeStartElement(source.getPrefix(), source.getLocalName(), source.getNamespaceURI());

                            for (int i = 0; i < source.getAttributeCount(); i++)
                                target.writeAttribute(source.getAttributeLocalName(i), source.getAttributeValue(i));
                            for (int i = 0; i < source.getNamespaceCount(); i++)
                                target.writeNamespace(source.getNamespacePrefix(i), source.getNamespaceURI(i));
                        } else {
                            if (source.getLocalName().equals("StandardBusinessDocumentHeader")) {
                                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                                header = unmarshaller.unmarshal(source, StandardBusinessDocumentHeader.class).getValue();
                            }
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        payload = !source.getNamespaceURI().equals(NS_SBDH);

                        if (payload) {
                            target.writeEndElement();
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (payload) {
                            target.writeCharacters(source.getText());
                        }
                        break;

                    case XMLStreamConstants.CDATA:
                        if (payload) {
                            target.writeCData(source.getText());
                        }
                        break;
                }

                target.flush();
            } while (source.hasNext() && source.next() > 0);

            return header;
        } catch (XMLStreamException | JAXBException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
