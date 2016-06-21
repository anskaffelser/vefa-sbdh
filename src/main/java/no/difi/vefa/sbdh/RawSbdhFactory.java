package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.RawEnvelopeExtractor;
import no.difi.vefa.sbdh.api.RawEnvelopeFactory;
import no.difi.vefa.sbdh.api.RawEnvelopeWrapper;
import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class RawSbdhFactory implements RawEnvelopeFactory<StandardBusinessDocumentHeader> {

    @Override
    public StandardBusinessDocumentHeader extract(InputStream inputStream, OutputStream outputStream) throws EnvelopeException {
        try (RawSbdhExtractor sbdhExtractor = new RawSbdhExtractor(inputStream)) {
            StandardBusinessDocumentHeader header = sbdhExtractor.readHeader();
            sbdhExtractor.readPayload(outputStream);
            return header;
        } catch (IOException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public RawEnvelopeExtractor<StandardBusinessDocumentHeader> extractor(InputStream inputStream) throws EnvelopeException {
        return new RawSbdhExtractor(inputStream);
    }

    @Override
    public void wrap(StandardBusinessDocumentHeader header, InputStream inputStream, OutputStream outputStream) throws EnvelopeException {
        try {
            try (RawSbdhWrapper sbdhWrapper = new RawSbdhWrapper(outputStream)) {
                sbdhWrapper.writeHeader(header);
                sbdhWrapper.writePayload(inputStream);
            }
        } catch (IOException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public RawEnvelopeWrapper<StandardBusinessDocumentHeader> wrapper(OutputStream outputStream) throws EnvelopeException {
        return new RawSbdhWrapper(outputStream);
    }
}
