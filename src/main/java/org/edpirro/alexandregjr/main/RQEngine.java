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

public class RQEngine {
    private Model model;
    private Query query;
    private String url;

    RQEngine() {
        model = ModelFactory.createDefaultModel();
    }

    RQEngine(Model model) {
        this.model = model;
    }

    RQEngine(String url) {
        this.url = url;
    }

    public void readModel(String path) {
        RDFDataMgr.read(model, path, RDFLanguages.filenameToLang(path));
    }

    public void createQueryByPath(String path) throws IOException {
        String queryStr = IOUtils.toString(new FileInputStream(path), "UTF-8");
        query = QueryFactory.create(queryStr);
    }

    public void createQueryByValue(String queryStr) {
        query = QueryFactory.create(queryStr);
    }

    public String executeQuery(String url) {
        QueryExecution qe;
        if (url == null || url.isEmpty())
            qe = QueryExecutionFactory.create(query, model);
        else
            qe = QueryExecutionFactory.sparqlService(url, query);

        if (query.isAskType()) {
            boolean result = qe.execAsk();
            return Boolean.toString(result);
        } else if (query.isConstructType()) {
            Model result = qe.execConstruct();

            StringWriter writer = new StringWriter();
            result.write(writer, "TURTLE");

            return writer.toString();
        } else if (query.isSelectType()) {
            ResultSet result = qe.execSelect();
            return ResultSetFormatter.asText(result);
        }

        return "ERROR: Query type not recognized.";
    }

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