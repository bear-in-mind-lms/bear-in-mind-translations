# Bear in Mind | Translations

The Bear in Mind is a free open-source LMS (learning management system). This is the library that provides support for
multilingual texts.

## Setup

### Maven

`settings.xml > profiles > profile > repositories`

```xml

<repository>
    <id>bear-in-mind-translations</id>
    <releases>
        <enabled>true</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
    <url>https://github.com/bear-in-mind-lms/bear-in-mind-translations/raw/mvn-artifact</url>
</repository>
```

`pom.xml`

```xml

<project>
    <properties>
        <bear-in-mind-translations.version>0.0.1</bear-in-mind-translations.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.kwezal.bearinmind</groupId>
            <artifactId>bear-in-mind-translations</artifactId>
            <version>${bear-in-mind-translations.version}</version>
        </dependency>
    </dependencies>
</project>
```

## Configuration

### YAML

`application.yml`

```yaml
application:
  locale: en
```

## Database schema

### Liquibase

`changelog-master.yml`

```yaml
databaseChangeLog:
  - includeAll:
      path: classpath*:com/kwezal/bearinmind/translation/db/changelog/changes
```

## Contribution

Your contribution is welcome and we appreciate it. 💝 Before you start, please make sure you have read
the [information for contributors][contributing].

## Code of Conduct

This project is governed by the [Bear in Mind Code of Conduct][conduct]. By participating, you are expected to uphold
this code of conduct.

## License

Bear in Mind Translations is released under the [Apache 2.0 License][license].

[contributing]: https://github.com/bear-in-mind-lms/bear-in-mind-core/blob/main/CONTRIBUTING.md

[conduct]: https://github.com/bear-in-mind-lms/bear-in-mind-core/blob/main/CODE_OF_CONDUCT.md

[license]: https://www.apache.org/licenses/LICENSE-2.0