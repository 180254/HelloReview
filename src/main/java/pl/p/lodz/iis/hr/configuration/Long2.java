package pl.p.lodz.iis.hr.configuration;

/**
 * Fix, server should server 400 instead of 500, if parameter was not added to request.<br/>
 * <br/>
 * If such statement was used:<br/>
 * RequestParam("xx") long id<br/>
 * and such parameter doesn't exist,<br/>
 * server will throw code 500.<br/>
 * <br/>
 * If own type is used:<br/>
 * RequestParam("xx") long id<br/>
 * and such parameter doesn't exist,<br/>
 * server will throw 'better; code 400.<br/>
 * <br/>
 * Problem descriptions:<br/>
 * http://stackoverflow.com/questions/27851775/spring-mvc-data-binding-primitive-types<br/>
 * http://stackoverflow.com/questions/23679564/spring-mvc-web-application-no-default-constructor-found<br/>
 * http://stackoverflow.com/questions/27851775/spring-mvc-data-binding-primitive-types<br/>
 * <br/>
 * Solution based on:<br/>
 * http://stackoverflow.com/questions/15005438/requestparam-custom-object<br/>
 * http://stackoverflow.com/questions/11273443/how-to-configure-spring-conversionservice-with-java-config<br/>
 */
public class Long2 {

    private final long value;

    public Long2() {
        value = 0L;
    }

    public Long2(long value) {
        this.value = value;
    }

    public Long2(String value) {
        this.value = Long.valueOf(value);
    }

    public long get() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}