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
public class rootHomeServlet extends HttpServlet {

	private Connection connection;
	private Statement statement;
	// private ResultSetTableModel tableModel;
	private ResultSetToHTMLFormatterClass table;
	private int sqlReturnedValue;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String command = request.getParameter("sqlCommand");
		String message_root = "";
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

				message_root = table.getHtmlRows(result);

			} else if (queryArray[0].equals("insert") || queryArray[0].equals("delete")
					|| queryArray[0].equals("update") || queryArray[0].equals("drop") || queryArray[0].equals("create")
					|| queryArray[0].equals("replace")) {

				statement.executeUpdate("drop table if exists beforeShipments");
				statement.executeUpdate("create table beforeShipments like shipments");
				statement.executeUpdate("insert into beforeShipments select * from shipments");

				int result = statement.executeUpdate(command);

				if (result >= 0) {

					int resultBussLogic = statement
							.executeUpdate("update suppliers set status = status + 5 where suppliers.snum in "
									+ "(select distinct snum from shipments where shipments.quantity >=100 and not exists "
									+ "(select * from beforeShipments" + " where shipments.snum = beforeShipments.snum"
									+ " and shipments.pnum = beforeShipments.pnum"
									+ " and shipments.jnum = beforeShipments.jnum"
									+ " and beforeshipments.quantity >= 100));");

					statement.executeUpdate(" drop table beforeShipments;");

					if (resultBussLogic > 0) {
						message_root = "<tr><td id = \"succes\"><center>The Statement Executed Successfully: " + result
								+ " row(s) affected" + "<br></br>Business Logic Detected! - Updating Supplier Status"
								+ "<br></br>Busines Logic updated " + resultBussLogic
								+ " supplier status marks.</td></center></tr>";
					} else {

						message_root = "<tr><td id = \"succes\"><center>The Statement Executed Successfully: " + result
								+ " row(s) affected" + "<br></br>Business Logic Not Triggered!</td></center></tr>";
					}
				}

			} else if (command.equals("")) {

				message_root = "<tr><td id = \"error\"><center>!!Please enter a command in the box above!!</td></center></tr>";

			}

			statement.close();
		} catch (SQLException e) {
			message_root = "<tr><td id = \"error\"><center>Error Executing the SQL Statement: " + e.getMessage()
					+ "</td></center></tr>";
		}
		HttpSession session = request.getSession();
		session.setAttribute("message_root", message_root);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/rootHome.jsp");
		dispatcher.forward(request, response);

	}

	public void getDBConnection() {

		Properties properties = new Properties();
		FileInputStream filein = null;
		MysqlDataSource dataSource = null;
		// read a properties file
		try {
			filein = new FileInputStream("webapps/Project-4/WEB-INF/lib/root.properties");
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
