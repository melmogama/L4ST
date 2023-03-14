import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class L4STMetaDataImpl implements L4STMetaData{

    private final String TABLE_NAME;
    private final String SCHEMA_NAME;
    private final int NUMBER_OF_COLUMNS;
    private final ArrayList<String> COLUMNS;
    private final ArrayList<String> PRIMARY_KEY_COLUMNS;
    private final int NUMBER_OF_PRIMARY_KEY_COLUMNS;
    private final LinkedHashMap<String, Map.Entry<String, String>> FOREIGN_KEY_COLUMNS;
    private final LinkedHashMap<String, Class> COLUMN_TYPES;

    L4STMetaDataImpl(final Connection connection) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();
        this.SCHEMA_NAME = this.setSchemaName();
        this.TABLE_NAME = this.setTableName();
        this.NUMBER_OF_COLUMNS = this.setNumberOfColumns(metaData);
        this.COLUMNS = this.setColumns(metaData);
        this.NUMBER_OF_PRIMARY_KEY_COLUMNS = this.setNumberOfPrimaryKeyColumns(metaData);
        this.PRIMARY_KEY_COLUMNS = this.setPrimaryKeysColumns(metaData);
        this.FOREIGN_KEY_COLUMNS = this.setForeignKeys(metaData);
        this.COLUMN_TYPES = this.setColumnTypes(metaData);
    }

    public String getSchemaName(){
        return this.SCHEMA_NAME;
    }
    public String getTableName() {
        return this.TABLE_NAME;
    }
    public int getNumberOfColumns() {
        return this.NUMBER_OF_COLUMNS;
    }
    public ArrayList<String> getColumns() {
        return this.COLUMNS;
    }
    public ArrayList<String> getPrimaryKeyColumns() {
        return this.PRIMARY_KEY_COLUMNS;
    }
    public int getNumberOfPrimaryKeyColumns() {
        return this.NUMBER_OF_PRIMARY_KEY_COLUMNS;
    }
    public LinkedHashMap<String, Map.Entry<String, String>> getForeignKeyColumns() {
        return this.FOREIGN_KEY_COLUMNS;
    }
    public LinkedHashMap<String, Class> getColumnTypes() {
        return this.COLUMN_TYPES;
    }

    private String setSchemaName() throws Exception {
        String[] schemaName = this.getClass().getSimpleName().split("_");
        if((schemaName.length == 2) && (!schemaName[0].isBlank())) {
            return schemaName[0];
        }
        else if ((schemaName.length == 1)) {
            return null;
        }
        throw new Exception("The naming convention of class " + this.getClass().getName() + " is improper. " +
                "Please follow the naming convention of {schema_name}_{table_name}.java, otherwise the schema and table names will not be recognized." +
                "Improper class naming conventions include, but are not limited to, _{schema_name}, {table_name}_{schema_name}, and {table_name}__" +
                "Proper class naming conventions are limited to {schema_name}_{table_name}, {table_name}_, and _{table_name}. Please use one of the following");
    }
    private String setTableName() throws Exception {
        String[] tableName = this.getClass().getSimpleName().split("_");
        if((tableName.length == 2) && (!tableName[1].isBlank())) {
            return tableName[1];
        }
        else if ((tableName.length == 1) && (!tableName[0].isBlank())) {
            return tableName[0];
        }
        throw new Exception("The naming convention of class " + this.getClass().getName() + " is improper. " +
                "Please follow the naming convention of {schema_name}_{table_name}.java, otherwise the schema and table names will not be recognized." +
                "Improper class naming conventions include, but are not limited to, _{schema_name}, {table_name}_{schema_name}, and {table_name}__" +
                "Proper class naming conventions are limited to {schema_name}_{table_name}, and _{table_name}. Please use one of the following");
    }
    private int setNumberOfColumns(DatabaseMetaData metaData) throws SQLException {
        try(ResultSet columns = metaData.getColumns(null, this.SCHEMA_NAME,  this.TABLE_NAME, null)) {
            if (columns != null) {
                columns.last();
            }
            return columns.getRow();
        }
        catch (Exception e) {
            throw e;
        }
    }
    private ArrayList<String> setColumns(DatabaseMetaData metaData) throws SQLException {
        try(ResultSet columns = metaData.getColumns(null, this.SCHEMA_NAME, this.TABLE_NAME, null)){
        if (columns == null || !columns.isBeforeFirst()) {
            //TODO throw a custom exception
        }
        ArrayList<String> columnsList = new ArrayList<>(this.NUMBER_OF_COLUMNS);
        while (columns.next()) {
            columnsList.add(columns.getString("COLUMN_NAME"));
        }
        return columnsList;
        }
        catch (Exception e){
            throw e;
        }
    }
    private int setNumberOfPrimaryKeyColumns(DatabaseMetaData metaData) throws SQLException {
        try(ResultSet primaryKeyColumns = metaData.getPrimaryKeys(null, this.SCHEMA_NAME, this.TABLE_NAME)) {
            if (primaryKeyColumns != null) {
                primaryKeyColumns.last();
            }
            return primaryKeyColumns.getRow();
        }
        catch (Exception e) {
            throw e;
        }
    }
    private ArrayList<String> setPrimaryKeysColumns(DatabaseMetaData metaData) throws SQLException {
        try(ResultSet primaryKeys = metaData.getPrimaryKeys(null, this.SCHEMA_NAME, this.TABLE_NAME)) {
            if (primaryKeys == null || !primaryKeys.isBeforeFirst()) {
                //TODO throw a custom exception
            }
            ArrayList<String> primaryKeyColumnsList = new ArrayList<>(this.NUMBER_OF_PRIMARY_KEY_COLUMNS);
            while (primaryKeys.next()) {
                primaryKeyColumnsList.add(primaryKeys.getString("COLUMN_NAME"));
            }
            return primaryKeyColumnsList;
        }
        catch (Exception e){
            throw e;
        }
    }
    private LinkedHashMap<String, Map.Entry<String, String>> setForeignKeys(DatabaseMetaData metaData) throws SQLException {
        try(ResultSet importedKeys = metaData.getImportedKeys(null, this.SCHEMA_NAME, this.TABLE_NAME)) {
            if (importedKeys == null || !importedKeys.isBeforeFirst()) {
                //TODO throw a custom exception
            }
            LinkedHashMap<String, Map.Entry<String, String>> foreignKeys = new LinkedHashMap<>();
            while (importedKeys.next()) {
                foreignKeys.put(importedKeys.getString("FKCOLUMN_NAME"), new AbstractMap.SimpleEntry<>(
                        importedKeys.getString("PKTABLE_NAME"), importedKeys.getString("PKCOLUMN_NAME")));
            }
            return foreignKeys;
        }
    }
    private LinkedHashMap<String, Class> setColumnTypes(DatabaseMetaData metaData) throws SQLException {
        try(ResultSet columns = metaData.getColumns(null, this.SCHEMA_NAME, this.TABLE_NAME, null)) {
            if (columns == null || !columns.isBeforeFirst()) {
                //TODO throw a custom exception
            }
            LinkedHashMap<String, Class> columnTypes = new LinkedHashMap<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int columnType = columns.getInt("DATA_TYPE");
                columnTypes.put(columnName, convertSqlTypeToJavaType(columnType));
            }
            return columnTypes;
        }
        catch (Exception e) {
            throw e;
        }
    }
    private static Class convertSqlTypeToJavaType(int typeNumber){

        ArrayList<Integer> ShortTypes = new ArrayList<>() {
            {
                add(Types.SMALLINT);
            }
        };
        ArrayList<Integer> IntegerTypes = new ArrayList<>() {
            {
                add(Types.INTEGER);
            }
        };
        ArrayList<Integer> LongTypes = new ArrayList<>() {
            {
                add(Types.BIGINT);
            }
        };
        ArrayList<Integer> BigDecimalTypes = new ArrayList<>() {
            {
                add(Types.DECIMAL);
                add(Types.NUMERIC);
            }
        };
        ArrayList<Integer> FloatTypes = new ArrayList<>() {
            {
                add(Types.FLOAT);
                add(Types.REAL);
            }
        };
        ArrayList<Integer> DoubleTypes = new ArrayList<>() {
            {
                add(Types.DOUBLE);
            }
        };
        ArrayList<Integer> StringTypes = new ArrayList<>() {
            {
                add(Types.CHAR);
                add(Types.VARCHAR);
                add(Types.LONGVARCHAR);
                add(Types.NCHAR);
                add(Types.NVARCHAR);
                add(Types.LONGNVARCHAR);

            }
        };
        ArrayList<Integer> ByteArrayTypes = new ArrayList<>() {
            {
                add(Types.BINARY);
                add(Types.VARBINARY);
                add(Types.LONGVARBINARY);
            }
        };
        ArrayList<Integer> BooleanTypes = new ArrayList<>() {
            {
                add(Types.BOOLEAN);
            }
        };
        ArrayList<Integer> DateTypes = new ArrayList<>() {
            {
                add(Types.DATE);
            }
        };
        ArrayList<Integer> TimeTypes = new ArrayList<>() {
            {
                add(Types.TIME);
            }
        };
        ArrayList<Integer> TimestampTypes = new ArrayList<>() {
            {
                add(Types.TIMESTAMP);
            }
        };
        ArrayList<Integer> ClobTypes = new ArrayList<>() {
            {
                add(Types.CLOB);
            }
        };
        ArrayList<Integer> BlobTypes = new ArrayList<>() {
            {
                add(Types.BLOB);
            }
        };
        ArrayList<Integer> ArrayTypes = new ArrayList<>() {
            {
                add(Types.ARRAY);
            }
        };
        ArrayList<Integer> StructTypes = new ArrayList<>() {
            {
                add(Types.STRUCT);
            }
        };
        ArrayList<Integer> ObjectTypes = new ArrayList<>() {
            {
                add(Types.JAVA_OBJECT);
            }
        };
        ArrayList<Integer> URLTypes = new ArrayList<>() {
            {
                add(Types.DATALINK);
            }
        };

        if (ShortTypes.contains(typeNumber)) {
            return Short.class;
        } else if (IntegerTypes.contains(typeNumber)){
            return Integer.class;
        } else if (LongTypes.contains(typeNumber)) {
            return Long.class;
        } else if (BigDecimalTypes.contains(typeNumber)) {
            return BigDecimal.class;
        } else if (FloatTypes.contains(typeNumber)) {
            return Float.class;
        } else if (DoubleTypes.contains(typeNumber)) {
            return Double.class;
        } else if (StringTypes.contains(typeNumber)) {
            return String.class;
        } else if (ByteArrayTypes.contains(typeNumber)) {
            return Byte[].class;
        } else if (BooleanTypes.contains(typeNumber)) {
            return Boolean.class;
        } else if (DateTypes.contains(typeNumber)) {
            return Date.class;
        } else if (TimeTypes.contains(typeNumber)) {
            return Time.class;
        } else if (TimestampTypes.contains(typeNumber)) {
            return Timestamp.class;
        } else if (ClobTypes.contains(typeNumber)) {
            return Clob.class;
        } else if (BlobTypes.contains(typeNumber)) {
            return Blob.class;
        } else if (ArrayTypes.contains(typeNumber)) {
            return Array.class;
        } else if (StructTypes.contains(typeNumber)) {
            return Struct.class;
        } else if (ObjectTypes.contains(typeNumber)) {
            return Object.class;
        } else if (URLTypes.contains(typeNumber)) {
            return URL.class;
        } else {
            // TODO Throw custom exception
            return null;
        }
    }
}