import java.io.File;
import java.io.FileWriter;


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
		String s = lemons.issueQuery(Globals.CURRENTRULEML, queryA).toXML();

		File x = new File("resultsA.xml");
		FileWriter out = new FileWriter(x);
		out.write(s);
		out.close();

		}
		catch(Exception e){e.printStackTrace();}

		}

		public static void main(String args[]){
			
			OOjDREWAPITest test = new OOjDREWAPITest();
			test.testA();
			
		}
	
	
}
