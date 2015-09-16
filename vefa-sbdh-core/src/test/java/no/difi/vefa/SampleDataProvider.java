package no.difi.vefa;

import org.testng.annotations.DataProvider;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.InputStream;

import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 10.09.15
 *         Time: 15.01
 */
public class SampleDataProvider {

    public static final String SAMPLE_ASIC_ASICE = "sample-asic.asice";

    @DataProvider(name = "sampleData")
    public static Object[][] creatAsicArchive() {


        InputStream resourceAsStream = SampleDataProvider.class.getClassLoader().getResourceAsStream(SAMPLE_ASIC_ASICE);
        assertNotNull(resourceAsStream, "Unable to locate " + SAMPLE_ASIC_ASICE + " in class path");

        InputStream sbdhAsStream = SampleDataProvider.class.getClassLoader().getResourceAsStream("sample-sbdh.xml");
        assertNotNull(resourceAsStream, "Unable to locate sample-sbdh.xml in class path");

        SbdhParser sbdhParser = SbdhParserFactory.sbdhParserWithExtractor();

        StandardBusinessDocumentHeader sbdh = sbdhParser.parse(sbdhAsStream);

        return new Object[][]{{resourceAsStream, sbdh}};
    }


}
