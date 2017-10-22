package org.anc.lapps.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.vocabulary.Annotations;

import java.util.Iterator;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
		description = "GATE Named Entity Recognizer",
		requires = {"http://vocab.lappsgrid.org/Lookup"},
		produces = {"http://vocab.lappsgrid.org/NamedEntity"}
)
public class NamedEntityRecognizer extends SimpleGateService
{
	public NamedEntityRecognizer()
	{
		super(NamedEntityRecognizer.class);
		createResource("gate.creole.ANNIETransducer");
	}

	public String execute(String input)
	{
		Document document = null;
		try
		{
			document = super.doExecute(input);
		}
		catch (Exception e)
		{
			return DataFactory.error("Unable to execute the Named Entity Recognizer.", e);
		}

		if (document == null)
		{
			return DataFactory.error("This was unexpected... the GATE NER component returned a NULL document.");
		}

		String producer = this.getClass().getName() + "_" + Version.getVersion();
		FeatureMap features = document.getFeatures();
		Integer step = (Integer) features.get("lapps:step");
		if (step == null)
		{
			step = 1;
		}
		AnnotationSet set = document.getAnnotations();
		Iterator<Annotation> iterator = set.iterator();
		boolean hasLocation = false;
		boolean hasPerson = false;
		boolean hasOrganization = false;
		// Counter so we know to stop searching once we've found all three
		// annotation types
		int found = 0;
		while (iterator.hasNext() && found < 3)
		{
			Annotation annotation = iterator.next();
			String type = annotation.getType();
			if ("Location".equals(type) && !hasLocation)
			{
				++found;
				hasLocation = true;
			} else if ("Organization".equals(type) && !hasOrganization)
			{
				++found;
				hasOrganization = true;
			} else if ("Person".equals(type) && !hasPerson)
			{
				++found;
				hasPerson = true;
			}
		}
		features.put("lapps:step", step + 1);

		// TODO The Annotations class has changed and the feature values added should be rolled
		// back to what they were originally.
//		if (hasLocation)
//		{
//			features.put("lapps:" + Discriminators.Uri.LOCATION, step + " " + producer + " gate");
//		}
//		if (hasPerson)
//		{
//			features.put("lapps:" + Discriminators.Uri.PERSON, step + " " + producer + " gate");
//		}
//		if (hasOrganization)
//		{
//			features.put("lapps:" + Discriminators.Uri.ORGANIZATION, step + " " + producer + " gate");
//		}
		if (hasLocation || hasPerson || hasOrganization) {
			features.put("lapps:" + Discriminators.Uri.NE, step + " " + producer + " gate");
		}
		String result = DataFactory.gateDocument(document.toXml());
		Factory.deleteResource(document);
		return result;
	}
}
