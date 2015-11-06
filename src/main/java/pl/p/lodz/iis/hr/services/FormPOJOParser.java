package pl.p.lodz.iis.hr.services;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.models.forms.FormViews;

import java.io.IOException;

public class FormPOJOParser {
    public static void main(String[] args) throws IOException {
        String xml = "\n" +
                "<form>\n" +
                "  <description>Proszę ocenić {projectname}. &lt;b>Hehe&lt;/b>&lt;br/>Deadline jest {deadline}.</description>\n" +
                "  <question>\n" +
                "    <questionText>Czy poprawnie sformatowano kod?</questionText>\n" +
                "    <additionalTips/>\n" +
                "    <input type=\"scale\">\n" +
                "      <required>true</required>\n" +
                "      <label>Oceń w skali</label>\n" +
                "      <fromLabel>TAK</fromLabel>\n" +
                "      <fromS>0</fromS>\n" +
                "      <toLabel>NIE</toLabel>\n" +
                "      <toS>10</toS>\n" +
                "    </input>\n" +
                "    <input type=\"text\">\n" +
                "      <required>true</required>\n" +
                "      <label>Dodatkowy komentarz:</label>\n" +
                "    </input>\n" +
                "  </question>\n" +
                "  <question>\n" +
                "    <questionText>Ala ma kota</questionText>\n" +
                "    <additionalTips/>\n" +
                "    <input type=\"text\">\n" +
                "      <required>true</required>\n" +
                "      <label>Coś napisz:</label>\n" +
                "    </input>\n" +
                "    <input type=\"scale\">\n" +
                "      <required>true</required>\n" +
                "      <label>Oceń w skali</label>\n" +
                "      <fromLabel>Super</fromLabel>\n" +
                "      <fromS>0</fromS>\n" +
                "      <toLabel>Beznadziejnie</toLabel>\n" +
                "      <toS>10</toS>\n" +
                "    </input>\n" +
                "  </question>\n" +
                "  <question>\n" +
                "    <questionText>Czy poprawnie użyto banana?</questionText>\n" +
                "    <additionalTips/>\n" +
                "    <input type=\"text\">\n" +
                "      <required>true</required>\n" +
                "      <label>Dodatkowy komentarz:</label>\n" +
                "    </input>\n" +
                "    <input type=\"scale\">\n" +
                "      <required>true</required>\n" +
                "      <label>Oceń w skali</label>\n" +
                "      <fromLabel>TAK</fromLabel>\n" +
                "      <fromS>0</fromS>\n" +
                "      <toLabel>NIE</toLabel>\n" +
                "      <toS>10</toS>\n" +
                "    </input>\n" +
                "  </question>\n" +
                "</form>";

        XmlMapper xmlMapper = new XmlMapperProvider().getXmlMapper();
        ObjectReader objectReader = xmlMapper.readerWithView(FormViews.ParseXML.class).forType(Form.class);


        System.out.println(xmlMapper);

    }
}
