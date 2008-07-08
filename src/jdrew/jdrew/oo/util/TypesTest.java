package jdrew.oo.util;

import java.io.File;

public class TypesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String queryRuleML =
		"<Subsumes>\n" +
		   "<Rel>Vehicle</Rel>\n" +
		   "<Rel>Car</Rel>\n" +
		"</Subsumes>";
		
		
		String query2 = "<GLB>\n" +
		   				"<Var>Result</Var>\n" +
		   				"<Rel>PassengerVehicle</Rel>\n" +
		   				"<Rel>Van</Rel>\n" +
					   "</GLB>";
		
		
		String query3 = "<LUB>\n" +
		   "<Var>Result</Var>\n" +
		   "<Rel>SportsCoupe</Rel>\n" +
		   "<Rel>ToyotaCorolla</Rel>\n" +
		   "<Rel>MiniVan</Rel>\n" +
		"</LUB>";

		
		File f = new File("P:\\types.rdf");
		
		TaxonomyQueryAPI api = new TaxonomyQueryAPI(f);
		
		//System.out.println(api.executeQueryRuleML(queryRuleML));
		
		//System.out.println(qp.executeQueryRuleML(query2));
		
		//System.out.println(qp.executeQueryRuleML(query3));

		System.out.println(api.executeQueryPOSL("lub(?Result, SportsCoupe, ToyotaCorolla, MiniVan)."));
		//System.out.println(api.executeQueryPOSL("lub(?Result, SportsCoupe, ToyotaCorolla, MiniVan)."));
		
	}

}
