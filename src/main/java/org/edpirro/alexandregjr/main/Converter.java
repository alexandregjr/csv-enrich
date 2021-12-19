package org.edpirro.alexandregjr.main;
import org.apache.jena.ontology.Individual;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.vocabulary.OWL2;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Converter {
    public static void main(String[] args) {

        String outputfile = null;
        if(args.length > 0) {
            outputfile = args[0];
        }

        String filePath = "data.csv";

        Charset ch = StandardCharsets.UTF_8;
        String separator = "\t";
        String ns = "http://alexandregjr.edpirro.org/gbif#";

        Ontology onto = new Ontology(ns, "gbif");
        RQEngine engineWD = new RQEngine("http://query.wikidata.org/sparql");
        RQEngine engineDBP = new RQEngine("https://dbpedia.org/sparql");

        try (InputStream is = new FileInputStream(filePath);
             Reader r = new InputStreamReader(is, ch);
             BufferedReader br = new BufferedReader(r)) {

            br.lines().skip(1).limit(5).forEach(line -> {
                String[] data = line.split(separator);
                ResultSet result;

                Individual organism;
                engineWD.createQueryByValue(Query.getOrganismByTaxonId(data[33]));
                result = engineWD.executeRemoteSelectQuery();
                if (result.hasNext()) {
                    organism = onto.createIndividual(result.next().get("species").toString(), onto.getClass("Organism"), "");
                } else {
                    organism = onto.createIndividual(data[34], onto.getClass("Organism"));
                }

                organism.addProperty(onto.getDatatypeProperty("kingdom"), data[3]);
                organism.addProperty(onto.getDatatypeProperty("phylum"), data[4]);
                organism.addProperty(onto.getDatatypeProperty("class"), data[5]);
                organism.addProperty(onto.getDatatypeProperty("order"), data[6]);
                organism.addProperty(onto.getDatatypeProperty("family"), data[7]);
                organism.addProperty(onto.getDatatypeProperty("genus"), data[8]);
                organism.addProperty(onto.getDatatypeProperty("species"), data[9]);

                Individual location;
                String locationName = data[16].split(",")[0];
                engineDBP.createQueryByValue(Query.locationByCityNameQuery(locationName));
                result = engineDBP.executeRemoteSelectQuery();
                if (result.hasNext()) {
                    location = onto.createIndividual(result.next().get("place").toString(), onto.getClass("Location"), "");
                } else {
                    location = onto.createIndividual(locationName, onto.getClass("Location"));
                }

                location.addProperty(onto.getDatatypeProperty("countryCode"), data[15]);
                location.addProperty(onto.getDatatypeProperty("locality"), data[16]);
                location.addProperty(onto.getDatatypeProperty("stateProvince"), data[17]);
                location.addProperty(onto.getDatatypeProperty("latitude"), data[21]);
                location.addProperty(onto.getDatatypeProperty("longitude"), data[22]);

                Individual occurrence = onto.createIndividual(data[2], onto.getClass("Occurrence"));
                occurrence.addProperty(onto.getDatatypeProperty("date"), data[29]);
                occurrence.addProperty(onto.getDatatypeProperty("status"), data[18]);
                occurrence.addProperty(onto.getObjectProperty("organism"), organism);
                occurrence.addProperty(onto.getObjectProperty("location"), location);
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        onto.writeModel(outputfile);
    }
}
