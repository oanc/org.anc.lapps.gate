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

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import org.apache.poi.ss.formula.functions.FactDouble;
import org.junit.Test;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

/**
 *
 */
public class MorphologicalAnalyzerTest
{
	public MorphologicalAnalyzerTest()
	{

	}

	@Test
	public void testMophologicalAnalyzer() throws ResourceInstantiationException
	{
		InputStream stream = this.getClass().getResourceAsStream("/karen-tagged.xml");
		assert stream != null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String xml = reader.lines().collect(Collectors.joining("\n"));
		Data data = new Data(Discriminators.Uri.GATE, xml);

		WebService service = new MorphologicalAnalyzer();
		String json = service.execute(data.asJson());

		data = Serializer.parse(json);
		System.out.println(data.getPayload());

		Document document = Factory.newDocument(data.getPayload().toString());
		Object feature = document.getFeatures().get("lapps:" + Discriminators.Uri.LEMMA);
		assert feature != null;
//		System.out.println(feature.toString());
	}
}
