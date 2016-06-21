package no.difi.vefa.sbdh.wrapper;

import com.google.common.io.ByteStreams;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

public class AsicToXmlWrapperTest {

    @Test
    public void simple() throws IOException {
        try (InputStream inputStream = new AsicToXmlWrapper(getClass().getResourceAsStream("/sample-asic.asice"))) {
            ByteStreams.copy(inputStream, System.out);
        }
    }
}
