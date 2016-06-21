package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.EnvelopeExtractor;
import no.difi.vefa.sbdh.api.RawEnvelopeExtractor;
import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SbdhExtractor extends SbdhContext implements EnvelopeExtractor {

    private RawEnvelopeExtractor<StandardBusinessDocumentHeader> rawExtractor;

    private Header header;

    public SbdhExtractor(InputStream inputStream) throws EnvelopeException {
        rawExtractor = rawFactory.extractor(inputStream);

        header = HeaderHelper.fromSbdh(rawExtractor.readHeader());
    }

    @Override
    public Header readHeader() throws EnvelopeException {
        return header;
    }

    @Override
    public void readPayload(OutputStream outputStream) throws EnvelopeException {
        rawExtractor.readPayload(outputStream);
    }

    @Override
    public void close() throws IOException {
        rawExtractor.close();
        rawExtractor = null;
    }
}
