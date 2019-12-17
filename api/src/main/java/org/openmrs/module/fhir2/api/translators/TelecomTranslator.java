package org.openmrs.module.fhir2.api.translators;

import org.hl7.fhir.r4.model.ContactPoint;
import org.openmrs.PersonAttribute;

public interface TelecomTranslator
		extends OpenmrsFhirTranslator<org.openmrs.PersonAttribute, org.hl7.fhir.r4.model.ContactPoint> {

	/**
	 * Maps an OpenMRS personAttribute to a FHIR contactPoint
	 *
	 * @param attribute the OpenMRS person attribute to translate
	 * @return the corresponding ContactPoint resource
	 */
	@Override
	ContactPoint toFhirResource(PersonAttribute attribute);

	/**
	 * Maps a FHIR contactPoint to an OpenMRS PersonAttribute
	 *
	 * @param contactPoint the FHIR contactPoint to translate
	 * @return the corresponding OpenMRS PersonAttribute
	 */
	@Override
	PersonAttribute toOpenmrsType(ContactPoint contactPoint);
}
