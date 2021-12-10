package org.edpirro.alexandregjr.main;
import org.apache.jena.ontology.Individual;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Converter {
    public static void main(String[] args) {

        String filePath = "E:\\Documents\\Git\\csv-enrich\\data.csv";

        Charset ch = StandardCharsets.UTF_8;
        String separator = "\t";
        String ns = "http://alexandregjr.edpirro.org/gbif#";

        Ontology onto = new Ontology(ns, "gbif");

        try (InputStream is = new FileInputStream(filePath);
             Reader r = new InputStreamReader(is, ch);
            BufferedReader br = new BufferedReader(r)) {

            br.lines().limit(5).forEach(line -> {
                String[] data = line.split(separator);


                Individual organism = onto.createIndividual(data[34], onto.getClass("Organism"));
                organism.addProperty(onto.getDatatypeProperty("kingdom"), data[3]);
                organism.addProperty(onto.getDatatypeProperty("phylum"), data[4]);
                organism.addProperty(onto.getDatatypeProperty("class"), data[5]);
                organism.addProperty(onto.getDatatypeProperty("order"), data[6]);
                organism.addProperty(onto.getDatatypeProperty("family"), data[7]);
                organism.addProperty(onto.getDatatypeProperty("genus"), data[8]);
                organism.addProperty(onto.getDatatypeProperty("species"), data[9]);

                Individual location = onto.createIndividual(data[21] + "_" + data[22], onto.getClass("Location"));

                location.addProperty(onto.getDatatypeProperty("countryCode"), data[15]);
                location.addProperty(onto.getDatatypeProperty("locality"), data[16]);
                location.addProperty(onto.getDatatypeProperty("stateProvince"), data[17]);
                location.addProperty(onto.getDatatypeProperty("latitude"), data[21]);
                location.addProperty(onto.getDatatypeProperty("longitude"), data[22]);


                Individual occurence = onto.createIndividual(data[2], onto.getClass("Occurrence"));
                occurence.addProperty(onto.getDatatypeProperty("date"), data[29]);
                occurence.addProperty(onto.getDatatypeProperty("status"), data[18]);
                occurence.addProperty(onto.getObjectProperty("organism"), organism);
                occurence.addProperty(onto.getObjectProperty("location"), location);
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        onto.writeModel();
    }
}
