package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.RawEnvelopeExtractor;
import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class RawSbdhExtractor extends RawSbdhContext implements RawEnvelopeExtractor<StandardBusinessDocumentHeader> {

    private XMLStreamReader source;

    private String encoding, version;

    public RawSbdhExtractor(InputStream inputStream) throws EnvelopeException {
        try {
            source = xmlInputFactory.createXMLStreamReader(inputStream);

            if (source.getEventType() != XMLStreamConstants.START_DOCUMENT)
                throw new EnvelopeException("Unable to find XML document start.");

            encoding = source.getEncoding();
            version = source.getVersion();

            nextElementStart();
            if (!source.getLocalName().equals("StandardBusinessDocument"))
                throw new EnvelopeException("Unable to find element 'StandardBusinessDocument'.");

            source.next();
            nextElementStart();
        } catch (XMLStreamException e) {
            throw new EnvelopeException("Unable to initiate XMLStreamReader.", e);
        }
    }

    @Override
    public StandardBusinessDocumentHeader readHeader() throws EnvelopeException {
        if (!source.getLocalName().equals("StandardBusinessDocumentHeader"))
            throw new EnvelopeException("Header not found.");

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StandardBusinessDocumentHeader header = unmarshaller.unmarshal(source, StandardBusinessDocumentHeader.class).getValue();

            nextElementStart();

            return header;
        } catch (JAXBException | XMLStreamException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public void readPayload(OutputStream outputStream) throws EnvelopeException {
        try {
            XMLStreamWriter target = xmlOutputFactory.createXMLStreamWriter(outputStream, source.getEncoding());
            target.writeStartDocument(encoding, version);

            int level = 0;

            do {
                switch (source.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:
                        level++;

                        target.writeStartElement(source.getPrefix(), source.getLocalName(), source.getNamespaceURI());

                        for (int i = 0; i < source.getNamespaceCount(); i++)
                            target.writeNamespace(source.getNamespacePrefix(i), source.getNamespaceURI(i));
                        for (int i = 0; i < source.getAttributeCount(); i++)
                            target.writeAttribute(source.getAttributeLocalName(i), source.getAttributeValue(i));
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        level--;

                        target.writeEndElement();
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        target.writeCharacters(source.getText());
                        break;

                    case XMLStreamConstants.CDATA:
                        target.writeCData(source.getText());
                        break;
                }

                if (level == 0)
                    break;
            } while (source.hasNext() && source.next() > 0);

            target.writeEndDocument();
            target.flush();
            target.close();
        } catch (XMLStreamException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            source.close();
        } catch (XMLStreamException e) {
            throw new IOException("Unable to close XMLStreamReader.", e);
        }
    }

    private void nextElementStart() throws XMLStreamException {
        while (source.getEventType() != XMLStreamConstants.START_ELEMENT)
            source.next();
    }
}
