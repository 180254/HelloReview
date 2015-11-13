package pl.p.lodz.iis.hr.utils;

import java.io.IOException;

@FunctionalInterface
public interface GitHubExecutorFI {

    void execute() throws IOException;
}
