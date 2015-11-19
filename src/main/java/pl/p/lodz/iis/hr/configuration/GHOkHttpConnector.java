package pl.p.lodz.iis.hr.configuration;

import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Improved OkHttpConnector for GitHub api connecting purposes.<br/>
 * - Set User-Agent header as requested by GitHub.<br/>
 * - Explicitly set Accept header as encouraged by GitHub.<br/>
 *
 * - Set Cache-control as "max-age=0, must-revalidate" to bypass header sent by gihub api,
 *   which tells "private, max-age=60, s-maxage=60" - so client should not sent request,
 *   even if with "if-none-match" header, just use cache.
 *
 *   OkHttpClient now will use my cache-control directive - will sent request every time,
 *   but still will ask if resource is modified, and use cache if not.
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
        connect.setRequestProperty("Cache-control", "max-age=0, must-revalidate");
        
        return connect;
    }
}
