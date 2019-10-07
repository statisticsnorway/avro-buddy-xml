package no.ssb.avro.convert.xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import no.ssb.avro.convert.core.DataElement;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class XmlToRecordsTest {

    private Schema schemaCdCatalog = SchemaBuilder
            .record("CATALOG")
            .fields()
            .name("CD").type(
                    SchemaBuilder.array()
                            .items(SchemaBuilder.record("cds")
                                    .fields()
                                    .name("TITLE").type().stringType().noDefault()
                                    .name("ARTIST").type().stringType().noDefault()
                                    .name("COUNTRY").type().stringType().noDefault()
                                    .name("COMPANY").type().stringType().noDefault()
                                    .name("PRICE").type().doubleType().noDefault()
                                    .name("YEAR").type().intType().noDefault()
                                    .endRecord()
                            )
            ).noDefault()
            .endRecord();

    @Test
    void testCdCatalog() {
        InputStream inputStream = TestUtils.catalogXml();
        List<GenericRecord> records = new ArrayList<>();
        try (XmlToRecords xmlToRecords = new XmlToRecords(inputStream, "CATALOG", schemaCdCatalog)) {
            xmlToRecords.forEach(records::add);
        } catch (XMLStreamException | IOException e) {
            throw new TestAbortedException(e.getMessage());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(new JsonParser().parse(records.toString()));
        String expected = "[\n" +
                "  {\n" +
                "    \"CD\": [\n" +
                "      {\n" +
                "        \"TITLE\": \"Empire Burlesque\",\n" +
                "        \"ARTIST\": \"Bob Dylan\",\n" +
                "        \"COUNTRY\": \"USA\",\n" +
                "        \"COMPANY\": \"Columbia\",\n" +
                "        \"PRICE\": 10.9,\n" +
                "        \"YEAR\": 1985\n" +
                "      },\n" +
                "      {\n" +
                "        \"TITLE\": \"Hide your heart\",\n" +
                "        \"ARTIST\": \"Bonnie Tyler\",\n" +
                "        \"COUNTRY\": \"UK\",\n" +
                "        \"COMPANY\": \"CBS Records\",\n" +
                "        \"PRICE\": 9.9,\n" +
                "        \"YEAR\": 1988\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";

        assertThat(result).isEqualTo(expected);
    }
}