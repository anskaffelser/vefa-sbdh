package no.difi.vefa.sbdh.api;

import no.difi.vefa.sbdh.lang.EnvelopeException;

import java.io.Closeable;
import java.io.InputStream;

public interface RawEnvelopeWrapper<H> extends Closeable {

    void writeHeader(H header) throws EnvelopeException;

    void writePayload(InputStream inputStream) throws EnvelopeException;

}
