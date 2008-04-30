import java.sql.*;
import java.io.*;
public class DatabaseConnection{
	
	public static void main(String args[]) throws IOException {
		
		// 1. Create an InputStreamReader using the standard input stream.
	    InputStreamReader isr = new InputStreamReader( System.in );

	    // 2. Create a BufferedReader using the InputStreamReader created.
	    BufferedReader stdin = new BufferedReader( isr );

	    // 3. Don't forget to prompt the user
	    System.out.print( "Type some data for the program: " );

	    // 4. Use the BufferedReader to read a line of text from the user.
	   // String input = stdin.readLine();
	    String input ="a";
	    // 5. Now, you can do anything with the input string that you need to.
	    // Like, output it to the user.
	    System.out.println( "input = " + input );	
		
		
	Connection con = null;
	
	 try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      con = DriverManager.getConnection("jdbc:mysql:///oojdrew", "root", "jdrew");

      if(!con.isClosed())
        System.out.println("Successfully connected to MySQL server...");
		System.out.println("Connection: " + con);
		
		Statement stmt;
		ResultSet rs;
		//Get a Statement object
		stmt = con.createStatement();
	
	  //drop a table	
	  try{
        stmt.executeUpdate("DROP TABLE myTable");
      }catch(Exception e){
        System.out.print(e);
        System.out.println(
                  "No existing table to delete");
      }//end catch
		
	  //create a table	
		stmt.executeUpdate(
            "CREATE TABLE myTable(test_id int,test_val char(15) not null)");
                  
      //insert values into the table            
      stmt.executeUpdate(
                "INSERT INTO myTable(test_id,test_val) VALUES(1,'One')");
      stmt.executeUpdate(
                "INSERT INTO myTable(test_id,test_val) VALUES(2,'Two')");
      stmt.executeUpdate(
                "INSERT INTO myTable(test_id,test_val) VALUES(3,'Three')");
      stmt.executeUpdate(
                "INSERT INTO myTable(test_id,test_val) VALUES(4,'Four')");
      stmt.executeUpdate(
                "INSERT INTO myTable(test_id,test_val) VALUES(5,'Five')");             
	
	  rs = stmt.executeQuery("SELECT * from myTable ORDER BY test_id");
	 
	 System.out.println("Display all results:");
      while(rs.next()){
        int theInt= rs.getInt("test_id");
        String str = rs.getString("test_val");
        System.out.println("\t test_id= " + theInt + "\t str = " + str);
      }//end while loop
	 
	 
	  System.out.println("Display row number 2:");
      if( rs.absolute(2) ){
        int theInt= rs.getInt("test_id");
        String str = rs.getString("test_val");
        System.out.println("\ttest_id= " + theInt + "\tstr = " + str);
      }//end if
	 
	 //Display all table names
	  try{
        DatabaseMetaData dbm = con.getMetaData();
        String[] types = {"TABLE"};
        ResultSet res = dbm.getTables(null,null,"%",types);
        System.out.println("Table name:");
        
        while (res.next()){
          String table = res.getString("TABLE_NAME");
          System.out.println(table);
          
        }
      }
      catch (SQLException s){
        System.out.println("No any table in the database " + s.toString());
      }
	
	  System.out.println("Getting All Rows from a table!");
	  //all rows from a table
      try{

        ResultSet result = stmt.executeQuery("SELECT * FROM  rideson2");
        System.out.println("Who"  + "\t\t" + "What: ");
        while (result.next()) {
          String i = result.getString("who");
          String s = result.getString("what");
          System.out.println(i + "\t\t" + s);
        }
        
      }
      catch (SQLException s){
        System.out.println("SQL code does not execute." + s.toString()); 
	} 
	
	//count rows
	try{
        int count=0;
        Statement st = con.createStatement();
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter table name:");
        String table = bf.readLine();
        ResultSet res = st.executeQuery("SELECT COUNT(*) FROM "+table);
        while (res.next()){
          count = res.getInt(1);
        }
        System.out.println("Number of rows:"+count);
      }
      catch (SQLException s){
        System.out.println("SQL statement is not executed!");
      }
		
	//column names
	try{
        Statement st = con.createStatement();
        rs = st.executeQuery("SELECT * FROM mytable");
        ResultSetMetaData md = rs.getMetaData();
        int col = md.getColumnCount();
        System.out.println("Number of Column : "+ col);
        System.out.println("Columns Name: ");
        for (int i = 1; i <= col; i++){
          String col_name = md.getColumnName(i);
          System.out.println(col_name);
        }
      }
      catch (SQLException s){
        System.out.println("SQL statement is not executed!");	
      }	
      	
      	
      	
      	
	catch(Exception e) {
      System.err.println("Exception: " + e.getMessage());
    } finally {
      try {
        if(con != null)
          con.close();
      } catch(SQLException e) {}
    }

		
		}
		catch(Exception e){
			
		}
}
}