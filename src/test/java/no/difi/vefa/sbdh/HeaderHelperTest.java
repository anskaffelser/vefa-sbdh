package no.difi.vefa.sbdh;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.sbdh.lang.SbdhException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.util.Date;

public class HeaderHelperTest {

    private ParticipantIdentifier sender = new ParticipantIdentifier("9908:987654321");
    private ParticipantIdentifier receiver = new ParticipantIdentifier("9908:123456789");
    private ProcessIdentifier processIdentifier = new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver1.0");
    private DocumentTypeIdentifier documentTypeIdentifier = new DocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0");
    private InstanceIdentifier instanceIdentifier = InstanceIdentifier.generateUUID();
    private Date creationTimestamp = new Date();

    private Header header = Header.newInstance()
            .setSenderIdentifier(sender)
            .setReceiverIdentifier(receiver)
            .setProcessIdentifier(processIdentifier)
            .setDocumentTypeIdentifier(documentTypeIdentifier)
            .setInstanceIdentifier(instanceIdentifier)
            .setCreationTimestamp(creationTimestamp);

    @Test
    public void simple() throws Exception {
        Header header = HeaderHelper.fromSbdh(HeaderHelper.toSbdh(this.header));

        Assert.assertEquals(header.getSenderIdentifier(), sender);
        Assert.assertEquals(header.getReceiverIdentifier(), receiver);
        Assert.assertEquals(header.getProcessIdentifier(), processIdentifier);
        // Assert.assertEquals(header.getDocumentTypeIdentifier(), documentTypeIdentifier);
        Assert.assertNotNull(header.getDocumentTypeIdentifier());
        // Assert.assertEquals(header.getInstanceIdentifier(), instanceIdentifier);
        Assert.assertNotNull(header.getInstanceIdentifier());
        Assert.assertEquals(header.getCreationTimestamp(), creationTimestamp);
    }

    @Test
    public void fromInputStreamTest() throws Exception {
        Header header = HeaderHelper.fromSbdh(getClass().getResourceAsStream("/sample-sbdh.xml"));

        Assert.assertEquals(header.getSenderIdentifier(), new ParticipantIdentifier("9908:810018902"));
        Assert.assertEquals(header.getReceiverIdentifier(), new ParticipantIdentifier("9908:810418052"));
        Assert.assertEquals(header.getProcessIdentifier(), new ProcessIdentifier("urn:www.cenbii.eu:profile:bii46:ver3.0"));
    }

    @Test
    public void simpleConstructor() {
        new HeaderHelper();
    }

    @Test(expectedExceptions = SbdhException.class)
    public void toSbdhWithoutSender() throws SbdhException {
        HeaderHelper.toSbdh(header.setSenderIdentifier(null));
    }

    @Test(expectedExceptions = SbdhException.class)
    public void toSbdhWithoutReceiver() throws SbdhException {
        HeaderHelper.toSbdh(header.setReceiverIdentifier(null));
    }

    @Test(expectedExceptions = SbdhException.class)
    public void toSbdhWithoutProcess() throws SbdhException {
        HeaderHelper.toSbdh(header.setProcessIdentifier(null));
    }

    @Test(expectedExceptions = SbdhException.class)
    public void toSbdhWithoutDocumentType() throws SbdhException {
        HeaderHelper.toSbdh(header.setDocumentTypeIdentifier(null));
    }
}
