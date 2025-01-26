import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.io.ObjectOutputStream.PutField;
import java.lang.invoke.StringConcatFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.table.TableModel;

import com.mysql.cj.jdbc.MysqlDataSource;

@SuppressWarnings("serial")
public class suppliersInsertServlet extends HttpServlet {

	private Connection connection;
	private Statement statement;
	// private ResultSetTableModel tableModel;
	private ResultSetToHTMLFormatterClass table;
	private int sqlReturnedValue;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String message = "";

		if (request.getParameter("status").equals("")) {
			message = "<tr><td id = \"error\"><center>!!Please fill all fields for the corresponding record insert!!</center></td></tr>";
		} else {
			String snum = request.getParameter("snum");
			String sname = request.getParameter("sname");
			Integer status = Integer.parseInt(request.getParameter("status"));
			String city = request.getParameter("city");

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			try {

				getDBConnection();

				PreparedStatement pstmt;

				pstmt = connection.prepareStatement("insert into suppliers values (?, ?, ?, ?)");
				pstmt.setString(1, snum);
				pstmt.setString(2, sname);
				pstmt.setInt(3, status);
				pstmt.setString(4, city);

				int result = pstmt.executeUpdate();

				if (result >= 0) {
					message = "<tr><td id = \"succes\"><center>New suppliers record: "
							+ "(" + snum + ", " + sname + ", " + status + ", " + city + ") - "
							+ "succesfully entered into database.</td></center></tr>";
				}

				pstmt.close();

			} catch (SQLException e) {
				message = "<tr><td id = \"error\"><center>Error Executing the SQL Statement: " + e.getMessage()
						+ "</td></center></tr>";
			}
		}
		HttpSession session = request.getSession();
		session.setAttribute("message", message);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataEntryHome.jsp");
		dispatcher.forward(request, response);

	}

	public void getDBConnection() {

		Properties properties = new Properties();
		FileInputStream filein = null;
		MysqlDataSource dataSource = null;
		// read a properties file
		try {
			filein = new FileInputStream("webapps/Project-4/WEB-INF/lib/dataentryuser.properties");
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
