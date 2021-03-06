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
            "SELECT DISTINCT ?species ?scientificName\n" +
            "   WHERE { \n" +
            "       ?species wdt:P846 \"%s\" ;\n" +
            "       wdt:P225 ?scientificName .\n" +
            "   } LIMIT 10";

    /**
     * Generates a SPARQL query using organism query template formatted with the given taxon ID
     * @param id - organism's taxon ID
     * @return String - SPARQL query ready to be used by RQEngine
     */
    public static String getOrganismByTaxonId(String id) {
        return String.format(ORGANISM_BY_ID, id);
    }

    /**
     * Template for taxon location range query
     */
    public static final String TAXON_RANGE_BY_SCIENTIFIC_NAME =
            "prefix dwc: <http://rs.tdwg.org/dwc/terms/>\n" +
            "prefix dwciri: <http://rs.tdwg.org/dwc/iri/>\n" +
            "prefix dct:     <http://purl.org/dc/terms/>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "SELECT\n" +
            "     (?measurementType as ?TYPE)\n" +
            "     (MAX(xsd:float(?measurementValue)) as ?MAX)\n" +
            "     (MIN(xsd:float(?measurementValue)) as ?MIN)\n" +
            "WHERE {\n" +
            "    ?taxon a dwc:Taxon;\n" +
            "        dwc:scientificName \"%s\";\n" +
            "        dct:relation ?measure.\n" +
            "    ?measure\n" +
            "        a                       dwc:MeasurementOrFact;\n" +
            "        dwc:measurementType     ?measurementType;\n" +
            "        dwc:measurementValue    ?measurementValue.\n" +
            "    OPTIONAL { ?measure dwc:measurementUnit     ?measurementUnit }\n" +
            "    OPTIONAL { ?measure dwciri:measurementUnit  ?measurementUnitUri }\n" +
            "    FILTER (?measurementType = \"latitude\"  || ?measurementType = \"longitude\")\n" +
            "} GROUP BY ?measurementType";

    /**
     * Generates a SPARQL query using organism query template formatted with the given taxon ID
     * @param id - organism's taxon ID
     * @return String - SPARQL query ready to be used by RQEngine
     */
    public static String getTaxonRangeByScientificName(String scientificName) {
        return String.format(TAXON_RANGE_BY_SCIENTIFIC_NAME, scientificName);
    }
}
