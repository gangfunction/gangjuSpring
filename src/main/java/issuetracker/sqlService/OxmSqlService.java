package issuetracker.sqlService;

import dao.UserDao;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService{
    public void setSqlmap(Resource sqlmap){
        this.oxmSqlReader.setSqlmap(sqlmap);
    }
    private final BaseSqlService baseSqlService= new BaseSqlService();
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        oxmSqlReader.setUnmarshaller(unmarshaller);
    }
    public void setSqlmapFile(String sqlmapFile) {
        oxmSqlReader.setSqlmapFile(sqlmapFile);
    }
    @PostConstruct
    public void loadSql(){
        this.baseSqlService.setSqlReader(this.oxmSqlReader);
        this.baseSqlService.setSqlRegistry(this.sqlRegistry);

        this.baseSqlService.loadSql();
    }
    public String getSql(String key) throws SqlRetrievalFailureException{
        return this.baseSqlService.getSql(key);
    }
    private static class OxmlSqlReader implements SqlReader{
        private final Resource sqlmap= new ClassPathResource("sqlmap.xml",UserDao.class);
        private Unmarshaller unmarshaller;
        private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
        private String sqlmapFile = DEFAULT_SQLMAP_FILE;
        public void setUnmarshaller(Unmarshaller unmarshaller){
            this.unmarshaller = unmarshaller;
        }
        public void setSqlmapFile(String sqlmapFile){
            this.sqlmapFile = sqlmapFile;
        }
        public void read(SqlRegistry sqlRegistry){
            try{
                Source source = new StreamSource(sqlmap.getInputStream());
                Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(source);
                for(SqlType sql : sqlmap.getSql()){
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            }catch(IOException e){
                throw new IllegalArgumentException(this.sqlmapFile.getFilename() + "을 읽을 수 없습니다.",e);
            }
        }
    }
}
