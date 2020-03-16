package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.SbdhParser;
import org.testng.annotations.DataProvider;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 10.09.15
 *         Time: 15.01
 */
public class SampleDataProvider {

    public static final String SAMPLE_ASIC = "sample-asic.asice";
    public static final String SAMPLE_SBD_XML = "sample-sbd.xml";
    public static final String SAMPLE_SBD_BC_XML = "sample-sbd-with-BinaryContent.xml";
    public static final String SAMPLE_SBD_XML_WITH_NS = "sample-sbd-ns.xml";

    @DataProvider(name = "sampleData")
    public static Object[][] creatAsicArchive() {

        InputStream asicInputStream = getStream(SAMPLE_ASIC);

        InputStream sbdhAsStream = SampleDataProvider.class.getClassLoader().getResourceAsStream("sample-sbdh-ns.xml");
        assertNotNull(sbdhAsStream, "Unable to locate sample-sbdh.xml in class path");
        
        InputStream sbdhWithNSAsStream = SampleDataProvider.class.getClassLoader().getResourceAsStream("sample-sbdh-ns.xml");
        assertNotNull(sbdhWithNSAsStream, "Unable to locate sample-sbdh-ns.xml in class path");

        SbdhParser sbdhParser = SbdhParserFactory.sbdhParserAndExtractor();

        StandardBusinessDocumentHeader sbdh = sbdhParser.parse(sbdhAsStream);

        StandardBusinessDocumentHeader sbdhWithNS = sbdhParser.parse(sbdhWithNSAsStream);

        return new Object[][]{{asicInputStream, sbdh, sbdhWithNS}};
    }

    @DataProvider(name = "sampleSbd")
    public static Object[][] sampleSbd() {
        InputStream sbdStream = getStream(SAMPLE_SBD_XML);

        return new Object[][]{ { sbdStream } };
    }
    
    @DataProvider(name = "sampleSbdBinaryContent")
    public static Object[][] sampleSbdBinaryContent() {
        InputStream sbdStream = getStream(SAMPLE_SBD_BC_XML);

        return new Object[][]{ { sbdStream } };
    }

    @DataProvider(name = "sampleSbdWithNS")
    public static Object[][] sampleSbdWithNS() {
        InputStream sbdStream2 = getStream(SAMPLE_SBD_XML_WITH_NS);

        return new Object[][]{ { sbdStream2 } };
    }

    @DataProvider(name="sampleAsicFile")
    public static Object[][] sampleAsicFile() {
        URL url = SampleDataProvider.class.getClassLoader().getResource(SAMPLE_ASIC);
        assertNotNull(url);

        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to convert URI to File object.", e);
        }
        return new Object[][]{{file}};
    }

    private static InputStream getStream(String resourceName) {
        InputStream inputStream = SampleDataProvider.class.getClassLoader().getResourceAsStream(resourceName);
        assertNotNull(inputStream, "Unable to locate " + resourceName + " in class path");

        return new BufferedInputStream(inputStream);
    }
}

