import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

abstract class L4STImpl extends L4STMetaDataImpl implements L4ST {

    final private Connection CONNECTION;
    private String query;
    PreparedStatement preparedStatement;

    private int[] parameters;
    final private LinkedHashMap<String, Object> queryParameters = new LinkedHashMap<>();
    final private LinkedHashMap<String, Object> record = new LinkedHashMap<>();
    final private LinkedHashMap<String, ArrayList<Object>> records = new LinkedHashMap<>();

    protected L4STImpl (final Connection connection) throws Exception {
        super(connection);
        this.CONNECTION = connection;
    }

}