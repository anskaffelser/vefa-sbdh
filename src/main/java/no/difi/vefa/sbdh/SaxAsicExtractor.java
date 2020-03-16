package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.AsicExtractor;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Extracts the ASiC base64 encoded payload from within a &lt;StandardBusinessDocument&gt; using SAX parser
 * and streaming the code.
 *
 * @author steinar
 *         Date: 22.09.15
 *         Time: 08.26
 */
class SaxAsicExtractor implements AsicExtractor {


    /**
     * Whether the data should be extracted and decoded. If set to false, only extraction will
     * be performed
     */
    boolean decodeFromBase64 = true;

    private String startElementName;

    public SaxAsicExtractor(String startElementName) {
        this.startElementName = startElementName;
    }

    @Override
    public boolean isDecodeFromBase64() {
        return decodeFromBase64;
    }

    @Override
    public void setDecodeFromBase64(boolean decodeFromBase64) {
        this.decodeFromBase64 = decodeFromBase64;
    }

    @Override
    public void extractAsic(InputStream sbdInputStream, OutputStream outputStream) {


        // Decodes rather than encodes the ouput data
        OutputStream decodingOutputStream = createDecodingOutputStream(outputStream);

        AsicHandler asicHandler = new AsicHandler(startElementName, decodingOutputStream);

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();

            // Performs the actual extraction
            saxParser.parse(sbdInputStream, asicHandler);

            // Ensures that we flush the wrapping outputstream or we will be missing the last part
            decodingOutputStream.flush();

        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("Unable to create parser, reason: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse input data, reason: " + e.getMessage(), e);
        }
    }

    private OutputStream createDecodingOutputStream(OutputStream outputStream) {
        OutputStream result;

        if (decodeFromBase64) {
            result = new Base64OutputStream(outputStream, false);
        } else {
            result = outputStream;
        }

        return result;
    }


    /**
     * SAX implementation, which will extract the base64 encoded ASiC archive and write the contents
     * to the supplied OutputStream.
     */
    static class AsicHandler extends DefaultHandler {

        boolean currentElementIsAsicPayload = false;

        private String startElementName;

        private OutputStream outputStreamWriter;

        AsicHandler(String startElementName, OutputStream outputStreamWriter) {
            this.startElementName = startElementName;
            this.outputStreamWriter = outputStreamWriter;
        }

        @Override
        public void startElement(String uri, String localName, String qname, Attributes attributes) {
            if (localName == null || localName.equals("")) {
                throw new IllegalStateException("Seems your XML input is invalid, namespace declaration is required in input");
            }

            // Found the <asic:asic> element, start handling the characters
            String mimeType = attributes.getValue("mimeType");
            if (localName.equalsIgnoreCase(startElementName)) {
                currentElementIsAsicPayload = mimeType == null || mimeType.equalsIgnoreCase("application/vnd.etsi.asic-3+zip");
            }
        }

        /**
         * All character data between &lt;asic:asic&gt; and &lt;/asic:asic&gt; are written out as is.
         * This method is invoked multiple times as per the SAX specification.
         */
        @Override
        public void characters(char ch[], int start, int length) {

            if (currentElementIsAsicPayload) {

                try {
                    String s = new String(ch, start, length);
                    outputStreamWriter.write(s.getBytes());
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to write contents from xml character data into output stream: " + e.getMessage(), e);
                }
            }

        }

        @Override
        public void endElement(String uri, String localName, String qname) {
            if (localName.equalsIgnoreCase(startElementName)) {
                currentElementIsAsicPayload = false;
                // We are done, never mind terminating the parsing, only two more elements left
            }
        }

    }
}
