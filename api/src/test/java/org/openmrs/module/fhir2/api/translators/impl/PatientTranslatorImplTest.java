/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.api.translators.impl;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;

import com.google.common.collect.Sets;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.module.fhir2.api.translators.AddressTranslator;
import org.openmrs.module.fhir2.api.translators.GenderTranslator;
import org.openmrs.module.fhir2.api.translators.PatientIdentifierTranslator;
import org.openmrs.module.fhir2.api.translators.PersonNameTranslator;

@RunWith(MockitoJUnitRunner.class)
public class PatientTranslatorImplTest {
	
	private static final String PATIENT_UUID = "123456-abcdef-123456";
	
	private static final String PATIENT_IDENTIFIER_UUID = "654321-fedcba-654321";
	
	private static final String PATIENT_GIVEN_NAME = "Jean Claude";
	
	private static final String PATIENT_FAMILY_NAME = "van Damme";
	
	private static final String ADDRESS_UUID = "135791-acegik-135791";
	
	private static final String ADDRESS_CITY = "Maputo";
	
	private static final Date PATIENT_DEATH_DATE = Date.from(Instant.ofEpochSecond(872986980L));
	
	@Mock
	private PatientIdentifierTranslator identifierTranslator;
	
	@Mock
	private PersonNameTranslator nameTranslator;
	
	@Mock
	private GenderTranslator genderTranslator;
	
	@Mock
	private AddressTranslator addressTranslator;
	
	private PatientTranslatorImpl patientTranslator;
	
	@Before
	public void setup() {
		patientTranslator = new PatientTranslatorImpl();
		patientTranslator.setIdentifierTranslator(identifierTranslator);
		patientTranslator.setNameTranslator(nameTranslator);
		patientTranslator.setGenderTranslator(genderTranslator);
		patientTranslator.setAddressTranslator(addressTranslator);
	}
	
