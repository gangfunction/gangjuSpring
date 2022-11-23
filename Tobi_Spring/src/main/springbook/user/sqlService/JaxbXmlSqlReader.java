package main.springbook.user.sqlService;

import java.io.InputStream;

public class JaxbXmlSqlReader implements SqlReader{

    private static final String DEFAULT_SQLMAP_FILE = "learningtest/proxy/sqlmap.xml";

    private String sqlmapFile = DEFAULT_SQLMAP_FILE;

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }
    public void read(SqlRegistry sqlRegistry) {
        String contextPath= Sqlmap.class.getPackage().getName();
        try{
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = getClass().getClassLoader().getResourceAsStream(sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
            for(SqlType sql : sqlmap.getSql()){
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }
        }catch(JAXBException e){
            throw new RuntimeException(e);
        }
    }
}
