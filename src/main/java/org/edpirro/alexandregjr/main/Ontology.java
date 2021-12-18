package org.edpirro.alexandregjr.main;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

public class Ontology {
    OntModel model;
    String namespace;
    String prefix;

    Ontology(String namespace, String prefix) {
        this.prefix = prefix == null ? "gbif" : prefix;
        this.namespace = namespace; 
        this.model = ModelFactory.createOntologyModel();
        this.model.setNsPrefixes( PrefixMapping.Standard)
                .setNsPrefix(this.prefix, this.namespace);

        // Organism Class
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


        // Location Class
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

        // Occurence Class
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
    
    public DatatypeProperty getDatatypeProperty(String name) {
        return this.model.getDatatypeProperty(this.namespace + name);
    }

    public ObjectProperty getObjectProperty(String name) {
        return this.model.getObjectProperty(this.namespace + name);
    }

    public OntClass getClass(String name) {
        return this.model.getOntClass(this.namespace + name);
    }

    public Individual createIndividual(String name, OntClass type) {
        return this.model.createIndividual(this.namespace + name, type);
    }

    public Individual createIndividual(String name, OntClass type, String ns) {
        return this.model.createIndividual(ns + name, type);
    }

    public void writeModel() {
        this.model.write(System.out, "ttl");
    }
}