package no.difi.vefa.sbdh.api;

import no.difi.vefa.sbdh.lang.EnvelopeException;

import java.io.Closeable;
import java.io.OutputStream;

public interface RawEnvelopeExtractor<H> extends Closeable {

    H readHeader() throws EnvelopeException;

    void readPayload(OutputStream outputStream) throws EnvelopeException;

}
