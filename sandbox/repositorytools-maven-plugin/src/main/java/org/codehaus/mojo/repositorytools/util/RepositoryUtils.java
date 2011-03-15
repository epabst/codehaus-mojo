package org.codehaus.mojo.repositorytools.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.repositorytools.validation.ArtifactValidator;
import org.codehaus.mojo.repositorytools.validation.ValidationMessage;

public class RepositoryUtils
{

	public static void printValidation(Log log, Map result)
	{
		for (Iterator iterator = result.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			ArtifactValidator key = (ArtifactValidator) entry.getKey();
			List value = (List) entry.getValue();
			log.info("Validation: " + key.getDescription());
			if (value.isEmpty()) {
				log.info("\tNo messages");
			} else {
				for (Iterator iterator2 = value.iterator(); iterator2.hasNext();) {
					ValidationMessage message = (ValidationMessage) iterator2.next();
					switch (message.getSeverity())
					{
					case ValidationMessage.ERROR:
						log.error(message.getMessage());
						break;
					case ValidationMessage.INFO:
						log.info(message.getMessage());
						break;
					case ValidationMessage.WARNING:
						log.warn(message.getMessage());
					}
				}
			}
		}
	}
}
