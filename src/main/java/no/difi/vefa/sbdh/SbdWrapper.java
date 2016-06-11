package no.difi.vefa.sbdh;

import org.apache.commons.codec.binary.Base64;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Wraps supplied ASiC archive InputStream in Base64 encoding, inside a <code>StandardBusinessDocument</code>.
 * The payload is wrapped in an &lt;asic:asic&gt; element.
 * 
 * Created by soc on 16.09.2015.
 */
public class SbdWrapper extends SbdhContext {

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
     * Basically the <code>flush()</code> method on <code>Base64OutputStream</code> is broken.
     *
     * @param inputStream holding the binary contents of the ASiC archive
     * @param outputStream into which the base64 encode data will be written.
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
                    // Default behavious for all blocks except the last one.
                    byte[] bytes = b64.encodeBase64(inputbuffer, false); // No padding thank you
                    outputStream.write(bytes);
                    outputStream.write("\r\n".getBytes());
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("Unable to copy input stream into Base64 outputstream: " + e.getMessage(), e);
        }
    }

     void emitStartAsic(OutputStream out) throws IOException {
        out.write("\n<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">\n".getBytes());
        out.flush();
    }

     void emitSbdh(StandardBusinessDocumentHeader sbdh, OutputStream out) {
        try {
            JAXBElement<StandardBusinessDocumentHeader> standardBusinessDocumentHeader = objectFactory.createStandardBusinessDocumentHeader(sbdh);

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true); // Prevents output of XML preamble
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(standardBusinessDocumentHeader, out);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to convert SBDH into plain XML:" + e.getMessage(), e);
        }
    }

    void emitStart(OutputStream out) throws IOException {
        out.write("<?xml version=\"1.0\"?>\n <StandardBusinessDocument>\n".getBytes());
    }
}
