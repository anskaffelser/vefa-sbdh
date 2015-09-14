package no.difi.vefa;

import org.testng.annotations.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.InputStream;
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
        assertNotNull(sbdh,"SBDH not found in ASiC archive");
    }
}