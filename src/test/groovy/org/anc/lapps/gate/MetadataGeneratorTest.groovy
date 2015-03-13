package org.anc.lapps.gate

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.discriminator.Discriminator
import org.lappsgrid.discriminator.DiscriminatorRegistry
import org.lappsgrid.metadata.*
//import static org.lappsgrid.metadata.AnnotationType.*
//import static org.lappsgrid.metadata.ContentType.*

//import spock.lang.Specification

/**
 * @author Keith Suderman
 */
@Ignore
class MetadataGeneratorTest {

    public MetadataGeneratorTest() { }

    ServiceMetadata makeMetadata(String name, String description, req, prod) {
        if (req != null && !(req instanceof Collection)) {
            req = [ req ]
        }
        if (prod && !(prod instanceof Collection)) {
            prod = [ prod ]
        }
        ServiceMetadata metadata = new GateMetadata()
        metadata.name = name
        metadata.description = description
        metadata.requires = new GateSpecification()
        metadata.produces = new GateSpecification()
        metadata.requires.annotations = req
        metadata.produces.annotations = prod
        return metadata
    }

//    AnnotationType type(String name) {
//        return new AnnotationType("http://vocab.lappsgrid.org/$name")
//    }

    @Ignore
    void run() {
        def info = [
                Coreferencer: [
                        name: Coreferencer.class.name,
                        description: "ANNIE Coreferencer from GATE.",
                        requires: [ PERSON ],
                        produces: [ MATCHES ]
                ],
                Tokenizer: [
                        name: Tokenizer.class.name,
                        description: "ANNIE Tokenizer from GATE.",
                        produces : [ TOKEN ],
                        format: [ TEXT, XML, GATE ]
                ],
                Gazetteer: [
                        name: Gazetteer.class.name,
                        description: "ANNIE Gazetteer from GATE.",
                        requires: [ TOKEN ],
                        produces: [ type("Lookup") ]
                ],
                NamedEntityRecognzier: [
                        name: NamedEntityRecognizer.name,
                        description: "ANNIE NER module from GATE.",
                        requires: [ TOKEN ],
                        produces: [ DATE, PERSON, ORGANIZATION, LOCATION ]
                ],
                NounPhraseChunker: [
                        name: NounPhraseChunker.name,
                        description: "GATE Noun chunker.",
                        requires: [ TOKEN ],
                        produces: [ type("NounChunk") ]
                ],
                VerbPhraseChunker: [
                        name: VerbPhraseChunker.name,
                        description: "GATE Verb chunker.",
                        requires: [ TOKEN ],
                        produces: [ type("VerbChunk")]
                ],
                OrthoMatcher: [
                        name: OrthoMatcher.name,
                        description: "GATE OrthoMatcher."
                ],
                SentenceSplitter: [
                        name: SentenceSplitter.name,
                        description: "ANNIE Sentence Splitter from GATE.",
                        requires: [ TOKEN ],
                        produces: [ SENTENCE ]
                ],
                Tagger: [
                        name: Tagger.name,
                        description: "ANNIE Part of Speech Tagger from GATE.",
                        requires: [ TOKEN ],
                        produces: [ POS ]
                ]

        ].each { name, pr ->
            println "Generating metadata for " + name
            String desc = pr.description ?: pr.name
            ServiceMetadata metadata = makeMetadata(pr.name, desc, pr.requires, pr.produces)
            if (pr.format) {
                metadata.requires.format = pr.format
            }
            File file = new File("src/main/resources/metadata/${name}.json")
            file.text = metadata.toPrettyJson()
            println "Wrote ${file.path}"
        }
    }

//    static void main(args) {
//        new MetadataGenerator().run()
//    }
}
class GateMetadata extends ServiceMetadata {
    public GateMetadata() {
        version = "2.0.0-SNAPSHOT"
        vendor = "http://www.anc.org"
        allow = "http://vocab.lappsgrid.org/ns/allow/any"
    }
}

class GateSpecification extends IOSpecification {
    public GateSpecification() {
        format = [ GATE ]
        language = "en"
    }
}