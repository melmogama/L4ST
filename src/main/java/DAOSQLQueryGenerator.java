import java.util.Map;

public class DAOSQLQueryGenerator {

    /**CREATE Query Format: <u><b>INSERT INTO {table} ({column}, {column}, ...) VALUES (?, ?, ...)</b></u>*/
    private static final String CREATE_SQL = "INSERT INTO %s %s VALUES %s";

    /**READ Query Format: <u><b>SELECT * FROM {table} WHERE 1=1 AND {column} = ? AND ...</b></u>*/
    private static final String READ_SQL = "SELECT * FROM %s WHERE 1=1 %s";

    /**UPDATE Query Format: <u><b>UPDATE {table} SET {column} = ?, ... WHERE {primary key} = ?</b></u>*/
    private static final String UPDATE_SQL = "UPDATE %s SET %s WHERE %s";

    /**DELETE Query Format: <u><b>DELETE FROM {table} WHERE 1=1 AND {column} = ? AND ...</b></u>*/
    private static final String DELETE_SQL = "DELETE FROM %s WHERE 1=1 %s";


    public static String createCreateSQLQuery(Map<String, Object> parameters){
        String fromTable = (String) parameters.get("table");
        StringBuilder toColumns = new StringBuilder("(");
        StringBuilder setValues = new StringBuilder("(");

        String primaryKeyColumnName = parameters.keySet().toArray()[1].toString();

        for(Map.Entry<String, Object> parameter : ((Map<String, Object>)parameters.get("parameters")).entrySet()){
            toColumns.append(parameter.getKey()).append(",");
            setValues.append("?,");
        }
        if(!primaryKeyColumnName.equals("parameters")){
            toColumns.append(primaryKeyColumnName).append(",");
            setValues.append("?,");
        }

        toColumns = new StringBuilder(toColumns.substring(0, toColumns.length() - 1) + ")");
        setValues = new StringBuilder(setValues.substring(0, setValues.length() - 1) + ")");

        return String.format(CREATE_SQL, fromTable, toColumns, setValues);
    }

    public static String createReadSQLQuery(Map<String, Object> parameters){
        String fromTable = (String) parameters.get("table");
        StringBuilder where = new StringBuilder();

        String primaryKeyColumnName = parameters.keySet().toArray()[1].toString();

        for(Map.Entry<String, Object> parameter : ((Map<String, Object>) parameters.get("parameters")).entrySet()){
            where.append(" AND ").append(parameter.getKey()).append(" = ?");
        }
        if(!primaryKeyColumnName.equals("parameters")){
            where.append(" AND ").append(primaryKeyColumnName).append(" = ?");
        }

        return String.format(READ_SQL, fromTable, where);
    }

    public static String createUpdateSQLQuery(Map<String, Object> parameters){
        String fromTable = (String) parameters.get("table");
        StringBuilder setColumnValues = new StringBuilder();
        String where = "";

        String primaryKeyColumnName = parameters.keySet().toArray()[1].toString();

        for(Map.Entry<String, Object> parameter : ((Map<String, Object>) parameters.get("parameters")).entrySet()){
            setColumnValues.append(parameter.getKey()).append(" = ?,");
        }
        if(!primaryKeyColumnName.equals("parameters")){
            where += primaryKeyColumnName + " = ?";
        }

        setColumnValues = new StringBuilder(setColumnValues.substring(0, setColumnValues.length() - 1));

        return String.format(UPDATE_SQL, fromTable, setColumnValues, where);
    }

    public static String createDeleteSQLQuery(Map<String, Object> parameters){
        String fromTable = (String) parameters.get("table");
        StringBuilder where = new StringBuilder();

        String primaryKeyColumnName = parameters.keySet().toArray()[1].toString();

        for(Map.Entry<String, Object> parameter : ((Map<String, Object>) parameters.get("parameters")).entrySet()){
            where.append(" AND ").append(parameter.getKey()).append(" = ?");
        }
        if(!primaryKeyColumnName.equals("parameters")){
            where.append(" AND ").append(primaryKeyColumnName).append(" = ?");
        }

        return String.format(DELETE_SQL, fromTable, where);

    }

}
