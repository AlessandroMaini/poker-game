# poker-game
### <a name="link">Links utili</a>
* [texas hold'em](https://it.wikipedia.org/wiki/Texas_hold_%27em)
## Accesso al gioco
L'applicazione mostra una schermata iniziale di accesso nella quale chiede all'utente di inserire il proprio **username**.

Da un database si andrà a trovare l'utente selezionato per poter ricavare il suo **conto**.

Il database salverà informazioni riguardanti:
* Nome utenete (**username**)
* Conto, quanti soldi ha a disposizione l'utente (si parte da un valore di default e quando si arriva a zero l'utente viene cancellato)
* Altre info (massima vincita, massimo valore del conto mai raggiunto, ...)

## Sessione di gioco
Una volta entrati con il proprio utente si può selezionare uno dei tavoli ai quali giocare (i diversi tavoli hanno puntate diverse e giocatori bot con conti diversi, cioè c'è un tavolo nel quale la puntata minima è 10,
un altro 50, poi 100, ecc.) -*questa feature la aggiungiamo più avanti*-.

I giocatori bot al tavolo selezionato variano da 3 a 5 e il loro conto sarà compreso tra [conto utente - 50%, conto utente + 50%]

La routine di gioco è la seguente:
* Viene selezionato il dealer e di conseguenza piccolo e grande buio
* Si distribuiscono le carte, da un mazzo mescolato di 52 carte (2 carte ad ogni giocatore)
* Si esegue il primo giro di puntate, secondo le regole del **poker texas hold'em no limit**
* Vengono mostrate le prime 3 carte comuni (**community cards**) dette **flop**
* Secondo giro di puntate
* Viene mostrata la quarta carta **turn**
* Terzo giro di puntate
* Viene mostrata la quinta carta **river**
* Ultimo giro di puntate
* **Showdown**, ovvero tutti i giocatori rimasti mostrano le loro carte

### Azioni di gioco
Le azioni a disposizione del giocatore quando è il suo turno sono:
* **Check**
* **Call**
* **Raise**
* **Fold**

(Per tutte le informazioni guardare la [pagina di wikipedia](#link))

Quando un giocatore fa fold le sue carte non vnegono mai scoperte, se l'utente vuole abbandonare il tavolo ci sarà un bottone che se premuto gli consentirà di alzarsi dal tavolo prima della partita successiva (non è
possibile abbandonare durante una partita).

-*La parte più complicata sarà creare dei bot in grado di giocare in maniera un minimo realistica a poker, per fare ciò guarderò qualche guida online e ti farò sapere*-

Nel caso in cui un giocatore bot finisse i soldi, questi lascerà il tavolo ad un altro bot. Nel caso l'utente finisse i soldi, questi sarà costretto ad alzarsi dal tavolo e **uscire dal gioco**.

## Dopo la sessione
Finita una sessione di gioco (quando ci si alza dal tavolo), all'utente verrà nuovamente mostrata la schermata iniziale con i molteplici tavoli -*nella prima versione sarà uno solo*-, mentre in background si aggiornerà la
tabella del database con il nuovo conto dell'utente.

A questo punto l'utente potrà decidere se rientrare in gioco oppure **uscire dal gioco**, così facendo si tornerà alla schermata di login, dove si potrà rientrare con un username oopure chiudere l'app.

