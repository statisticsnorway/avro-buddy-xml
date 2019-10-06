package no.ssb.avro.convert.xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.apache.avro.Schema;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

class TestUtils {

    private static ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    private static Schema loadSchema(String fileName) {
        try {
            return new Schema.Parser().parse(getInputStream(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream loadXml(String fileName) {
        try {
            return getInputStream(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String loadString(String fileName) {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(getInputStream(fileName), writer, "UTF-8");
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStream(String fileName) throws FileNotFoundException {
        InputStream stream = classloader.getResourceAsStream(fileName);
        if (stream == null) {
            throw new FileNotFoundException(fileName);
        }
        return stream;
    }

    private static String jsonFromFile(String fileName) {
        try {
            InputStream stream = getInputStream(fileName);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(new JsonParser().parse(new InputStreamReader(stream)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String expectedSkattGenericRecordsAsJson() {
        return jsonFromFile("test-data/skatt/expectedSkattGenericRecords.json");
    }

    static String expectedFregPersonGenericRecordsAsJson() {
        return jsonFromFile("test-data/freg/expectedFregPersonGenericRecords.json");
    }

    static String expectedFregHendelseGenericRecordsAsJson() {
        return jsonFromFile("test-data/freg/expectedFregHendelseGenericRecords.json");
    }

    static Schema skattSchema() {
        return loadSchema("test-data/skatt/skatt.avsc");
    }

    static Schema fregPersonSchema() {
        return loadSchema("test-data/freg/person.avsc");
    }

    static Schema fregHendelseSchema() {
        return loadSchema("test-data/freg/hendelse.avsc");
    }

    static InputStream fregPersonXml() {
        return loadXml("test-data/freg/person-document-42056399946.xml");
    }

    static InputStream fregHendelseXml() {
        return loadXml("test-data/freg/event-document-6a6855100509e7fc1d86c1154a075d3d.xml");
    }

    static InputStream skattXml() {
        return loadXml("test-data/skatt/skatt-one-element.xml");
    }


    static InputStream catalogXml() {
        return loadXml("test-data/catalog/catalog.xml");
    }
}
