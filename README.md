# avro-buddy-xml
Lib for converting xml to GenericRecord which can be used to create Avro/Parquet files  

Depending on [avro-buddy-core](https://github.com/statisticsnorway/avro-buddy-core)

## Usage
```java
try (XmlToRecords xmlToRecords = new XmlToRecords(inputStream, "topElement", schema)) {
    for (GenericRecord record : xmlToRecords) {
        // Process record
    }
} catch (XMLStreamException | IOException e) {
    // handle exception
}

```
