package no.difi.vefa.sbdh;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class StaxPayloadExtractorTest {

    private Logger logger = LoggerFactory.getLogger(StaxPayloadExtractorTest.class);

    @Test
    public void simple() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new StaxPayloadExtractor().extract(getClass().getResourceAsStream("/peppol-bis-invoice-sbdh.xml"), byteArrayOutputStream);

        JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<InvoiceType> o = unmarshaller.unmarshal(
                new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
                InvoiceType.class
        );
        Assert.assertEquals(o.getValue().getCustomizationID().getValue(), "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0");
    }
}
