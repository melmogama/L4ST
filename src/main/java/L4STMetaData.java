import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public interface L4STMetaData {
    String getTableName();
    String getSchemaName();
    int getNumberOfAllColumns();
    ArrayList<String> getAllColumns();
    ArrayList<String> getPrimaryKeyColumns();
    int getNumberOfPrimaryKeyColumns();
    ArrayList<String> getForeignKeyColumns();
    int getNumberOfForeignKeyColumns();
    LinkedHashMap<String, Map.Entry<String, String>> getForeignReferences();
    public int getNumberOfForeignReferences();
    public ArrayList<String> getNonGeneratedColumns();
    public int getNumberOfNonGeneratedColumns();
    LinkedHashMap<String, Class> getColumnTypes();
}
