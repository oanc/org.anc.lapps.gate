/*
 * Copyright (c) 2017 The American National Corpus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.anc.lapps.gate;

import gate.creole.tokeniser.DefaultTokeniser;
import org.junit.*;
import static org.junit.Assert.*;

import org.lappsgrid.api.WebService;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;

import java.util.List;
import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 * @author Keith Suderman
 */
public class TokenizerTest
{
	protected WebService tokenizer;
	String sentence = "Goodbye cruel world I am leaving you today. This is a test sentence. This is a third sentence to be used for testing.";

	public TokenizerTest()
	{

	}

	@Before
	public void setup()
	{
		tokenizer = new Tokenizer();
	}

	@After
	public void teardown()
	{
		tokenizer = null;
	}

	@Test
	public void testText() {
		Data<String> data = new Data<>(Uri.TEXT, sentence);
		String json = tokenizer.execute(data.asJson());
		assertNotNull(json);

		Data result = Serializer.parse(json, Data.class);
		assertEquals(result.getPayload().toString(), Uri.GATE, result.getDiscriminator());
	}
}
