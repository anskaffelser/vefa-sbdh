package no.difi.vefa.sbdh;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * Class containing resources to be available in other classes by inheritance.
 */
abstract class StaxContext extends SbdhContext {

    protected static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    protected static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

}
