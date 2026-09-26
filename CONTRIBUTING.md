# Come contribuire

Grazie per l'interesse verso questo progetto! Ogni contributo e benvenuto.

## Flusso di lavoro

1. Fai un fork del repository.
2. Crea un branch dalla `develop`: `git checkout -b feature/nome-feature`.
3. Applica le modifiche con commit chiari (vedi convenzione sotto).
4. Assicurati che il progetto compili: `./mvnw clean package`.
5. Fai push del branch e apri una Pull Request verso `develop`.

## Convenzione dei commit

Usa [Conventional Commits](https://www.conventionalcommits.org/it/):

```
<tipo>(<ambito opzionale>): <descrizione breve>
```

Tipi comuni: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, `perf`, `build`, `ci`.

Esempi:
- `feat(batch): aggiunge import delle rotte aeree`
- `fix(native): corregge la reflect-config per i DTO Jackson`
- `docs(readme): allinea i comandi di build`

## Stile del codice

- Java 17, formattazione standard dell'IDE.
- Niente segreti o credenziali nei commit: usa variabili d'ambiente o file `application-local.yml` (ignorato da git).
- Un commit dovrebbe fare una cosa sola ed essere descritto in modo comprensibile.

## Segnalazioni

Apri una [issue](../../issues) descrivendo il problema, i passi per riprodurlo e il comportamento atteso.
