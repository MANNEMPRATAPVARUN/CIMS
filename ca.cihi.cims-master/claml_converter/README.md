# ClaMl PDF Conversion

This application converts ICD10 from JSON to XML and PDF. The instructions assume that a user is on a Windows computer
and using Windows Command Prompt to execute each step.

## Prerequisites

* Java 11
* Maven 3.6+

The data files for conversion must be made available and placed in the `data/` directory, e.g.

* `CCI_2022_ENG.json`
* `CCI_2022_FRA.json`
* `ICD-10-CA_2022_ENG.json`
* `CD-10-CA_2022_FRA.json`

Place English language image files in `data/images/en`
Place English language image files in `data/images/fr`

## Compile the application

From the cloned repository directory, run

```
make build
```

## CihiJsonReader

This class generates all the json paths and their values. Used purely to find all the available fields in the JSON, not
directly useful for producing Claml or PDF content. An example of the output produced is below. The following JSON

```json
{
  "test": {
    "standalone-key": "standalone-value",
    "parent-key": {
      "child-key": "child-value"
    }
  }
}
```

will produce the following text file

```text
test.standalone-key: "standalone-value"
test.parent-key.child-key: "child-value"
```

There is only one public static method called `parseJson` which takes a json string and `PrintStream` to write the
output. An example of this call is available in the main method of this class.

Run this step to generate json paths from `CCI_2022_ENG.json`.

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.CihiJsonReader \
    data/CCI_2022_ENG.json \
    data/cci_json_paths_with_value.txt
```

## ClamlConverter (EN and FR)

This process converts the ICD-10-CA JSON file to XML. The constructor for this class sets it up. Check the documentation
on the constructor to see what we are expecting to be passed in. See `main` method for an example. There is one public
method called `convert` which takes no parameters.

Run once for English and again for French.

**English command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.ClamlConverter EN \
    data/ICD-10-CA_2022_ENG.json \
    data/images/en \
    data/claml-eng.xml
```

**French command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.ClamlConverter FR \
    data/ICD-10-CA_2022_FRA.json \
    data/images/fr \
    data/claml-fra.xml
```

## ClamlToPdfConverter (EN and FR)

This process converts the ICD-10-CA CLAML to PDF. The constructor for this class sets it up. This is a 2-step process.
First step is to convert CLAML to HTML with `convertClamlToHtml` method. The next step is to convert that HTML to PDF
with the `convert` method. This process takes some time. So there are some flags and fields that can omit rendering all
content for testing specific parts of it. See documentation on constructor, methods and fields for more information.
See `main` method for an example.

Run once for English and again for French.

**English command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.ClamlToPdfConverter EN \
    data \
    data/claml-eng.xml \
    data/claml-eng.html \
    data/claml-eng.pdf
```

**French command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.ClamlToPdfConverter FR \
    data \
    data/claml-fra.xml \
    data/claml-fra.html \
    data/claml-fra.pdf
```

**CciClamlConverter (EN and FR)**

This process converts the CCI JSON file to XML. The constructor for this class sets it up. Check the documentation on
the constructor to see what we are expecting to be passed in. There is one public method called `convert` which takes no
parameters. See `main` method for an example.

**English command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.CciClamlConverter EN \
    data/CCI_2022_ENG.json \
    data/images/en \
    data/claml-cci-eng.xml
```

**French command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.CciClamlConverter FR \
    data/CCI_2022_FRA.json \
    data/images/fr \
    data/claml-cci-fra.xml
```

**CciClamlToPdfConverter (EN and FR)**

This process converts the CCI CLAML to PDF. The constructor for this class sets it up. This is a 2-step process.
First step is to convert CLAML to HTML with `convertClamlToHtml` method. The next step is to convert that HTML to PDF
with the `convert` method. This process takes some time. So there are some flags and fields that can omit rendering all
content for testing specific parts of it. See documentation on constructor, methods and fields for more information.
See `main` method for an example.

**English command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.CciClamlToPdfConverter EN \
    data/claml-cci-eng.xml \
    data/claml-cci-eng.html \
    data/claml-cci-eng.pdf
```

**French command**

```
java -cp target/claml-converter-1.0-SNAPSHOT.jar \
    org.cihi.claml.converter.CciClamlToPdfConverter FR \
    data/claml-cci-fra.xml \
    data/claml-cci-fra.html \
    data/claml-cci-fra.pdf
```

**Source code commands**

Remove build products

```
make clean
```

Compile Java code

```
make build
```

Builds and runs all unit tests and generates the code coverage report To view the report, open file
target/site/jacoco/index.html

```
make test
```

Builds and installs to local repository

```
make install
```

Publish artifacts to Nexus (requires Nexus or different Maven repository)

```
make package
```

Prints the version of the application

```
make version
```




