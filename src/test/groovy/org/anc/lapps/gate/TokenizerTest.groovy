package org.anc.lapps.gate

import org.anc.lapps.serialization.Container
import org.junit.Test
import org.lappsgrid.api.Data
import org.lappsgrid.api.WebService
import org.lappsgrid.core.DataFactory
import org.lappsgrid.discriminator.Types

import static org.junit.Assert.assertTrue

/**
 * @author Keith Suderman
 */
class TokenizerTest {
    @Test
    void tokenizerTest() {
        String text = "Hello world. Goodbye cruel world."
        Data input = DataFactory.text(text)
        WebService service = new Tokenizer()
        Data result = service.execute(input)
        assertTrue(result.payload, result.discriminator != Types.ERROR)
        Container container = new Container(result.payload)
        println container.toPrettyJson()
    }
}
