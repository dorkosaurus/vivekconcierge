package vivek.concierge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.rdbms.AppEngineDriver;

@SuppressWarnings("serial")
public class Concierge extends HttpServlet {
	Connection c = null;
    TimeZone pst = TimeZone.getTimeZone("America/Los_Angeles");			    
    int hoursOffset = (pst.getDSTSavings()>0)?7:8;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String msg="";
		try{
			DriverManager.registerDriver(new AppEngineDriver());
			c = DriverManager.getConnection("jdbc:google:rdbms://vivekconcierge:concierge2/concierge");
			String cals = req.getParameter("calories");
			if(cals!=null && cals.trim().length()>0){
				int calories = Integer.parseInt(cals);
				msg+=this.calories(calories,c);
			}
			
			String minutes_cardio = req.getParameter("minutes_cardio");
			if(minutes_cardio!=null && minutes_cardio.trim().length()>0){
				int mincard = Integer.parseInt(minutes_cardio);
				msg+=cardio(mincard,c);
			}
			String html = "<html><body>"+msg+"</body></html>";
		    resp.getOutputStream().write(html.getBytes());
		    resp.getOutputStream().flush();

			c.close();
		    c=null;
		}
		catch(Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private String calories(int calories, Connection c)throws Exception{
			    String statement ="INSERT INTO calories (calories, calories_timestamp) VALUES( ? , NOW() )";
			    PreparedStatement stmt = c.prepareStatement(statement);
			    
			    stmt.setInt(1, calories);
			    stmt.executeUpdate();
			    stmt.close();
			    c.commit();
			    stmt=null;

			    
			    Calendar cal = Calendar.getInstance();
			    cal.setTimeZone(pst);
			    int mm = cal.get(Calendar.MONTH)+1;
			    
			    
			    
			    int yyyy = cal.get(Calendar.YEAR);
			    int dim = cal.get(Calendar.DAY_OF_MONTH);
			    String starting_tz = ""+yyyy+"-"+mm+"-"+dim+" 00:00:00";
			    
			    String query = 
			    	"select sum(calories)daily_consumed " +
			    	"from calories where date_sub(calories_timestamp,INTERVAL "+hoursOffset+" HOUR) " +
			    	"between '"+starting_tz+"' and date_sub(now(),INTERVAL "+hoursOffset+" HOUR)";
			    
			    stmt = c.prepareStatement(query);
			    ResultSet rs = stmt.executeQuery();
			    rs.next();
			    int daily_consumed = rs.getInt("daily_consumed");
			    rs.close();
			    stmt.close();
			    rs=null;
			    stmt=null;
			    String msg="<H1><p>consumed "+daily_consumed+" calories</p><a href='/concierge.jsp'>BACK</a></H1>";
			    return msg;
	}
	private String cardio(int minutes_cardio, Connection c)throws Exception{
	    String statement ="INSERT INTO cardio (minutes_cardio, cardio_timestamp) VALUES( ? , NOW() )";
	    PreparedStatement stmt = c.prepareStatement(statement);
	    
	    stmt.setInt(1, minutes_cardio);
	    stmt.executeUpdate();
	    stmt.close();
	    c.commit();
	    stmt=null;

	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTimeZone(pst);
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

	    int mm = cal.get(Calendar.MONTH)+1;
	    int yyyy = cal.get(Calendar.YEAR);
	    int dim = cal.get(Calendar.DAY_OF_MONTH);
	    String starting_tz = ""+yyyy+"-"+mm+"-"+dim+" 00:00:00";
	    
	    String query = 
	    	"select sum(minutes_cardio)weekly_cardio " +
	    	"from cardio where date_sub(cardio_timestamp,INTERVAL "+hoursOffset+" HOUR) " +
	    	"between '"+starting_tz+"' and date_sub(now(),INTERVAL "+hoursOffset+" HOUR)";
	    
	    
	    stmt = c.prepareStatement(query);
	    ResultSet rs = stmt.executeQuery();
	    rs.next();
	    int weekly_cardio = rs.getInt("weekly_cardio");
	    rs.close();
	    stmt.close();
	    rs=null;
	    stmt=null;
	    String msg="<H1>"+query+"<p>weekly cardio "+weekly_cardio+"</p><a href='/concierge.jsp'>BACK</a></H1>";
	    return msg;
}
}
