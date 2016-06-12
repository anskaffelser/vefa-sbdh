package no.difi.vefa.sbdh.util;

import com.google.common.io.ByteStreams;
import org.testng.annotations.Test;

import java.io.IOException;

public class InputStreamAsAsicXmlWrapperTest {

    @Test
    public void simple() throws IOException {
        ByteStreams.copy(new InputStreamAsAsicXmlWrapper(getClass().getResourceAsStream("/sample-asic.asice")), System.out);
    }
}
