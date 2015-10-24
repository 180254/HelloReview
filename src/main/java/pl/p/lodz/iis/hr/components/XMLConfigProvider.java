package pl.p.lodz.iis.hr.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;
import pl.p.lodz.iis.hr.exceptions.UnableToInitializeException;
import pl.p.lodz.iis.hr.xmlconfig.XMLConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class XMLConfigProvider {

    private static final String XMlConfigFilename = "HelloReviewConfig.xml";
    private XMLConfig XMLConfig;

    public XMLConfigProvider() throws UnableToInitializeException {
        reload();
    }

    public XMLConfig getXMLConfig() {
        return XMLConfig;
    }

    public void reload() throws UnableToInitializeException {
        String content = readXMLConfigFromFile();
        XMLConfig = parseXMLConfig(content);
    }

    private String readXMLConfigFromFile() throws UnableToInitializeException {
        try {
            Path configPath = Paths.get(XMlConfigFilename);
            byte[] configBytes = Files.readAllBytes(configPath);
            return new String(configBytes);

        } catch (IOException e) {
            throw new UnableToInitializeException(XMLConfigProvider.class,
                    "Error during processing xml config: file incorrect format!", e);
        }
    }

    private XMLConfig parseXMLConfig(String content) throws UnableToInitializeException {
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
