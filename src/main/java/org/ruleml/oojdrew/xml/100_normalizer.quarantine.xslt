<?xml version="1.0"?>

<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:ruleml="http://ruleml.org/spec"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

<!-- XSLT stylesheet for normalizing RuleML 1.00  documents which is built upon
	 the 0.91 version and has been tested with examples at ruleml.org/1.0/exa.
	
	 More test cases would be needed to test this XSLT to its full capabilities 
	 and therefore the occasional bug appears occasionally. 
	 
	 All skipped role tags are reconstructed, resulting in fully-expanded, normalized
	 RuleML which is compatible with RDF.
-->

<xsl:output method="xml" version="1.0"/>

<xsl:template match="/">
		<!-- enter newlines to separate xml declaration and root element -->
		<xsl:text></xsl:text>
		<xsl:apply-templates />
</xsl:template>

<!-- This is a catch-all template, this will run for every tag, unless there is a more 
specific match. The template then runs through a large choose statement (if, then structure)
that then matches the tags to their names and checks to see if the proper role tag is in place,

In several 'otherwise' cases, the following is used:
"<xsl:copy>
	<xsl:copy-of select="@*"/>								
	<xsl:apply-templates/>	
</xsl:copy>"

 In many cases, this is used for leniency as RuleML 1.0 is still being worked on. Ideally,
 when RuleML 1.0 is complete these will be replaced or require the DEBUG variable to be
 set to true.
-->

