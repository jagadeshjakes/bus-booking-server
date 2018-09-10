import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class GetSeatsServlet extends HttpServlet{
  public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException {
    int busId=Integer.parseInt(request.getParameter("busId"));
    int tripId=Integer.parseInt(request.getParameter("tripId"));
    Connection connection=null;
    Statement statement=null;
    try{
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mysql://localhost/travels","root","");
      statement = connection.createStatement();
      String sql="select seats.seat_id,seats.is_available,seats.seat_no,seat_types.type_name,sp.side,sp.position,"+
      "rc.row_no,rc.column_no,tf.fare,passenger.gender from seats "+
      "inner join seat_types on seat_types.seat_type_id=seats.seat_type_id "+
      "inner join seat_positions sp on sp.position_id=seats.position_id "+
      "inner join seat_row_column rc on rc.row_column_id=seats.row_column_id "+
      "inner join trip_fare tf on (tf.position_id=sp.position_id and tf.trip_id="+tripId+") "+
      "left join passenger_booking on (passenger_booking.seat_id=seats.seat_id and passenger_booking.trip_id="+tripId+") "+
      "left join passenger on passenger.passenger_id=passenger_booking.passenger_id "+
      "where seats.bus_id="+busId+";";
      ResultSet rs=statement.executeQuery(sql);
      JSONArray jarray=new JSONArray();
      while(rs.next()){
        JSONObject obj=new JSONObject();
        obj.put("seatId",rs.getInt(1));
        obj.put("isAvailable",rs.getString(2));
        obj.put("seatNo",rs.getString(3));
        String type=rs.getString(4);
        if(type.equals("sleeper")){
          obj.put("isSleeper",true);
        }
        else{
          obj.put("isSleeper",false);
        }
        obj.put("side",rs.getString(5));
        obj.put("position",rs.getString(6));
        obj.put("row",rs.getInt(7));
        obj.put("column",rs.getInt(8));
        obj.put("fare",rs.getDouble(9));
        String status=rs.getString(10);
        if(status==null){
          obj.put("isBooked",false);
        }
        else if(status.equals("FEMALE")){
          obj.put("isBooked",true);
          obj.put("isFemale",true);
        }
        else{
          obj.put("isBooked",true);
          obj.put("isFemale",false);
        }
        obj.put("isSelected",false);
        System.out.println(obj.toString());
        jarray.add(obj);
      }
      statement.close();
      connection.close();
      response.setContentType("application/json");
  		response.setCharacterEncoding("UTF-8");
  		PrintWriter out=response.getWriter();
  		out.print(jarray);
    }
    catch(Exception ex){
      ex.printStackTrace();
      System.out.println(ex);
    }
  }
}
