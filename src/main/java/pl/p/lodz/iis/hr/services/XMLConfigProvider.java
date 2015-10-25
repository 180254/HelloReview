package pl.p.lodz.iis.hr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jetbrains.annotations.NonNls;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.UnableToInitializeException;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class XMLConfigProvider {

    @NonNls
    private static final String XML_CONFIG_FILENAME = "HelloReviewConfig.xml";
    private final XMLConfig xmlConfig;

    public XMLConfigProvider() {
        String content = readXMLConfigContentFromFile();
        xmlConfig = parseXMLConfig(content);
    }

    public XMLConfig getXMLConfig() {
        return xmlConfig;
    }

    private String readXMLConfigContentFromFile() {
        try {
            Path configPath = Paths.get(XML_CONFIG_FILENAME);
            byte[] configBytes = Files.readAllBytes(configPath);
            return new String(configBytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new UnableToInitializeException(XMLConfigProvider.class,
                    "Error during processing xml config: file incorrect format!", e);
        }
    }

    private XMLConfig parseXMLConfig(String content) {
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);

        XmlMapper xmlMapper = new XmlMapper(module);
        ObjectReader reader = xmlMapper.readerFor(XMLConfig.class);

        try {
            return reader.<XMLConfig>readValue(content);

        } catch (JsonProcessingException e) {
            throw new UnableToInitializeException(XMLConfigProvider.class,
                    "Error during processing xml config: Config file has incorrect format!", e);

        } catch (IOException e) {
            throw new UnableToInitializeException(XMLConfigProvider.class,
                    "Error during processing xml config: Other exception occurred!", e);
        }
    }
}
