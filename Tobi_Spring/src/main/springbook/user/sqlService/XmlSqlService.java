package main.springbook.user.sqlService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {
    private final Map<String, String> sqlMap = new HashMap<>();
    public String findSql(String key) throws SqlNotFoundException{
        String sql = sqlMap.get(key);
        if(sql == null) throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        else
            return sql;
    }
    public void registerSql(String key, String sql){
        sqlMap.put(key, sql);
    }


    @PostConstruct
    public void loadSql(){
        this.sqlReader.read(this.sqlRegistry);
    }
}
