# Changelog

Tutte le modifiche rilevanti a questo progetto sono documentate in questo file.

Il formato si ispira a [Keep a Changelog](https://keepachangelog.com/it/1.1.0/)
e il progetto aderisce al [Versionamento Semantico](https://semver.org/lang/it/).

## [Unreleased]

### Changed
- Riordino del repository: README riscritto, struttura cartelle normalizzata, asset statici rinominati.
- `pom.xml`: rimosso `spring-boot-maven-plugin` erroneamente dichiarato tra le dependencies.
- `.gitignore` esteso (file OS, dump GraalVM, env/secrets locali).

### Removed
- File obsoleti: `README.html`, `_assets/dir.txt`, `_assets/CHANGELOG.md`.

## [0.3.0] - 2024-03-17

### Added
- Import dei paesi (`importCountries`).
- Spring Boot Actuator + Micrometer/Prometheus (config beta).
- Configurazione Grafana (alpha).

### Changed
- Refactoring per centralizzare la logica comune.

## [0.1.0] - 2024-02-22

### Added
- Inizializzazione del progetto: verticali batch Airport e AirportType, controller REST, dashboard HTML.
- Dockerfile e docker-compose per GraalVM JDK 17 (JVM, nativo macOS, nativo Windows).

[Unreleased]: https://github.com/XtremeAlex/xtr-aeroport-batch/compare/v0.3.0...HEAD
[0.3.0]: https://github.com/XtremeAlex/xtr-aeroport-batch/releases/tag/v0.3.0
[0.1.0]: https://github.com/XtremeAlex/xtr-aeroport-batch/releases/tag/v0.1.0
