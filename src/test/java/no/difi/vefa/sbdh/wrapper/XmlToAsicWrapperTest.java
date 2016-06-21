package no.difi.vefa.sbdh.wrapper;

import com.google.common.io.ByteStreams;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class XmlToAsicWrapperTest {

    @Test
    public void simple() throws Exception {
        try (OutputStream outputStream = new XmlToAsicWrapper(new FileOutputStream("output-asic.zip"))) {
            ByteStreams.copy(getClass().getResourceAsStream("/sample-asic.xml"), outputStream);
        }
    }

    @Test
    public void insideSbdh() throws Exception {
        try (OutputStream outputStream = new XmlToAsicWrapper(new FileOutputStream("output-sbd.zip"))) {
            ByteStreams.copy(getClass().getResourceAsStream("/sample-sbd.xml"), outputStream);
        }
    }
}
