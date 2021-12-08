package org.edpirro.alexandregjr.main;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public class Converter {
    public static void main(String[] args) {
        Charset ch = StandardCharsets.UTF_8;
        String separator = "\t";
        String filepath = "/home/alexandregjr/courses/scc0283/work/data.csv";
        String ns = "http://alexandregjr.edpirro.org/gbif#";
        UnaryOperator<String> nameToURI = s -> ns + s.toLowerCase().replace(" ", "_");

        Model m = ModelFactory.createDefaultModel()
                .setNsPrefixes(PrefixMapping.Standard)
                .setNsPrefix("gbif", ns);
        Resource clazz = m.createResource(ns + "Animal", OWL.Class);

        try (InputStream is = new FileInputStream(filepath);
             Reader r = new InputStreamReader(is, ch);
             BufferedReader br = new BufferedReader(r)) {
            String first = br.lines().findFirst().orElseThrow(IllegalArgumentException::new);
            List<Property> props = Arrays.stream(first.split(separator))
                    .map(s -> m.createResource(nameToURI.apply(s), OWL.DatatypeProperty)
                            .addProperty(RDFS.label, s).as(Property.class)).toList();
//            br.lines().forEach(line -> {
//                String[] data = line.split(separator);
//                if (data.length != props.size()) throw new IllegalArgumentException();
//                Resource individual = m.createResource(clazz);
//                for (int i = 0; i < data.length; i++) {
//                    individual.addProperty(props.get(i), data[i]);
//                }
//            });
            String line = br.lines().findFirst().orElseThrow(NullPointerException::new);
            String[] data = line.split(separator);
            if (data.length != props.size()) throw new IllegalArgumentException();
            Resource individual = m.createResource(clazz);
            for (int i = 0; i < data.length; i++) {
                individual.addProperty(props.get(i), data[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        m.write(System.out, "ttl");
    }
}
