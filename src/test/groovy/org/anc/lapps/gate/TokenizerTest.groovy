package org.anc.lapps.gate

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.api.Data
import org.lappsgrid.api.WebService
import org.lappsgrid.core.DataFactory
import org.lappsgrid.discriminator.Discriminator
import org.lappsgrid.discriminator.DiscriminatorRegistry
import org.lappsgrid.discriminator.Helpers
import org.lappsgrid.discriminator.Types
import org.lappsgrid.discriminator.Uri

import static org.junit.Assert.assertTrue

/**
 * @author Keith Suderman
 */
class TokenizerTest {
    @Ignore
    void tokenizerTest() {
        String text = "Hello world. Goodbye cruel world."
        Data input = DataFactory.text(text)
        WebService service = new Tokenizer()
        Data result = service.execute(input)
        assertTrue(result.payload, Helpers.type(result) != Types.ERROR)
        println result.payload
//        Container container = new Container(result.payload)
//        println container.toPrettyJson()
    }

    @Test
    void discriminatorTest() {
        assertTrue Types.TOKEN == DiscriminatorRegistry.get("token")
        assertTrue Types.TOKEN == DiscriminatorRegistry.get(Uri.TOKEN)
    }
}
