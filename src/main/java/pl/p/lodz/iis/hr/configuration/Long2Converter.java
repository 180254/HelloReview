package pl.p.lodz.iis.hr.configuration;


import org.springframework.core.convert.converter.Converter;

class Long2Converter implements Converter<String, Long2> {

    @Override
    public Long2 convert(String source) {
        return new Long2(source);
    }
}
