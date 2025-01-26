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
public class accountantHomeServlet extends HttpServlet {

	private Connection connection;
	private Statement statement;
	// private ResultSetTableModel tableModel;
	private ResultSetToHTMLFormatterClass table;
	private int sqlReturnedValue;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String selectedValue = request.getParameter("radioList");
		String message_acc = "";
		String command = "";
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {

			getDBConnection();

			if (selectedValue == null) {
				message_acc = "<tr><td id = \"error\"><center>!!Please make a selection from the list above!!</td></center></tr>";
			} else {

				if (selectedValue.equals("1")) {
					command = "call Get_The_Maximum_Status_Of_All_Suppliers";
				} else if (selectedValue.equals("2")) {
					command = "call Get_The_Sum_Of_All_Parts_Weights";
				} else if (selectedValue.equals("3")) {
					command = "call Get_The_Total_Number_Of_Shipments";
				} else if (selectedValue.equals("4")) {
					command = "call Get_The_Name_Of_The_Job_With_The_Most_Workers";
				} else if (selectedValue.equals("5")) {
					command = "call List_The_Name_And_Status_Of_All_Suppliers";
				}
			}

				ResultSet result = statement.executeQuery(command);
				table = new ResultSetToHTMLFormatterClass();

				message_acc = table.getHtmlRows(result);

				statement.close();
			
		} catch (SQLException e) {
			message_acc = "<tr><td id = \"error\"><center>Error Executing the SQL Statement: " + e.getMessage()
					+ " Please make a selection from the list above.</td></center></tr>";
		}
		HttpSession session = request.getSession();
		session.setAttribute("message_acc", message_acc);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/accountantHome.jsp");
		dispatcher.forward(request, response);

	}

	public void getDBConnection() {

		Properties properties = new Properties();
		FileInputStream filein = null;
		MysqlDataSource dataSource = null;
		// read a properties file
		try {
			filein = new FileInputStream("webapps/Project-4/WEB-INF/lib/accountant.properties");
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
