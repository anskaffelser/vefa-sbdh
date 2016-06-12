package no.difi.vefa.sbdh;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.sbdh.lang.SbdhException;
import no.difi.vefa.sbdh.util.AsicAsXmlInputStream;
import org.testng.annotations.Test;

public class StaxWrapperTest {

    @Test
    public void simple() throws SbdhException {
        StaxWrapper.wrap(Header.newInstance()
                        .setSenderIdentifier(new ParticipantIdentifier("9908:987654321"))
                        .setReceiverIdentifier(new ParticipantIdentifier("9908:123456789"))
                        .setProcessIdentifier(new ProcessIdentifier("urn:www.cenbii.eu:profile:bii04:ver1.0"))
                        .setDocumentTypeIdentifier(new DocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0")),
                new AsicAsXmlInputStream(getClass().getResourceAsStream("/sample-asic.asice")),
                System.out
        );
    }

    @Test
    public void simpleConstructor() {
        new StaxWrapper();
    }
}
