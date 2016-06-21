package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.EnvelopeExtractor;
import no.difi.vefa.sbdh.api.EnvelopeFactory;
import no.difi.vefa.sbdh.api.EnvelopeWrapper;
import no.difi.vefa.sbdh.lang.EnvelopeException;
import no.difi.vefa.sbdh.wrapper.AsicToXmlWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SbdhFactory implements EnvelopeFactory {

    @Override
    public Header extract(InputStream inputStream, OutputStream outputStream) throws EnvelopeException {
        try (EnvelopeExtractor extractor = extractor(inputStream)) {
            Header header = extractor.readHeader();
            extractor.readPayload(outputStream);
            return header;
        } catch (IOException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public EnvelopeExtractor extractor(InputStream inputStream) throws EnvelopeException {
        return new SbdhExtractor(inputStream);
    }

    @Override
    public void wrap(Header header, InputStream inputStream, OutputStream outputStream) throws EnvelopeException {
        try {
            try (EnvelopeWrapper wrapper = wrapper(outputStream)) {
                wrapper.writeHeader(header);
                wrapper.writePayload(new AsicToXmlWrapper(inputStream)); // TODO
            }
        } catch (IOException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    @Override
    public EnvelopeWrapper wrapper(OutputStream outputStream) throws EnvelopeException {
        return new SbdhWrapper(outputStream);
    }
}
