package pl.p.lodz.iis.hr.configuration.other;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpConnector;
import org.kohsuke.github.RateLimitHandler;
import org.kohsuke.github.extras.OkHttpConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.appconfig.GitHubDummy;
import pl.p.lodz.iis.hr.exceptions.UnableToInitializeException;
import pl.p.lodz.iis.hr.utils.ByteUnit;

import java.io.File;
import java.io.IOException;

@Configuration
@DependsOn("appConfig")
public class GitHubApiClientConfiguration {

    public static final double CACHE_SIZE_MB = 10.0;

    @Autowired private AppConfig appConfig;

    @Bean
    public GitHub gitHub() {
        try {
            String cacheDir = appConfig.getGeneral().getCacheDir();
            Cache cache = new Cache(new File(cacheDir), Math.round(ByteUnit.MIB.toBytes(CACHE_SIZE_MB)));

            OkHttpClient okHttpClient = new OkHttpClient().setCache(cache);
            HttpConnector okHttpConnector = new OkHttpConnector(new OkUrlFactory(okHttpClient));

            GitHubDummy dummy = appConfig.getGitHubConfig().getDummy();

            return new GitHubBuilder()
//                    .withConnector(okHttpConnector)
                    .withRateLimitHandler(RateLimitHandler.FAIL)
                    .withPassword(dummy.getUsername(), dummy.getPassword())
                    .build();

        } catch (IOException e) {

            throw new UnableToInitializeException(GitHubApiClientConfiguration.class,
                    "Unable to initialize GitHub api client.", e);
        }
    }
}
