<a name="readme-top"></a>

<div align="center">
  <img src="_assets/images/banner-dark.png" alt="Aeroport Batch" width="100%">
  <br /><br />
  <img src="_assets/images/logo.png" width="400" alt="Logo">
</div>

# xtr-aeroport-batch

Il batch che carica i dati di aeroporti e paesi della suite Aeroport. Parte da file JSON e, un passo alla volta, li porta in un database relazionale.

> Stato: attivo, come strumento offline. Lo si lancia una volta, su un PC con un po' di potenza, e produce i dati che poi usa l'API. Non gira sul Raspberry Pi.

Dentro convivono due cose:

- l'applicazione Spring Batch storica (Java 17, Spring Boot 3.2.1, PostgreSQL), rimasta com'era nel 2024 a parte il riordino del 2026;
- lo script `tools/build-sqlite.py`, aggiunto più di recente, che genera il file `aeroport.sqlite` di sola lettura usato da `xtr-aeroport-api-spring`.

<details>
  <summary>Sommario</summary>
  <ol>
    <li><a href="#perché-esiste">Perché esiste</a></li>
    <li><a href="#la-suite">La suite</a></li>
    <li><a href="#stack-tecnologico">Stack tecnologico</a></li>
    <li><a href="#per-iniziare">Per iniziare</a></li>
    <li><a href="#generare-il-database-sqlite-per-lapi">Generare il database SQLite per l'API</a></li>
    <li><a href="#play--test">Play &amp; Test</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#come-contribuire">Come contribuire</a></li>
    <li><a href="#licenza">Licenza</a></li>
    <li><a href="#contatti">Contatti</a></li>
    <li><a href="#ringraziamenti">Ringraziamenti</a></li>
  </ol>
</details>

## Perché esiste

È un progetto personale nato per provare tecnologie e framework recenti su un caso concreto, invece che sui soliti esempi giocattolo. L'idea era costruire una base solida, con un'impostazione da progetto aziendale, per importare in blocco informazioni su aeroporti e rotte aeree dentro un database relazionale.

È uno dei moduli di una serie più ampia, che ho pubblicato perché chiunque possa usarla e migliorarla.

## La suite

