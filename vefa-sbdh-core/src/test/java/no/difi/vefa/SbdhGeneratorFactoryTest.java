package no.difi.vefa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author steinar
 *         Date: 10.09.15
 *         Time: 12.23
 */
public class SbdhGeneratorFactoryTest {

    public static final Logger log = LoggerFactory.getLogger(SbdhGeneratorFactoryTest.class);


    @Test
    public void verifySampleData() throws Exception {
        URL mainDocumentUrl = SbdhGeneratorFactoryTest.class.getClassLoader().getResource("sbdh-peppol-sample-v1.3.xml");
        assertNotNull(mainDocumentUrl, "Unable to find the test resources");
    }

    @Test(dataProvider = "sampleAsic", dataProviderClass = no.difi.vefa.SampleAsicProvider.class)
    public void verifyContentsOfAsicArchive(InputStream inputStream) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;

        boolean sbdhFound = false;
        while ((entry=zipInputStream.getNextEntry()) != null) {
            if (entry.getName().equalsIgnoreCase("sbdh.xml")) {
                sbdhFound = true;
            }
        }

        assertTrue(sbdhFound, "vefa.xml not found in ASiC archive");
    }


    @Test(dataProvider = "sampleAsic", dataProviderClass = no.difi.vefa.SampleAsicProvider.class)
    public void obtainCreatorForBackendSBDH(InputStream inputStream) throws Exception {

        assertNotNull(inputStream, "No inputstream provided");
        SbdhGenerator generator = SbdhGeneratorFactory.messageLevelGenerator();
        generator.generate(inputStream, new FileOutputStream("sample-sbd.xml"));
    }
}
