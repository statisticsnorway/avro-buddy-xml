package no.ssb.avro.convert.xml;

import no.ssb.avro.convert.core.DataElement;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RecursiveXmlParser implements AutoCloseable, Iterable<DataElement> {
    private final InputStream stream;
    private final XMLEventReader reader;
    private final String topElement;

    public RecursiveXmlParser(String fileName, String topElement) throws XMLStreamException, FileNotFoundException {
        this(new FileInputStream(fileName), topElement);
    }

    public RecursiveXmlParser(InputStream inputStream, String topElement) throws XMLStreamException {
        this.topElement = topElement;

        stream = inputStream;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        reader = factory.createXMLEventReader(stream, "utf-8");
    }

    @Override
    public void close() throws XMLStreamException, IOException {
        reader.close();
        stream.close();
    }

    @Override
    public Iterator<DataElement> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                while (reader.hasNext()) {
                    final XMLEvent event = getXmlEvent();
                    if (event.isStartElement()) {
                        StartElement startElement = event.asStartElement();
                        String elementName = startElement.getName().getLocalPart();
                        if (elementName.equals(topElement)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public DataElement next() {
                if (!reader.hasNext()) {
                    throw new NoSuchElementException();
                }
                try {
                    return new RecursiveElement(reader, topElement).parse();
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }

            private XMLEvent getXmlEvent() {
                try {
                    return reader.nextEvent();
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private static class RecursiveElement {
        private final XMLEventReader reader;
        private final DataElement dataElement;

        private RecursiveElement(XMLEventReader reader, String topStartElement) {
            this.reader = reader;
            this.dataElement = new DataElement(topStartElement, null);
        }

        private DataElement parse() throws XMLStreamException {
            parseInternal(dataElement);
            return dataElement;
        }

        private void parseInternal(DataElement parentDataElement) throws XMLStreamException {
            while (reader.hasNext()) {
                final XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();
                    DataElement childDataElement = new DataElement(elementName);
                    parentDataElement.addChild(childDataElement);
                    parseInternal(childDataElement);
                } else if (event.isCharacters()) {
                    Characters characters = event.asCharacters();
                    if (characters.isWhiteSpace()) {
                        continue;
                    }
                    parentDataElement.setValue(characters.getData());
                } else if (event.isEndElement()) {
                    break;
                }
            }
        }

        @Override
        public String toString() {
            return dataElement.toString();
        }
    }
}