<xsl:template match="*">
	<!--DEBUG variable allows unknown tags to be printed in the final result for debugging purposes-->
	<xsl:variable name="DEBUG">TRUE</xsl:variable>
	
	<!--Copy tags prints all tags it in encounters via apply templates in the final transformation--> 
	<xsl:copy>
		<!--This copies the tag's attributes-->
		<xsl:copy-of select="@*"/> 
		<!--This copies the tags included text (generally not used)-->
		<xsl:copy-of select="text()"/>
		
		<xsl:choose>
			<!--Implies should only have a if,then then,if structure for children, partial role tags are not expected.
			For example, if the first child is 'then' and the second child is not 'if', then the normalizer will not
			work as assumed-->
			<xsl:when test="name(.)='Implies'">	
				<xsl:choose>
					<!--If if OR then exists in the Implies, put them in the proper order.
					Namespaces are required here-->
					<xsl:when test="child::*[self::ruleml:if] and child::*[self::ruleml:then]">
						<xsl:apply-templates select="ruleml:oid"/>
						<xsl:apply-templates select="ruleml:if"/>
						<xsl:apply-templates select="ruleml:then"/>
						<xsl:for-each select="*[not(self::ruleml:if) and not(self::ruleml:then) and not(self::ruleml:oid)]">
							<xsl:apply-templates select="."/>
						</xsl:for-each>
					</xsl:when>
					<!--Otherwise the if then tags must be added-->
					<xsl:otherwise>	
						<xsl:apply-templates select="ruleml:oid"/>
						<xsl:for-each select="*[not(self::ruleml:oid)] | comment()">
							<xsl:choose>
								<xsl:when test="self::comment()">
									<xsl:call-template name="comments"/>
								</xsl:when>	
								<xsl:when test="position()=1 and name(.)!='then' and name(.)!='if'"> 
									<xsl:element name="if">
										<xsl:apply-templates select="."/>
									</xsl:element>
								</xsl:when>
								<xsl:when test="position()=2 and name(.)!='if' and name(.)!='then'"> 
									<xsl:element name="then">
										<xsl:apply-templates select="."/>
									</xsl:element>
								</xsl:when>
								<xsl:otherwise>													
									<xsl:copy>
										<xsl:copy-of select="@*"/>								
										<xsl:apply-templates/>	
									</xsl:copy>					
								</xsl:otherwise>					
							</xsl:choose>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			
			<xsl:when test="name(.)='Expr'">
				<xsl:for-each select="* | comment()">
					<xsl:choose>
						<xsl:when test="self::comment()">
							<xsl:call-template name="comments"/>
						</xsl:when>	
						<!--Wraps 'Fun' tag in 'op' tag-->
						<xsl:when test="name(.)='Fun' and name(..)!='op'">
							<xsl:element name="op">
								<xsl:apply-templates select="."/>
							</xsl:element>
						</xsl:when>
						<!--Wraps the following tags in an 'arg' tag with an index attribute.-->
						<xsl:when test="( name(.)='Ind' or name(.)='Var' or name(.)='Expr' or name(.)='Plex' or name(.)='Reify' or name(.)='Data' or name(.)='Skolem' ) and name(..)!='arg'">
							<xsl:element name="arg">
								<xsl:attribute name="index"><xsl:value-of select="count(preceding-sibling::*[name()='Ind' or name()='Var' or name()='Expr' or name()='Plex' or name()='Reify' or name()='Data' or name()='Skolem'])+1"/></xsl:attribute>
								<xsl:apply-templates select="."/>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy>
								<xsl:copy-of select="@*"/>								
								<xsl:apply-templates/>	
							</xsl:copy>
						</xsl:otherwise>	
					</xsl:choose>
				</xsl:for-each>		
			</xsl:when>				
			
			<xsl:when test="name(.)='Atom'">
				<xsl:for-each select="* | comment()">
					<xsl:choose>
						<xsl:when test="self::comment()">
							<xsl:call-template name="comments"/>
						</xsl:when>	
						<!--Wraps 'Fun' tag in 'op' tag-->
						<xsl:when test="name(.)='Rel' and name(..)!='op'">
							<xsl:element name="op">
								<xsl:apply-templates select="."/>
							</xsl:element>
						</xsl:when>
						<!--Wraps the following tags in an 'arg' tag with an index attribute.-->
						<xsl:when test="( name(.)='Ind' or name(.)='Var' or name(.)='Fun' or name(.)='Plex' or name(.)='Reify' or name(.)='Data' or name(.)='Skolem' ) and name(..)!='arg'">
							<xsl:element name="arg">
								<xsl:attribute name="index"><xsl:value-of select="count(preceding-sibling::*[name()='Ind' or name()='Var' or name()='Expr' or name()='Plex' or name()='Reify' or name()='Data' or name()='Skolem'])+1"/></xsl:attribute>
								<xsl:apply-templates select="."/>
							</xsl:element>
						</xsl:when>
						<!--Calls the Equal template-->
						<xsl:when test="name()='Equal'">
							<xsl:call-template name="equal"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy>
								<xsl:copy-of select="@*"/>								
								<xsl:apply-templates/>	
							</xsl:copy>
						</xsl:otherwise>	
					</xsl:choose>
				</xsl:for-each>		
			</xsl:when>

			<xsl:when test="name(.)='Uniterm'">
				<xsl:for-each select="* | comment()">
					<xsl:choose>
						<xsl:when test="self::comment()">
							<xsl:call-template name="comments"/>
						</xsl:when>	
						<xsl:when test="( position() = 1 or name(preceding-sibling)='oid' ) and name(.)!='op' and name(.)!='oid' and name(..)!='op'">
							<xsl:element name="op">
								<xsl:apply-templates select="."/>
							</xsl:element>
						</xsl:when>
						<!--Wraps the following tags in an 'arg' tag with an index attribute.-->
						<xsl:when test="( name(.)='Const' or name(.)='Var' or name(.)='Reify' or name(.)='Uniterm' or name(.)='Skolem' ) and name(..)!='arg'">
							<xsl:element name="arg">
								<xsl:attribute name="index"><xsl:value-of select="count(preceding-sibling::*[name()='Const' or name()='Var' or name()='Uniterm' or name()='Reify' or name()='Skolem'])"/></xsl:attribute>
								<xsl:apply-templates select="."/>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy>
								<xsl:copy-of select="@*"/>								
								<xsl:apply-templates/>	
							</xsl:copy>
						</xsl:otherwise>	
					</xsl:choose>
				</xsl:for-each>		
			</xsl:when>
			
			<!--List of type tags that have no role associated with them. -->
			<xsl:when test="name(.)='Reify' or name(.)='Rulebase' or name(.)='Plex' or name(.)='Entails'">				
				<xsl:apply-templates/>	
			</xsl:when>
			
			<!--SWSL Elements -->
			<xsl:when test=" name(.)='InstanceOf' or name(.)='SubclassOf' or name(.)='SlotProd' or name(.)='Set' or name(.)='Signature'">
				<xsl:apply-templates/>
			</xsl:when>
			
			<!--List of role tags, this is to insure that if partial normalized xml files are used
			with this XSLT, it will be able to process it -->
			<xsl:when test="name(.)='oid'or name(.)='torso'or name(.)='op' or
						name(.)='slot' or name(.)='degree' or name(.)='resl' or name(.)='repo' or
						name(.)='formula' or name(.)='declare' or name(.)='content' or name(.)='strong'
						or name(.)='weak' or name(.)='if' or name(.)='then'">				
				<xsl:apply-templates/>	
			</xsl:when>
			
			<!--As the case for equals is used in more than one place in the document, a template has been created for it below,
			in this way, no code needs to copied multiple times-->
			<xsl:when test="name()='Equal'">
				<xsl:call-template name="equal"/>
			</xsl:when>
			
			<xsl:when test="name()='Forall' or name()='Exists'">
				<xsl:call-template name="forallExists"/>
			</xsl:when>
			
			<xsl:otherwise>
				<!--Assigning role tag value for variable "role", this is used for type tags that only have a single role tag.
				However in some cases, these single role assumptions can be considered too broad. When this case is discovered,
				they are removed from this section and given their own xsl:when condition, similar to Equals-->
				<xsl:variable name="role">
					<xsl:choose>
						<xsl:when test="name(.)='Assert' or name(.)='And' or name(.)='Or' or name(.)='Retract'">formula</xsl:when>
						<xsl:when test="name(.)='RuleML' or name(.)='Query'"></xsl:when>
						<xsl:when test="name(.)='Neg'">strong</xsl:when>										
						<xsl:when test="name(.)='Naf'">weak</xsl:when>
						<xsl:when test="name(.)='Equivalent'">torso</xsl:when>	
						<!--Tag Debug Check, DEBUG must be set to true near the top of the document-->
						<xsl:when test="$DEBUG='TRUE'">PARENTERROR</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<!--For each child-->
				<xsl:for-each select="* | comment()">
					<xsl:choose>
						<xsl:when test="self::comment()">
							<xsl:call-template name="comments"/>
						</xsl:when>
						<!--Applies the role tag stored in variable named "role" on children that does not already have it-->
						<xsl:when test="name(.)!=$role">
							<xsl:element name="{$role}">
								<xsl:apply-templates select="."/>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy>
								<xsl:copy-of select="@*"/>							
								<xsl:apply-templates/>	
							</xsl:copy>
						</xsl:otherwise>	
					</xsl:choose>
				</xsl:for-each>
			</xsl:otherwise>	
		</xsl:choose>		
	</xsl:copy>
