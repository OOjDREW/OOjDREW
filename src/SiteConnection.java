import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

public class SiteConnection{
	
 public static void main(String[] args) {

	String url = "http://www.jdrew.org/oojdrew/kb.txt";

   try {
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod( url );
    method.setFollowRedirects( true );

    // Execute the GET method
    int statusCode = client.executeMethod( method );
    if( statusCode != -1 ) {
      String contents = method.getResponseBodyAsString();
      method.releaseConnection();
      
      System.out.println("=======================");
      System.out.println( contents );
      
      
    }
   }
   catch( Exception e ) {
    e.printStackTrace();
   }
 }
}