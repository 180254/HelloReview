package pl.p.lodz.iis.hr.configuration.other;

import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class OkHttpConnector2 extends OkHttpConnector {

    private final String userAgent;

    public OkHttpConnector2(OkUrlFactory urlFactory, String userAgent) {
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
