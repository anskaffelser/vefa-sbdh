package no.difi.vefa.sbdh;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EnvelopeFactoryFactoryTest {

    @Test
    public void simpleConstructor() {
        new EnvelopeFactoryFactory();
    }

    @Test
    public void simple() {
        Assert.assertNotNull(EnvelopeFactoryFactory.rawSbdhFactory());
        Assert.assertNotNull(EnvelopeFactoryFactory.sbdhFactory());
    }
}
