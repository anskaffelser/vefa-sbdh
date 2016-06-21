package no.difi.vefa.sbdh.wrapper;

import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

public class XmlToAsicWrapper extends FilterOutputStream {

    private static final String ASIC_IDENT = "urn:etsi.org:specification:02918:v1.2.1::asic";

    private static SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

    private PipedOutputStream pipedOutputStream = new PipedOutputStream();
    private PipedInputStream pipedInputStream;

    public XmlToAsicWrapper(OutputStream outputStream) throws EnvelopeException {
        super(new Base64OutputStream(outputStream, false));

        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        pipedInputStream = new PipedInputStream(pipedOutputStream);

                        saxParserFactory.setNamespaceAware(true);
                        SAXParser saxParser = saxParserFactory.newSAXParser();
                        saxParser.parse(pipedInputStream, new LocalDefaultHandler());
                    } catch (InterruptedIOException e) {
                        // No action.
                    } catch (SAXException | IOException | ParserConfigurationException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }.start();
        } catch (RuntimeException e) {
            throw new EnvelopeException(e.getCause().getMessage(), e.getCause());
        }
    }

    @Override
    public void write(int b) throws IOException {
        pipedOutputStream.write(b);
    }

    @Override
    public void flush() throws IOException {
        pipedOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        pipedOutputStream.close();
    }

    private class LocalDefaultHandler extends DefaultHandler {

        private boolean inside = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            inside = ASIC_IDENT.equals(String.format("%s::%s", uri, localName));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            inside = !ASIC_IDENT.equals(String.format("%s::%s", uri, localName));
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inside) {
                try {
                    out.write(new String(ch, start, length).getBytes());
                } catch (IOException e) {
                    throw new SAXException(e.getMessage(), e);
                }
            }
        }
    }
}
