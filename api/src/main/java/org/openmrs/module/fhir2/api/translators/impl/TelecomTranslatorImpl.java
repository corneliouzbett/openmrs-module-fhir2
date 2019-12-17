package org.openmrs.module.fhir2.api.translators.impl;

import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.ContactPoint;
import org.openmrs.PersonAttribute;
import org.openmrs.module.fhir2.api.translators.TelecomTranslator;
import org.springframework.stereotype.Component;

@Component
@Setter(AccessLevel.PACKAGE)
public class TelecomTranslatorImpl implements TelecomTranslator {

	/**
	 * @see org.openmrs.module.fhir2.api.translators.TelecomTranslator#toFhirResource(org.openmrs.PersonAttribute)
	 */
	@Override
	public ContactPoint toFhirResource(PersonAttribute attribute) {
		ContactPoint contactPoint = new ContactPoint();
		contactPoint.setId(attribute.getUuid());
		contactPoint.setValue(attribute.getValue());
		return contactPoint;
	}

	/**
	 * @see org.openmrs.module.fhir2.api.translators.TelecomTranslator#toOpenmrsType(org.hl7.fhir.r4.model.ContactPoint)
	 */
	@Override
	public PersonAttribute toOpenmrsType(ContactPoint contactPoint) {
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setUuid(contactPoint.getId());
		personAttribute.setValue(contactPoint.getValue());
		return personAttribute;
	}
}
