package jig.erd;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ConditionalOnBean(DataSource.class)
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
public class JigErdAutoconfig {

    @Bean
    JigErdEndpoint jigErdEndpoint(JigErd jigErd) {
        return new JigErdEndpoint(jigErd);
    }

    @Bean
    JigErd jigErd(DataSource dataSource) {
        return new JigErd(dataSource::getConnection);
    }
}
