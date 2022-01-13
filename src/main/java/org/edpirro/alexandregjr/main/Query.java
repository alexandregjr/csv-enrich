package org.edpirro.alexandregjr.main;

/**
 * Defines a query, used to easily generate query strings to be processed by RQEngine
 * @see RQEngine
 * 
 * @author Alexandre Galocha Pinto Juniro - 10734706
 * @author Eduardo Pirro - 10734665
 */
public class Query {

    /**
     * Template for location queries
     */
    private static final String LOCATION_BY_CITY_NAME =
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
            "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
            "SELECT DISTINCT ?place ?label ?lat ?lng\n" +
            "   WHERE { \n" +
            "       ?place a dbo:Place .\n" +
            "       ?place rdfs:label ?label \n" +
            "           VALUES ?label { \"%s\"@en} .\n" +
            "       ?place geo:lat ?lat .\n" +
            "       ?place geo:long ?lng .\n" +
            "   } LIMIT 10";

    /**
     * Generates a SPARQL query using location query template formatted with the given city name
     * @param city - location name
     * @return String - SPARQL query ready to be used by RQEngine
     */
    public static String locationByCityNameQuery(String city) {
        return String.format(LOCATION_BY_CITY_NAME, city);
    }

    /**
     * Template for location queries by latitude and longitude
     */
    private static final String LOCATION_BY_LAT_LONG =
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                    "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                    "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                    "PREFIX xsd: <w3.org/2001/XMLSchema#>\n" +
                    "SELECT DISTINCT ?place ?label ?lat ?lng\n" +
                    "   WHERE { \n" +
                    "       ?place a dbo:Place .\n" +
                    "       ?place rdfs:label ?label .\n" +
                    "       ?place geo:lat \"%f\"^^xsd:float .\n" +
                    "       ?place geo:long \"%f\"^^xsd:float .\n" +
                    "   } LIMIT 10";

    /**
     * Generates a SPARQL query using location query template formatted with the given city name
     * @param lat - location latitude
     * @param lng - location longitude
     * @return String - SPARQL query ready to be used by RQEngine
     */
    public static String locationByLatLongQuery(float lat, float lng) {
        return String.format(LOCATION_BY_LAT_LONG, lat, lng);
    }

    /**
     * Template for organism queries
     */
    public static final String ORGANISM_BY_ID =
            "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
            "SELECT DISTINCT ?species\n" +
            "   WHERE { \n" +
            "       ?species wdt:P846 \"%s\" .\n" +
            "   } LIMIT 10";

    /**
     * Generates a SPARQL query using organism query template formatted with the given taxon ID
     * @param id - organism's taxon ID
     * @return String - SPARQL query ready to be used by RQEngine
     */
    public static String getOrganismByTaxonId(String id) {
        return String.format(ORGANISM_BY_ID, id);
    }
}
