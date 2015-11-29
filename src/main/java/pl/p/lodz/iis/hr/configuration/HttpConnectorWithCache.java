package pl.p.lodz.iis.hr.configuration;

import org.kohsuke.github.HttpConnector;

/**
 * @see GHOkHttpConnector
 */
public interface HttpConnectorWithCache extends HttpConnector {

    boolean isCacheEnabled();

    void enableCache();

    void disableCache();
}
