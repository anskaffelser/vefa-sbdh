package no.difi.vefa.sbdh.util;

import com.google.common.io.ByteStreams;
import org.testng.annotations.Test;

import java.io.IOException;

public class AsicAsXmlInputStreamTest {

    @Test
    public void simple() throws IOException {
        ByteStreams.copy(new AsicAsXmlInputStream(getClass().getResourceAsStream("/sample-asic.asice")), System.out);
    }
}
