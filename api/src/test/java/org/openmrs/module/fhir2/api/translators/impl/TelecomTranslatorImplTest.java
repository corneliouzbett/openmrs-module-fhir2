package org.openmrs.module.fhir2.api.translators.impl;

import org.hl7.fhir.r4.model.ContactPoint;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttribute;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class TelecomTranslatorImplTest {

	private static final String CONTACT_ATTRIBUTE_UUID = "12233-34b34-345234";

	private static final String CONTACT_VALUE = "0723781237";

	private TelecomTranslatorImpl telecomTranslator;

	@Before
	public void setUp() {
		this.telecomTranslator = new TelecomTranslatorImpl();
	}

	@Test
	public void shouldTranslatePersonAttributeIdToFhirType() {
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setUuid(CONTACT_ATTRIBUTE_UUID);
		assertThat(personAttribute, notNullValue());

		ContactPoint result = telecomTranslator.toFhirResource(personAttribute);
		assertThat(result, notNullValue());
		assertThat(result.getId(), is(CONTACT_ATTRIBUTE_UUID));
	}

	@Test
	public void shouldTranslatePersonAttributeValueToFhirType() {
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setValue(CONTACT_VALUE);
		assertThat(personAttribute, notNullValue());

		ContactPoint result = telecomTranslator.toFhirResource(personAttribute);
		assertThat(result, notNullValue());
		assertThat(result.getValue(), is(CONTACT_VALUE));
	}

	@Test
	public void shouldTranslateContactPointIdToOpenmrsType() {
		ContactPoint contactPoint = new ContactPoint();
		contactPoint.setId(CONTACT_ATTRIBUTE_UUID);
		assertThat(contactPoint, notNullValue());

		PersonAttribute personAttribute = telecomTranslator.toOpenmrsType(contactPoint);
		assertThat(personAttribute, notNullValue());
		assertThat(personAttribute.getUuid(), equalTo(CONTACT_ATTRIBUTE_UUID));
	}

	@Test
	public void shouldTranslateContactPointValueToOpenmrsType() {
		ContactPoint contactPoint = new ContactPoint();
		contactPoint.setValue(CONTACT_VALUE);
		assertThat(contactPoint, notNullValue());

		PersonAttribute personAttribute = telecomTranslator.toOpenmrsType(contactPoint);
		assertThat(personAttribute, notNullValue());
		assertThat(personAttribute.getValue(), equalTo(CONTACT_VALUE));
	}

}
