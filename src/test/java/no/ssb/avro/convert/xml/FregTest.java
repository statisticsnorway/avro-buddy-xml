package no.ssb.avro.convert.xml;

import org.apache.avro.Schema;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Disabled // Only used for experiments
public class FregTest {

    private Schema readSchema(String schemaFileName) throws IOException {
        return new Schema.Parser().parse(getClass().getClassLoader().getResourceAsStream(schemaFileName));
    }

    private String readXml(String pathToXml) throws IOException {
        return Files.readString(Path.of(pathToXml));
    }

    @Test
    public void convertSingleXmlRecord() throws Exception {
        Schema personSchema = readSchema("test-data/freg/schema/freg-person_v1.4.avsc");
        String xml = readXml("src/test/resources/test-data/freg/person-1.xml");
        InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes());
        try (XmlToRecords xmlToRecords = new XmlToRecords(xmlInputStream, "folkeregisterperson", personSchema)) {
            xmlToRecords.forEach(record -> {
                  System.out.println(record.toString());
              }
            );
        } catch (Exception e) {
            throw new TestAbortedException(e.getMessage());
        }
    }

}
