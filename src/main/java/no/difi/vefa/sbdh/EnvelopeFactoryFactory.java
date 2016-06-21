package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.EnvelopeFactory;
import no.difi.vefa.sbdh.api.RawEnvelopeFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

public class EnvelopeFactoryFactory {

    public static EnvelopeFactory sbdhFactory() {
        return new SbdhFactory();
    }

    public static RawEnvelopeFactory<StandardBusinessDocumentHeader> rawSbdhFactory() {
        return new RawSbdhFactory();
    }

    EnvelopeFactoryFactory() {
        // No action.
    }
}
