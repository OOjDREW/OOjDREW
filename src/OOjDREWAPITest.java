import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jdrew.oo.util.ParseException;
import jdrew.oo.util.SubException;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import antlr.RecognitionException;
import antlr.TokenStreamException;


public class OOjDREWAPITest {

		public static void main(String args[]){
			
			File f = new File("P:\\ben.posl");
			File tax = new File("P:\\types.posl");
			
			
			try {
				COjDA api = new COjDA(COjDA.POSL, COjDA.POSL, f, tax);
				
				
				String a =
					"<Query><Implies mapClosure=\"universal\"><Atom>" +
			         "<Rel>a</Rel>" +
			         "<Var>x</Var>" +
			         "<Var>y</Var>" +
			         "<Var>z</Var>" +
			         "<Var>a</Var>" +
			      "</Atom><Atom><Rel>$top</Rel></Atom></Implies></Query>";
				
				String poslQuery = "a(?x,?y,?z,?a)";
				
				System.out.println("====Tests set 1======");
				System.out.println(api.issueKBQuery_RuleML(a));
				System.out.println(api.issueKBQuery_POSL(poslQuery));
				System.out.println(api.issueTaxonomyQuery_POSL("lub(?Result, SportsCoupe, ToyotaCorolla, MiniVan)."));
				
				File poslFileQuery = new File("P:\\poslQuery.txt");
				File RuleMLFileQuery = new File("P:\\RuleMLQuery.txt");
				
				
				System.out.println(api.issueKBQuery_POSL(poslFileQuery));
				System.out.println(api.issueKBQuery_RuleML(RuleMLFileQuery));
				
				
				
				System.out.println("====Tests set 2======");
				
				COjDA api2 = new COjDA(COjDA.POSL, COjDA.POSL, f, tax);
				
				
				String queryRuleML =
					"<SubsumesPlus>\n" +
					   "<Rel>Vehicle</Rel>\n" +
					   "<Rel>Car</Rel>\n" +
					"</SubsumesPlus>";
				
				
				System.out.println(api2.issueKBQuery_RuleML(a));
				System.out.println(api2.issueKBQuery_POSL(poslQuery));
				System.out.println(api2.issueKBQuery_POSL(poslFileQuery));
				System.out.println(api2.issueKBQuery_RuleML(RuleMLFileQuery));
				
				System.out.println(api2.issueTaxonomyQuery_POSL("lub(?Result, SportsCoupe, ToyotaCorolla, MiniVan)."));
				System.out.println(api2.issusTaxonomyQuery_RuleML(queryRuleML));
				
				File lub = new File("P:\\lub.txt");
				File sub = new File("P:\\sub.txt");
				
				System.out.println(api2.issueTaxonomyQuery_POSL(lub));
				System.out.println(api2.issueTaxonomyQuery_RuleML(sub));
				
				
				
				System.out.println("====Tests set 3======");
				
				File insurace = new File("P:\\ins.RuleML");
				File carTypes = new File("P:\\carTypes.rdfs");
								
				COjDA api3 = new COjDA(COjDA.RULEML91, COjDA.RDFS, insurace, carTypes);
				String poslQuery2 = "base_price(customer->[sex->male; name->\"John Doe\"; age->28]; vehicle->vehicle:ToyotaCorolla; price->?money:Integer).";
				System.out.println(api3.issueKBQuery_POSL(poslQuery2));
				
				
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
			} catch (SubException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	
}
