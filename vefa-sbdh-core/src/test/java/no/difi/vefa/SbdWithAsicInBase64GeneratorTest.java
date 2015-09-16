package no.difi.vefa;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ObjectFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import static org.testng.Assert.*;

/**
 * @author steinar
 *         Date: 11.09.15
 *         Time: 11.55
 */
public class SbdWithAsicInBase64GeneratorTest {

    @Test(dataProvider = "sampleAsic", dataProviderClass = no.difi.vefa.SampleAsicProvider.class)
    public void testExtractSbdh(InputStream inputStream) throws Exception {
        SbdWithAsicInBase64Generator generator = new SbdWithAsicInBase64Generator();
        StandardBusinessDocumentHeader sbdh = generator.extractSbdh(new ZipInputStream(inputStream));
        assertNotNull(sbdh, "SBDH not found in ASiC archive");
    }

    @Test(dataProvider = "sampleAsic", dataProviderClass = no.difi.vefa.SampleAsicProvider.class)
    public void marshalToXml(InputStream inputStream) throws Exception {

        assertTrue(inputStream.markSupported());
        inputStream.mark(Integer.MAX_VALUE);

        SbdWithAsicInBase64Generator generator = new SbdWithAsicInBase64Generator();
        StandardBusinessDocumentHeader sbdh = generator.extractSbdh(new ZipInputStream(inputStream));

        inputStream.reset();

        OutputStream out = System.out;


        JAXBContext jc = JAXBContext.newInstance(StandardBusinessDocumentHeader.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        JAXBElement<StandardBusinessDocumentHeader> standardBusinessDocumentHeader = new ObjectFactory().createStandardBusinessDocumentHeader(sbdh);

        System.out.println("<?xml version=\"1.0\"?>\n" +
                "<StandardBusinessDocument xmlns:sbd=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\">");

        marshaller.marshal(standardBusinessDocumentHeader, out);

        System.out.println("<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">");

        // Base64 contents goes here:
        Base64OutputStream base64OutputStream = new Base64OutputStream(out);

        IOUtils.copy(inputStream, base64OutputStream);
        base64OutputStream.flush();
        System.out.println("</asic:asic>");
        System.out.println("</StandardBusinessDocument>");
    }
}