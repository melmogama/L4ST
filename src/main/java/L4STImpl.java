import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

abstract class L4STImpl extends L4STMetaDataImpl implements L4ST {
    final private Connection CONNECTION;
    private String query;
    private final ArrayList<String> parametersToBeSet = new ArrayList<>();
    private final HashMap<String, Object> entityFieldsFoundInTable = new HashMap<>();
    PreparedStatement preparedStatement;
    private ResultSet records;
    private boolean allowNullValues = false;

    protected L4STImpl(final Connection connection) throws Exception {
        super(connection);
        this.CONNECTION = connection;
    }
    protected L4STImpl(final Connection connection, boolean allowNullValues) throws Exception {
        super(connection);
        this.CONNECTION = connection;
        this.allowNullValues = allowNullValues;
    }

    public boolean create(L4STEntity entity) throws SQLException {
        this.resetFields(entity);
        this.query = String.format(QueryStrings.INSERT_INTO, super.getTableName(), this.insertIntoColumns(), this.insertIntoParameters());
        this.createPreparedStatement();
        return this.executePreparedStatement();
    }
    public boolean select(L4STEntity entity) throws SQLException {
        this.resetFields(entity);
        this.query = String.format(QueryStrings.SELECT, "*", super.getTableName(), this.primaryKeyConditions());
        this.createPreparedStatement();
        return this.executePreparedStatement();
    }
    public boolean select(L4STEntity entity, String columns) throws SQLException {
        this.resetFields(entity);
        this.query = String.format(QueryStrings.SELECT, columns, super.getTableName(), this.primaryKeyConditions());
        this.createPreparedStatement();
        return this.executePreparedStatement();
    }
    public boolean update(L4STEntity entity) throws SQLException {
        this.resetFields(entity);
        this.query = String.format(QueryStrings.UPDATE, super.getTableName(), this.updateColumnValues(), this.primaryKeyConditions());
        this.createPreparedStatement();
        return this.executePreparedStatement();
    }
    public boolean delete(L4STEntity entity) throws SQLException {
        this.resetFields(entity);
        this.query = String.format(QueryStrings.DELETE, super.getTableName(), this.primaryKeyConditions());
        this.createPreparedStatement();
        return this.executePreparedStatement();
    }

    public ResultSet getRecords() {
        return this.records;
    }
    public void allowNullValues() {
        this.allowNullValues = true;
    }

    private String insertIntoColumns() {
        StringBuilder columns = new StringBuilder();
        for(int columnsIndex = 0; columnsIndex < super.getNumberOfNonGeneratedColumns(); columnsIndex++) {
            columns.append(super.getNonGeneratedColumns().get(columnsIndex)).append(",");
            this.parametersToBeSet.add(super.getNonGeneratedColumns().get(columnsIndex));
        }
        if(columns.length() > 0) {
            columns.deleteCharAt(columns.length() - 1);
        }
        return String.valueOf(columns);
    }
    private String insertIntoParameters() {
        StringBuilder parameters = new StringBuilder();
        parameters.append("?,".repeat(this.parametersToBeSet.size()));
        if(parameters.length() > 0) {
            parameters.deleteCharAt(parameters.length() - 1);
        }
        return String.valueOf(parameters);
    }
    private String primaryKeyConditions() throws SQLException {
        StringBuilder primaryKeyWhereClause = new StringBuilder();
        for(int columnsIndex = 0; columnsIndex < super.getNumberOfPrimaryKeyColumns(); columnsIndex++) {
            if(columnExistsInEntityAndIsNotNull(super.getPrimaryKeyColumns().get(columnsIndex))) {
                if(columnsIndex != 0) {
                    primaryKeyWhereClause.append(" AND ");
                }
                primaryKeyWhereClause.append(super.getPrimaryKeyColumns().get(columnsIndex)).append(" = ?");
                this.parametersToBeSet.add(super.getPrimaryKeyColumns().get(columnsIndex));
            }
        }
        if(primaryKeyWhereClause.isEmpty()) {
            throw new SQLException("The entity you used as an input does not specify a WHERE clause, " +
                                   "which could lead to unintended database manipulations.");
        }
        return String.valueOf(primaryKeyWhereClause);
    }
    private String updateColumnValues() throws SQLException {
        StringBuilder columnEqualsValuesClause = new StringBuilder();
        for(int columnIndex = 0; columnIndex < super.getNumberOfNonGeneratedColumns(); columnIndex++) {
            if(columnExistsInEntityAndIsNotNull(super.getNonGeneratedColumns().get(columnIndex))) {
                if (columnIndex != 0) {
                    columnEqualsValuesClause.append(",");
                }
                columnEqualsValuesClause.append(super.getNonGeneratedColumns().get(columnIndex)).append(" = ?");
                this.parametersToBeSet.add(super.getNonGeneratedColumns().get(columnIndex));
            }
        }
        if(columnEqualsValuesClause.isEmpty()) {
            throw new SQLException("The entity you used as an input does not specify any columns/ fields to update.");
        }
        return String.valueOf(columnEqualsValuesClause);
    }
    private boolean columnExistsInEntityAndIsNotNull(String tableColumn) {
        return (this.entityFieldsFoundInTable.containsKey(tableColumn)) && (this.allowNullValues || (this.entityFieldsFoundInTable.get(tableColumn) != null));
    }

    private void createPreparedStatement() throws SQLException {
        this.preparedStatement = CONNECTION.prepareStatement(this.query);
        this.setParameters();
    }
    private void setParameters() throws SQLException {
        for(int parameterIndex = 0; parameterIndex < this.parametersToBeSet.size(); parameterIndex++) {
            try {
                Object parameter = this.entityFieldsFoundInTable.get(this.parametersToBeSet.get(parameterIndex));
                if(parameter.getClass().equals(super.getColumnTypes().get(this.parametersToBeSet.get(parameterIndex)))) {
                    this.preparedStatement.setObject(parameterIndex + 1, parameter);
                    continue;
                }
                this.preparedStatement.setObject(parameterIndex + 1, null);
            }
            catch(Exception e) {
                this.preparedStatement.setObject(parameterIndex + 1, null);
            }
        }
    }
    private boolean executePreparedStatement() throws SQLException {
        if(this.preparedStatement.execute()) {
            this.records = this.preparedStatement.getResultSet();
            return true;
        }
        return false;
    }

    private void resetFields(L4STEntity entity) {
        this.query = "";
        this.parametersToBeSet.clear();
        this.entityFieldsFoundInTable.clear();
        Field[] tempFieldsArray = entity.getClass().getDeclaredFields();
        for(Field tempField : tempFieldsArray) {
            if (super.getAllColumns().contains(tempField.getName())) {
                try {
                    tempField.setAccessible(true);
                    this.entityFieldsFoundInTable.put(tempField.getName(), tempField.get(entity));
                } catch (Exception ignored) {}
            }
        }
        try {
            if (this.preparedStatement != null){
                this.preparedStatement.clearParameters();
                this.preparedStatement.close();
            }
            if (this.records != null) {
                this.records.close();
            }
        }
        catch(Exception ignored) {}
    }


    public static class QueryStrings {
        private static final String INSERT_INTO = "INSERT INTO %s (%s) VALUES (%s)";
        private static final String SELECT = "SELECT %s FROM %s WHERE %s";
        private static final String UPDATE = "UPDATE %s SET %s WHERE %s";
        private static final String DELETE = "DELETE FROM %s WHERE %s";
    }
}