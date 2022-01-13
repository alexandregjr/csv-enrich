package org.edpirro.alexandregjr.main;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Creates an engine to provision queries using the given endpoints.
 * 
 * @author Alexandre Galocha Pinto Juniro - 10734706
 * @author Eduardo Pirro - 10734665
 */
public class RQEngine {
    private Model model;
    private Query query;
    private String url;

    /**
     * Default constructor, generates a new model to query with.
     */
    RQEngine() {
        model = ModelFactory.createDefaultModel();
    }

    /**
     * Instantiates engine with given model.
     * @param model
     */
    RQEngine(Model model) {
        this.model = model;
    }

    /**
     * Instantiates engine defining query endpoint.
     * @param url
     */
    RQEngine(String url) {
        this.url = url;
    }

    /**
     * Populates engine model by reading file with given path.
     * @param path
     */
    public void readModel(String path) {
        RDFDataMgr.read(model, path, RDFLanguages.filenameToLang(path));
    }

    /**
     * Creates a query from filepath.
     * @param path
     */
    public void createQueryByPath(String path) throws IOException {
        String queryStr = IOUtils.toString(new FileInputStream(path), "UTF-8");
        query = QueryFactory.create(queryStr);
    }

    /**
     * Creates a query instance with given SPARQL query string.
     * @param queryStr
     */
    public void createQueryByValue(String queryStr) {
        query = QueryFactory.create(queryStr);
    }

    /**
     * Executes the last defined query against the given url (if no url is supplied, then default is used)
     * @param url
     * @return String - Formatted Query Result
     */
    public String executeQuery(String url) {
        QueryExecution qe;
        if (url == null || url.isEmpty())
            qe = QueryExecutionFactory.create(query, model);
        else
            qe = QueryExecutionFactory.sparqlService(url, query);

        // check query type
        if (query.isAskType()) { // if ask execAsk
            boolean result = qe.execAsk();
            return Boolean.toString(result);
        } else if (query.isConstructType()) { // if construct execConstruct
            Model result = qe.execConstruct();

            StringWriter writer = new StringWriter();
            result.write(writer, "TURTLE");

            return writer.toString();
        } else if (query.isSelectType()) { // if select execSelect
            ResultSet result = qe.execSelect();
            return ResultSetFormatter.asText(result);
        }

        return "ERROR: Query type not recognized.";
    }

    /**
     * Executes last defined query as a select qeury against default URL.
     * @return ResultSet - raw select result set
     */
    public ResultSet executeRemoteSelectQuery() {
        if (this.url == null) {
            return null;
        }
        QueryExecution qe = QueryExecutionFactory.sparqlService(url, query);
        if (query.isSelectType()) {
            return qe.execSelect();
        }

        return null;
    }

    /**
     * Main procedure - used for debugging and tests
     * @param args
     */
    public static void main(String[] args) {
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "select distinct ?place ?label where {" +
                "?place rdfs:label ?label \n" +
        "           VALUES ?label { \"Madrid\"@en} .\n" +
                "} LIMIT 10";

        RQEngine engine = new RQEngine();

        engine.createQueryByValue(query);

        String result = engine.executeQuery("https://dbpedia.org/sparql");

        System.out.println(result);
    }
}