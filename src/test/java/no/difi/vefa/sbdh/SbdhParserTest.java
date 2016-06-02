package no.difi.vefa.sbdh;

import org.testng.annotations.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.InputStream;

import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 22.09.15
 *         Time: 09.41
 */
public class SbdhParserTest {

    @Test(dataProvider = "sampleSbd", dataProviderClass = SampleDataProvider.class)
    public void parseSampleSbdhFromLargeStandardBusinessDocument(InputStream sbdInputStream) throws Exception {

        // Creates a parser, which will extract the SBDH from a really large xml file
        SbdhParser sbdhParser = SbdhParserFactory.sbdhParserAndExtractor();

        // Performs the actual parsing
        StandardBusinessDocumentHeader standardBusinessDocumentHeader = sbdhParser.parse(sbdInputStream);

        // Ensures that we got something
        assertNotNull(standardBusinessDocumentHeader);
    }
    
    @Test(dataProvider = "sampleSbdWithNS", dataProviderClass = SampleDataProvider.class)
    public void parseSampleSbdhFromLargeStandardBusinessDocumentWithNS(InputStream sbdInputStream) throws Exception {

        // Creates a parser, which will extract the SBDH from a really large xml file
        SbdhParser sbdhParser = SbdhParserFactory.sbdhParserAndExtractor();

        // Performs the actual parsing
        StandardBusinessDocumentHeader standardBusinessDocumentHeader = sbdhParser.parse(sbdInputStream);

        // Ensures that we got something
        assertNotNull(standardBusinessDocumentHeader);
    }
}
