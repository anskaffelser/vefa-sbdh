package no.difi.vefa.sbdh;

/**
 * Created by soc on 16.09.2015.
 */
public class SbdhParserFactory {


    /**
     * Creates SBDH parser that extracts the SBDH from a potentially large file and parses the SBDH.
     *
     * @return
     */
    public static SbdhParser sbdhParserWithExtractor() {
        return new SbdhFastParser();
    }

    /**
     * Creates SBDH parser that parses an XML stream holding only a single SBDH element.
     *
     * @return
     */
    public static SbdhParser parserForSbdhOnly() {
        return new SbdhOnlyParser();
    }

}
