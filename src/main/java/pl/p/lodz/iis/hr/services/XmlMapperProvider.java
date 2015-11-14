package pl.p.lodz.iis.hr.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;

@Service
public class XmlMapperProvider {

    private final XmlMapper xmlMapper;

    XmlMapperProvider() {
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);

        xmlMapper = new XmlMapper(module);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    /*
        it cannot be bean, as XmlMapper extends ObjectMapper.
        already have bean with type ObjectMapper
     */
    public XmlMapper getXmlMapper() {
        return xmlMapper;
    }
}
