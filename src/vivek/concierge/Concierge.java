package vivek.concierge;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.rdbms.AppEngineDriver;

@SuppressWarnings("serial")
public class Concierge extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Connection c = null;
		String msg="";
		try{
			
			String cals = req.getParameter("calories");
			if(cals!=null && cals.trim().length()>0){
				int calories = Integer.parseInt(cals);
				DriverManager.registerDriver(new AppEngineDriver());
				c = DriverManager.getConnection("jdbc:google:rdbms://vivekconcierge:concierge2/concierge");
			    String statement ="INSERT INTO calories (calories, calories_timestamp) VALUES( ? , NOW() )";
			    PreparedStatement stmt = c.prepareStatement(statement);
			    stmt.setInt(1, calories);
			    int success = stmt.executeUpdate();
			    stmt.close();
			    stmt=null;

			    String query = 
			    	"select sum(calories)daily_consumed " +
			    	"from calories where calories_timestamp " +
			    	"between timestamp(curdate()) and now()";
			    
			    stmt = c.prepareStatement(query);
			    ResultSet rs = stmt.executeQuery();
			    rs.next();
			    int daily_consumed = rs.getInt("daily_consumed");
			    rs.close();
			    stmt.close();
			    c.close();
			    rs=null;
			    stmt=null;
			    c=null;
			    msg+="<p>consumed "+daily_consumed+" calories</p>";
			    req.getRequestDispatcher("/concierge.jsp?msg="+msg).forward(req,resp);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
