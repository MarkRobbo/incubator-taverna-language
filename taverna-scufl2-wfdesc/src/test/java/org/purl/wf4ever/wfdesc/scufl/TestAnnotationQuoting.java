package org.purl.wf4ever.wfdesc.scufl;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.purl.wf4ever.wfdesc.scufl2.ROEvoSerializer;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WriterException;

public class TestAnnotationQuoting {
	private static final String T3_1226 = "T3-1226-annotations-with-quotes.t2flow";
	
	ROEvoSerializer roEvo = new ROEvoSerializer();
	WorkflowBundleIO io = new WorkflowBundleIO();

	private WorkflowBundle workflow;

	private ByteArrayOutputStream output = new ByteArrayOutputStream();
	
	@Before
	public void loadDepdenency() throws ReaderException, IOException, WriterException, RDFParseException, RepositoryException, QueryEvaluationException, MalformedQueryException {
		InputStream localStream = getClass().getResourceAsStream("/" + T3_1226);
		assertNotNull(localStream);
		workflow = io.readBundle(localStream, "application/vnd.taverna.t2flow+xml");
		assertNotNull(workflow);
	}
	
	
	@Test
	public void wfdesc() throws Exception {		
 		io.writeBundle(workflow, output, "text/vnd.wf4ever.wfdesc+turtle");
 		
		Repository myRepository = new SailRepository(new MemoryStore());
		myRepository.initialize();
		RepositoryConnection con = myRepository.getConnection();
		String root = "app://600aac93-0ea8-4e9d-9593-081149e31d5a/";
		System.out.write(output.toByteArray());
		con.add(new ByteArrayInputStream(output.toByteArray()), root, RDFFormat.TURTLE);
		
		TupleQueryResult results = con.prepareTupleQuery(QueryLanguage.SPARQL, 
				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#>  " +
				"PREFIX wf4ever: <http://purl.org/wf4ever/wf4ever#>  " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
				"PREFIX roterms: <http://purl.org/wf4ever/roterms#>  " +
				"PREFIX dc: <http://purl.org/dc/elements/1.1/>  " +
				"PREFIX dcterms: <http://purl.org/dc/terms/>  " + 				
				"PREFIX biocat: <http://biocatalogue.org/attribute/>  " +
				"SELECT ?author ?title ?desc ?portDesc ?example  " +
				"WHERE {  " +
				"?wf a wfdesc:Workflow ; " +
				"  dc:creator ?author ; " +				
				"  dcterms:title ?title ; " +
				"  dcterms:description ?desc ; " +
				"  wfdesc:hasInput ?in . " +
				"?in a wfdesc:Input ; " +
				"  dcterms:description ?portDesc ; " +
				"  biocat:exampleData ?example . " +				
			    "}").evaluate();

		
		
		assertTrue("wfdesc not in expected structure", results.hasNext());
		BindingSet bind = results.next();
		assertEquals("Stian Soiland-Reyes", bind.getValue("author").stringValue());
		assertEquals("T3-1226 test with 'single quote'", bind.getValue("title").stringValue());
		// Note: The quotes below are only escaped in this Java source code
		assertEquals("This comment contains \"\"\"triple quotes\"\"\" inside.", bind.getValue("desc").stringValue());
		assertEquals("\"quote at the start", bind.getValue("portDesc").stringValue());
		assertEquals("quote at the end\"", bind.getValue("example").stringValue());
		
	}
	
}