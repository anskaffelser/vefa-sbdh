package no.difi.vefa.sbdh;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class HeaderTest {

    private static Logger logger = LoggerFactory.getLogger(HeaderTest.class);

    @Test
    public void simple() {
        Header header = Header.newInstance()
                .setSenderIdentifier(new ParticipantIdentifier("9908:987654321"))
                .setReceiverIdentifier(new ParticipantIdentifier("9908:123456789"))
                .setProcessIdentifier(new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver1.0"))
                .setDocumentTypeIdentifier(new DocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));

        logger.info("{}", header);

        Assert.assertNotNull(header.hashCode());

        Assert.assertTrue(header.equals(header));
        Assert.assertFalse(header.equals(null));
        Assert.assertFalse(header.equals(new Object()));
        Assert.assertTrue(header.equals(header.setProcessIdentifier(new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver1.0"))));
        Assert.assertFalse(header.equals(header.setCreationTimestamp(new Date())));
        Assert.assertFalse(header.equals(header.setInstanceIdentifier(InstanceIdentifier.generateUUID())));
        Assert.assertFalse(header.equals(header.setSenderIdentifier(new ParticipantIdentifier("9908:555"))));
        Assert.assertFalse(header.equals(header.setReceiverIdentifier(new ParticipantIdentifier("9908:555"))));
        Assert.assertFalse(header.equals(header.setProcessIdentifier(new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver2.0"))));
    }

}
