$(document).ready(function () {
    // Intervallo iniziale di 2 secondi, lo so è sporco come metodo ma non volevo aumentare inserire troppi componenti a livello be per gestire aperture soket ecc...
    let pollingInterval = 2000;
    // ID per clearInterval()
    let fetchIntervalId = null;
    // Memorizza i dettagli dei job, incluso l'errore
    let jobDetails = {};

    function startPolling(interval) {
        // Ferma il polling corrente se è già in esecuzione
        if (fetchIntervalId !== null) {
            clearInterval(fetchIntervalId);
        }
        // Avvia un nuovo polling con l'intervallo specificato per averne due e non uno fisso
        fetchIntervalId = setInterval(fetchJobDetails, interval);
    }

    function fetchJobDetails() {
        $.getJSON('/xtr-aeroport-batch/jobs/details', function (jobsDetails) {

            // Pulisco tutto per evitare duplicazioni
            jobDetails = {};
            $('#jobs-list').empty();

            // Default: tutti i job siano completati o falliti
            let allCompletedOrFailed = true;

            jobsDetails.forEach(function (job) {
                // Aggiorna il jobDetails con le informazioni correnti, inclusi gli errori mi serve per la modale
                jobDetails[job.name] = job;

                if (job.status === 'FAILED') {
                    // Trova il bottone per questo job e aggiorna il suo stato e testo
                    $(`button[data-job-name="${job.name}"]`)
                        .removeClass('btn-primary btn-warning')
                        .addClass('btn-danger')
                        .prop('disabled', false)
                        .text('Dettagli Errore')
                        .attr('data-bs-toggle', 'collapse')
                        .attr('data-bs-target', `#errorDetails-${job.name}`);
                }

                const jobNameUpper = job.name.toUpperCase();

                /*  Versione lite

                let startButtonHtml =
                    job.status === 'COMPLETED' ? '<button class="btn btn-completed" disabled>Completato</button>' :
                        job.status === 'FAILED' ? `<button class="btn btn-danger" data-bs-toggle="collapse" data-bs-target="#errorDetails-${job.name}">Dettagli Errore</button>` :
                            job.status === 'STARTED' ? '<button class="btn btn-warning" disabled>In esecuzione...</button>' :
                                `<button class="btn btn-primary start-job" data-job-name="${job.name}" data-job-status="PENDING">Avvia</button>`;
                */

                let startButtonHtml =
                    job.status === 'COMPLETED' ? '<button class="btn btn-completed" disabled>Completato</button>' :
                        job.status === 'FAILED' ? `<button class="btn btn-danger" data-bs-toggle="collapse" data-bs-target="#errorDetails-${job.name}">Dettagli Errore</button>` :
                            job.status === 'STARTED' ? `<button class="btn btn-warning" disabled>
                                            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                                            In esecuzione...
                                        </button>` :
                                `<button class="btn btn-primary start-job" data-job-name="${job.name}" data-job-status="PENDING">Avvia</button>`;

                let errorCollapseHtml = job.status === 'FAILED' ? `
                    <div class="collapse" id="errorDetails-${job.name}">
                        <div class="card card-body bg-dark text-white">
                            ${job.error || "Errore sconosciuto"}
                        </div>
                    </div>
                    ` : '';

                if (job.status !== 'COMPLETED' && job.status !== 'FAILED') {
                    // Se c'è almeno un job non completato, continua il polling frequente, cosi da avere una ui gradevole
                    allCompletedOrFailed = false;
                }

                // Aggiungi la riga del job al DOM
                $('#jobs-list').append(generateJobRow(jobNameUpper, startButtonHtml, job, errorCollapseHtml));
            });

            // Se tutti i job sono completati o falliti, aggiorna l'intervallo di polling
            if (allCompletedOrFailed && pollingInterval !== 20000) {
                pollingInterval = 20000;
                startPolling(pollingInterval);
                console.log("Polling interval updated to 10 seconds.");
            }
        });
    }

    // Funzione per generare il codice HTML per ogni JOB
    function generateJobRow(jobNameUpper, startButtonHtml, job, errorCollapseHtml) {
        const progressBarId = `progress-bar-${job.name}`;

        // Aggiornamento della progress bar in base allo stato del job, cosi da farla piu carina.
        let addClass = "";
        if (job.status === 'STARTED') {
            addClass = 'bg-warning';
        } else if (job.status === 'FAILED') {
            addClass = 'bg-danger';
        } else if (job.status === 'COMPLETED') {
            addClass = 'bg-success';
        }

        // Controllo se il job ha fallito
        if (job.status === 'FAILED') {
            startButtonHtml = `<button class="btn btn-danger" data-job-name="${job.name}" data-bs-toggle="modal" data-bs-target="#errorModal">Dettagli Errore</button>`;
        }

        const jobProgress = job.progress.replace(',', '.');
        const progressPercentAll = jobProgress + "%";
        const progressPercent = parseFloat(jobProgress).toFixed(2);
        const progressPercentWidth = progressPercent + "%";

        const textColorClass = job.status !== 'COMPLETED' ? "text-black" : "";
        return `
                <tr>
                    <td>${jobNameUpper}</td>
                    <td>${startButtonHtml}</td>
                    <td>
                        <div class="progress">
                            <div id="${progressBarId}" class="progress-bar progress-bar-striped progress-bar-animated ${addClass} ${textColorClass}" role="progressbar"
                                style="width: ${progressPercentWidth}; color: ${job.status !== 'COMPLETED' ? 'black' : 'white'};" aria-valuenow="${progressPercent}"
                                aria-valuemin="0" aria-valuemax="100">${progressPercentAll}</div>
                        </div>
                    </td>
                    <td>${job.startTime || 'N/A'}</td>
                    <td>${job.endTime || 'N/A'}</td>
                    <td>${job.duration || 'N/A'}</td>
                </tr>
                ${errorCollapseHtml}
                `;
    }


    $(document).on('click', '.start-job', function () {
        const jobName = $(this).data('job-name');
        const $btn = $(this);

        $.ajax({
            url: `/xtr-aeroport-batch/startJob/${jobName}`,
            type: 'GET',
            success: function (response) {
                console.log(response);
                //$btn.prop('disabled', true).text('In esecuzione...').removeClass('btn-primary').addClass('btn-warning');
                $btn.html(`<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> In esecuzione`)
                    .prop('disabled', true)
                    .removeClass('btn-primary')
                    .addClass('btn-warning');
            },
            error: function (xhr) {
                console.error("Errore nell'avvio del job", xhr);
                if (xhr.status === 409) {
                    console.log("Job già eseguito o in esecuzione");
                } else {
                    $btn.prop('disabled', false).addClass('btn-primary').text('Avvia');
                }
            }
        });

    });

    $(document).on('click', '.btn-danger', function () {
        const jobName = $(this).data('job-name');
        const jobError = jobDetails[jobName] ? jobDetails[jobName].error : "Errore sconosciuto";

        // Imposta il titolo della modale con il nome del job
        $('#errorModalLabel').text(`Dettagli Errore - ${jobName.toUpperCase()}`);
        // Imposta il messaggio di errore nella modale
        $('#errorModal .modal-body').text(jobError);

        // Mostra la modale
        $('#errorModal').modal('show');
    });

    // Esegue un refresh della pagina
    fetchJobDetails();

    // Avvia il polling iniziale
    startPolling(pollingInterval);
});