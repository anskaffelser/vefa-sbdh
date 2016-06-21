package no.difi.vefa.sbdh.wrapper;

import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.apache.commons.codec.binary.Base64OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

public class XmlToAsicWrapper extends FilterOutputStream implements Runnable {

    private static final String ASIC_IDENT = "urn:etsi.org:specification:02918:v1.2.1::asic";

    private static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

    private PipedOutputStream pipedOutputStream = new PipedOutputStream();
    private PipedInputStream pipedInputStream;

    private XMLStreamReader source;
    private boolean inside = false;

    public XmlToAsicWrapper(OutputStream outputStream) throws EnvelopeException {
        super(new Base64OutputStream(outputStream, false));

        try {
            pipedInputStream = new PipedInputStream(pipedOutputStream);
            new Thread(this).start();
        } catch (IOException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        boolean run = true;
        try {
            source = xmlInputFactory.createXMLStreamReader(pipedInputStream);

            do {
                switch (source.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        inside = ASIC_IDENT.equals(String.format("%s::%s", source.getNamespaceURI(), source.getLocalName()));
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        if (inside) {
                            inside = !ASIC_IDENT.equals(String.format("%s::%s", source.getNamespaceURI(), source.getLocalName()));
                            run = false;
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (inside) {
                            out.write(source.getText().getBytes());
                            // System.out.println(source.getText().replaceAll("[ \r\n]", ""));
                        }
                        break;
                }

            } while (run && source.hasNext() && source.next() > 0);

            // Finish stream.
            while (source.hasNext())
                source.next();
        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
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
        try {
            pipedOutputStream.close();
            source.close();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
