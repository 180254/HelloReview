package pl.p.lodz.iis.hr.configuration.long2;

public class Long2 {

    private long value;

    public Long2() {
        value = 0L;
    }

    public Long2(long value) {
        this.value = value;
    }

    public Long2(String value) {
        try {
            this.value = Long.valueOf(value);
        } catch (NumberFormatException ignored) {
            this.value = 0L;
        }
    }

    public long get() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
