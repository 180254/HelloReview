package pl.p.lodz.iis.hr.utils;

import java.io.IOException;

@FunctionalInterface
public interface GHIRunner1<T> {

    T execute() throws IOException;
}
