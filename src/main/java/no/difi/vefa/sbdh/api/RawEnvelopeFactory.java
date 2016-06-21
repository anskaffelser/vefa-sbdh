package no.difi.vefa.sbdh.api;

import no.difi.vefa.sbdh.lang.EnvelopeException;

import java.io.InputStream;
import java.io.OutputStream;

public interface RawEnvelopeFactory<H> {

    H extract(InputStream inputStream, OutputStream outputStream) throws EnvelopeException;

    RawEnvelopeExtractor<H> extractor(InputStream inputStream) throws EnvelopeException;

    void wrap(H header, InputStream inputStream, OutputStream outputStream) throws EnvelopeException;

    RawEnvelopeWrapper<H> wrapper(OutputStream outputStream) throws EnvelopeException;

}
