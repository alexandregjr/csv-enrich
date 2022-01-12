package org.edpirro.alexandregjr.main;
import org.apache.jena.ontology.Individual;
import org.apache.jena.query.ResultSet;

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

                Individual organism = null;

                if (!data[33].isEmpty()) {
                    System.out.println("\tFetching organism...");
                    engineWD.createQueryByValue(Query.getOrganismByTaxonId(data[33]));
                    result = engineWD.executeRemoteSelectQuery();
                    if (result.hasNext()) {
                        System.out.println("\tFound organism! Using existing organism.");
                        organism = onto.createIndividual(result.next().get("species").toString(), onto.getClass("Organism"), "");
                    } else {
                        System.out.println("\tCreating organism.");
                        organism = onto.createIndividual(data[33], onto.getClass("Organism"));
                    }
                } else {
                    System.out.println("\tNULL Organism.");
                }

                if (organism != null) {
                    if (!data[3].isEmpty())
                        organism.addProperty(onto.getDatatypeProperty("kingdom"), data[3]);
                    if (!data[4].isEmpty())
                        organism.addProperty(onto.getDatatypeProperty("phylum"), data[4]);
                    if (!data[5].isEmpty())
                        organism.addProperty(onto.getDatatypeProperty("class"), data[5]);
                    if (!data[6].isEmpty())
                        organism.addProperty(onto.getDatatypeProperty("order"), data[6]);
                    if (!data[7].isEmpty())
                        organism.addProperty(onto.getDatatypeProperty("family"), data[7]);
                    if (!data[8].isEmpty())
                        organism.addProperty(onto.getDatatypeProperty("genus"), data[8]);
                    if (!data[9].isEmpty())
                        organism.addProperty(onto.getDatatypeProperty("species"), data[9]);
                }

                Individual location = null;
                if (!data[16].isEmpty()) {
                    System.out.println("\tFetching location...");
                    String locationName = data[16].split(",")[0];
                    engineDBP.createQueryByValue(Query.locationByCityNameQuery(locationName));
                    result = engineDBP.executeRemoteSelectQuery();
                    if (result.hasNext()) {
                        System.out.println("\tFound resource! Using existing location.");
                        location = onto.createIndividual(result.next().get("place").toString(), onto.getClass("Location"), "");
                    } else {
                        System.out.println("\tCreating location.");
                        location = onto.createIndividual(locationName, onto.getClass("Location"));
                    }
                } else {
                    System.out.println("\tNULL Location.");
                }

                if (location != null) {
                    if (!data[15].isEmpty())
                        location.addProperty(onto.getDatatypeProperty("countryCode"), data[15]);
                    if (!data[16].isEmpty())
                        location.addProperty(onto.getDatatypeProperty("locality"), data[16]);
                    if (!data[17].isEmpty())
                        location.addProperty(onto.getDatatypeProperty("stateProvince"), data[17]);
                    if (!data[21].isEmpty())
                        location.addProperty(onto.getDatatypeProperty("latitude"), data[21]);
                    if (!data[22].isEmpty())
                        location.addProperty(onto.getDatatypeProperty("longitude"), data[22]);
                }

                if (!data[2].isEmpty()) {
                    Individual occurrence = onto.createIndividual(data[2], onto.getClass("Occurrence"));
                    if (!data[29].isEmpty())
                        occurrence.addProperty(onto.getDatatypeProperty("date"), data[29]);
                    if (!data[18].isEmpty())
                        occurrence.addProperty(onto.getDatatypeProperty("status"), data[18]);
                    if (organism != null)
                        occurrence.addProperty(onto.getObjectProperty("organism"), organism);
                    if (location != null)
                        occurrence.addProperty(onto.getObjectProperty("location"), location);
                    System.out.println("Done with line " + count[0]++ + "!\n");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        onto.writeModel(outputfile);
    }
}
