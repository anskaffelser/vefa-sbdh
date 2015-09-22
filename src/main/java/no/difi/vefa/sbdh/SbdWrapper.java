package no.difi.vefa.sbdh;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ObjectFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;

/**
 * Wraps supplied InputStream in Base64 encoding, inside a <code>StandardBusinessDocument</code>.
 * <p/>
 * Created by soc on 16.09.2015.
 */
public class SbdWrapper {


    private ObjectFactory objectFactory;
    private final Marshaller marshaller;

    public SbdWrapper() {
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(StandardBusinessDocumentHeader.class);
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true); // Prevents output of XML preamble
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            throw new IllegalStateException("Internal error: " + e.getMessage(), e);
        }
        objectFactory = new ObjectFactory();

    }

    public void wrapInputStream(StandardBusinessDocumentHeader sbdh, InputStream inputStream, OutputStream outputStream) {

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to create PrintWriter: " + e.getMessage(), e);
        }

        // Emits the <StandardBusinessDocument> element
        emitStart(out);

        emitSbdh(sbdh, out);    // <StandardBusinessDocumentHeader>

        emitStartAsic(out);


        try {
            // Base64 contents goes here:
            Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream);
            IOUtils.copy(inputStream, base64OutputStream);
            base64OutputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to copy input stream into Base64 outputstream: " + e.getMessage(), e);
        }

        out.println("\n</asic:asic>");
        out.println("</StandardBusinessDocument>");

        out.flush();
        out.close();
    }

    private void emitStartAsic(PrintWriter out) {
        out.println("\n<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">");
        out.flush();
    }

    private void emitSbdh(StandardBusinessDocumentHeader sbdh, PrintWriter out) {
        try {
            JAXBElement<StandardBusinessDocumentHeader> standardBusinessDocumentHeader = objectFactory.createStandardBusinessDocumentHeader(sbdh);
            marshaller.marshal(standardBusinessDocumentHeader, out);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to convert SBDH into plain XML:" + e.getMessage(), e);
        }
    }

    private void emitStart(PrintWriter out) {
        out.println("<?xml version=\"1.0\"?>\n" +
                "<StandardBusinessDocument>");
    }
}
