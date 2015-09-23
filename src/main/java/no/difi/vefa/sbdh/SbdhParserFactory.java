package no.difi.vefa.sbdh;

/**
 * Factory for SbdhParser instances.
 *
 * Created by soc on 16.09.2015.
 */
public class SbdhParserFactory {


    /**
     * Creates SBDH parser that extracts the SBDH from a potentially large file and parses the SBDH.
     *
     * @return  instance of SBDH extractor and parser.
     */
    public static SbdhParser sbdhParserAndExtractor() {
        return new SbdhFastParser();
    }

    /**
     * Creates SBDH parser that parses an XML stream holding only a single SBDH element.
     * Possibly slow when there is a large payload.
     *
     * @return instance of parser, which will only handle a single SBDH element.
     */
    public static SbdhParser parserForSbdhOnly() {
        return new SbdhOnlyParser();
    }

}
