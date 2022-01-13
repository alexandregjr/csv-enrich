package org.edpirro.alexandregjr.main;
import org.apache.jena.ontology.Individual;
import org.apache.jena.query.ResultSet;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Converter class, holds a single method: the main procedure. 
 * Responsible for instancing and calling all other classes in order to fully convert the input CSV into an Otology model in ttl format.
 * The operations executed are listed below and will be further explained in their respective part of the code:<br>
 *  1. Import CSV </br>
 *  2. For each line in the CSV do: </br>
 *  3. Lookup dbpedia for a city with the given location name </br>
 *  4. Use the found resource if existent else create a new Location resource to use </br>
 *  5. Lookup wikidate for an organism with the give taxon ID </br>
 *  6. Use the found resource if existent else create a new Organism to link to </br>
 *  7. Create a new Occurence into the model linking to the previously defined individuals </br>
 *  8. Write output model to either a file or the standard output </br>
 * 
 * @see Ontology
 * @see Query
 * @see RQEngine
 * 
 * @author Alexande Galocha Pinto Junior - 10734706
 * @author Eduardo Pirro - 10734665
 */
public class Converter {

    /**
     * Main procedure, effectively executes the conversion
     * @params args: arguments to be passed upon execution, may have up 3 args:
     *      no args: [] Writes first 5 lines to the standard output
     *      1 arg: [outputfile]: Writes first 5 lines to the outputfile path
     *      2 args: [offset, limit]: Defines the offset and limit of processed records which will be outputed to the standard output
     *      2 args: [outputfile, offset, limit]: Defines the offset and limit of processed records which will be written into the given outputfile path
     */
    public static void main(String[] args) {

        String outputfile = null;
        int offset, limit;
        offset = 0;
        limit = 5;

        // Arguments parsing
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

        // create an ontology which will ultimately be the result of the following processes
        Ontology onto = new Ontology(ns, "gbif");

        // Defines two SPARQL query engines one for wikidata queries (used to fetch organisms data), and another for depedia queries (to fetch locations data)
        RQEngine engineWD = new RQEngine("http://query.wikidata.org/sparql");
        RQEngine engineDBP = new RQEngine("https://dbpedia.org/sparql");

        int[] count = {offset + 1};

        // Reads input CSV
        try (InputStream is = new FileInputStream(filePath);
            Reader r = new InputStreamReader(is, ch);
            BufferedReader br = new BufferedReader(r)) {

            // For each line in the input CSV, ignoring header
            br.lines().skip(1 + offset).limit(limit).forEach(line -> {
                System.out.println("Processing line " + count[0] + "...");
                String[] data = line.split(separator);
                ResultSet result;

                Individual organism = null;

                if (!data[33].isEmpty()) { // data[33] references the 34th (0-based) column in the CSV which is taxonKey
                    System.out.println("\tFetching organism...");
                    engineWD.createQueryByValue(Query.getOrganismByTaxonId(data[33])); // search wikidata for an organism with the given taxonKey
                    result = engineWD.executeRemoteSelectQuery();
                    if (result.hasNext()) { // if such organism exists, use it
                        System.out.println("\tFound organism! Using existing organism.");
                        organism = onto.createIndividual(result.next().get("species").toString(), onto.getClass("Organism"), "");
                    } else { // else create a new one
                        System.out.println("\tCreating organism.");
                        organism = onto.createIndividual(data[33], onto.getClass("Organism"));
                    }
                } else { // CSV entry has no organism
                    System.out.println("\tNULL Organism.");
                }

                if (organism != null) { // link available data to the organism
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
                if (!data[16].isEmpty()) { // column 17: locality
                    System.out.println("\tFetching location...");
                    String locationName = data[16].split(",")[0];
                    engineDBP.createQueryByValue(Query.locationByCityNameQuery(locationName)); // query dbpedia for location
                    result = engineDBP.executeRemoteSelectQuery();
                    if (result.hasNext()) { // if such location exists, use it
                        System.out.println("\tFound resource! Using existing location.");
                        location = onto.createIndividual(result.next().get("place").toString(), onto.getClass("Location"), "");
                    } else { // else create a new one
                        System.out.println("\tCreating location.");
                        location = onto.createIndividual(locationName, onto.getClass("Location"));
                    }
                } else {
                    System.out.println("\tNULL Location.");
                } // CSV entry has no location

                if (location != null) { // populate location data
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

                if (!data[2].isEmpty()) { // column 3: occurrenceID

                    // Creates an Occurrence with the given ID into the model
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
        onto.writeModel(outputfile); // output model to file/console
    }
}
