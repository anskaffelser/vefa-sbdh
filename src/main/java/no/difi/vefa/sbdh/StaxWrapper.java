package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.ContentWithManifest;
import no.difi.vefa.sbdh.lang.SbdhException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;

public class StaxWrapper extends StaxContext {

    public static void wrap(Header header, InputStream inputStream, OutputStream outputStream) throws SbdhException {
        wrap(HeaderHelper.toSbdh(header), inputStream, outputStream);
    }

    public static void wrap(StandardBusinessDocumentHeader sbdh, InputStream inputStream, OutputStream outputStream) throws SbdhException {
        try {
            XMLStreamWriter target = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");

            // Initiate SBDH
            target.writeStartDocument("UTF-8", "1.0");
            target.writeStartElement("", "StandardBusinessDocument", NS_SBDH);
            target.writeNamespace("", NS_SBDH);

            // Add manifest
            if (inputStream instanceof ContentWithManifest)
                sbdh.setManifest(((ContentWithManifest) inputStream).getManifest());

            // Write header
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(objectFactory.createStandardBusinessDocumentHeader(sbdh), target);

            // Copy XML into stream.
            XMLStreamReader source = xmlInputFactory.createXMLStreamReader(inputStream);
            do {
                switch (source.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        target.writeStartElement(source.getPrefix(), source.getLocalName(), source.getNamespaceURI());

                        for (int i = 0; i < source.getNamespaceCount(); i++)
                            target.writeNamespace(source.getNamespacePrefix(i), source.getNamespaceURI(i));
                        for (int i = 0; i < source.getAttributeCount(); i++)
                            target.writeAttribute(source.getAttributeLocalName(i), source.getAttributeValue(i));
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        target.writeEndElement();
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        target.writeCharacters(source.getText());
                        break;

                    case XMLStreamConstants.CDATA:
                        target.writeCData(source.getText());
                        break;
                }

                target.flush();
            } while (source.hasNext() && source.next() > 0);
            source.close();

            // Finalize SBDH
            target.writeEndElement();
            target.writeEndDocument();

            target.close();
        } catch (XMLStreamException | JAXBException e) {
            throw new SbdhException(e.getMessage(), e);
        }
    }

    StaxWrapper() {
        // No action.
    }
}
