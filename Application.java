
/*
Name: Cristhian Velasquez
Course: CNT 4714 Fall 2023
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: October 29, 2023
Class: Enterprise Computing
*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.awt.SystemColor;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;

public class Application extends JFrame {

	private ResultSetTableModel tableModel;

	// Declare instances for Event Handlers
	private ConnectDatabaseHandler procConnect;
	private UrlDatabaseHandler dropUrl;
	private UserDatabaseHandler dropUser;
	private ClearSqlCmd clearSqlCmd;

	// Declare labels, JTextFields, Buttons, JtextArea, drop down lists

	private JButton connectDB, clearSqlB, executeSql, clearResultWindowB;

	private JTextField usernameT;
	private JTextField connectionStatusT;
	private JPasswordField passwordT;
	private JTable resultTable;
	private JTextArea queryArea;
	public JScrollPane scrollPane;

	// Additional Variables
	private String database, user, userConnected;
	private boolean connectedToDatabase = false;
	private Connection connection;

	/**
	 * Create the frame.
	 */
	public Application() {
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 12));

		setTitle("SQL Client Application (Enterprise Computing - Fall 2023 - Project 3)");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		JLabel connectionDetails = new JLabel("Connection Details:");
		connectionDetails.setFont(new Font("Tahoma", Font.BOLD, 12));
		connectionDetails.setBounds(10, 11, 127, 15);
		getContentPane().add(connectionDetails);
		connectionDetails.setForeground(new Color(0, 0, 255));

		JLabel urllabel = new JLabel("DB URL Properties");
		urllabel.setBackground(Color.DARK_GRAY);
		urllabel.setBounds(10, 37, 113, 14);
		getContentPane().add(urllabel);

		JLabel lblNewLabel_1 = new JLabel("User Properties");
		lblNewLabel_1.setBounds(10, 62, 113, 14);
		getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Username");
		lblNewLabel_2.setBounds(10, 87, 113, 14);
		getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Password");
		lblNewLabel_3.setBounds(10, 112, 113, 14);
		getContentPane().add(lblNewLabel_3);

		String[] databases = { "project3.properties", "bikedb.properties" };
		JComboBox<String> urlDropB = new JComboBox<String>(databases);
		urlDropB.setBounds(133, 33, 187, 22);
		getContentPane().add(urlDropB);
		dropUrl = new UrlDatabaseHandler();
		urlDropB.addActionListener(dropUrl);
		database = urlDropB.getSelectedItem().toString();

		String[] users = { "root.properties", "client1.properties", "client2.properties" };
		JComboBox<String> userDropB = new JComboBox<String>(users);
		userDropB.setBounds(133, 58, 187, 22);
		getContentPane().add(userDropB);
		dropUser = new UserDatabaseHandler();
		userDropB.addActionListener(dropUser);
		user = userDropB.getSelectedItem().toString();

		usernameT = new JTextField();
		usernameT.setFont(new Font("Tahoma", Font.BOLD, 12));
		usernameT.setBounds(133, 84, 187, 20);
		getContentPane().add(usernameT);
		usernameT.setColumns(10);

		JLabel lblEnterAnSql = new JLabel("Enter An SQL Command:");
		lblEnterAnSql.setForeground(Color.BLUE);
		lblEnterAnSql.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEnterAnSql.setBounds(405, 11, 160, 15);
		getContentPane().add(lblEnterAnSql);

		queryArea = new JTextArea();
		queryArea.setLineWrap(true);
		queryArea.setFont(new Font("Tahoma", Font.BOLD, 12));
		queryArea.setWrapStyleWord(true);
		queryArea.setBounds(405, 32, 371, 93);
		getContentPane().add(queryArea);

		connectDB = new JButton("Connect to Database");
		connectDB.setForeground(Color.YELLOW);
		connectDB.setFont(new Font("Tahoma", Font.BOLD, 12));
		connectDB.setBackground(SystemColor.textHighlight);
		connectDB.setBounds(20, 149, 164, 23);
		getContentPane().add(connectDB);
		procConnect = new ConnectDatabaseHandler();
		connectDB.addActionListener(procConnect);

		connectionStatusT = new JTextField();
		connectionStatusT.setFont(new Font("Tahoma", Font.BOLD, 12));
		connectionStatusT.setText("NO CONNECTION NOW");
		connectionStatusT.setForeground(Color.RED);
		connectionStatusT.setBackground(SystemColor.desktop);
		connectionStatusT.setBounds(10, 183, 766, 30);
		getContentPane().add(connectionStatusT);
		connectionStatusT.setColumns(10);

		passwordT = new JPasswordField();
		passwordT.setFont(new Font("Tahoma", Font.BOLD, 12));
		passwordT.setBounds(133, 109, 187, 20);
		getContentPane().add(passwordT);

		JLabel lblSqlExecutionResult = new JLabel("SQL Execution Result Window");
		lblSqlExecutionResult.setForeground(Color.BLUE);
		lblSqlExecutionResult.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSqlExecutionResult.setBounds(10, 235, 199, 15);
		getContentPane().add(lblSqlExecutionResult);

		clearSqlB = new JButton("Clear SQL Command");
		clearSqlB.setForeground(Color.RED);
		clearSqlB.setFont(new Font("Tahoma", Font.BOLD, 12));
		clearSqlB.setBackground(Color.LIGHT_GRAY);
		clearSqlB.setBounds(405, 149, 160, 23);
		getContentPane().add(clearSqlB);
		clearSqlCmd = new ClearSqlCmd();
		clearSqlB.addActionListener(clearSqlCmd);

		executeSql = new JButton("Execute SQL Command");
		executeSql.setFont(new Font("Tahoma", Font.BOLD, 12));
		executeSql.setBackground(new Color(0, 255, 64));
		executeSql.setBounds(598, 148, 178, 23);
		getContentPane().add(executeSql);

		resultTable = new JTable();
		resultTable.setSurrendersFocusOnKeystroke(true);
		resultTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
		resultTable.setGridColor(Color.BLACK);

		scrollPane = new JScrollPane(resultTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 261, 766, 235);
		getContentPane().add(scrollPane);

		clearResultWindowB = new JButton("Clear Result Window");
		clearResultWindowB.setFont(new Font("Tahoma", Font.BOLD, 12));
		clearResultWindowB.setBackground(new Color(255, 255, 0));
		clearResultWindowB.setBounds(10, 517, 164, 23);
		getContentPane().add(clearResultWindowB);

		centerFrame(800, 600);

		// Event Listener for Execute SQL Command Button
		executeSql.addActionListener(

				new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						System.out.println("The Execute Sql button was pressed");

						if (connectedToDatabase == true) {

							String query = queryArea.getText();

							String my_pattern = "\\s";
							Pattern pattern = Pattern.compile(my_pattern);
							String[] queryArray = pattern.split(query);

							try {
								tableModel = new ResultSetTableModel(connection);
								OperationsLog opsLog = new OperationsLog();

								if (queryArray[0].equals("select")) {

									int result;
									result = tableModel.setQuery(query);

									if (result >= 0) {

										opsLog.numQueries(userConnected + "@localhost");
									}

								} else if (queryArray[0].equals("insert") || queryArray[0].equals("delete")
										|| queryArray[0].equals("update")) {

									int result;
									result = tableModel.setUpdate(query);

									if (result >= 0) {

										JOptionPane.showMessageDialog(null,
												"Succesful Update..." + result + " rows updated.", "Succesful Update",
												JOptionPane.INFORMATION_MESSAGE);

										opsLog.numUpdates(userConnected + "@localhost");
									}

								}

								JTable resultTable = new JTable(tableModel);
								resultTable.setGridColor(Color.BLACK);
								scrollPane.setViewportView(resultTable);

							} catch (SQLException e1) {

								// e1.printStackTrace();

								JOptionPane.showMessageDialog(null, e1.getMessage(), "Database Error",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(null, "There is No Connection Stablished to the Database!",
									"Database Error", JOptionPane.ERROR_MESSAGE);
						}

					}
				}

		);

		// Event Listener for Clear Result Window Button
		clearResultWindowB.addActionListener(

				new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						resultTable.setModel(new DefaultTableModel());
						scrollPane.setViewportView(resultTable);
					}
				}

		);

	}

	public void centerFrame(int frameWidth, int frameHeight) {
		Toolkit aToolkit = Toolkit.getDefaultToolkit();

		Dimension screen = aToolkit.getScreenSize();

		int xPositionFrame = (screen.width - frameWidth) / 2;
		int yPositionFrame = (screen.height - frameHeight) / 2;

		setBounds(xPositionFrame, yPositionFrame, frameWidth, frameHeight);

	}

	private class UrlDatabaseHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			@SuppressWarnings("unchecked")
			JComboBox<String> url = (JComboBox<String>) e.getSource();
			database = url.getSelectedItem().toString();
			connectedToDatabase = false;
			connectionStatusT.setText("NO CONNECTION NOW");
			connectionStatusT.setForeground(Color.RED);

		}
	}

	private class UserDatabaseHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			user = combo.getSelectedItem().toString();
			connectedToDatabase = false;
			connectionStatusT.setText("NO CONNECTION NOW");
			connectionStatusT.setForeground(Color.RED);
		}
	}

	private class ConnectDatabaseHandler implements ActionListener {
		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e) {
			System.out.println("Connect Button was pressed!!");

			Properties propertiesDB = new Properties();
			Properties propertiesUser = new Properties();
			FileInputStream fileinDB = null;
			FileInputStream fileinUser = null;
			MysqlDataSource dataSource = null;

			try {
				fileinDB = new FileInputStream(database);
				fileinUser = new FileInputStream(user);
				propertiesDB.load(fileinDB);
				propertiesUser.load(fileinUser);
				dataSource = new MysqlDataSource();
				dataSource.setURL(propertiesDB.getProperty("MYSQL_DB_URL"));
				dataSource.setUser(propertiesUser.getProperty("MYSQL_DB_USERNAME"));
				dataSource.setPassword(propertiesUser.getProperty("MYSQL_DB_PASSWORD"));
				OperationsLog opsLog = new OperationsLog();
				
				if (usernameT.getText().equals(propertiesUser.getProperty("MYSQL_DB_USERNAME"))
						&& passwordT.getText().equals(propertiesUser.getProperty("MYSQL_DB_PASSWORD"))) {

					// connect to database
					// establish connection to database
					connection = dataSource.getConnection();

					// update database connection status
					connectedToDatabase = true;
					
					opsLog.insertUser(usernameT.getText() + "@localhost");
					userConnected = usernameT.getText();
					
				}

				else {
					connectionStatusT.setText("NOT CONNECTED - User Credentials Do Not Match Properties File!");
					connectionStatusT.setForeground(Color.RED);
				}

			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				// System.exit( 1 );
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			if (connectedToDatabase == true) {
				connectionStatusT.setText("CONNECTED TO: " + dataSource.getURL());
				connectionStatusT.setForeground(Color.YELLOW);
			}

		}
	}

	private class ClearSqlCmd implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			System.out.println("The Clear SQL Command button was pressed!");

			queryArea.setText("");

		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application frame = new Application();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
