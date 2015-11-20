package pl.p.lodz.iis.hr.configuration;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpConnector;
import org.kohsuke.github.RateLimitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.appconfig.GHDummy;
import pl.p.lodz.iis.hr.exceptions.UnableToInitializeException;
import pl.p.lodz.iis.hr.utils.ByteUnit;

import java.io.File;
import java.io.IOException;

@Configuration
@DependsOn("appConfig")
class GHApi3Config {

    public static final double CACHE_SIZE_MB = 10.0;

    @Autowired private AppConfig appConfig;

    @Bean(name = "okHttpConnector")
    public HttpConnector okHttpConnector() {
        String appName = appConfig.getGitHubConfig().getApplication().getAppName();
        String cacheDir = appConfig.getGeneralConfig().getCacheDir();

        File cacheDirFile = new File(cacheDir);
        long cacheSizeBytes = Math.round(ByteUnit.MIB.toBytes(CACHE_SIZE_MB));

        Cache cache = new Cache(cacheDirFile, cacheSizeBytes);
        OkHttpClient okHttpClient = new OkHttpClient().setCache(cache);
        OkUrlFactory okUrlFactory = new OkUrlFactory(okHttpClient);
        return new GHOkHttpConnector(okUrlFactory, appName);
    }

    public GitHub gitHub(RateLimitHandler rateLimitHandler) {
        GHDummy dummy = appConfig.getGitHubConfig().getDummy();

        try {
            return new GitHubBuilder()
                    .withConnector(okHttpConnector())
                    .withRateLimitHandler(rateLimitHandler)
                    .withOAuthToken(dummy.getToken())
                    .build();
        } catch (IOException e) {
            throw new UnableToInitializeException(
                    GHApi3Config.class,
                    "Unable to initialize GitHub api fail client.", e
            );
        }
    }


    @Bean(name = "ghFail")
    public GitHub gitHubFail() {
        return gitHub(RateLimitHandler.FAIL);
    }

    @Bean(name = "ghWait")
    public GitHub gitHubWait() {
        return gitHub(RateLimitHandler.WAIT);
    }

    @Bean
    public CredentialsProvider jGitCredentials() {
        GHDummy ghDummy = appConfig.getGitHubConfig().getDummy();
        return new UsernamePasswordCredentialsProvider(ghDummy.getToken(), "");
    }
}
