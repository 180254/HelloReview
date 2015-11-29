package pl.p.lodz.iis.hr.services;


import com.squareup.okhttp.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class GHTaskOkCacheClean implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GHTaskOkCacheClean.class);

    private final Cache okCache;

    GHTaskOkCacheClean(Cache okCache) {
        this.okCache = okCache;

        LOGGER.info("OkCache clean scheduled");

    }

    @Override
    public void run() {
        try {
            okCache.flush();
            okCache.evictAll();

            LOGGER.info("OkCache clean succeeded = {}", true);

        } catch (IOException e) {
            LOGGER.info("OkCache clean succeeded = {}", false);
            LOGGER.warn("OkCache clean failed", e);
        }
    }
}
