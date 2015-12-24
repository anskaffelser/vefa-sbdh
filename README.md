[![Build Status](https://travis-ci.org/difi/vefa-sbdh.svg?branch=master)](https://travis-ci.org/difi/vefa-sbdh)

# vefa-sbdh - manipulates StandardBusinessDocument and StandardBusinessDocumentHeader 

Use this component to:

* wrap an ASiC archive as base64 encoded payload within a StandardBusinessDocument (SBD) for use with PEPPOL, eSENS etc. 
* parse (extract) base64 encoded ASiC archive from SBD payload
* parse (extract) SBDH from really large XML documents very fast.

See [UN/CEFACT SBDH Technical Specification](http://www.gs1.org/docs/gs1_un-cefact_%20xml_%20profiles/CEFACT_SBDH_TS_version1.3.pdf)
for the details of the SBD and SBDH.

Note: The term "SBDH" is often used as a synonym for "SBD", which is kind of confusing.

## ASiC as base64 encoded payload within StandardBusinessDocument
```xml
<?xml version="1.0"?>
<StandardBusinessDocument>
<StandardBusinessDocumentHeader xmlns="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">
    <HeaderVersion>1.0</HeaderVersion>
    <Sender>
        <Identifier Authority="iso6523-actorid-upis">9908:810018902</Identifier>
    </Sender>
    <Receiver>
        <Identifier Authority="iso6523-actorid-upis">9908:810418052</Identifier>
    </Receiver>
    <DocumentIdentification>
        <Standard>urn:oasis:names:specification:ubl:schema:xsd:Tender-2</Standard>
        <TypeVersion>2.1</TypeVersion>
        <InstanceIdentifier>FA4A6819-6149-4134-95C3-C53A65338EB6</InstanceIdentifier>
        <Type>Tender</Type>
        <CreationDateAndTime>2015-07-26T20:08:00+01:00</CreationDateAndTime>
    </DocumentIdentification>
    <Manifest>
        <NumberOfItems>1</NumberOfItems>
        <ManifestItem>
            <MimeTypeQualifierCode>application/vnd.etsi.asic-e+zip</MimeTypeQualifierCode>
            <UniformResourceIdentifier>#asic</UniformResourceIdentifier>
            <Description>ASiC archive containing the business documents.</Description>
        </ManifestItem>
    </Manifest>
    <BusinessScope>
        <Scope>
            <Type>PROCESSID</Type>
            <InstanceIdentifier>urn:www.cenbii.eu:profile:bii46:ver3.0</InstanceIdentifier>
        </Scope>
        <Scope>
            <Type>DOCUMENTID</Type>
            <InstanceIdentifier>
                urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1
            </InstanceIdentifier>
        </Scope>
    </BusinessScope>
</StandardBusinessDocumentHeader>
<asic:asic xmlns:asic="urn:etsi.org:specification:02918:v1.2.1" id="asic">
UEsDBAoAAAgAAI1ZMEeKIflFHwAAAB8AAAAIAAAAbWltZXR5cGVhcHBsaWNhdGlvbi92bmQuZXRz
aS5hc2ljLWUremlwUEsDBBQACAgIAI1ZMEcAAAAAAAAAAAAAAAAIAAAAc2JkaC54bWytVk1z2kgQ
   .... loads of data removed for readability ......
bWxQSwUGAAAAAAcABwAUAgAAbBYAACgAbWltZXR5cGU9YXBwbGljYXRpb24vdm5kLmV0c2kuYXNp
Yy1lK3pp
</asic:asic>
</StandardBusinessDocument>
   
```

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
