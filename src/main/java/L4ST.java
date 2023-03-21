import java.sql.ResultSet;
import java.sql.SQLException;

public interface L4ST {

    public boolean create(L4STEntity entity) throws SQLException;
    public boolean select(L4STEntity entity) throws SQLException;
    public boolean select(L4STEntity entity, String columns) throws SQLException;
    public boolean update(L4STEntity entity) throws SQLException;
    public boolean delete(L4STEntity entity) throws SQLException;

    public ResultSet getRecords();
    public void allowNullValues();

}
