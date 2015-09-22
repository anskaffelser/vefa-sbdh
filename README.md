# vefa-sbdh - manipulates StandardBusinessDocument and StandardBusinessDocumentHeader 

Use this component to:

* wrap an ASiC archive as base64 encoded payload within a StandardBusinessDocument (SBD) for use with PEPPOL, eSENS etc. 
* parse (extract) base64 encoded ASiC archive from SBD payload
* parse (extract) SBDH from really large XML documents very fast.

See [UN/CEFACT SBDH Technical Specification](http://www.gs1.org/docs/gs1_un-cefact_%20xml_%20profiles/CEFACT_SBDH_TS_version1.3.pdf)
for the details of the SBD and SBDH.

Note: The term "SBDH" is often used as a synonym for "SBDH", which is kind of confusing.

## Wrapping ASiC archive as base64 encoded payload within StandardBusinessDocument

In order to transport an ASiC archive as payload within a StandardBusinessDocument (SBD), the payload must 
be base64 encoded.

```java
/** Wraps the ASiC archive supplied in the "inputStream" into a SBD, with the supplied SBDH */
public void wrapSampleData(InputStream inputStream, StandardBusinessDocumentHeader standardBusinessDocumentHeader) throws Exception {
    SbdWrapper sbdWrapper = new SbdWrapper();

    File outputFile = File.createTempFile("vefa-sbdh", ".xml");
    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

    sbdWrapper.wrapInputStream(standardBusinessDocumentHeader, inputStream, fileOutputStream);
    log.debug("Wrote sample StandardBusinessDocument into " + outputFile.toString());
}
```

Please review the [unit tests](src/test/java/no/difi/vefa/sbdh) for further details and examples.
  

## Extracting base64 encoded ASiC archive from SBD payload

Retrieving and decoding the base64 encoded ASiC archive from an SBD is easy:

```java
/** Illustrates how to extract base64 encoded ASiC archive */
@Test(dataProvider = "sampleSbd", dataProviderClass = SampleDataProvider.class)
public void extractAsicArchiveFromPayload(InputStream inputStream) throws Exception {

    // Creates the extractor
    AsicExtractor asicExtractor = AsicExtractorFactory.defaultAsicExtractor();

    // Creates a temporary file to hold the results
    File asiceFile = File.createTempFile("vefa-sample-asic", ".asice");
    FileOutputStream fileOutputStream = new FileOutputStream(asiceFile);
    BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);

    // Performs the actual extraction
    asicExtractor.extractAsic(inputStream, outputStream);

    inputStream.close();
    outputStream.close();
}
```

Please review the [unit tests](src/test/java/no/difi/vefa/sbdh) for further details and examples.

## Extracting the SBDH only from SBD with large payload

Extracting the SBDH (the SBD Header) from a really large StandardBusinessDocument (due to base64 encoded payload), 
is shown below. Note that the parser will not parse the entire document, i.e. read all the bytes. Only the SBDH 
will be extracted and parse.

```java
public void parseSampleSbdhFromLargeStandardBusinessDocument(InputStream sbdInputStream) throws Exception {

    // Creates a parser, which will extract the SBDH from a really large xml file
    SbdhParser sbdhParser = SbdhParserFactory.sbdhParserAndExtractor();

    // Performs the actual parsing
    StandardBusinessDocumentHeader standardBusinessDocumentHeader = sbdhParser.parse(sbdInputStream);

    // Ensures that we got something
    assertNotNull(standardBusinessDocumentHeader);
}

```

Please review the [unit tests](src/test/java/no/difi/vefa/sbdh) for further details and examples.
