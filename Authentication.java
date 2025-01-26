import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.Scanner;

@SuppressWarnings("serial")
public class Authentication extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		File credentialsFile = new File("webapps/Project-4/WEB-INF/lib/credentials.csv");
		FileReader inputFileReader = null;
		BufferedReader inputBuffReader = null;
		Scanner aScanner = null;
		String line, usernameFile, userPassword;
		boolean found = false;

		try {
			inputFileReader = new FileReader(credentialsFile);
			inputBuffReader = new BufferedReader(inputFileReader);

			line = inputBuffReader.readLine();

			whileloop: while (line != null) {
				aScanner = new Scanner(line).useDelimiter("\\s*,\\s*");
				usernameFile = aScanner.next();
				userPassword = aScanner.next();

				if (usernameFile.equals(username) && userPassword.equals(password)) {
					found = true;
					if (username.equals("root")) {
						response.sendRedirect("/Project-4/rootHome.jsp");
						break whileloop;
					}
					else if (username.equals("client"))
					{
						response.sendRedirect("/Project-4/clientHome.jsp");
						break whileloop;
					}
					else if (username.equals("dataentryuser"))
					{
						response.sendRedirect("/Project-4/dataEntryHome.jsp");
						break whileloop;
					}
					else if (username.equals("accountant"))
					{
						response.sendRedirect("/Project-4/accountantHome.jsp");
						break whileloop;
					}
				} else {
					line = inputBuffReader.readLine();
				}

			}
			if (found == false) {
				response.sendRedirect("/Project-4/errorpage.html");
			}

		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
			System.out.println("File not found!!");
		}

	}
}
