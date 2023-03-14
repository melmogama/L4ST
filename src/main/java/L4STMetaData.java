import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public interface L4STMetaData {
    String getTableName();
    String getSchemaName();
    int getNumberOfColumns();
    ArrayList<String> getColumns();
    ArrayList<String> getPrimaryKeyColumns();
    int getNumberOfPrimaryKeyColumns();
    LinkedHashMap<String, Map.Entry<String, String>> getForeignKeyColumns();
    LinkedHashMap<String, Class> getColumnTypes();
}
