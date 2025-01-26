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
public class shipmentsInsertServlet extends HttpServlet {

	private Connection connection, connection_root;
	private Statement statement, statement_root;
	// private ResultSetTableModel tableModel;
	private ResultSetToHTMLFormatterClass table;
	private int sqlReturnedValue;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String message = "";

		if (request.getParameter("quantity").equals("")) {
			message = "<tr><td id = \"error\"><center>!!Please fill all fields for the corresponding record insert!!</center></td></tr>";
		} else {
			String snum = request.getParameter("snum_ship");
			String pnum = request.getParameter("pnum_ship");
			Integer quantity = Integer.parseInt(request.getParameter("quantity"));
			String jnum = request.getParameter("jnum_ship");

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			try {

				getDBConnection();
				getDBConnection_Root();

				PreparedStatement pstmt;

				pstmt = connection.prepareStatement("insert into shipments values (?, ?, ?, ?)");
				pstmt.setString(1, snum);
				pstmt.setString(2, pnum);
				pstmt.setString(3, jnum);
				pstmt.setInt(4, quantity);

				if (quantity >= 100) {
					statement_root.executeUpdate("drop table if exists beforeShipments");
					statement_root.executeUpdate("create table beforeShipments like shipments");
					statement_root.executeUpdate("insert into beforeShipments select * from shipments");
				}

				int result = pstmt.executeUpdate();

				if (result >= 0) {

					if (quantity >= 100) {
						
						statement_root.executeUpdate("update suppliers set status = status + 5 where suppliers.snum in "
								+ "(select distinct snum from shipments where shipments.quantity >=100 and not exists "
								+ "(select * from beforeShipments"
								+ " where shipments.snum = beforeShipments.snum"
								+ " and shipments.pnum = beforeShipments.pnum"
								+ " and shipments.jnum = beforeShipments.jnum"
								+ " and beforeshipments.quantity >= 100));");
						statement_root.executeUpdate(" drop table beforeShipments;");
						
						message = "<tr><td id = \"succes\"><center>New shipments record: " + "(" + snum + ", " + pnum
								+ ", " + jnum + ", " + quantity + ") - "
								+ "succesfully entered into database. <br></br>Bussiness logic triggered.</td></center></tr>";

					} else {
						message = "<tr><td id = \"succes\"><center>New shipments record: " + "(" + snum + ", " + pnum
								+ ", " + jnum + ", " + quantity + ") - "
								+ "succesfully entered into database. <br></br>Bussiness logic not triggered.</td></center></tr>";
					}
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
	
	public void getDBConnection_Root() {

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
			connection_root = dataSource.getConnection();
			statement_root = connection_root.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
