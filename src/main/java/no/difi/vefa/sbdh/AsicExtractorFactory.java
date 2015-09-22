package no.difi.vefa.sbdh;

/**
 * @author steinar
 *         Date: 22.09.15
 *         Time: 08.26
 */
public class AsicExtractorFactory {
    public static AsicExtractor defaultAsicExtractor() {
        return new SaxAsicExtractor();
    }
}
