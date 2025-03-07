package jig.erd;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ConditionalOnClass(name = {"javax.sql.DataSource", "org.springframework.web.bind.annotation.RequestMapping"})
@ConditionalOnProperty(value = "jig.erd.enabled", matchIfMissing = true)
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
