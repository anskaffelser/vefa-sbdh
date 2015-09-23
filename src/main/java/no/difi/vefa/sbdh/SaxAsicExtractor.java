package no.difi.vefa.sbdh;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Extracts the ASiC base64 encoded payload from within a &lt;StandardBusinessDocument&gt; using SAX parser
 * and streaming the code.
 *
 * @author steinar
 *         Date: 22.09.15
 *         Time: 08.26
 */
class SaxAsicExtractor implements AsicExtractor {


    public static final Logger log = LoggerFactory.getLogger(SaxAsicExtractor.class);

    @Override
    public void extractAsic(InputStream sbdInputStream, OutputStream outputStream) {

        // Decodes rather than encodes the ouput data
        Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream, false);

        AsicHandler asicHandler = new AsicHandler(new OutputStreamWriter(base64OutputStream));

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(sbdInputStream, asicHandler);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("Unable to create parser, reason: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse input data, reason: " + e.getMessage(), e);
        }
    }

    /** SAX implementation, which will extract the base64 encoded ASiC archive and write the contents
     * to the supplied OutputStream.
     */
    protected static class AsicHandler extends DefaultHandler {

        boolean currentElementIsAsicPayload = false;

        OutputStreamWriter outputStreamWriter;

        public AsicHandler(OutputStreamWriter outputStreamWriter) {
            this.outputStreamWriter = outputStreamWriter;
        }

        @Override
        public void startElement(String uri, String localName, String qname, Attributes attributes) {
            if (localName == null || localName.equals("")) {
                throw new IllegalStateException("Seems your XML input is invalid, namespace declaration is required in input");
            }

            // Found the <asic:asic> element, start handling the characters
            if (localName.equals("asic")) {
                currentElementIsAsicPayload = true;
            }
        }

        /** All character data between &lt;asic:asic&gt; and &lt;/asic:asic&gt; are written out as is.
         * This method is invoked multiple times as per the SAX specification.
         */
        @Override
        public void characters(char ch[], int start, int length) {

            if (currentElementIsAsicPayload) {
                try {
                    outputStreamWriter.write(ch, start, length);
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to write contents from xml character data into output stream: " + e.getMessage(), e);
                }
            }

        }

        @Override
        public void endElement(String uri, String localName, String qname) {
            if (localName.equals("asic")) {
                currentElementIsAsicPayload = false;
                // We are done, never mind terminating the parsing, only two more elements left
            }
        }

    }
}
