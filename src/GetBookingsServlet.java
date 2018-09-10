import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class GetBookingsServlet extends HttpServlet{
  public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException {
    int bookingId=Integer.parseInt(request.getParameter("bookingId"));
    Connection connection=null;
    Statement statement=null;
    try{
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mysql://localhost/travels","root","");
      statement = connection.createStatement();
      String sql="select booking.booking_id,booking.booking_date,booking.seats_booked,boarding.sub_location_name as boarding_point,"+
      "dropping.sub_location_name as dropping_point,payment.amount,trips.departure,trips.arrival,trips.contact,"+
      "dep.location_name as from_location,arr.location_name as to_location,bus.bus_registration from booking "+
      "inner join sub_locations boarding on boarding.sub_location_id=booking.boarding_point "+
      "inner join sub_locations dropping on dropping.sub_location_id =booking.dropping_point "+
      "inner join payment on booking.payment_id=payment.payment_id "+
      "inner join trips on trips.trip_id=booking.trip_id "+
      "inner join route on route.route_id=trips.route_id "+
      "inner join locations dep on route.from_location=dep.location_id "+
      "inner join locations arr on route.to_location=arr.location_id "+
      "inner join trip_bus on trips.trip_id=trip_bus.trip_id "+
      "inner join bus on bus.bus_id=trip_bus.bus_id where booking_id="+bookingId;
      System.out.println(sql);
      ResultSet rs=statement.executeQuery(sql);
      JSONArray jarray=new JSONArray();
      JSONObject json= new JSONObject();
      while(rs.next()){
        json.put("id",rs.getInt(1));
        json.put("date",rs.getString(2));
        json.put("seats",rs.getInt(3));
        json.put("boarding",rs.getString(4));
        json.put("dropping",rs.getString(5));
        json.put("totalAmount",rs.getDouble(6));
        json.put("departure",rs.getString(7));
        json.put("arrival",rs.getString(8));
        json.put("contact",rs.getString(9));
        json.put("from",rs.getString(10));
        json.put("to",rs.getString(11));
        json.put("bus",rs.getString(12));
      }

      sql="select passenger.passenger_name,passenger.gender,passenger.DOB,b.fare,seats.seat_no,seat_types.type_name from passenger_booking b"+
      " inner join passenger on b.passenger_id=passenger.passenger_id "+
      "inner join seats on b.seat_id=seats.seat_id "+
      "inner join seat_types on seats.seat_type_id=seat_types.seat_type_id where b.booking_id="+bookingId;
      rs=statement.executeQuery(sql);
      while(rs.next()){
        JSONObject obj=new JSONObject();
        obj.put("name",rs.getString(1));
        obj.put("gender",rs.getString(2));
        obj.put("DOB",rs.getString(3));
        obj.put("fare",rs.getDouble(4));
        obj.put("seatNo",rs.getString(5));
        obj.put("type",rs.getString(6));
        jarray.add(obj);
      }
      json.put("passengers",jarray);
      statement.close();
      connection.close();
      response.setContentType("application/json");
  		response.setCharacterEncoding("UTF-8");
  		PrintWriter out=response.getWriter();
  		out.print(json);
    }
    catch(Exception ex){
      System.out.println(ex);
    }
  }
}
