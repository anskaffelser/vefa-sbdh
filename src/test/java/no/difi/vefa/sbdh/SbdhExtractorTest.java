package no.difi.vefa.sbdh;

import com.google.common.io.ByteStreams;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SbdhExtractorTest {

    private SbdhFactory sbdhFactory = (SbdhFactory) EnvelopeFactoryFactory.sbdhFactory();

    @Test
    public void simple() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Header header = sbdhFactory.extract(
                getClass().getResourceAsStream("/peppol-bis-invoice-sbdh.xml"), byteArrayOutputStream);

        Assert.assertNotNull(header);

        ByteStreams.copy(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), System.out);

        JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<InvoiceType> o = unmarshaller.unmarshal(
                new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
                InvoiceType.class
        );
        Assert.assertEquals(
                o.getValue().getCustomizationID().getValue(),
                "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0"
        );
    }
}
