package no.difi.vefa.sbdh.util;

import org.apache.commons.codec.binary.Base64InputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;

public class InputStreamAsAsicXmlWrapper extends InputStream {

    private final static byte[] preContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">\n".getBytes();
    private final static byte[] postContent = "</asic:asic>".getBytes();

    private InputStream inputStream;

    public InputStreamAsAsicXmlWrapper(InputStream inputStream) {
        this.inputStream = new SequenceInputStream(Collections.enumeration(Arrays.asList(
                new ByteArrayInputStream(preContent),
                new Base64InputStream(inputStream, true, 75, "\n".getBytes()),
                new ByteArrayInputStream(postContent)
        )));
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}
