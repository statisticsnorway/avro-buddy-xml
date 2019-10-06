package no.ssb.avro.convert.xml;

import no.ssb.avro.convert.core.DataElement;
import no.ssb.avro.convert.core.SchemaAwareElement;
import no.ssb.avro.convert.core.SchemaBuddy;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class XmlToRecords implements AutoCloseable, Iterable<GenericRecord> {

    private final RecursiveXmlParser recursiveXmlParser;
    private final SchemaBuddy schemaBuddy;
    private Callback callBack;

    public XmlToRecords(String fileName, String topElement, Schema schema) throws XMLStreamException, FileNotFoundException {
        this(new FileInputStream(fileName), topElement, schema);
    }

    public XmlToRecords(InputStream inputStream, String topElement, Schema schema) throws XMLStreamException {
        this.recursiveXmlParser = new RecursiveXmlParser(inputStream, topElement);
        this.schemaBuddy = SchemaBuddy.parse(schema);
    }

    public XmlToRecords withCallBack(Callback callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    public void close() throws XMLStreamException, IOException {
        recursiveXmlParser.close();
    }

    @Override
    public Iterator<GenericRecord> iterator() {
        Iterator<DataElement> dataElementIterator = recursiveXmlParser.iterator();

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return dataElementIterator.hasNext();
            }

            @Override
            public GenericRecord next() {
                DataElement dataElement = dataElementIterator.next();
                if(callBack != null) {
                    callBack.onElement(dataElement);
                }
                return SchemaAwareElement.toRecord(dataElement, schemaBuddy);
            }
        };
    }

    public interface Callback {
        void onElement(DataElement dataElement);
    }
}
