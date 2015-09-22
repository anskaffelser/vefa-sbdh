package no.difi.vefa.sbdh;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ObjectFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.Arrays;

/**
 * Wraps supplied InputStream in Base64 encoding, inside a <code>StandardBusinessDocument</code>.
 * 
 * Created by soc on 16.09.2015.
 */
public class SbdWrapper {


    private ObjectFactory objectFactory;
    private final Marshaller marshaller;

    public SbdWrapper() {
        JAXBContext jc;
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


        // Emits the <StandardBusinessDocument> element
        try {
            emitStart(outputStream);
            emitSbdh(sbdh, outputStream);    // <StandardBusinessDocumentHeader>

            emitStartAsic(outputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to emit data.", e);
        }

        emitBase64EncodedData(inputStream, outputStream);

        try {
            outputStream.write("\n</asic:asic>\n".getBytes());
            outputStream.write("</StandardBusinessDocument>\n".getBytes());
            outputStream.close();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write outputdata, reason:" + e.getMessage(), e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to close outputstream");
            }
        }
    }

    /**
     * There is a bug in Base64OutputStream forcing us to hand write this stuff.
     *
     * @param inputStream
     * @param outputStream
     */
    void emitBase64EncodedData(InputStream inputStream, OutputStream outputStream) {
        try {
            // Base64 contents goes here:
            int bufsize = 57; // multiple of 3!
            byte inputbuffer[] = new byte[bufsize];
            int bytesRead;

            Base64 b64 = new Base64();

            while ((bytesRead = inputStream.read(inputbuffer)) > -1) {
                byte[] b;

                // If number of bytes is less than bufsize, we have read the last block of data
                if (bytesRead < bufsize) {
                    b = Arrays.copyOf(inputbuffer, bytesRead);
                    byte[] bytes = b64.encodeBase64(b, true);    // Last bytes must be padded
                    outputStream.write(bytes);

                } else {
                    byte[] bytes = b64.encodeBase64(inputbuffer, false); // No padding thank you
                    outputStream.write(bytes);
                    outputStream.write("\r\n".getBytes());
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("Unable to copy input stream into Base64 outputstream: " + e.getMessage(), e);
        }
    }

    private void emitStartAsic(OutputStream out) throws IOException {
        out.write("\n<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">\n".getBytes());
        out.flush();
    }

    private void emitSbdh(StandardBusinessDocumentHeader sbdh, OutputStream out) {
        try {
            JAXBElement<StandardBusinessDocumentHeader> standardBusinessDocumentHeader = objectFactory.createStandardBusinessDocumentHeader(sbdh);
            marshaller.marshal(standardBusinessDocumentHeader, out);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to convert SBDH into plain XML:" + e.getMessage(), e);
        }
    }

    private void emitStart(OutputStream out) throws IOException {
        out.write("<?xml version=\"1.0\"?>\n <StandardBusinessDocument>\n".getBytes());
    }
}
