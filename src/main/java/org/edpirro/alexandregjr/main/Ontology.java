package org.edpirro.alexandregjr.main;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.RDFS;

import java.io.PrintWriter;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

/**
 * Ontology class, creates classes definitions to be used throughout the conversion.
 * Implements functions to create and retrieve instances into/from the model, and to easily write model to a given output.
 * 
 * @author Alexandre Galocha Pinto Juniro - 10734706
 * @author Eduardo Pirro - 10734665
 */
public class Ontology {
    OntModel model;
    String namespace;
    String prefix;

    /**
     * Ontology constructor, initiates a model with defined classes for Organisms, Locations and Occurrences, also creating their respective data and object properties.
     * @param namespace - namespace to be prefixed into newly created resources
     * @param prefix - prefix to be added into the ontology to link with existing resources
     */
    Ontology(String namespace, String prefix) {
        this.prefix = prefix == null ? "gbif" : prefix;
        this.namespace = namespace; 
        this.model = ModelFactory.createOntologyModel();
        this.model.setNsPrefixes( PrefixMapping.Standard)
                .setNsPrefix(this.prefix, this.namespace);

        // Definition of an Organism class with Datatype Properties [kingdom, phylum, class, order, family, genus, species]
        this.model.createClass(this.namespace + "Organism");
        this.model.createDatatypeProperty(this.namespace + "kingdom")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Oraganism"));
        this.model.createDatatypeProperty(this.namespace + "phylum")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Oraganism"));
        this.model.createDatatypeProperty(this.namespace + "class")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Oraganism"));
        this.model.createDatatypeProperty(this.namespace + "order")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Oraganism"));
        this.model.createDatatypeProperty(this.namespace + "family")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Oraganism"));
        this.model.createDatatypeProperty(this.namespace + "genus")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Organism"));
        this.model.createDatatypeProperty(this.namespace + "species")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Organism"));
        this.model.createDatatypeProperty(this.namespace + "taxonRangeMinLat")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Organism"));
        this.model.createDatatypeProperty(this.namespace + "taxonRangeMaxLat")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Organism"));
        this.model.createDatatypeProperty(this.namespace + "taxonRangeMinLong")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Organism"));
        this.model.createDatatypeProperty(this.namespace + "taxonRangeMaxLong")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Organism"));


        // Definition of a Location class with Datatype Properties [countryCode, locality, stateProvince, latitude, longitude]
        this.model.createClass(this.namespace + "Location");
        this.model.createDatatypeProperty(this.namespace + "countryCode")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Location"));
        this.model.createDatatypeProperty(this.namespace + "locality")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Location"));
        this.model.createDatatypeProperty(this.namespace + "stateProvince")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Location"));
        this.model.createDatatypeProperty(this.namespace + "latitude")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Location"));
        this.model.createDatatypeProperty(this.namespace + "longitude")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Location"));

        // Definition of a Location class with Datatype Properties [data, status] and Object Properties [organism, location] linking to previously defined classes
        this.model.createClass(this.namespace + "Occurrence");
        this.model.createDatatypeProperty(this.namespace + "date")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Occurrence"));
        this.model.createDatatypeProperty(this.namespace + "status")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Occurrence"));            
        this.model.createObjectProperty(this.namespace + "organism")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Occurrence"))
            .addProperty(RDFS.range, this.model.getResource(this.namespace + "Organism"));
        this.model.createObjectProperty(this.namespace + "location")
            .addProperty(RDFS.domain, this.model.getResource(this.namespace + "Occurrence"))
            .addProperty(RDFS.range, this.model.getResource(this.namespace + "Location"));
    }
    
    /**
     * Helper function to get a DatatypeProperty by name from the Model
     * @param name
     * @return DatatypeProperty with the given name.
     */
    public DatatypeProperty getDatatypeProperty(String name) {
        return this.model.getDatatypeProperty(this.namespace + name);
    }

    /**
     * Helper function to get an ObjectProperty by name from the Model
     * @param name
     * @return ObjectProperty with the given name.
     */
    public ObjectProperty getObjectProperty(String name) {
        return this.model.getObjectProperty(this.namespace + name);
    }

    /**
     * Helper function to get a Class by name from the Model
     * @param name
     * @return OntClass with the given name.
     */
    public OntClass getClass(String name) {
        return this.model.getOntClass(this.namespace + name);
    }

    /**
     * Helper function to insert an Individual with given name and type into the model
     * @param name
     * @param type
     * @return Individual - Created individual (or retrieved one if an individual with same name already exists)
     */
    public Individual createIndividual(String name, OntClass type) {
        return this.model.createIndividual(this.namespace + name, type);
    }

    /**
     * Helper function to insert an Individual with given name and type into the model using a namespace other than default
     * @param name
     * @param type
     * @param ns - namespace to used instead of default
     * @return Individual - Created individual (or retrieved one if an individual with same name already exists)
     */
    public Individual createIndividual(String name, OntClass type, String ns) {
        return this.model.createIndividual(ns + name, type);
    }

    /**
     * Attempts to write the model into the file specified. If filename is null or writing to the file fails, it'll be logged to the System's standard output.
     * @param filename
     */
    public void writeModel(String filename) {
        if(filename == null) {
            this.model.write(System.out, "ttl");
        } else {
            try {
                System.out.println("Writing model to " + filename + ".");
                PrintWriter pw = new PrintWriter(filename);
                this.model.write(pw, "ttl");
            } catch (Exception e) {
                System.out.println("Could not write to file " + filename + ". Writing to System.out instead.");
                this.model.write(System.out, "ttl");
            }
        }
    }
}