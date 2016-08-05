package org.anc.lapps.gate

import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

/**
 * @author Keith Suderman
 */
class SerializerTest {
    public static void main(String[] args) {
        File file = new File('/var/corpora/Yemen/XIN_ENG_20071218.0267.lif')
        File directory = new File('/var/corpora/Yemen')
        FileFilter filter = { File f -> f.name.endsWith('.lif') }
        directory.listFiles(filter).each { File f ->
            println "Parsing ${f.path}"
            Data data = Serializer.parse(f.text, Data)
            println data.asPrettyJson()
        }
    }
}
