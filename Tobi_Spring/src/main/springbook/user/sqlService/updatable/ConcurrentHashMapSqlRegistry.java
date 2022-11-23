package main.springbook.user.sqlService.updatable;

import main.springbook.user.sqlService.UpdatableSqlRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
    private final Map<String, String> sqlMap= new ConcurrentHashMap<>();
    public String findSql(String key) throws SqlNotFoundException{
        String sql = sqlMap.get(key);
        if(sql==null){
            throw new SqlNotFoundException(key);
        }
        else{
            return sql;
        }
    }
    public void registerSql(String key, String sql){
        sqlMap.put(key, sql);
    }
    public void updateSql(String key, String sql) throws SqlUpdateFailureException{
        if(sqlMap.get(key)==null){
            throw new SqlUpdateFailureException(key);
        }
        else{
            sqlMap.put(key, sql);
        }
    }
    public void updateSql(Map<String, String> sqlmal) throws SqlUpdateFailureException{
        for(Map.Entry<String, String> entry: sqlmal.entrySet()){
            updateSql(entry.getKey(), entry.getValue());
        }
    }
}
