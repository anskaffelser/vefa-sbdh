package no.difi.vefa.sbdh;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ObjectFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.URL;

import static org.testng.Assert.*;

/**
 * Created by soc on 16.09.2015.
 */
@SuppressWarnings("ALL")
public class SbdWrapperTest {

    public static final Logger log = LoggerFactory.getLogger(SbdWrapperTest.class);

    /**
     * Experimental code to verify some assumptions, i.e. does not use the SbdWrapper
     *
     * @param asicInputStream
     * @param sbdh
     * @throws Exception
     */
    @Test(dataProvider = "sampleData", dataProviderClass = SampleDataProvider.class)
    public void wrapSampleDataExperiment(InputStream asicInputStream, StandardBusinessDocumentHeader sbdh) throws Exception {

        // Establishes the output stream for our xml data
        File outputFile = File.createTempFile("vefa-sbdh", ".xml");
        log.debug("Writing sample data to " + outputFile.toString());
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8")));

        out.println("<?xml version=\"1.0\"?>\n" +
                "<StandardBusinessDocument>");

        // Emits the SBDH
        JAXBContext jc = JAXBContext.newInstance(StandardBusinessDocumentHeader.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        JAXBElement<StandardBusinessDocumentHeader> standardBusinessDocumentHeader = new ObjectFactory().createStandardBusinessDocumentHeader(sbdh);

        marshaller.marshal(standardBusinessDocumentHeader, out);

        // Now for the base64 encoded ASiC archive
        out.println("\n<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">");
        out.flush();

        // Base64 contents goes here:
        Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream);

        IOUtils.copy(asicInputStream, base64OutputStream);
        base64OutputStream.flush();

        // Wrap up
        out.println("\n</asic:asic>");
        out.println("</StandardBusinessDocument>");

        out.flush();
        out.close();

        log.debug("SBD written to " + outputFile);
    }


    /**
     * Encodes the ASiC archive to base64 and decodes back to binary again.
     *
     * @throws Exception
     */
    @Test
    public void encodeAndDecode() throws Exception {
        URL sampleAsicUrl = SbdWrapperTest.class.getClassLoader().getResource(SampleDataProvider.SAMPLE_ASIC);
        assertNotNull(sampleAsicUrl);

        File asicFile = new File(sampleAsicUrl.toURI());

        File base64Copy = File.createTempFile("test-asic", ".b64");

        FileOutputStream base64CopyOutputStream = new FileOutputStream(base64Copy);
        Base64OutputStream base64OutputStream = new Base64OutputStream(base64CopyOutputStream);

        // Converts from binary -> base64

        IOUtils.copy(new FileInputStream(asicFile), base64OutputStream);
        base64OutputStream.close();
        assertTrue(base64Copy.length() > asicFile.length(), "Base encoding failed");


        // Converts back from base64 -> binary
        File asicFileCopy = File.createTempFile("asic-copy", ".asice");
        Base64InputStream base64InputStream = new Base64InputStream(new FileInputStream(base64Copy));
        FileOutputStream outputStreamForAsicCopy = new FileOutputStream(asicFileCopy);
        IOUtils.copy(base64InputStream, outputStreamForAsicCopy);
        outputStreamForAsicCopy.close();

        // They should be identical
        assertEquals(asicFile.length(), asicFileCopy.length());

        log.debug("Wrote base64 encoded contents to " + base64Copy);
    }


    /**
     * Performs the actual testing using the SbdWrapper class
     *
     * @param asicInputStream
     * @param standardBusinessDocumentHeader
     * @throws Exception
     */
    @Test(dataProvider = "sampleData", dataProviderClass = SampleDataProvider.class)
    public void wrapSampleData(InputStream asicInputStream, StandardBusinessDocumentHeader standardBusinessDocumentHeader) throws Exception {
        SbdWrapper sbdWrapper = new SbdWrapper();

        File outputFile = File.createTempFile("vefa-sbdh", ".xml");
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

        sbdWrapper.wrapInputStream(standardBusinessDocumentHeader, asicInputStream, fileOutputStream);
        fileOutputStream.close();
        log.debug("Wrote sample StandardBusinessDocument into " + outputFile.toString());

        // Extracts the data to verify
        File binaryFile = File.createTempFile("vefa-recreated", ".asice");
        OutputStream out = new FileOutputStream(binaryFile);

        // Extracts the base64 encoded ASiC payload
        AsicExtractor asicExtractor = AsicExtractorFactory.defaultAsicExtractor();
        asicExtractor.extractAsic(new FileInputStream(outputFile), out);
        out.close();

        log.debug("Re-created the ASiC file into " + binaryFile);


        URL originalAsicFileUrl = SbdWrapperTest.class.getClassLoader().getResource(SampleDataProvider.SAMPLE_ASIC);
        assertNotNull(originalAsicFileUrl);

        File originalAsicFile = new File(originalAsicFileUrl.toURI());
        assertEquals(originalAsicFile.length(), binaryFile.length()," The wrapped-and-unwrapped ASiC file does not equal in size to the original" );

    }


}
