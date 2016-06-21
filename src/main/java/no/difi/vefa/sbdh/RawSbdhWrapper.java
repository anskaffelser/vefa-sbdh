package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.RawEnvelopeWrapper;
import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class RawSbdhWrapper extends RawSbdhContext implements RawEnvelopeWrapper<StandardBusinessDocumentHeader> {

    private XMLStreamWriter target;

    public RawSbdhWrapper(OutputStream outputStream) throws EnvelopeException {
        try {
            this.target = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");

            // Initiate SBDH
            target.writeStartDocument("UTF-8", "1.0");
            target.writeStartElement("", "StandardBusinessDocument", NS_SBDH);
            target.writeNamespace("", NS_SBDH);
        } catch (XMLStreamException e) {
            throw new EnvelopeException("Unable to initiate XMLStreamWriter.", e);
        }
    }

    @Override
    public void writeHeader(StandardBusinessDocumentHeader header) throws EnvelopeException {
        try {
            // Write header
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(objectFactory.createStandardBusinessDocumentHeader(header), target);
        } catch (JAXBException e) {
            throw new EnvelopeException("Unable to write header.", e);
        }
    }

    @Override
    public void writePayload(InputStream inputStream) throws EnvelopeException {
        try {
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

        } catch (XMLStreamException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            // Finalize SBDH
            target.writeEndElement();
            target.writeEndDocument();

            target.close();
        } catch (XMLStreamException e) {
            throw new IOException("Unable to close XMLStreamWriter.", e);
        }
    }
}
