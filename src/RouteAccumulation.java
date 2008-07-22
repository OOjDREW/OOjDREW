import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import jdrew.oo.util.ParseException;
import jdrew.oo.util.SubException;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import antlr.RecognitionException;
import antlr.TokenStreamException;


public class RouteAccumulation {



	public static void main(String args[]){
		
		ArrayList<DTRObject> list = new ArrayList<DTRObject>();
		ArrayList<VisitedNode> visited = new ArrayList<VisitedNode>();
		try{
			
		String out = "";
		 File f = new File("P:\\testShortestPathGraph.txt");
		 FileReader inFile = new FileReader(f);
         BufferedReader in = new BufferedReader(inFile);
         String read ="";
         String contents="";
         
         while((read = in.readLine()) != null)
         {
                 contents = contents + read + '\n';
         }
         in.close();
         
               
         StringTokenizer st = new StringTokenizer(contents, "\n");
         
         while(st.hasMoreTokens()){
              	 
        	 StringTokenizer part = new StringTokenizer(st.nextToken(), ";");
        	 
        	 String start = part.nextToken();
        	 start = start.substring(start.indexOf("->") + 2);
        	 
        	 String end = part.nextToken();
        	 end = end.substring(end.indexOf("->") + 2);
        	 
        	 String route = part.nextToken();
        	 route = route.substring(route.indexOf("->") + 2);
        	 
        	 String time = part.nextToken();
        	 time = time.substring(time.indexOf("->") + 2);
        	 StringTokenizer timeTokenizer = new StringTokenizer(time, ":");
        	 String time2 = timeTokenizer.nextToken();
        	 //String time2 = timeTokenizer.nextToken();

        	 DTRObject dtr = new DTRObject(start, end, route, time2);
        	
        	 list.add(dtr);
        	 
         }
         
         for(int i = 0; i < list.size(); i++){
        	 
        	 DTRObject dtr = list.get(i);
        	 String start = dtr.getStart();
        	 String end = dtr.getEnd();

        	 VisitedNode node = new VisitedNode(start,end);
        	 
        	 boolean found = false;
        	 
        	 for(int y = 0; y  < visited.size(); y++){
        		         		 
        		 VisitedNode previous = visited.get(y);
        		  		  
        		 if((previous.getStart().equalsIgnoreCase(start) && previous.getEnd().equalsIgnoreCase(end)) ||
        		     (previous.getStart().equalsIgnoreCase(end) && previous.getEnd().equalsIgnoreCase(start))){
        			 
        			 found = true;
        		 }
        		 
        	 }

        	 if(!found){
        		 
        		 visited.add(node);
        		 ArrayList<DTRObject> same = new ArrayList<DTRObject>();
               		 
        		 for(int j = 0; j < list.size(); j++){
        			 DTRObject dtr2 = list.get(j);

        			 if((start.equalsIgnoreCase(dtr2.getStart()) && end.equalsIgnoreCase(dtr2.getEnd()))) {// ||
        			   // (start.equalsIgnoreCase(dtr2.getEnd()) && end.equalsIgnoreCase(dtr2.getStart()))){
        				 same.add(dtr2);        				 
        			 }
        			 
        			 
        		 }
        		
        		 out = "dTRList(startPoint->" + same.get(0).getStart() + "; endPoint->" + same.get(0).getEnd() + "; routeList->[";
            	 //System.out.println("Set: ");
            	 for(int q = 0; q < same.size(); q++){
            		
            		 out = out + "[" + same.get(q).getPath()+ ","+ same.get(q).getTime() + ":Real" + "]";
            		 
            		 if(!(same.size()-1 == q)){
            			 out = out + ",";
            		 }
            		 
            	 }
            	 out = out + "]; count->" + same.size() + ":Integer).";
            	 System.out.println(out);
        		 
        	 }
        	 

        	 
        	 
         }
          
         
         
		}catch(Exception e){
			System.out.println(e.toString());
			
		}
	}
}
