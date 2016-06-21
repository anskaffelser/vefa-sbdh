package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.EnvelopeWrapper;
import no.difi.vefa.sbdh.api.RawEnvelopeWrapper;
import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SbdhWrapper extends SbdhContext implements EnvelopeWrapper {

    private RawEnvelopeWrapper<StandardBusinessDocumentHeader> rawWrapper;

    private StandardBusinessDocumentHeader header;

    public SbdhWrapper(OutputStream outputStream) throws EnvelopeException {
        rawWrapper = rawFactory.wrapper(outputStream);
    }

    @Override
    public void writeHeader(Header header) throws EnvelopeException {
        this.header = HeaderHelper.toSbdh(header);
    }

    @Override
    public void writePayload(InputStream inputStream) throws EnvelopeException {
        if (header != null) {
            rawWrapper.writeHeader(header);
            header = null;
        }

        rawWrapper.writePayload(inputStream);
    }

    @Override
    public void close() throws IOException {
        if (header != null) {
            try {
                rawWrapper.writeHeader(header);
                header = null;
            } catch (EnvelopeException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        rawWrapper.close();
        rawWrapper = null;
    }
}
