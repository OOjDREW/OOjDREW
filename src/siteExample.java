import java.io.File;
import java.io.IOException;

import jdrew.oo.util.ParseException;
import jdrew.oo.util.SubException;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import antlr.RecognitionException;
import antlr.TokenStreamException;


public class siteExample {
	public static void main(String args[]){
	
	File types = new File("P:\\Types.rdfs");
	File kb = new File("P:\\KB.posl");
	try {
		COjDA api = new COjDA(COjDA.POSL, COjDA.RDFS, kb, types);
		
		String kbPOSLQuery = "base_price(customer->[sex->male; name->\"John Doe\"; age->28]; vehicle->vehicle:ToyotaCorolla; price->?money:Integer).";
		String result = api.issueKBQuery_POSL(kbPOSLQuery);
		
		System.out.println(result);
		
		String taxonomyPOSLQuery = "subsumesPlus(?X, MiniVan)";
		String resultTaxonomy = api.issueTaxonomyQuery_POSL(taxonomyPOSLQuery);
		System.out.println(resultTaxonomy);
	} catch (RecognitionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (TokenStreamException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ValidityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
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
