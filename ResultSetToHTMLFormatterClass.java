
import java.sql.*;

import javax.swing.text.html.HTML;

public class ResultSetToHTMLFormatterClass {

	public static synchronized String getHtmlRows(ResultSet result) throws SQLException{
		
		StringBuffer htmlRows= new StringBuffer();
		
		ResultSetMetaData metaData = result.getMetaData();
		
		int numCol = metaData.getColumnCount();
		
		htmlRows.append("<tr id = \"row1\">");
		for (int i = 0; i< numCol; i++ ) {
			htmlRows.append("<th><center>" + metaData.getColumnName(i+1) + "</center></th>");
		}
		htmlRows.append("</tr>");
		
		while(result.next()) {
			htmlRows.append("<tr>");
			for (int i = 0; i < numCol; i++) {
				htmlRows.append("<td><center>" + result.getString(i+1) + "</center></td>");
			}
			htmlRows.append("</tr>");
		}
		
		return htmlRows.toString();
	}
	
}
