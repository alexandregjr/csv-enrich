package org.edpirro.alexandregjr.main;

public class Query {
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

    public static String locationByCityNameQuery(String city) {
        return String.format(LOCATION_BY_CITY_NAME, city);
    }

    public static final String ORGANISM_BY_SPECIES =
            "PREFIX up: <http://purl.uniprot.org/core/>\n" +
            "SELECT DISTINCT ?species ?label\n" +
            "   WHERE { \n" +
            "       ?species up:scientificName ?label\n" +
            "           VALUES ?label { \"%s\"} .\n" +
            "   } LIMIT 10";

    public static String getOrganismBySpecies(String species) {
        return String.format(ORGANISM_BY_SPECIES, species);
    }
}
