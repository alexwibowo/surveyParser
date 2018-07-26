# Survey Parser

[![Build Status](https://travis-ci.org/alexwibowo/surveyParser.svg?branch=master)](https://travis-ci.org/alexwibowo/surveyParser)
[![BCH compliance](https://bettercodehub.com/edge/badge/alexwibowo/surveyParser?branch=master)](https://bettercodehub.com/)

To build the application, run

```
gradle clean build shadowJar
```

This will create runnable 'application-all.jar' inside
```
BASE/application/build/libs folder
```

## Modules

There are three modules:
* Model
* IO
* Application

---------------------------------------

### Model

This module contains all the POJO used throughout the project

I have modeled 'Theme' and 'QuestionType' as enum, assuming that they are not 'dynamic' in nature.
I.e. the CSV survey file cant just define new theme / question type.

---------------------------------------

### IO

This module is used to construct the POJO. E.g. from CSV parser, to print the summary into console

There are four main classes:

#### CsvSurveyReader

Parses the CSV question file

#### CsvSurveyResponseReader

Parses the CSV response file, and keep all responses in memory. This parser is not suitable to parse large file.
However, as it keeps all the responses in memory, it is easier to 'query' them.

#### CsvStreamingSurveyResponseReader

Parses the CSV response file, and only keep the stats in memory. Although it is memory friendly, it is not as flexible when it comes to answering new query.

#### LoggerSurveyResponseSummaryRenderer

To print the survey response to logger

---------------------------------------

### Application

Application runner.
