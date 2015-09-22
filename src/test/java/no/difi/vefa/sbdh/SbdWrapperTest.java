package no.difi.vefa.sbdh;

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

/**
 * Created by soc on 16.09.2015.
 */
public class SbdWrapperTest {

    public static final Logger log = LoggerFactory.getLogger(SbdWrapperTest.class);

    /**
     * Experimental code to verify some assumptions
     *
     * @param inputStream
     * @param sbdh
     * @throws Exception
     */
    @Test(dataProvider = "sampleData", dataProviderClass = SampleDataProvider.class)
    public void wrapSampleDataExperiment(InputStream inputStream, StandardBusinessDocumentHeader sbdh) throws Exception {

        File outputFile = File.createTempFile("vefa-sbdh", ".xml");
        log.debug("Writing sample data to " + outputFile.toString());
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8")));

        JAXBContext jc = JAXBContext.newInstance(StandardBusinessDocumentHeader.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        JAXBElement<StandardBusinessDocumentHeader> standardBusinessDocumentHeader = new ObjectFactory().createStandardBusinessDocumentHeader(sbdh);

        out.println("<?xml version=\"1.0\"?>\n" +
                "<StandardBusinessDocument>");

        marshaller.marshal(standardBusinessDocumentHeader, out);

        out.println("\n<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">");
        out.flush();

        // Base64 contents goes here:
        Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream);

        IOUtils.copy(inputStream, base64OutputStream);
        base64OutputStream.flush();
        out.println("\n</asic:asic>");
        out.println("</StandardBusinessDocument>");

        out.flush();
        out.close();
    }

    /**
     * Performs the actual testing using the SbdWrapper class
     *
     * @param inputStream
     * @param standardBusinessDocumentHeader
     * @throws Exception
     */
    @Test(dataProvider = "sampleData", dataProviderClass = SampleDataProvider.class)
    public void wrapSampleData(InputStream inputStream, StandardBusinessDocumentHeader standardBusinessDocumentHeader) throws Exception {
        SbdWrapper sbdWrapper = new SbdWrapper();

        File outputFile = File.createTempFile("vefa-sbdh", ".xml");
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

        sbdWrapper.wrapInputStream(standardBusinessDocumentHeader, inputStream, fileOutputStream);
        log.debug("Wrote sample StandardBusinessDocument into " + outputFile.toString());
    }
}