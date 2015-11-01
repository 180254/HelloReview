package pl.p.lodz.iis.hr.configuration.appconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jetbrains.annotations.NonNls;
import pl.p.lodz.iis.hr.exceptions.UnableToInitializeException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class AppConfigParser {

    @NonNls
    private static final String APP_CONFIG_FILENAME = "HelloReviewConfig.xml";
    private final AppConfig appConfig;

    AppConfigParser() {
        String content = readAppConfigContentFromFile();
        appConfig = parseAppConfig(content);
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    private String readAppConfigContentFromFile() {
        try {
            Path configPath = Paths.get(APP_CONFIG_FILENAME);
            byte[] configBytes = Files.readAllBytes(configPath);
            return new String(configBytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new UnableToInitializeException(AppConfigParser.class,
                    "Error during processing xml config: file incorrect format!", e);
        }
    }

    private AppConfig parseAppConfig(String content) {
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);

        XmlMapper xmlMapper = new XmlMapper(module);
        ObjectReader reader = xmlMapper.readerFor(AppConfig.class);

        try {
            return reader.<AppConfig>readValue(content);

        } catch (JsonProcessingException e) {
            throw new UnableToInitializeException(AppConfigParser.class,
                    "Error during processing xml config: Config file has incorrect format!", e);

        } catch (IOException e) {
            throw new UnableToInitializeException(AppConfigParser.class,
                    "Error during processing xml config: Other exception occurred!", e);
        }
    }
}
