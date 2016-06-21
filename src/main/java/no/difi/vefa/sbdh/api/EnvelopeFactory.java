package no.difi.vefa.sbdh.api;

import no.difi.vefa.sbdh.Header;
import no.difi.vefa.sbdh.lang.EnvelopeException;

import java.io.InputStream;
import java.io.OutputStream;

public interface EnvelopeFactory {

    Header extract(InputStream inputStream, OutputStream outputStream) throws EnvelopeException;

    EnvelopeExtractor extractor(InputStream inputStream) throws EnvelopeException;

    void wrap(Header header, InputStream inputStream, OutputStream outputStream) throws EnvelopeException;

    EnvelopeWrapper wrapper(OutputStream outputStream) throws EnvelopeException;

}
