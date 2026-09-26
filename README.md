<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Dependency Graph][maven.shield]][dependencies-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="">
    <img src="_assets/images/logo.png" width="500" alt="Logo">
  </a>

  <h3 align="center">Aeroport BATCH</h3>

  <p align="center">
    <strong>xtr-aeroport-batch</strong> e un microservizio batch che importa dati di aeroporti e paesi da sorgenti JSON e li migra, in piu step, verso una banca dati relazionale.
    <br />
    <a href="https://github.com/XtremeAlex/xtr-aeroport-batch"><strong>Esplora la documentazione &raquo;</strong></a>
    <br />
    <br />
    <a href="https://github.com/XtremeAlex/xtr-aeroport-batch/issues">Segnala un bug</a>
    &middot;
    <a href="https://github.com/XtremeAlex/xtr-aeroport-batch/issues">Richiedi una feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Indice</summary>
  <ol>
    <li><a href="#info-sul-progetto">Info sul progetto</a></li>
    <li><a href="#stack-tecnologico">Stack tecnologico</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#build-jvm">Build JVM</a></li>
    <li><a href="#build-nativa-graalvm">Build nativa (GraalVM)</a></li>
    <li><a href="#build-docker">Build Docker</a></li>
    <li><a href="#play--test">Play &amp; Test</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#come-contribuire">Come contribuire</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contatti">Contatti</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## Info sul progetto

Questo progetto nasce come piattaforma sperimentale personale per mettere alla prova tecnologie e framework moderni in un contesto realistico. L'obiettivo e fornire una base solida e "enterprise like" per l'importazione batch di informazioni su aeroporti e rotte aeree verso un database relazionale.

E uno dei moduli di una serie piu ampia, pensata per essere condivisa e arricchita con il contributo della community.

### Perche contribuire

- Sperimentare con tecnologie e pattern moderni in un progetto concreto.
- Crescere insieme scambiando idee, codice e feedback.
- Partire da una base strutturata secondo le best practice per i propri sviluppi futuri.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- STACK -->
## Stack tecnologico

- [![Java][java.shield]][java.url] Java 17 (GraalVM)
- [![Spring][spring.shield]][spring.url] Spring Boot 3.2.1 (Spring Batch, Data JPA, Actuator)
- PostgreSQL, HikariCP
- MapStruct, Lombok
- Micrometer + Prometheus, Grafana
- Docker, Helm / Kubernetes
- [![Linux][Linux.shield]][Linux.url] [![Macos][Macos.shield]][Macos.url] [![Windows][Windows.shield]][Windows.url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

Il progetto usa Maven per dipendenze e build. E sviluppato con Spring Boot 3 e Java 17 e puo essere avviato e testato in locale.

### Prerequisiti

- Git (>= 2.43)
- GraalVM JDK 17 (per la build nativa) oppure un JDK 17 qualsiasi (per la build JVM)
- Maven (>= 3.9.6) — oppure il wrapper `./mvnw` incluso
- Docker (per il database e le build containerizzate)

### Coordinate del progetto

| Proprieta | Valore |
|---|---|
| groupId | `com.xtremealex` |
| artifactId | `aeroport-batch` |
| version | `0.3.0` |
| main class | `com.xtremealex.aeroport.AeroportBatchApplication` |
| context-path | `/xtr-aeroport-batch` |
| porta | `8081` |

### Clonare il repository

```bash
git clone https://github.com/XtremeAlex/xtr-aeroport-batch.git
cd xtr-aeroport-batch
```

### Avviare il database

```bash
docker-compose -f docker/postgres.docker-compose.yml down && \
docker-compose -f docker/postgres.docker-compose.yml up
```

<img src="_assets/images/db-run-postgressql.png" alt="Avvio PostgreSQL" />

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- BUILD JVM -->
## Build JVM

```bash
./mvnw clean package -DskipTests
java -jar ./target/aeroport-batch-0.3.0.jar
```

<img src="_assets/images/mvn-build.png" alt="Build Maven" />

Poi apri il browser su: <http://localhost:8081/xtr-aeroport-batch/progress>

<img src="_assets/images/run-by-graal-jdk17.png" alt="Avvio applicazione" />

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- BUILD NATIVA -->
## Build nativa (GraalVM)

### 1. Generare i metadati di reflection

La native image lavora a closed-world: reflection, proxy, risorse e serializzazione dinamiche vanno dichiarate a build time. Il `native-image-agent` genera questi metadati eseguendo prima il jar sulla JVM ed esercitando i vari percorsi dell'applicazione.

```bash
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
  -jar ./target/aeroport-batch-0.3.0.jar
```

I file `.json` finiscono in `src/main/resources/META-INF/native-image/`; da qui il `native-maven-plugin` li rileva automaticamente, senza bisogno di `buildArg` espliciti.

### 2. Compilare l'immagine nativa

```bash
./mvnw package -DskipTests -Pnative
```

<img src="_assets/images/native-mvn-build.png" alt="Build nativa" />

Il binario viene prodotto in `./target/aeroport-batch-app` (su Windows con estensione `.exe`).

<img src="_assets/images/native-macos-result-build.png" alt="Risultato build macOS" />
<img src="_assets/images/native-windows-result-build.png" alt="Risultato build Windows" />

### 3. Avviare il binario nativo

```bash
./target/aeroport-batch-app
```

### Note su ARM64 (Apple Silicon)

Su Apple M1/M2 (Aarch64) quasi tutte le funzionalita GraalVM sono supportate, con alcune limitazioni ([riferimento GraalVM](https://www.graalvm.org/reference-manual/native-image/)):

- `WriteableCodeCache` deve essere disabilitato.
- `--libc=musl` non e supportato.
- Il Garbage Collector G1 non e supportato.

### Recap dei comandi

```bash
./mvnw clean
./mvnw package -DskipTests
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar ./target/aeroport-batch-0.3.0.jar
./mvnw package -DskipTests -Pnative
./target/aeroport-batch-app
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- BUILD DOCKER -->
## Build Docker

Build dell'immagine tramite buildpacks (profili per architettura):

```bash
# Apple Silicon (ARM64)
./mvnw package -DskipTests -Pdocker-m1-arm

# x86
./mvnw package -DskipTests -Pdocker-x86
```

Esecuzione del container:

```bash
docker run -p 8081:8081 artifactory.io/k8s-test/namespace/com.xtremealex/aeroport-batch:0.3.0
```

<img src="_assets/images/native-docker-arm-build.png" alt="Build Docker ARM" />

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE -->
## Play & Test

- Dashboard job: <http://127.0.0.1:8081/xtr-aeroport-batch/progress>
- Metriche Prometheus: <http://127.0.0.1:8081/xtr-aeroport-batch/actuator/prometheus>

### Benchmark

- **Windows 11** (i7-10750H, 64 GB RAM)
  <img src="_assets/images/windows_benchmark.png" alt="Benchmark Windows" />
- **macOS Sonoma 14.2.1** (M1 16 GB / M1 Max 64 GB RAM)
  <img src="_assets/images/mac_benchmark.png" alt="Benchmark macOS" />

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ROADMAP -->
## Roadmap

- [x] Verticale batch AirportType
- [x] Verticale batch Airport
- [x] Controller REST
- [x] Dashboard HTML di avanzamento
- [x] Dockerfile e docker-compose per GraalVM JDK 17 (JVM, nativo macOS, nativo Windows)
- [ ] Profilo OpenJDK 17
- [ ] Suite di test e raccolta delle statistiche

Consulta le [open issues](https://github.com/XtremeAlex/xtr-aeroport-batch/issues) per l'elenco completo di feature proposte e bug noti.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Come contribuire

I contributi sono cio che rende la community open source un posto straordinario per imparare e creare. Ogni contributo e molto apprezzato.

1. Fai un fork del progetto
2. Crea il tuo feature branch (`git checkout -b feature/nome-feature`)
3. Fai commit delle modifiche (`git commit -m "Aggiunge nome-feature"`)
4. Fai push sul branch (`git push origin feature/nome-feature`)
5. Apri una Pull Request

Se hai un suggerimento, apri pure una issue con il tag appropriato. E non dimenticare di mettere una stella al progetto!

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distribuito sotto licenza Apache 2.0. Vedi il file [`LICENSE`](LICENSE) per i dettagli.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
## Contatti

Andrei Alexandru Dabija — [LinkedIn](https://www.linkedin.com/in/andrei-alexandru-dabija/)

Link al progetto: <https://github.com/XtremeAlex/xtr-aeroport-batch>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/XtremeAlex/xtr-aeroport-batch.svg?style=for-the-badge
[contributors-url]: https://github.com/XtremeAlex/xtr-aeroport-batch/graphs/contributors
[dependencies-url]: https://github.com/XtremeAlex/xtr-aeroport-batch/network/dependencies
[forks-shield]: https://img.shields.io/github/forks/XtremeAlex/xtr-aeroport-batch.svg?style=for-the-badge
[forks-url]: https://github.com/XtremeAlex/xtr-aeroport-batch/network/members
[stars-shield]: https://img.shields.io/github/stars/XtremeAlex/xtr-aeroport-batch.svg?style=for-the-badge
[stars-url]: https://github.com/XtremeAlex/xtr-aeroport-batch/stargazers
[issues-shield]: https://img.shields.io/github/issues/XtremeAlex/xtr-aeroport-batch.svg?style=for-the-badge
[issues-url]: https://github.com/XtremeAlex/xtr-aeroport-batch/issues
[license-shield]: https://img.shields.io/github/license/XtremeAlex/xtr-aeroport-batch.svg?style=for-the-badge
[license-url]: https://github.com/XtremeAlex/xtr-aeroport-batch/blob/develop/LICENSE
[linkedin-shield]: https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white
[linkedin-url]: https://www.linkedin.com/in/andrei-alexandru-dabija/
[java.shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[java.url]: https://wikipedia.org/wiki/Java_(programming_language)
[spring.shield]: https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[spring.url]: https://spring.io
[Linux.shield]: https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black
[Linux.url]: https://wikipedia.org/wiki/Linux
[Macos.shield]: https://img.shields.io/badge/mac%20os-000000?style=for-the-badge&logo=apple&logoColor=white
[Macos.url]: https://wikipedia.org/wiki/MacOS
[Windows.shield]: https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white
[Windows.url]: https://wikipedia.org/wiki/Windows10
[maven.shield]: https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white
