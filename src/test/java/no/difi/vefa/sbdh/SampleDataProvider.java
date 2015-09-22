package no.difi.vefa.sbdh;

import com.javafx.tools.doclets.internal.toolkit.util.DocFinder;
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
    public static final String SAMPLE_SBD_XML = "sample-sbd.xml";

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

    @DataProvider(name = "sampleSbd")
    public static Object[][] sampleSbd() {
        InputStream sbdStream = SampleDataProvider.class.getClassLoader().getResourceAsStream(SAMPLE_SBD_XML);
        assertNotNull(sbdStream, "Unable to locate " + SAMPLE_SBD_XML + " in class path");


        return new Object[][]{ { sbdStream } };
    }

}

