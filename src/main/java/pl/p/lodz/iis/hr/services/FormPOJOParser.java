package pl.p.lodz.iis.hr.services;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import pl.p.lodz.iis.hr.models.forms.fromxml.FormPOJO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

public class FormPOJOParser {
    public static void main(String[] args) throws IOException {
        String xml = "<form>\n" +
                "    <description><![CDATA[\n" +
                "        <b>Proszę</b>\n" +
                "        ocenić {projectname}.\n" +
                "        <br/>\n" +
                "        Deadline jest {deadline}.]]>\n" +
                "    </description>\n" +
                "\n" +
                "    <question>\n" +
                "        <questionText>Czy poprawnie sformatowano kod?</questionText>\n" +
                "        <extraText><![CDATA[Nunc ullamcorper odio non leo bibendum, vitae aliquet lectus varius.Praesent viverra leo\n" +
                "            tellus, in eleifend enim elementum eget. Nunc ac euismod elit.In ornare quam nisi, sed elementum risus\n" +
                "            dapibus quis. Vestibulum vel quam condimentum,elementum risus et, euismod arcu. Suspendisse egestas, mauris\n" +
                "            sed scelerisque blandit,libero quam sagittis justo, eget efficitur leo justo non eros. Phasellus massa quam,\n" +
                "            mattisnon est in, volutpat auctor lacus. Etiam risus risus, dignissim quis enim eget, placerat sagittis leo.\n" +
                "            Etiam egestas rutrum magnavitae tempus. Nullam at consectetur mauris. Suspendisse fringilla, nibh rhoncus\n" +
                "            dictum faucibus,lectus neque mattis libero, a scelerisque turpis..]]>\n" +
                "        </extraText>\n" +
                "\n" +
                "        <input type=\"scale\">\n" +
                "            <fromLabel>Źle</fromLabel>\n" +
                "            <from>0</from>\n" +
                "            <toLabel>Prawidłowo</toLabel>\n" +
                "            <to>10</to>\n" +
                "        </input>\n" +
                "\n" +
                "        <input type=\"text\">\n" +
                "            <label>Dodatkowy komentarz</label>\n" +
                "            <required>true</required>\n" +
                "        </input>\n" +
                "    </question>\n" +
                "</form>";

        System.out.println(xml);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);

        XmlMapper xmlMapper = new XmlMapper(module);
        ObjectReader reader = xmlMapper.readerFor(FormPOJO.class);

        FormPOJO o = (FormPOJO) reader.readValue(xml);
        System.out.println(o);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<FormPOJO>> validate = validator.validate(o);
        System.out.println("X");

    }
}
