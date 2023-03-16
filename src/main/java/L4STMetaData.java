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
    public int getNumberOfForeignKeyColumns();
    public ArrayList<String> getNonGeneratedColumns();
    public int getNumberOfNonGeneratedColumns();
    LinkedHashMap<String, Class> getColumnTypes();
}
