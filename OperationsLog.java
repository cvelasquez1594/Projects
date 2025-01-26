import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

public class OperationsLog {

	private PreparedStatement pstmt;
	private Connection connection;
	private ResultSet result;

	public OperationsLog() throws SQLException {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("project3app");
		dataSource.setPassword("project3app");
		dataSource.setURL("jdbc:mysql://localhost:3306/operationslog");

		// establish a connection to the dataSource - i.e. the database
		connection = dataSource.getConnection();

	}

	public void insertUser(String username) throws SQLException {

		pstmt = connection.prepareStatement("insert ignore into operationscount values (?, 0, 0)");
		pstmt.setString(1, username);
		pstmt.executeUpdate();

	}

	public void numQueries(String username) throws SQLException {
		pstmt = connection.prepareStatement(
				"update operationscount " + "set num_queries = num_queries + 1 where login_username = ?");
		pstmt.setString(1, username);
		pstmt.executeUpdate();
	}

	public void numUpdates(String username) throws SQLException {
		pstmt = connection.prepareStatement(
				"update operationscount " + "set num_updates = num_updates + 1 where login_username = ?");
		pstmt.setString(1, username);
		pstmt.executeUpdate();
	}

}
