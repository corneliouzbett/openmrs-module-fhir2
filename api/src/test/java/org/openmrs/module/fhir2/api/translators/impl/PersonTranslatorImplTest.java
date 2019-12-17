/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.api.translators.impl;

import com.google.common.collect.Sets;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.module.fhir2.api.translators.GenderTranslator;
import org.openmrs.module.fhir2.api.translators.PersonNameTranslator;
import org.openmrs.module.fhir2.api.translators.TelecomTranslator;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonTranslatorImplTest {

	private static final String PERSON_FAMILY_NAME = "bett";

	private static final String PERSON_UUID = "1223et-098342-2723bsd";

	private static final String PERSON_GIVEN_NAME = "cornelious";

	private static final String CONTACT_VALUE = "0712893493";

	private static final String PERSON_ATTRIBUTE_CONTACT_UUID = "1232-3434-3434-sd45";

	private static final String PERSON_ATTRIBUTE_TYPE_VALUE = "contact";

	private static final int PERSON_ATTRIBUTE_TYPE_ID = 8;

	@Mock
	private GenderTranslator genderTranslator;

	@Mock
	private PersonNameTranslator nameTranslator;

	@Mock
	private TelecomTranslator telecomTranslator;

	private PersonTranslatorImpl personTranslator;

	private org.openmrs.Person person;

	@Before
	public void setup() {
		person = new Person();
		personTranslator = new PersonTranslatorImpl();
		personTranslator.setGenderTranslator(genderTranslator);
		personTranslator.setNameTranslator(nameTranslator);
		personTranslator.setTelecomTranslator(telecomTranslator);
	}

	@Test
	public void shouldTranslateOpenmrsPersonToFhirPerson() {
		assertNotNull(person);
		org.hl7.fhir.r4.model.Person result = personTranslator.toFhirResource(person);
		assertNotNull(result);
	}

	@Test
	public void shouldTranslatePersonUuidToFhirIdType() {
		person.setUuid(PERSON_UUID);
		org.hl7.fhir.r4.model.Person result = personTranslator.toFhirResource(person);
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(result.getId(), PERSON_UUID);
	}

	@Test
	public void shouldTranslateOpenmrsGenderToFhirGenderType() {
		when(genderTranslator.toFhirResource(argThat(equalTo("F")))).thenReturn(Enumerations.AdministrativeGender.FEMALE);
		person.setGender("F");

		org.hl7.fhir.r4.model.Person result = personTranslator.toFhirResource(person);
		assertNotNull(result);
		assertNotNull(result.getGender());
		assertEquals(result.getGender(), Enumerations.AdministrativeGender.FEMALE);

	}

	@Test
	public void shouldTranslateUnVoidedPersonToActive() {
		assertNotNull(person);
		person.setVoided(false);
		org.hl7.fhir.r4.model.Person  result = personTranslator.toFhirResource(person);
		assertNotNull(result);
		assertThat(result.getActive(), is(true));
	}

	@Test
	public void shouldTranslateVoidedPersonToInactive() {
		assertNotNull(person);
		person.setVoided(true);
		org.hl7.fhir.r4.model.Person  result = personTranslator.toFhirResource(person);
		assertNotNull(result);
		assertThat(result.getActive(), is(false));
	}

	@Test
	public void shouldTranslateOpenmrsPersonNameToFhirPersonName() {
		HumanName humanName = new HumanName();
		humanName.addGiven(PERSON_GIVEN_NAME);
		humanName.setFamily(PERSON_FAMILY_NAME);
		when(
				nameTranslator.toFhirResource(argThat(allOf(hasProperty("givenName", equalTo(PERSON_GIVEN_NAME)),
						hasProperty("familyName", equalTo(PERSON_FAMILY_NAME)))))).thenReturn(humanName);

		org.openmrs.Person person = new org.openmrs.Person();
		PersonName name = new PersonName();
		name.setGivenName(PERSON_GIVEN_NAME);
		name.setFamilyName(PERSON_FAMILY_NAME);
		person.setNames(Sets.newHashSet(name));

		org.hl7.fhir.r4.model.Person result = personTranslator.toFhirResource(person);
		assertThat(result.getName(), not(empty()));
		assertThat(result.getName().get(0), notNullValue());
		assertThat(result.getName().get(0).getGivenAsSingleString(), equalTo(PERSON_GIVEN_NAME));
		assertThat(result.getName().get(0).getFamily(), equalTo(PERSON_FAMILY_NAME));
	}

	@Test
	public void shouldAddPersonLinksIfPatient() {
		person.setUuid(PERSON_UUID);
		org.hl7.fhir.r4.model.Person result = personTranslator.toFhirResource(person);
		assertNotNull(result);
		assertNotNull(result.getLink());
	}

	@Test
	public void shouldTranslateContactPersonAttributeToFhirTelecomType() {
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setUuid(PERSON_ATTRIBUTE_CONTACT_UUID);
		personAttribute.setValue(CONTACT_VALUE);
		PersonAttributeType attributeType  = new PersonAttributeType();
		attributeType.setId(PERSON_ATTRIBUTE_TYPE_ID);
		attributeType.setName(PERSON_ATTRIBUTE_TYPE_VALUE);
		personAttribute.setAttributeType(attributeType);
		person.addAttribute(personAttribute);
		assertNotNull(person);
		org.hl7.fhir.r4.model.Person result = personTranslator.toFhirResource(person);
		assertNotNull(result);
		assertNotNull(result.getTelecom());
		assertFalse(result.getTelecom().isEmpty());
	}

}
