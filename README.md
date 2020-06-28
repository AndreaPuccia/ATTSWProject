[![Build Status](https://travis-ci.org/AndreaPuccia/ATTSWProject.svg?branch=master)](https://travis-ci.org/AndreaPuccia/ATTSWProject)
[![Coverage Status](https://coveralls.io/repos/github/AndreaPuccia/ATTSWProject/badge.svg?branch=master&service=github)](https://coveralls.io/github/AndreaPuccia/ATTSWProject?branch=master&service=github)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.nuti.puccia%3AATTSWProject&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.nuti.puccia%3AATTSWProject)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.nuti.puccia%3AATTSWProject&metric=coverage)](https://sonarcloud.io/dashboard?id=com.nuti.puccia%3AATTSWProject)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.nuti.puccia%3AATTSWProject&metric=bugs)](https://sonarcloud.io/dashboard?id=com.nuti.puccia%3AATTSWProject)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.nuti.puccia%3AATTSWProject&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.nuti.puccia%3AATTSWProject)


# Report Advanced Techniques and Tools for Software Development
Il progetto sviluppato riguarda la realizzazione di un'applicazione per la gestione delle prenotazioni agli esami da parte degli studenti.
L'applicazione consente di aggiungere, rimuovere e visualizzare le prenotazioni relative agli esami effettuate dagli studenti, permettendo inoltre l'inserimento di nuovi esami e studenti.

## Maven Build
Tramite il comando maven specificato è possibile effettuare una build completa del progetto durante la quale il progetto viene compilato, vengono eseguiti gli Unit Test, poi viene avviato il container Docker contenente il server MySql, vengono eseguiti gli Integration Test e gli End2End, viene stoppato il container docker, viene eseguita la fase di verifica del plugin failsafe e infine sono generati i report della copertura del codice e dei Mutation Test tramite Pit.
```
  maven clean verify
```
## Esecuzione applicazione
Per eseguire l'applicazione deve essere prima avviato un server MySql, ad esempio attraverso il lancio di un Docker container, e in seguito avviata l'applicazione passando il valore desiderato nei parametri disponibili, tra parentesi è specificato il valore di default:
* `--mysql-host`: l'indirizzo del server ("localhost")
* `--mysql-port`: la porta associata al server (3306)
* `--db-name`: il nome del database da utilizzare ("attsw")
* `--username`: il nome utente con cui accedere al database ("root")
* `--password`: la password con cui accedere al database ("")

Con il seguente comando è possibile eseguire un container Docker MySql che può essere utilizzato per lanciare l'applicazione con i suoi valori di default:
```
docker run -d -p 3306:3306 -e MYSQL_DATABASE="attsw" -e MYSQL_ROOT_PASSWORD="" -e MYSQL_ALLOW_EMPTY_PASSWORD="yes" mysql
```
