openmrs-module-fhir2
==========================
[![Build Status](https://travis-ci.com/ibacher/openmrs-module-fhir2.svg?branch=master)](https://travis-ci.com/ibacher/openmrs-module-fhir2)
[![codecov](https://codecov.io/gh/ibacher/openmrs-module-fhir2/branch/master/graph/badge.svg)](https://codecov.io/gh/ibacher/openmrs-module-fhir2)

Description
-----------
This is intended to be a replacement for the current OpenMRS FHIR module,
using FHIR R4.

Development Principles
----------------------

There are a couple of things that are not standard practice for other OpenMRS
modules that should be borne in mind while developing this module.

1. The OpenMRS service layer, e.g. `PatientService`, `EncounterService` etc.
should only be used inside DAO objects.
1. Favour JSR-330 style `@Inject` over `@Autowired` whenever possible. This
will allow us to de-couple from Spring if necessary.
1. Avoid using `Context` except as a last resort
