package main.springbook.user.sqlService.updatable;

import main.springbook.user.sqlService.SqlRegistry;

import java.util.HashMap;
import java.util.Map;

public class HashMapSqlRegistry implements SqlRegistry {
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
}
