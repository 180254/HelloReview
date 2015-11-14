package pl.p.lodz.iis.hr.utils;

import java.io.IOException;

@FunctionalInterface
public interface GitHubExecutorFI<T> {

    T execute() throws IOException;
}
