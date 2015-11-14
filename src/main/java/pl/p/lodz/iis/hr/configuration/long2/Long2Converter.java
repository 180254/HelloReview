package pl.p.lodz.iis.hr.configuration.long2;

import org.springframework.core.convert.converter.Converter;

public class Long2Converter implements Converter<String, Long2> {

    @Override
    public Long2 convert(String source) {
        return new Long2(source);
    }
}
