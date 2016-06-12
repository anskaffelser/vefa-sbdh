package no.difi.vefa.sbdh;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import no.difi.vefa.sbdh.lang.SbdhException;
import no.difi.vefa.sbdh.util.StaxCharacterWriter;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaxWrapper extends SbdhContext {

    protected static final String NS_SBDH = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";
    protected static final String NS_ASIC = "urn:etsi.org:specification:02918:v1.2.1";

    protected static BaseEncoding encoding = BaseEncoding.base64();

    // protected static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    protected static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

    public static void wrap(Header header, InputStream inputStream, OutputStream outputStream) throws SbdhException{
        wrap(HeaderHelper.toSbdh(header), inputStream, outputStream);
    }

    public static void wrap(StandardBusinessDocumentHeader sbdh, InputStream inputStream, OutputStream outputStream) throws SbdhException{
        try {
            XMLStreamWriter target = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");

            // Initiate SBDH
            target.writeStartDocument("UTF-8", "1.0");
            target.writeStartElement("", "StandardBusinessDocument", NS_SBDH);
            target.writeNamespace("", NS_SBDH);

            // Write header
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(objectFactory.createStandardBusinessDocumentHeader(sbdh), target);

            // Initiate ASIC
            target.writeStartElement("asic", "asic", NS_ASIC);
            target.writeNamespace("asic", NS_ASIC);
            target.writeAttribute("id", "asic");

            // Write ASiC as base64 content.
            OutputStream asicStream = encoding.encodingStream(new StaxCharacterWriter(target));
            ByteStreams.copy(inputStream, asicStream);
            asicStream.close();

            // Finalize ASIC
            target.writeEndElement();

            // Finalize SBDH
            target.writeEndElement();
            target.writeEndDocument();

            target.close();
        } catch (XMLStreamException | JAXBException | IOException e) {
            throw new SbdhException(e.getMessage(), e);
        }
    }
}
