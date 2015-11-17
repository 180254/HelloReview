package pl.p.lodz.iis.hr.configuration;

import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Improved OkHttpConnector.<br/>
 * - Set User-Agent header as requested by GitHub.<br/>
 * - Explicitly set Accept header as encouraged by GitHub.<br/>
 */
class GHOkHttpConnector extends OkHttpConnector {

    private final String userAgent;

    GHOkHttpConnector(OkUrlFactory urlFactory, String userAgent) {
        super(urlFactory);
        this.userAgent = userAgent;
    }

    @Override
    public HttpURLConnection connect(URL url) throws IOException {
        HttpURLConnection connect = super.connect(url);
        connect.setRequestProperty("User-Agent", userAgent);
        connect.setRequestProperty("Accept", "application/vnd.github.v3+json");
        return connect;
    }
}