</xsl:template>

<!--Special case for Equal, as it requires 2 separate role tags for its children,
works as an example for type tags that give their children different role tags-->
<xsl:template name="equal">
		<xsl:for-each select="* | comment()">
			<xsl:choose>
				<xsl:when test="self::comment()">
					<xsl:call-template name="comments"/>
				</xsl:when>	
				<xsl:when test="position()=1 and name(.)!='left' and name(.)!='right' and (name(following-sibling::*[1]) = 'Expr' or name(following-sibling::*[1]) = 'right')">
					<xsl:element name="left">
						<xsl:apply-templates select="."/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="position()=1 and name(.)!='left' and name(.)!='right' and name(following-sibling::*[1]) = 'left'">
					<xsl:element name="right">
						<xsl:apply-templates select="."/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="position()=2 and name(.)!='left' and name(.)!='right' and (name(preceding-sibling::*[1]) = 'Expr' or name(preceding-sibling::*[1]) = 'left')">					
					<xsl:element name="right">
						<xsl:apply-templates select="."/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="position()=2 and name(.)!='left' and name(.)!='right' and name(preceding-sibling::*[1]) = 'right'">					
					<xsl:element name="left">
						<xsl:apply-templates select="."/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy>
						<xsl:apply-templates/>
					</xsl:copy>						
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
</xsl:template>

<!--For All and Exists expects all children except for the last one to have declare tags.
The last child would then have a formula tag. For the time being, the assumption made is that
nobody would place their formula anywhere but the last position-->
<xsl:template name="forallExists">
	<xsl:for-each select="* | comment()">
		<xsl:choose>
		<xsl:when test="self::comment()">
			<xsl:call-template name="comments"/>
		</xsl:when>	
		<xsl:when test="position()!=last() and name(.)!='declare' and name(.)!='formula'">
			<xsl:element name="declare">
				<xsl:apply-templates select="."/>
			</xsl:element>
		</xsl:when>
		<xsl:when test="position()=last() and name(.)!='formula' and name(.)!='declare'">					
			<xsl:element name="formula">
				<xsl:apply-templates select="."/>
			</xsl:element>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy>
				<xsl:apply-templates/>
			</xsl:copy>						
		</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
</xsl:template>

<!--Matches all comments and calls the special 'comments' template-->
<xsl:template match="comment()">
	<xsl:call-template name="comments"/>
</xsl:template>

<!-- preserve comments -->
<xsl:template name="comments">
	<!-- enter newlines around comments to increase readability -->
	<xsl:text></xsl:text>
	<!-- prevent commented-out code from being escaped -->
	<xsl:text disable-output-escaping="yes">&lt;!--</xsl:text>
	<xsl:value-of disable-output-escaping="yes" select="."/>
	<xsl:text disable-output-escaping="yes">--></xsl:text>
	<xsl:text></xsl:text>			
</xsl:template>	


</xsl:stylesheet>