# avro-buddy-xml
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a32936cc5cb64980950d33de90a6fb88)](https://www.codacy.com/manual/RuneLind/avro-buddy-xml?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=statisticsnorway/avro-buddy-xml&amp;utm_campaign=Badge_Grade)

[![codecov](https://codecov.io/gh/statisticsnorway/avro-buddy-xml/branch/develop/graph/badge.svg)](https://codecov.io/gh/statisticsnorway/avro-buddy-xml)

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
