package jdrew.oo.util;

public class TypesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String query = "<Subsumes>\n\t" +
						 "<Rel>Vehicle</Rel>\n\t"+
						 "<Rel>Car</Rel>\n" +
					   "</Subsumes>";	
		
		QueryTypesAPI qp = new QueryTypesAPI();
		
		System.out.println(qp.executeQuery(query));
		
		

	}

}