	@Test
	public void shouldTranslateOpenmrsPatientToFhirPatient() {
		org.openmrs.Patient patient = new org.openmrs.Patient();
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result, notNullValue());
	}
	
	@Test
	public void shouldTranslatePatientUuidToFhirIdType() {
		org.openmrs.Patient patient = new org.openmrs.Patient();
		patient.setUuid(PATIENT_UUID);
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getId(), equalTo(PATIENT_UUID));
	}
	
	@Test
	public void shouldMarkVoidedPatientAsInactive() {
		org.openmrs.Patient patient = new org.openmrs.Patient();
		patient.setVoided(true);
		
		assertThat(patientTranslator.toFhirResource(patient).getActive(), is(false));
	}
	
	@Test
	public void shouldTranslateAlivePatientToAlive() {
		org.openmrs.Patient patient = new org.openmrs.Patient();
		patient.setDead(false);
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getDeceased(), notNullValue());
		assertThat(result.getDeceasedBooleanType(), notNullValue());
		assertThat(result.getDeceasedBooleanType().booleanValue(), is(false));
	}
	
	@Test
	public void shouldTranslateDeadPatientWithoutDeathDateToDead() {
		org.openmrs.Patient patient = new org.openmrs.Patient();
		patient.setDead(true);
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getDeceased(), notNullValue());
		assertThat(result.getDeceasedBooleanType(), notNullValue());
		assertThat(result.getDeceasedBooleanType().booleanValue(), is(true));
	}
	
	@Test
	public void shouldTranslateDeadPatientWithDeathDateToPatientWithDeathDate() {
		org.openmrs.Patient patient = new org.openmrs.Patient();
		patient.setDead(true);
		patient.setDeathDate(PATIENT_DEATH_DATE);
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getDeceased(), notNullValue());
		assertThat(result.getDeceasedDateTimeType(), notNullValue());
		assertThat(result.getDeceasedDateTimeType().getValue(), equalTo(PATIENT_DEATH_DATE));
	}
	
	@Test
	public void shouldTranslatePatientIdentifierToFhirIdentifier() {
		Identifier id = new Identifier();
		id.setValue(PATIENT_IDENTIFIER_UUID);
		when(identifierTranslator.toFhirResource(argThat(hasProperty("uuid", equalTo(PATIENT_IDENTIFIER_UUID)))))
		        .thenReturn(id);
		
		org.openmrs.Patient patient = new org.openmrs.Patient();
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setUuid(PATIENT_IDENTIFIER_UUID);
		patient.addIdentifier(patientIdentifier);
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getIdentifier(), not(empty()));
		assertThat(result.getIdentifier(), hasItem(hasProperty("value", equalTo(PATIENT_IDENTIFIER_UUID))));
	}
	
	@Test
	public void shouldTranslateOpenmrsPatientNameToFhirPatientName() {
		HumanName humanName = new HumanName();
		humanName.addGiven(PATIENT_GIVEN_NAME);
		humanName.setFamily(PATIENT_FAMILY_NAME);
		when(
		    nameTranslator.toFhirResource(argThat(allOf(hasProperty("givenName", equalTo(PATIENT_GIVEN_NAME)),
		        hasProperty("familyName", equalTo(PATIENT_FAMILY_NAME)))))).thenReturn(humanName);
		
		org.openmrs.Patient patient = new org.openmrs.Patient();
		PersonName name = new PersonName();
		name.setGivenName(PATIENT_GIVEN_NAME);
		name.setFamilyName(PATIENT_FAMILY_NAME);
		patient.setNames(Sets.newHashSet(name));
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getName(), not(empty()));
		assertThat(result.getName().get(0), notNullValue());
		assertThat(result.getName().get(0).getGivenAsSingleString(), equalTo(PATIENT_GIVEN_NAME));
		assertThat(result.getName().get(0).getFamily(), equalTo(PATIENT_FAMILY_NAME));
	}
	
	@Test
	public void shouldTranslateOpenMRSPatientGenderToFhirGender() {
		when(genderTranslator.toFhirResource(argThat(equalTo("M")))).thenReturn(Enumerations.AdministrativeGender.MALE);
		
		org.openmrs.Patient patient = new org.openmrs.Patient();
		patient.setGender("M");
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getGender(), equalTo(Enumerations.AdministrativeGender.MALE));
	}
	
	@Test
	public void shouldTranslateOpenMRSAddressToFhirAddress() {
		Address address = new Address();
		address.setId(ADDRESS_UUID);
		address.setCity(ADDRESS_CITY);
		when(addressTranslator.toFhirResource(argThat(hasProperty("uuid", equalTo(ADDRESS_UUID))))).thenReturn(address);
		
		org.openmrs.Patient patient = new org.openmrs.Patient();
		PersonAddress personAddress = new PersonAddress();
		personAddress.setUuid(ADDRESS_UUID);
		personAddress.setCityVillage(ADDRESS_CITY);
		patient.addAddress(personAddress);
		
		Patient result = patientTranslator.toFhirResource(patient);
		assertThat(result.getAddress(), not(empty()));
		assertThat(result.getAddress(), hasItem(hasProperty("id", equalTo(ADDRESS_UUID))));
		assertThat(result.getAddress(), hasItem(hasProperty("city", equalTo(ADDRESS_CITY))));
	}
	
	@Test
	public void shouldTranslateFhirPatientToOpenmrsPatient() {
		Patient patient = new Patient();
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result, notNullValue());
	}
	
	@Test
	public void shouldTranslateFhirPatientIdToUuid() {
		Patient patient = new Patient();
		patient.setId(PATIENT_UUID);
		
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result.getUuid(), equalTo(PATIENT_UUID));
	}
	
	@Test
	public void shouldVoidInactivePatient() {
		Patient patient = new Patient();
		patient.setActive(false);
		
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result.getVoided(), is(true));
		assertThat(result.getVoidReason(), equalTo("Voided by FHIR module"));
	}
	
	@Test
	public void shouldTranslateAliveFhirPatientToAlive() {
		Patient patient = new Patient();
		patient.setDeceased(new BooleanType(false));
		
		assertThat(patientTranslator.toOpenmrsType(patient).getDead(), is(false));
	}
	
	@Test
	public void shouldTranslateDeceasedPatientToDeceased() {
		Patient patient = new Patient();
		patient.setDeceased(new BooleanType(true));
		
		assertThat(patientTranslator.toOpenmrsType(patient).getDead(), is(true));
	}
	
	@Test
	public void shouldTranslateDeathDateToDeceasedPatient() {
		Patient patient = new Patient();
		patient.setDeceased(new DateTimeType(PATIENT_DEATH_DATE));
		
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result.getDead(), is(true));
		assertThat(result.getDeathDate(), equalTo(PATIENT_DEATH_DATE));
	}
	
	@Test
	public void shouldTranslateFhirIdentifierToPatientIdentifier() {
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setUuid(PATIENT_IDENTIFIER_UUID);
		when(identifierTranslator.toOpenmrsType(argThat(hasProperty("value", equalTo(PATIENT_IDENTIFIER_UUID)))))
		        .thenReturn(patientIdentifier);
		
		Patient patient = new Patient();
		Identifier id = new Identifier();
		id.setValue(PATIENT_IDENTIFIER_UUID);
		patient.addIdentifier(id);
		
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result.getPatientIdentifier(), notNullValue());
		assertThat(result.getPatientIdentifier(), hasProperty("uuid", equalTo(PATIENT_IDENTIFIER_UUID)));
	}
	
	@Test
	public void shouldTranslateFhirPatientNameToOpenmrsPatientName() {
		PersonName personName = new PersonName();
		personName.setGivenName(PATIENT_GIVEN_NAME);
		personName.setFamilyName(PATIENT_FAMILY_NAME);
		when(nameTranslator.toOpenmrsType(any())).thenReturn(personName);
		
		Patient patient = new Patient();
		HumanName name = new HumanName();
		name.addGiven(PATIENT_GIVEN_NAME);
		name.setFamily(PATIENT_FAMILY_NAME);
		patient.addName(name);
		
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result.getGivenName(), equalTo(PATIENT_GIVEN_NAME));
		assertThat(result.getFamilyName(), equalTo(PATIENT_FAMILY_NAME));
	}
	
	@Test
	public void shouldTranslateFhirPatientGenderToOpenmrsGender() {
		when(genderTranslator.toOpenmrsType(Enumerations.AdministrativeGender.FEMALE)).thenReturn("F");
		
		Patient patient = new Patient();
		patient.setGender(Enumerations.AdministrativeGender.FEMALE);
		
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result.getGender(), equalTo("F"));
	}
	
	@Test
	public void shouldTranslateFhirAddressToOpenMRSAddress() {
		PersonAddress personAddress = new PersonAddress();
		personAddress.setUuid(ADDRESS_UUID);
		personAddress.setCityVillage(ADDRESS_CITY);
		when(addressTranslator.toOpenmrsType(argThat(hasProperty("id", equalTo(ADDRESS_UUID))))).thenReturn(personAddress);
		
		Patient patient = new Patient();
		Address address = new Address();
		address.setId(ADDRESS_UUID);
		address.setCity(ADDRESS_CITY);
		patient.addAddress(address);
		
		org.openmrs.Patient result = patientTranslator.toOpenmrsType(patient);
		assertThat(result.getAddresses(), not(empty()));
		assertThat(result.getAddresses(), hasItem(hasProperty("uuid", equalTo(ADDRESS_UUID))));
		assertThat(result.getAddresses(), hasItem(hasProperty("cityVillage", equalTo(ADDRESS_CITY))));
	}
	
}
