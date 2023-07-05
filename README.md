# poker-game
### <a name="link">Info utili</a>
* [texas hold'em](https://it.wikipedia.org/wiki/Texas_hold_%27em)
* Per salvare le tue modifiche alla repository da IntelliJ vai in **Git**, poi **Branches** o **New Branch**, dai il nome alla branch (e.g. guido), in seguito fai la **Push** o la **Commit** (pulsanti in alto a destra) sulla nuova branch
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

I giocatori bot al tavolo selezionato **sono 3** e il loro conto sarà compreso tra [conto utente - 50%, conto utente + 50%]

<a name="routine">La routine di gioco è la seguente:</a>
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

Quando un giocatore fa fold le sue carte non vengono mai scoperte, se l'utente vuole abbandonare il tavolo ci sarà un **toggle button** che se premuto gli consentirà di alzarsi dal tavolo prima della partita successiva (non è possibile abbandonare durante una partita).

-*La parte più complicata sarà creare dei bot in grado di giocare in maniera un minimo realistica a poker, per fare ciò guarderò qualche guida online e ti farò sapere*-

Nel caso in cui un giocatore bot finisse i soldi, questi lascerà il tavolo ad un altro bot. Nel caso l'utente finisse i soldi, questi sarà costretto ad alzarsi dal tavolo e **uscire dal gioco**.

## Dopo la sessione
Finita una sessione di gioco (quando ci si alza dal tavolo), all'utente verrà nuovamente mostrata la schermata iniziale con i molteplici tavoli -*nella prima versione sarà uno solo*-, mentre in background si aggiornerà la
tabella del database con il nuovo conto dell'utente.

A questo punto l'utente potrà decidere se rientrare in gioco oppure **uscire dal gioco**, così facendo si tornerà alla schermata di login, dove si potrà rientrare con un username oopure chiudere l'app.

## Piccolo brainstorming
* La gestione del **database** -*credo che sarà fatta tramite JDBC e SQLite, devo ancora vedere bene come fare...*- **UPDATE**: come non detto, si fa tutto con JSON e Jackson, è più semplice, più veloce e più sicuro!
* Alcune delle classi essenziali saranno:
  * **Carta** (con valore e seme)
  * **Mazzo** (con le 52 carte e il metodo mescola, pesca)
  * **Mano**: io la considererei come sia le 2 carte che si hanno in mano, che le **community cards**, dovrebbe avere un metodo che calcola la mano migliore possibile date le carte a disposizione finora
(sarà utile soprattutto per programmare i bot)
  * **Hand** (perchè non mi vengono altri nomi): questa è la classe con un array di 5 carte come attributo e con un metodo che restituisce il valore della mano, quest'ultimo deve essere molto preciso per evitare problemi quando bisogna calcolare un vincitore, quindi terrà in considerazione sia le carte che partecipano al punteggio (e.g. in un tris le 3 carte con lo stesso valore), sia quello che non lo fanno (e.g. in un tris le 2 carte con valori diversi), sia le 2 carte che il giocatore ha in mano (quest'ultime necessarie in caso di pareggi)
  * **Pot**: tiene conto delle puntate ed eventuali [**side pot**](#link)
  * **Giocatore** (che ha un **username** e **conto**, anche per i bot)
* La parte grafica sarà principalmente costituita da bottoni e panel, eventualmente si può mettere nella schermata di login e quella iniziale una tabella con tutti i dati del **database** sui vari giocatori a mo di classfica

Direi che questo è quanto Guido, aiuto il tempo è pochissimo e devo ancora iniziare [mannaggina](https://cpad.ask.fm/251/623/336/910003011-1qesacg-cl8jblr7cfbpr2f/original/tumblr_mkxmzykB2l1rdln34o1_500.jpg)

### UPDATE 2023.06.25
Le seguenti parti del gioco sono state fatte (**non finite!**, si possono ancora perfezionare):

* Login iniziale con annesso database dei giocatori in formato JSON (il database contine solo **username** e **conto**)
* Lobby in cui il giocatore seleziona il tavolo per giocare (il tavolo è **uno solo**)
* Turnazione base del gioco, ovvero i punti presentati [qui](#routine) -*Molto ma **MOLTO** più difficile di quanto possa sembrare, ma direi di avercela fatta*-
* Grafiche delle carte, del background, del segnaposto del **dealer** e delle chips -*Decisamente migliorabili*-
* Uscita dal tavolo e dal gioco con salvataggio dei dati della sessione

Le seguenti parti del gioco sono ancora da fare:

* Logica di gioco, ovvero:
  * Metodo che calcola il valore di una mano
  * Metodo che determina la migliore mano di un giocatore
  * Determinazione del vincitore per ogni mano e assegnazione della vincita
  * All-in & co. (side pot, ...)
* Logica dei bot -*Bella tosta pure questa...*-
* Mogliorie varie...

### UPDATE 2023.07.02
Le seguenti parti del gioco sono state [terminate](https://media.tenor.com/dR6vK_dQ1UgAAAAM/%C3%B3culos-escuro.gif):

* Logica di gioco (sia valutazione delle mani, che determinazione mano migliore, vincitore e gestione di all in e side pots)
* Rallentamento della turnazione con l'aggiunta di un **bottone** da premere per andare all'azione successiva
* Migliorie varie, tra cui:
  * Distinzione tra grafica e logica di gioco con l'aggiunta di un'apposita classe **GameLogic** per gestire quest'ultima
  * Conseguente alleggerimento di **PokerGameController** -*Che aveva raggiunto più di 1000 righe*- che ora contiene sostanziamente solo metodi grafici

Rimane da fare solo la **LOGICA DEI BOT** -*bel casino*-.

Per il resto il gioco risulta completo e pronto all'uso -*dico a te Guido, nel caso tu voglia fare beta testing*-.

### UPDATE 2023.07.05
[**Progetto terminato**](https://thumbs.gfycat.com/UnripeThoroughHalibut-max-1mb.gif)!!

Adesso inizia la fase di **beta testing**. 
