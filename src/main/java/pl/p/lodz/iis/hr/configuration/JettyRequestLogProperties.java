package pl.p.lodz.iis.hr.configuration;


import org.apache.commons.lang3.LocaleUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Properties used in org.eclipse.jetty.server.NCSARequestLog configuration.
 *
 * @see JettyRequestsLogCustomizer
 * @see org.eclipse.jetty.server.NCSARequestLog
 */
@ConfigurationProperties("jetty.req.log")
public class JettyRequestLogProperties {

    private String filename;
    private String filenameDateFormat;
    private boolean extended;
    private boolean append;
    private boolean logCookies;
    private boolean logLatency;
    private boolean logServer;
    private List<String> ignorePaths = new ArrayList<>(0);
    private String locale;
    private String logTimeZone;
    private int retainDays;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilenameDateFormat() {
        return filenameDateFormat;
    }

    public void setFilenameDateFormat(String filenameDateFormat) {
        this.filenameDateFormat = filenameDateFormat;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public boolean isLogCookies() {
        return logCookies;
    }

    public void setLogCookies(boolean logCookies) {
        this.logCookies = logCookies;
    }

    public boolean isLogLatency() {
        return logLatency;
    }

    public void setLogLatency(boolean logLatency) {
        this.logLatency = logLatency;
    }

    public boolean isLogServer() {
        return logServer;
    }

    public void setLogServer(boolean logServer) {
        this.logServer = logServer;
    }

    public List<String> getIgnorePaths() {
        return ignorePaths;
    }

    public String[] getIgnorePathsAsArray() {
        return ignorePaths.toArray(new String[ignorePaths.size()]);
    }

    public void setIgnorePaths(List<String> ignorePaths) {
        this.ignorePaths = ignorePaths;
    }

    public String getLocale() {
        return locale;
    }

    public Locale getLocaleObject() {
        return LocaleUtils.toLocale(locale);
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLogTimeZone() {
        return logTimeZone;
    }

    public void setLogTimeZone(String logTimeZone) {
        this.logTimeZone = logTimeZone;
    }

    public int getRetainDays() {
        return retainDays;
    }

    public void setRetainDays(int retainDays) {
        this.retainDays = retainDays;
    }
}
