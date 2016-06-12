package no.difi.vefa.sbdh.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;

public class StaxCharacterWriter extends Writer {

    private XMLStreamWriter writer;

    public StaxCharacterWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        try {
            writer.writeCharacters(cbuf, off, len);
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void flush() throws IOException {
        try {
            writer.flush();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        // No action
    }
}
