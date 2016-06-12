package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.api.AsicExtractor;

/**
 * AsicExtractor factory using the factory design pattern in order to make code a little more future proof.
 *
 * @author steinar
 *         Date: 22.09.15
 *         Time: 08.26
 */
public class AsicExtractorFactory {

    /** Provides the default AsicExtractor */
    public static AsicExtractor defaultAsicExtractor() {
        return new SaxAsicExtractor();
    }
}
