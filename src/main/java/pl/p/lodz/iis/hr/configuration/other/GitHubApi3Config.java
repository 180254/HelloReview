package pl.p.lodz.iis.hr.configuration.other;

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
import pl.p.lodz.iis.hr.configuration.appconfig.AppConfig;
import pl.p.lodz.iis.hr.configuration.appconfig.GitHubDummy;
import pl.p.lodz.iis.hr.exceptions.UnableToInitializeException;

import java.io.IOException;

@Configuration
@DependsOn("appConfig")
class GitHubApi3Config {

    public static final double CACHE_SIZE_MB = 10.0;

    @Autowired private AppConfig appConfig;

    @Bean(name = "okHttpConnector")
    public HttpConnector okHttpConnector() {
        String appName = appConfig.getGitHubConfig().getApplication().getAppName();
        String cacheDir = appConfig.getGeneralConfig().getCacheDir();

        OkHttpClient okHttpClient = new OkHttpClient();

//        Cache cache = new Cache(new File(cacheDir), Math.round(ByteUnit.MIB.toBytes(CACHE_SIZE_MB)));
//        okHttpClient.setCache(cache);

        OkUrlFactory okUrlFactory = new OkUrlFactory(okHttpClient);

        return new OkHttpConnector2(okUrlFactory, appName);
    }

    @Bean(name = "gitHubFail")
    public GitHub gitHubFail() {
        try {

            GitHubDummy dummy = appConfig.getGitHubConfig().getDummy();
            return new GitHubBuilder()
                    .withConnector(okHttpConnector())
                    .withRateLimitHandler(RateLimitHandler.FAIL)
                    .withOAuthToken(dummy.getToken())
                    .build();

        } catch (IOException e) {

            throw new UnableToInitializeException(GitHubApi3Config.class,
                    "Unable to initialize GitHub api client.", e);
        }
    }

    @Bean(name = "gitHubWait")
    public GitHub gitHubWait() {
        try {

            GitHubDummy dummy = appConfig.getGitHubConfig().getDummy();
            return new GitHubBuilder()
                    .withConnector(okHttpConnector())
                    .withRateLimitHandler(RateLimitHandler.WAIT)
                    .withOAuthToken(dummy.getToken())
                    .build();

        } catch (IOException e) {

            throw new UnableToInitializeException(GitHubApi3Config.class,
                    "Unable to initialize GitHub api client.", e);
        }
    }


    @Bean
    public CredentialsProvider jGitCredentials() {
        GitHubDummy dummy = appConfig.getGitHubConfig().getDummy();
        return new UsernamePasswordCredentialsProvider(dummy.getToken(), "");
    }
}
