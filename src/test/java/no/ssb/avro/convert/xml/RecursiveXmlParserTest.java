package no.ssb.avro.convert.xml;


import no.ssb.avro.convert.core.DataElement;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecursiveXmlParserTest {

    @Test
    void testCdCatalog() {
        InputStream inputStream = TestUtils.catalogXml();
        DataElement root = new DataElement("root");
        try (RecursiveXmlParser dataElements = new RecursiveXmlParser(inputStream, "CATALOG")) {
            dataElements.forEach(root::addChild);
        } catch (XMLStreamException | IOException e) {
            throw new TestAbortedException(e.getMessage());
        }

        String expected = "root value:null\n" +
                " |-- CATALOG value:null\n" +
                " |    |-- CD value:null\n" +
                " |    |    |-- TITLE value:Empire Burlesque\n" +
                " |    |    |-- ARTIST value:Bob Dylan\n" +
                " |    |    |-- COUNTRY value:USA\n" +
                " |    |    |-- COMPANY value:Columbia\n" +
                " |    |    |-- PRICE value:10.90\n" +
                " |    |    |-- YEAR value:1985\n" +
                " |    |-- CD value:null\n" +
                " |    |    |-- TITLE value:Hide your heart\n" +
                " |    |    |-- ARTIST value:Bonnie Tyler\n" +
                " |    |    |-- COUNTRY value:UK\n" +
                " |    |    |-- COMPANY value:CBS Records\n" +
                " |    |    |-- PRICE value:9.90\n" +
                " |    |    |-- YEAR value:1988\n";

        assertThat(expected).isEqualTo(root.toString(true));
    }

    @Test
    void shouldThrowNoSuchElementException() {
        InputStream inputStream = TestUtils.catalogXml();
        try (RecursiveXmlParser dataElements = new RecursiveXmlParser(inputStream, "CD")) {
            Iterator<DataElement> iterator = dataElements.iterator();

            assertThat(iterator.hasNext()).isTrue();
            DataElement element1 = iterator.next();
            assertThat(element1.findChildByName("ARTIST").getValue()).isEqualTo("Bob Dylan");

            assertThat(iterator.hasNext()).isTrue();
            DataElement element2 = iterator.next();
            assertThat(element2.findChildByName("ARTIST").getValue()).isEqualTo("Bonnie Tyler");

            assertThat(iterator.hasNext()).isFalse();

            assertThatThrownBy(iterator::next)
                    .isInstanceOf(NoSuchElementException.class);

        } catch (XMLStreamException | IOException e) {
            throw new TestAbortedException(e.getMessage());
        }
    }
}
