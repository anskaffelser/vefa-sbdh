package no.difi.vefa.sbdh;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * Class containing resources related to StAX to be available in other classes by inheritance.
 */
abstract class StaxContext {

    protected static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    protected static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

}
