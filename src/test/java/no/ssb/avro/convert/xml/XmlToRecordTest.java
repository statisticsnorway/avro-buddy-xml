package no.ssb.avro.convert.xml;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import no.ssb.avro.convert.core.DataElement;
import no.ssb.avro.convert.core.SchemaAwareElement;
import no.ssb.avro.convert.core.SchemaBuddy;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

class XmlToRecordTest {

    @Test
    void skattCreateRecordsShouldMapRecursivelyToSchema() throws XMLStreamException {
        String json = getRecordsAsString(TestUtils.skattXml(), TestUtils.skattSchema(), "skattepliktig");

        assertThat(json).isEqualTo(TestUtils.expectedSkattGenericRecordsAsJson());
    }

    @Test
    void fregPersonCreateRecordsShouldMapRecursivelyToSchema() throws XMLStreamException {
        String json = getRecordsAsString(TestUtils.fregPersonXml(), TestUtils.fregPersonSchema(), "folkeregisterperson");

        assertThat(json).isEqualTo(TestUtils.expectedFregPersonGenericRecordsAsJson());
    }

    @Test
    void fregHendelseCreateRecordsShouldMapRecursivelyToSchema() throws XMLStreamException {
        String json = getRecordsAsString(TestUtils.fregHendelseXml(), TestUtils.fregHendelseSchema(), "dokumentForHendelse");

        assertThat(json).isEqualTo(TestUtils.expectedFregHendelseGenericRecordsAsJson());
    }

    Schema schemaCdCatalog = SchemaBuilder
            .record("CATALOG")
            .fields()
            .name("cds").type(
                    SchemaBuilder.array()
                            .items(SchemaBuilder.record("CD")
                                    .fields()
                                    .name("TITLE").type().stringType().noDefault()
                                    .name("ARTIST").type().stringType().noDefault()
                                    .name("COUNTRY").type().stringType().noDefault()
                                    .name("COMPANY").type().stringType().noDefault()
                                    .name("PRICE").type().doubleType().noDefault()
                                    .name("YEAR").type().intType().noDefault()
                                    .name("BIG_NUM").type().optional().longType()
                                    .endRecord()
                            )
            ).noDefault()
            .endRecord();

    private String getRecordsAsString(InputStream inputStream, Schema schema, String topElement) throws XMLStreamException {
        RecursiveXmlParser xmlParser = new RecursiveXmlParser(inputStream, topElement);

        Iterator<DataElement> iterator = xmlParser.iterator();

        SchemaBuddy schemaBuddy = SchemaBuddy.parse(schema);

        assert iterator.hasNext();
        DataElement dataElement = iterator.next();
        assert !iterator.hasNext();

        GenericRecord record = SchemaAwareElement.toRecord(dataElement, schemaBuddy);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(new JsonParser().parse(record.toString()));
    }
}