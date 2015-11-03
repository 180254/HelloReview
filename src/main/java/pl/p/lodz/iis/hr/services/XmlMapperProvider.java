package pl.p.lodz.iis.hr.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;

@Service
public class XmlMapperProvider {

    private final XmlMapper xmlMapper;

    public XmlMapperProvider() {
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);

        xmlMapper = new XmlMapper(module);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

//        xmlMapper.enableDefaultTyping();
//        xmlMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public XmlMapper getXmlMapper() {
        return xmlMapper;
    }
}
