package pl.p.lodz.iis.hr.configuration;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * @see Hibernate5Config
 */
public class Hibernate5PhyNamStrategy implements PhysicalNamingStrategy {

    private final Converter<String, String> caseConverter
            = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    private Identifier convert(Identifier identifier) {
        if ((identifier == null) || StringUtils.isBlank(identifier.getText())) {
            return identifier;
        }

        String newName = caseConverter.convert(identifier.getText());
        return Identifier.toIdentifier(newName);
    }
}
