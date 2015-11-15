package pl.p.lodz.iis.hr.configuration.other;

import com.barney4j.utils.unit.ByteUnit;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
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

import java.io.File;
import java.io.IOException;

@Configuration
@DependsOn("appConfig")
class GitHubConfiguration {

    public static final double CACHE_SIZE_MB = 10.0;

    @Autowired private AppConfig appConfig;

    @Bean
    public GitHub gitHub() {
        try {
            String cacheDir = appConfig.getGeneralConfig().getCacheDir();
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

            throw new UnableToInitializeException(GitHubConfiguration.class,
                    "Unable to initialize GitHub api client.", e);
        }
    }

    @Bean
    public CredentialsProvider jGitCredentials() {
        GitHubDummy dummy = appConfig.getGitHubConfig().getDummy();
        return new UsernamePasswordCredentialsProvider(dummy.getUsername(), dummy.getPassword());
    }
}
