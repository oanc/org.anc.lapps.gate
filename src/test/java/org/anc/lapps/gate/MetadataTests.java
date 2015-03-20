package org.anc.lapps.gate;

import com.github.fge.jsonschema.core.report.*;
import org.anc.json.validator.Validator;
import org.junit.*;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;
import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 * @author Keith Suderman
 */
public class MetadataTests
{
	private Validator validator;

	@Before
	public void setup()
	{
		URL url = this.getClass().getResource("/service-schema.json");
		validator = new Validator(url);
	}

	@After
	public void cleanup()
	{
		validator = null;
	}

	@Test
	public void coreferencer () throws InstantiationException, IllegalAccessException
	{
		check(Coreferencer.class);
//		check(Gazetteer.class);
//		check(NamedEntityRecognizer.class);
//		check(NounPhraseChunker.class);
//		check(OrthoMatcher.class);
//		check(SentenceSplitter.class);
//		check(Tagger.class);
//		check(Tokenizer.class);
//		check(VerbPhraseChunker.class);
	}

	@Test
	public void gazetteer() throws InstantiationException, IllegalAccessException
	{
		check(Gazetteer.class);
	}

	@Test
	public void ner() throws InstantiationException, IllegalAccessException
	{
		check(NamedEntityRecognizer.class);
	}

	@Test
	public void npchunker() throws InstantiationException, IllegalAccessException
	{
		check(NounPhraseChunker.class);
	}

	@Test
	public void orthoMatcher() throws InstantiationException, IllegalAccessException
	{
		check(OrthoMatcher.class);
	}

	@Test
	public void splitter() throws InstantiationException, IllegalAccessException
	{
		check(SentenceSplitter.class);
	}

	@Test
	public void tagger() throws InstantiationException, IllegalAccessException
	{
		check(Tagger.class);
	}

	@Test
	public void tokenizer() throws InstantiationException, IllegalAccessException
	{
		check(Tokenizer.class);
	}

	@Test
	public void vpchunker() throws InstantiationException, IllegalAccessException
	{
		check(VerbPhraseChunker.class);
	}

	private void check(Class<? extends WebService> serviceClass) throws IllegalAccessException, InstantiationException
	{
		System.out.println("Validating " + serviceClass.getCanonicalName());
		WebService service = serviceClass.newInstance();
		String json = service.getMetadata();
		assertNotNull(json);
		Data<Map> data = Serializer.parse(json, Data.class);
		assertEquals(data.getDiscriminator(), Uri.META);
		validate(data.asJson());
		ServiceMetadata metadata = new ServiceMetadata(data.getPayload());
		assertNotNull(metadata);
		assertEquals(metadata.getVersion(), Version.getVersion());
		assertEquals(metadata.getVendor(), "http://www.anc.org");
		assertEquals(metadata.getName(), serviceClass.getCanonicalName());
	}

	private void validate(String json)
	{
		ProcessingReport report = validator.validate(json);
		if (!report.isSuccess())
		{
			for (ProcessingMessage message : report)
			{
				System.out.println(message.getMessage());
			}
			fail("Validation failed.");
		}
	}
}
