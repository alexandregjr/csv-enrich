package org.edpirro.alexandregjr.main;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDFS;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public class Converter {
    public static void main(String[] args) {
        boolean DEBUG = true;

        Charset ch = StandardCharsets.UTF_8;
        String separator = "\t";
        String ns = "http://alexandregjr.edpirro.org/gbif#";
        UnaryOperator<String> nameToURI = s -> ns + s.toLowerCase().replace(" ", "_");

        // TODO: import model, not create default
        Model m = ModelFactory.createDefaultModel()
                .setNsPrefixes(PrefixMapping.Standard)
                .setNsPrefix("gbif", ns);
        Resource clazz = m.createResource(ns + "Occurrence", OWL2.Class);

        try (InputStream is = new FileInputStream("data.csv");
             Reader r = new InputStreamReader(is, ch);
             BufferedReader br = new BufferedReader(r)) {
            String first = br.lines().findFirst().orElseThrow(IllegalArgumentException::new);
            List<Property> props = Arrays.stream(first.split(separator))
                    .map(s -> m.createResource(nameToURI.apply(s), OWL2.DatatypeProperty)
                            .addProperty(RDFS.label, s).as(Property.class)).toList();
            if (!DEBUG) {
                br.lines().forEach(line -> {
                    String[] data = line.split(separator);
                    if (data.length != props.size()) throw new IllegalArgumentException();
                    Resource individual = m.createResource(clazz);
                    // TODO: filter properties
                    for (int i = 0; i < data.length; i++) {
                        individual.addProperty(props.get(i), data[i]);
                    }
                });
            } else {
                String line = br.lines().findFirst().orElseThrow(NullPointerException::new);
                String[] data = line.split(separator);
                if (data.length != props.size()) throw new IllegalArgumentException();
                Resource individual = m.createResource(clazz);
                // TODO: filter properties
                for (int i = 0; i < data.length; i++) {
                    individual.addProperty(props.get(i), data[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        m.write(System.out, "ttl");
    }
}
