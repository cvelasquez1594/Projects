import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.io.ObjectOutputStream.PutField;
import java.lang.invoke.StringConcatFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.table.TableModel;

import com.mysql.cj.jdbc.MysqlDataSource;

@SuppressWarnings("serial")
public class clientHomeServlet extends HttpServlet {

	private Connection connection;
	private Statement statement;
	// private ResultSetTableModel tableModel;
	private ResultSetToHTMLFormatterClass table;
	private int sqlReturnedValue;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String command = request.getParameter("sqlCommand");
		String message_client = "";
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {

			getDBConnection();

			String my_pattern = "\\s";
			Pattern pattern = Pattern.compile(my_pattern);
			String[] queryArray = pattern.split(command);

			if (queryArray[0].equals("select")) {

				ResultSet result = statement.executeQuery(command);
				table = new ResultSetToHTMLFormatterClass();

				message_client = table.getHtmlRows(result);

			} else if (queryArray[0].equals("insert") || queryArray[0].equals("delete")
					|| queryArray[0].equals("update") || queryArray[0].equals("drop") || queryArray[0].equals("create")
					|| queryArray[0].equals("replace")) {

				int result = statement.executeUpdate(command);

			} else if (command.equals("")) {
				
				message_client = "<tr><td id = \"error\"><center>!!Please enter a command in the box above!!</td></center></tr>";
				
			}

			statement.close();
		} catch (SQLException e) {
			message_client = "<tr><td id = \"error\"><center>Error Executing the SQL Statement: " + e.getMessage() + "</td></center></tr>";
		}
		HttpSession session = request.getSession();
		session.setAttribute("message_client", message_client);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
		dispatcher.forward(request, response);

	}

	public void getDBConnection() {

		Properties properties = new Properties();
		FileInputStream filein = null;
		MysqlDataSource dataSource = null;
		// read a properties file
		try {
			filein = new FileInputStream("webapps/Project-4/WEB-INF/lib/client.properties");
			properties.load(filein);
			dataSource = new MysqlDataSource();
			dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
			dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
			dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
			connection = dataSource.getConnection();
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
