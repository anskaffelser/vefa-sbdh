package no.difi.vefa;

import no.difi.asic.AsicWriter;
import no.difi.asic.AsicWriterFactory;
import no.difi.asic.SignatureHelper;
import no.difi.vefa.innlevering.KeyStoreUtil;
import no.difi.vefa.innlevering.Util;
import org.testng.annotations.DataProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author steinar
 *         Date: 10.09.15
 *         Time: 15.01
 */
public class SampleAsicProvider {

    @DataProvider(name = "sampleAsic")
    public static Object[][] creatAsicArchive() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Establish the SignatureHelper to be used by the SbdhAsicCreator
        SignatureHelper signatureHelper = new SignatureHelper(KeyStoreUtil.sampleKeyStoreStream(), KeyStoreUtil.getKeyStorePassword(), KeyStoreUtil.getKeyStoreAlias(), KeyStoreUtil.getPrivateKeyPassord());

        // Locates the inner SBDH
        InputStream sbdhInputStream = SampleAsicProvider.class.getClassLoader().getResourceAsStream(Util.SAMPLE_SBDH_RESOURCE);

        // Locates the main bis document
        InputStream mainDocInputStream = SampleAsicProvider.class.getClassLoader().getResourceAsStream(Util.SAMPLE_UBL_DOCUMENT);

        // Locates a sample attachment
        InputStream attachmentInputStream = SampleAsicProvider.class.getClassLoader().getResourceAsStream(Util.SAMPLE_ATTACHMENT);

        AsicWriterFactory asicWriterFactory = AsicWriterFactory.newFactory();
        try {
            AsicWriter asicWriter = asicWriterFactory.newContainer(baos);
            asicWriter.add(sbdhInputStream, "sbdh.xml")
                .add(mainDocInputStream, "trdm090-submit-tender-sample.xml")
                .add(attachmentInputStream,"sample-readme.txt")
                .sign(signatureHelper);

        } catch (IOException e) {
            System.out.println("Ooops " + e.getMessage());
            throw new IllegalStateException("Unable to create new container.", e);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
        return new Object[][] { {byteArrayInputStream}};
    }
}
