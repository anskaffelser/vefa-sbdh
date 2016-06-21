package no.difi.vefa.sbdh.wrapper;

import com.google.common.io.ByteStreams;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class XmlToAsicWrapperTest {

    @Test
    public void simple() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream outputStream = new XmlToAsicWrapper(baos)) {
            ByteStreams.copy(getClass().getResourceAsStream("/sample-asic.xml"), outputStream);
        }

        try (OutputStream outputStream = new FileOutputStream("output-asic.zip")) {
            ByteStreams.copy(new ByteArrayInputStream(baos.toByteArray()), outputStream);
        }

        Assert.assertEquals(baos.toByteArray().length, 6334);
    }

    @Test
    public void insideSbdh() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream outputStream = new XmlToAsicWrapper(baos)) {
            ByteStreams.copy(getClass().getResourceAsStream("/sample-sbd.xml"), outputStream);
        }

        try (OutputStream outputStream = new FileOutputStream("output-sbdh.zip")) {
            ByteStreams.copy(new ByteArrayInputStream(baos.toByteArray()), outputStream);
        }

        Assert.assertEquals(baos.toByteArray().length, 6334);
    }
}
