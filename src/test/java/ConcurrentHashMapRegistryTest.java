import issuetracker.sqlService.ConcurrentHashMapSqlRegistry;
import issuetracker.sqlService.updatable.EmbeddedDbSqlRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import issuetracker.sqlService.UpdatableSqlRegistry;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public abstract class AbstractUpdatableSqlRegistryTest {
    UpdatableSqlRegistry sqlRegistry;
    abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();

    protected void checkFind(String expected1, String expected2, String expected3){

    }
    @Before
    public void setUp(){
        sqlRegistry = new ConcurrentHashMapSqlRegistry();
        sqlRegistry.registerSql("KEY1","SQL1");
        sqlRegistry.registerSql("KEY2","SQL2");
        sqlRegistry.registerSql("KEY3","SQL3");
    }

    @Test
    public void find(){
        checkFindResult("SQL1","SQL2","SQL3");
    }
    private void checkFindResult(String expected1, String expected2, String expected3){
        assertThat(sqlRegistry.findSql("KEY1"),is(expected1));
        assertThat(sqlRegistry.findSql("KEY2"),is(expected2));
        assertThat(sqlRegistry.findSql("KEY3"),is(expected3));
    }
    @Test(expected = SqlNotFoundException.class)
    public void unknownKey(){
        sqlRegistry.findSql("UNKNOWN");
    }
    @Test
    public void updateSingle(){
        sqlRegistry.updateSql("KEY2","Modified2");
        checkFindResult("SQL1","Modified2","SQL3");
    }
    @Test
    public void updateMulti(){
        Map<String, String> sqlMap = new HashMap<>();
        sqlMap.put("KEY1","Modified1");
        sqlMap.put("KEY3","Modified3");
        sqlRegistry.updateSql(sqlMap);
        checkFindResult("Modified1","SQL2","Modified3");
    }
    @Test(expected = SqlUpdateFailureException.class)
    public void updateFailure(){
        sqlRegistry.updateSql("KEY1","SQL1");
        sqlRegistry.updateSql("KEY1","SQL1");
    }
}
public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
    EmbeddedDatabase db;
    protected UpdatableSqlRegistry createUpdatableSqlRegistry(){
        db=new EmbeddedDatabaseBuilder()
                .setType(HSQL)
                .addScript(sqlRegistrySchema.sql)
                .build();
        EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
        embeddedDbSqlRegistry.setDataSource(db);
        return embeddedDbSqlRegistry;
    }
    @After
    public void tearDown(){
        db.shutdown();
    }
}