# Pragmasoft project

## Java 17 project template

### Build

`./mvnw package`

### Test

`./mvnw test`

### Run

`./mvnw exec:java`

## Formatting

This project uses automatic validation of source formatting rules. Format validation is performed automatically during validation phase, that is, first phase of maven build lifecycle, so any of the commands below also performs validation.

To skip format validation, use formatter.skip system property, for example

`./mvnw -Dformatter.skip validate`

To automatically reformat all sources according to rules, you can use following command

`./mvnw formatter:format`

You can also use formatter configuration `${project.basedir}/eclipse-java-google-style.xml` to set up formatting rules in your IDE.

These rules are in the format native to Eclipse formatter, so in other IDEs like IDEA, you will need
special [plugin](https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter) installed to be able to use this configuration.

You can also copy or symlink a pre-commit git hook from `src/main/git/hooks` to `.git/hooks`, which will automatically validate formatting rules
before git commits.

Alternatively, you may wish to edit hook to automatically reformat `./mvnw formatter:format` code, instead of validation