| Modulo | A cosa serve | Stato |
|---|---|---|
| `xtr-aeroport-api-spring` | API unica per aeroporti, tipologie, paesi e messaggi EDIFACT (non ancora pubblicata su GitHub) | Attivo |
| `xtr-aeroport-api-quarkus` | Porting della stessa API su Quarkus (non ancora pubblicato su GitHub) | Sperimentale |
| `xtr-aeroport-edifact-spring-web` | Console web EDIFACT, ha preso il posto di `xtr-aeroport-web-java` (non ancora pubblicata su GitHub) | Attivo |
| [`xtr-aeroport-batch`](https://github.com/XtremeAlex/xtr-aeroport-batch) | Import massivo dei dati (questo modulo) | Attivo, offline |
| [`xtr-aeroport-common-lib`](https://github.com/XtremeAlex/xtr-aeroport-common-lib) | Libreria condivisa | Legacy |
| [`xtr-aeroport-ms`](https://github.com/XtremeAlex/xtr-aeroport-ms) | Microservizio di ricerca aeroporti | Deprecato |
| [`xtr-aeroport-typology`](https://github.com/XtremeAlex/xtr-aeroport-typology) | Servizio dati tipologici | Deprecato |
| [`xtr-aeroport-web-java`](https://github.com/XtremeAlex/xtr-aeroport-web-java) | Frontend web | Deprecato |

## Stack tecnologico

- Java 17 (GraalVM)
- Spring Boot 3.2.1 (Spring Batch, Data JPA, Actuator)
- PostgreSQL, HikariCP
- MapStruct, Lombok
- Micrometer + Prometheus, Grafana
- Docker, Helm / Kubernetes
- Python 3 per lo script che genera il database SQLite
- Gira su Linux, macOS e Windows

## Per iniziare
Si compila con Maven. Il progetto è su Spring Boot 3 e Java 17 e si avvia tranquillamente in locale.

### Cosa serve

- Git (>= 2.43)
- GraalVM JDK 17 per la build nativa, oppure un qualsiasi JDK 17 per la build JVM
- Maven (>= 3.9.6), oppure il wrapper `./mvnw` già incluso
- Docker, per il database e per le build in container

### Coordinate del progetto

| Proprietà | Valore |
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

### Build JVM

```bash
./mvnw clean package -DskipTests
java -jar ./target/aeroport-batch-0.3.0.jar
```

<img src="_assets/images/mvn-build.png" alt="Build Maven" />

A questo punto apri <http://localhost:8081/xtr-aeroport-batch/progress> e segui l'avanzamento dei job.

<img src="_assets/images/run-by-graal-jdk17.png" alt="Avvio applicazione" />

### Build nativa (GraalVM)

**1. Generare i metadati di reflection**

La native image ragiona "a mondo chiuso": reflection, proxy, risorse e serializzazione dinamica vanno dichiarati prima, in fase di build. Ci pensa il `native-image-agent`: si avvia il jar sulla JVM, si usano i vari percorsi dell'applicazione e l'agent annota tutto quello che serve.

```bash
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
  -jar ./target/aeroport-batch-0.3.0.jar
```

I file `.json` generati finiscono in `src/main/resources/META-INF/native-image/`. Il `native-maven-plugin` li trova da solo lì, quindi non serve aggiungere `buildArg` espliciti.

<img src="_assets/images/run-agentlib.png" alt="Generazione metadati con native-image-agent" />

**2. Compilare l'immagine nativa**

```bash
./mvnw package -DskipTests -Pnative
```

<img src="_assets/images/native-mvn-build.png" alt="Build nativa" />

Il binario esce in `./target/aeroport-batch-app` (su Windows con estensione `.exe`).

<img src="_assets/images/native-macos-result-build.png" alt="Risultato build macOS" />
<img src="_assets/images/native-windows-result-build.png" alt="Risultato build Windows" />

**3. Avviare il binario nativo**

```bash
./target/aeroport-batch-app
```

**Se sei su ARM64 (Apple Silicon)**

Su Apple M1/M2 (Aarch64) GraalVM supporta quasi tutto, con qualche eccezione ([riferimento GraalVM](https://www.graalvm.org/reference-manual/native-image/)):

- `WriteableCodeCache` va disabilitato;
- `--libc=musl` non è supportato;
- il Garbage Collector G1 non è supportato.

**Tutti i comandi in fila**

```bash
./mvnw clean
./mvnw package -DskipTests
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar ./target/aeroport-batch-0.3.0.jar
./mvnw package -DskipTests -Pnative
./target/aeroport-batch-app
```

### Build Docker

L'immagine si costruisce con i buildpacks, con un profilo per ogni architettura:

```bash
# Apple Silicon (ARM64)
./mvnw package -DskipTests -Pdocker-m1-arm

# x86
./mvnw package -DskipTests -Pdocker-x86
```

E per avviare il container:

```bash
docker run -p 8081:8081 artifactory.io/k8s-test/namespace/com.xtremealex/aeroport-batch:0.3.0
```

<img src="_assets/images/native-docker-arm-build.png" alt="Build Docker ARM" />

## Generare il database SQLite per l'API

L'API attuale (`xtr-aeroport-api-spring`) non legge PostgreSQL: usa un file SQLite di sola lettura, pensato per stare leggero su un Raspberry Pi. Quel file lo produce `tools/build-sqlite.py`, sempre offline e una volta sola. Lo script legge i dataset JSON e crea `aeroport.sqlite` con lo schema e gli indici che l'API si aspetta.

```bash
python3 tools/build-sqlite.py \
    --airports src/main/resources/dataset/airports/world-airport.json \
    --countries src/main/resources/dataset/country/countries-flag.json \
    --out target/aeroport.sqlite
```

Il file risultante va copiato sul Raspberry Pi e montato in sola lettura nel container dell'API.

## Play & Test

- Dashboard dei job: <http://127.0.0.1:8081/xtr-aeroport-batch/progress>
- Metriche Prometheus: <http://127.0.0.1:8081/xtr-aeroport-batch/actuator/prometheus>

### Benchmark

- **Windows 11** (i7-10750H, 64 GB RAM)
  <img src="_assets/images/windows_benchmark.png" alt="Benchmark Windows" />
- **macOS Sonoma 14.2.1** (M1 16 GB / M1 Max 64 GB RAM)
  <img src="_assets/images/mac_benchmark.png" alt="Benchmark macOS" />

## Roadmap

- [x] Verticale batch AirportType
- [x] Verticale batch Airport
- [x] Controller REST
- [x] Dashboard HTML di avanzamento
- [x] Dockerfile e docker-compose per GraalVM JDK 17 (JVM, nativo macOS, nativo Windows)
- [x] Generazione del database SQLite di sola lettura per l'API
- [ ] Profilo OpenJDK 17
- [ ] Suite di test e raccolta delle statistiche

L'elenco completo di idee e bug noti è nelle [open issues](https://github.com/XtremeAlex/xtr-aeroport-batch/issues).

## Come contribuire

Ogni contributo è ben accetto, anche piccolo. Il giro è quello classico:

1. fai un fork del progetto;
2. crea un branch per la tua modifica (`git checkout -b feature/nome-feature`);
3. fai commit (`git commit -m "Aggiunge nome-feature"`);
4. fai push del branch (`git push origin feature/nome-feature`);
5. apri una Pull Request.

Se hai solo un'idea, apri una issue con l'etichetta giusta. E se il progetto ti è utile, una stella fa sempre piacere.

## Licenza
Doppia licenza: **GNU AGPL-3.0** (vedi [`LICENSE`](LICENSE)) per l'uso open source, e **licenza commerciale** per l'uso dentro prodotti proprietari (vedi [`COMMERCIAL-LICENSE.md`](COMMERCIAL-LICENSE.md)).

## Contatti

Andrei Alexandru Dabija (XtremeAlex) · [alexdabi92@gmail.com](mailto:alexdabi92@gmail.com) · [2ad.bubume.it](https://2ad.bubume.it/) · [LinkedIn](https://www.linkedin.com/in/andrei-alexandru-dabija/) · [github.com/XtremeAlex](https://github.com/XtremeAlex)

## Ringraziamenti

- [Spring Boot](https://spring.io/projects/spring-boot) e [Spring Batch](https://spring.io/projects/spring-batch)
- [GraalVM](https://www.graalvm.org/) per la compilazione nativa
- [Micrometer](https://micrometer.io/) + [Prometheus](https://prometheus.io/) e [Grafana](https://grafana.com/) per l'osservabilità
- [Best-README-Template](https://github.com/othneildrew/Best-README-Template), da cui ho preso spunto per la struttura

<p align="right">(<a href="#readme-top">torna su</a>)</p>
