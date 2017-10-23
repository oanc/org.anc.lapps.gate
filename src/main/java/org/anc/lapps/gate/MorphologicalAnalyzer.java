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
import gate.FeatureMap;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.core.DataFactory;
import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 *
 */
@ServiceMetadata(
		description = "GATE Morphological Analyser",
		requires = { "http://vocab.lappsgrid.org/Token"}, //"http://vocab.lappsgrid.org/Token#pos", "http://vocab.lappsgrid.org/Sentence"},
		produces = "http://vocab.lappsgrid.org/Token#lemma"
)

public class MorphologicalAnalyzer extends SimpleGateService
{
	public MorphologicalAnalyzer()
	{
		super(MorphologicalAnalyzer.class);
		createResource("gate.creole.morph.Morph");
	}

	@Override
	public String execute(String input)
	{
		Document document = null;
		try
		{
			FeatureMap features = Factory.newFeatureMap();
			features.put("rootFeatureName", "lemma");
			document = doExecute(input, Uri.TOKEN, features);
		}
		catch (Exception e)
		{
			return DataFactory.error("Unable to execute the Tokenizer.", e);
		}
		if (document == null)
		{
			return DataFactory.error(BUSY);
		}


		String result = DataFactory.gateDocument(document.toXml());
		Factory.deleteResource(document);
		return result;
	}
}
