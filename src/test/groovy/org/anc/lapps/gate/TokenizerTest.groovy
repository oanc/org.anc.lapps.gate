package org.anc.lapps.gate

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.api.WebService
import org.lappsgrid.core.DataFactory
import org.lappsgrid.discriminator.Constants
import org.lappsgrid.discriminator.DiscriminatorRegistry
import org.lappsgrid.discriminator.Helpers
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals
import static org.lappsgrid.discriminator.Discriminators.Uri;
import static org.lappsgrid.discriminator.Discriminators.Values;

import static org.junit.Assert.assertTrue

/**
 * @author Keith Suderman
 */
class TokenizerTest {
    @Test
    void tokenizerTest() {
        String text = "Hello world. Goodbye cruel world."
        String input = DataFactory.text(text)
        WebService service = new Tokenizer()
        String json = service.execute(input)
        Data result = Serializer.parse(json, Data)
        assertNotEquals(result.payload, result.getDiscriminator(), Uri.ERROR);
        assertEquals("Wrong discriminator type. Found " + result.getDiscriminator(), result.getDiscriminator(), Uri.GATE);

//        println result.payload
//        Container container = new Container(result.payload)
//        println container.toPrettyJson()
    }

    @Test
    void discriminatorTest() {
        assertTrue Values.TOKEN == DiscriminatorRegistry.get("token")
        assertTrue Values.TOKEN == DiscriminatorRegistry.get(Uri.TOKEN)
    }
}
