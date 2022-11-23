package test;

import main.springbook.user.dao.UserDao;
import main.springbook.user.sqlService.OxmSqlService;
import main.springbook.user.sqlService.SqlRegistry;
import main.springbook.user.sqlService.SqlService;
import main.springbook.user.sqlService.updatable.EmbeddedDbSqlRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@Configuration
public class SqlServiceContext {
    @Bean
    public SqlService sqlService(){
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        sqlService.setSqlmap(new ClassPathResource("learningtest/proxy/sqlmap.xml", UserDao.class));
        return sqlService;
    }
    @Bean
    public SqlRegistry sqlRegistry(){
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDatabase(this.embeddedDatabase);
        return sqlRegistry;
    }
    @Bean
    public Unmarshaller unmarshaller(){
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("issuetracker.sqlService.jaxb");
        return marshaller;
    }
    @Bean
    public DataSource embeddedDatabase(){
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(HSQL)
                .addScript("sqlRegistrySchema.sql")
                .build();
    }

}
