package pl.p.lodz.iis.hr.utils;

import java.io.IOException;

@FunctionalInterface
public interface IRunnerGH1<T> {

    T execute() throws IOException;
}
