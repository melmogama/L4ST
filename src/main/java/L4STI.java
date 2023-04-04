import java.sql.ResultSet;
import java.sql.SQLException;

public interface L4STI {

    /**
     * Create a record in the corresponding table,
     * mapping fields from the entity to the query
     * parameters.
     * <br><br>
     * @param entity L4STEntity extended object with columns as fields
     * @return boolean indicating whether command executed or not
     * @throws SQLException
     */
    public boolean create(L4STEntity entity) throws SQLException;

    /**
     * Select all columns from a record from the
     * corresponding table, mapping fields from
     * the entity to the query parameters.
     * <br><br>
     * @param entity L4STEntity extended object with columns as fields
     * @return boolean indicating whether command executed or not
     * @throws SQLException
     */
    public boolean select(L4STEntity entity) throws SQLException;

    /**
     * Select columns, specified by {@code columns}, from
     * a record from the corresponding table, mapping fields
     * from the entity to the query parameters.
     * <br><br>
     * @param entity L4STEntity extended object with columns as fields
     * @param columns comma seperated columns to select from table
     * @return boolean indicating whether command executed or not
     * @throws SQLException
     */
    public boolean select(L4STEntity entity, String columns) throws SQLException;

    /**
     * Update all columns of a record from the
     * corresponding table, mapping fields from
     * the entity to the query parameters.
     * <br><br>
     * @param entity L4STEntity extended object with columns as fields
     * @return boolean indicating whether command executed or not
     * @throws SQLException
     */
    public boolean update(L4STEntity entity) throws SQLException;

    /**
     * Delete a record from the corresponding table,
     * mapping fields from the entity to the query parameters.
     * <br><br>
     * @param entity L4STEntity extended object with columns as fields
     * @return boolean indicating whether command executed or not
     * @throws SQLException
     */
    public boolean delete(L4STEntity entity) throws SQLException;

    /**
     * Gets an object holding the table's metadata
     * <br><br>
     * @return L4STMetaData object
     */
    public L4STMetaData getMetaData();

    /**
     * Get the record(s) from the previously executed query
     * as a ResultSet
     * <br><br>
     * @return ResultSet of the record(S) retrieved from previous query
     */
    public ResultSet getRecords();

    /**
     * Sets {@code allowNullValues} to {@code true}
     */
    public void allowNullValues();

}
