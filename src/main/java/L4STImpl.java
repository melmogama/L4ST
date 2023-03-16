import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;

abstract class L4STImpl extends L4STMetaDataImpl implements L4ST {

    final private Connection CONNECTION;
    private String query = "";
    PreparedStatement preparedStatement;

    private int[] parameters;
    final private LinkedHashMap<String, Object> queryParameters = new LinkedHashMap<>();
    final private LinkedHashMap<String, Object> record = new LinkedHashMap<>();
    final private LinkedHashMap<String, ArrayList<Object>> records = new LinkedHashMap<>();

    protected L4STImpl (final Connection connection) throws Exception {
        super(connection);
        this.CONNECTION = connection;
    }

    public void create() {
        this.query += "INSERT INTO " + super.getTableName() + " " + this.columnsToSet() + " VALUES " + this.parametersToSet();
    }

    public void setParameters(L4STDomainObject domainObject) {

    }

    private String columnsToSet() {
        StringBuilder valueColumns = new StringBuilder("(");
        for(int i = 0; i < super.getNumberOfNonGeneratedColumns(); i++) {
            valueColumns.append(super.getNonGeneratedColumns().get(i)).append(",");
        }
        valueColumns.deleteCharAt(valueColumns.length() - 1);
        valueColumns.append(")");
        return String.valueOf(valueColumns);
    }
    private String parametersToSet() {
        StringBuilder parameterString = new StringBuilder("(");
        parameterString.append("?,".repeat(Math.max(0, super.getNumberOfNonGeneratedColumns())));
        parameterString.deleteCharAt(parameterString.length() - 1);
        parameterString.append(")");
        return String.valueOf(parameterString);
    }
}