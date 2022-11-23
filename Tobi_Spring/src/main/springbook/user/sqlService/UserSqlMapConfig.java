package main.springbook.user.sqlService;

import user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class UserSqlMapConfig implements SqlMapConfig {
    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("learningtest/proxy/sqlmap.xml", UserDao.class);
    }
}
@Configuration
public class SqlServiceContext{
    @Autowired SqlMapConfig sqlMapConfig;

    @Bean
    public SqlService sqlService(){
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        sqlService.setSqlmap(this.sqlMapConfig.getSqlMapResource());
        return sqlService;
    }
    @Bean
    public SqlMapConfig sqlMapConfig(){
        return new UserSqlMapConfig();
    }

}