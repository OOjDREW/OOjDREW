import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jdrew.oo.util.ParseException;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import antlr.RecognitionException;
import antlr.TokenStreamException;


public class OOjDREWAPITest {

	/********************
	 *    int mode      *
	 *    File   Types  *
	 *    File   KB     *    
	 *    
	 *    
	 ********************/

	 File kbFile;
	 File typeFile;
	 
	 int mode;
 
	OOjDREWAPI lemons;

	OOjDREWAPITest(){

	//this.kbFile = new File("Output/RuleMLPopulatedOntology[New].ruleml");
	
	this.kbFile = new File("myfile40.txt");
	this.typeFile= new File("taxonomy.rdfs");
	
	
	this.mode = Globals.CURRENTRULEML;
	this.lemons = new OOjDREWAPI(mode, typeFile, kbFile);
	}

	public void testA(){
		/*******************************
		 * This Query asks for any object in the system
		 *
		 * 
		 * returns: id ->
		Identifier
		 * 
		rest->  OtherFacts

		 ********************************/

		String queryA =
		"<Query>"+
		"<Implies mapClosure=\"universal\">"+
		"<Atom>"+

		"<Rel>object</Rel>"+

		"<slot>"+

		"<Ind>id</Ind>"+

		"<Var>Identifier</Var>"+
		"</slot>"+

		"<resl>"+

		"<Var>$Anon</Var>"+
		"</resl>"+
		"</Atom>"+
		"<Atom><Rel>top$</Rel></Atom>"+
		 "</Implies>"+
		"</Query>";


		//POSL"object(id->?Identifier !?Monday)";


		try{
		//query = lemons.appendQuery(query);
		//String s = lemons.issueQuery(Globals.CURRENTRULEML, queryA).toXML();
		//System.out.println(s);
			
			
			
			
		//File x = new File("resultsA.xml");
		//FileWriter out = new FileWriter(x);
		//out.write(s);
		//out.close();

		}
		catch(Exception e){e.printStackTrace();}

		}

		public static void main(String args[]){
			
			File f = new File("P:\\ben.posl");
			
			try {
				CODjA api = new CODjA(CODjA.POSL,f);
				api.issueKBQuery_POSL("a(?x,?y)");
				
			} catch (RecognitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TokenStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	
}
