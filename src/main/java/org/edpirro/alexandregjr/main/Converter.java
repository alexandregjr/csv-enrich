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
        int offset, limit;
        offset = 0;
        limit = 5;
        if(args.length == 1) {
            outputfile = args[0];
        } else if(args.length == 2) {
            offset = Integer.parseInt(args[0]);
            limit = Integer.parseInt(args[1]);
        } else if(args.length > 2) {
            outputfile = args[0];
            offset = Integer.parseInt(args[1]);
            limit = Integer.parseInt(args[2]);
        }

        if(offset < 0 || limit < 0) {
            offset = 0;
            limit = 236489; // maxElems + 1 -> all items
        }
        String format = "Exporting lines %d to %d, model will attempt to write to %s.";
        System.out.println(String.format(format, offset + 1, offset + limit, outputfile == null ? "the standard output" : outputfile));

        String filePath = "data.csv";

        Charset ch = StandardCharsets.UTF_8;
        String separator = "\t";
        String ns = "http://alexandregjr.edpirro.org/gbif#";

        Ontology onto = new Ontology(ns, "gbif");
        RQEngine engineWD = new RQEngine("http://query.wikidata.org/sparql");
        RQEngine engineDBP = new RQEngine("https://dbpedia.org/sparql");
        int[] count = {offset + 1};


        try (InputStream is = new FileInputStream(filePath);
             Reader r = new InputStreamReader(is, ch);
             BufferedReader br = new BufferedReader(r)) {

            br.lines().skip(1 + offset).limit(limit).forEach(line -> {
                System.out.println("Processing line " + count[0] + "...");
                String[] data = line.split(separator);
                ResultSet result;

                System.out.println("\tFetching organism...");
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

                System.out.println("\tFetching location...");
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
                System.out.println("Done with line " + count[0]++ + "!\n");
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        onto.writeModel(outputfile);
    }
}
