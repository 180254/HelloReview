package pl.p.lodz.iis.hr.configuration;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
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

/**
 * Configuration of beans that are used to communicate with GitHub.<br/>
 * <br/>
 * First used lib is org.kohsuke.github - to use GitHub rest API v3.<br/>
 * Second used lib is org.eclipse.jgit - to do general git operations (pull, checkcout, commit, push, etc).<br/>
 * <br/>
 * Additionally OkHttp is used, as proposed by org.kohsuke.github documentation, to cache github api responses.<br/>
 * "304 response does not count against your Rate Limit," - as mentioned in GH api documentation.<br/>
 * More info about rate limit: https://developer.github.com/v3/#rate-limiting
 */
@Configuration
@DependsOn("appConfig")
class GHApi3Config {

    public static final double CACHE_SIZE_MB = 10.0;

    @Autowired private AppConfig appConfig;

    @Bean(name = "okHttpCache")
    public Cache cache() {
        String cacheDir = appConfig.getGeneralConfig().getCacheDir();

        File cacheDirFile = new File(cacheDir);
        long cacheSizeBytes = Math.round(ByteUnit.MIB.toBytes(CACHE_SIZE_MB));

        return new Cache(cacheDirFile, cacheSizeBytes);
    }

    @Bean(name = "httpConnector")
    public HttpConnectorWithCache httpConnector() {
        String appName = appConfig.getGitHubConfig().getApplication().getAppName();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(cache());

        OkUrlFactory okUrlFactory = new OkUrlFactory(okHttpClient);
        return new GHOkHttpConnector(okUrlFactory, appName);
    }

    public GitHub gitHub(RateLimitHandler rateLimitHandler) {
        GHDummy dummy = appConfig.getGitHubConfig().getDummy();

        try {
            return new GitHubBuilder()
                    .withConnector(httpConnector())
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

    /**
     * GitHub bean, which gives GitHub API access, with FAIL as RateLimitHandler.<br/>
     * FAIL means "Fail immediately.", and throw error.
     *
     * @return GitHub, with FAIL as as RateLimitHandler.
     */
    @Bean(name = "ghFail")
    public GitHub gitHubFail() {
        return gitHub(RateLimitHandler.FAIL);
    }

    /**
     * GitHub bean, which gives GitHub API access, with WAIT as RateLimitHandler.<br/>
     * FAIL means "Block until the API rate limit is reset. Useful for long-running batch processing.",<br/>
     *
     * @return GitHub, with WAIT as as RateLimitHandler.
     */
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
