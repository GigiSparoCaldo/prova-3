# App Aperte — Flip Cover Apps

App Android che mostra, in una lista compatta, le app usate/aperte di recente
(icona + nome), toccabile per passare subito a quell'app. Pensata per essere
usata sullo **schermo esterno** del Galaxy Z Flip 7.

## Perché serve Good Lock / MultiStar

Samsung, di default, permette solo a una manciata di app "ufficiali" (Maps,
WhatsApp, YouTube...) di girare sullo schermo di copertina. Per far girare
**qualsiasi** app — inclusa questa — sullo schermo esterno, serve il modulo
gratuito **MultiStar** di **Good Lock** (entrambi di Samsung, scaricabili dal
Galaxy Store). Questa app da sola, una volta installata, funziona come una
normale app; è MultiStar che la "aggiunge" allo schermo di copertina.

## 1. Metti il progetto su GitHub

1. Crea un nuovo repository su GitHub (es. `flip-cover-apps`), vuoto.
2. Sul tuo computer, dentro la cartella di questo progetto:
   ```bash
   git init
   git add .
   git commit -m "Prima versione"
   git branch -M main
   git remote add origin https://github.com/TUO-UTENTE/flip-cover-apps.git
   git push -u origin main
   ```
   (In alternativa puoi caricare i file direttamente dall'interfaccia web di
   GitHub, trascinando le cartelle.)

## 2. Compila l'APK (automatico)

Appena fai push su `main`, GitHub Actions compila l'APK da solo grazie al
workflow incluso in `.github/workflows/build.yml`. Per scaricarlo:

1. Vai nella tab **Actions** del repository.
2. Apri l'ultima esecuzione ("Build APK").
3. In fondo alla pagina, sotto **Artifacts**, scarica `FlipCoverApps-debug`
   (è uno zip che contiene l'APK).
4. In alternativa, trovi lo stesso APK anche nella tab **Releases** del
   repository (viene creata automaticamente a ogni build su `main`).

Non serve installare nulla sul computer: la compilazione avviene sui server
di GitHub.

## 3. Installa l'APK sul telefono

1. Trasferisci il file `.apk` sul telefono (email a te stesso, Drive, cavo...).
2. Apri il file dal telefono. Se richiesto, consenti l'installazione da
   "sorgenti sconosciute" per l'app che usi per aprirlo (Impostazioni te lo
   chiede al primo tentativo).
3. Apri l'app "App Aperte" e tocca **Concedi accesso**: ti porta nelle
   impostazioni di "Accesso all'utilizzo" del telefono — attiva l'interruttore
   per "App Aperte". Senza questo permesso l'app non può leggere quali app
   hai usato di recente (è una restrizione di sistema Android, non si può
   evitare).

## 3bis. La panoramica "vera" di sistema (identica a quella interna)

Oltre alla lista, l'app ha un pulsante **"Apri panoramica di sistema"**: non
ricostruisce nulla, richiama la vera schermata "App recenti" di Android (le
stesse anteprime live che vedi scorrendo verso l'alto sullo schermo interno),
usando un **servizio di accessibilità** — la stessa tecnica delle app di
gesture/navigazione per simulare la pressione del tasto Recenti.

1. Al primo tocco del pulsante, l'app ti porta in **Impostazioni →
   Accessibilità**.
2. Cerca **"App Aperte"** nell'elenco e attivala.
3. Torna nell'app e ritocca il pulsante: si apre la panoramica di sistema.

**Limite onesto:** questa è la UI di sistema (SystemUI), non un'Activity
della nostra app — MultiStar qui non c'entra, perché non stiamo mostrando
una nostra schermata ma richiamando quella del sistema. Se la inneschi
mentre stai usando lo schermo esterno, *dovrebbe* comparire lì, perché
Samsung fa sì che sia sempre il pannello attivo (non uno secondario) a
disegnare tutta l'interfaccia di sistema quando il telefono è chiuso. Non
ho però modo di verificarlo sul tuo firmware specifico: provalo sul
telefono e, se non dovesse comparire sullo schermo esterno, usa la lista
personalizzata (punto precedente), che invece è una nostra Activity e quindi
segue le regole di MultiStar.

## 4. Aggiungila allo schermo esterno con MultiStar

1. Installa **Good Lock** dal Galaxy Store.
2. Dentro Good Lock, tab **Plugin**, scarica **MultiStar**.
3. Apri MultiStar → **I ♡ Galaxy Foldable** → **Launcher Widget**.
4. Nella lista delle app, seleziona anche **App Aperte** (la nostra app).
5. Vai in Impostazioni del telefono → **Schermo di copertina** → **Widget**,
   trova il widget "Launcher" di MultiStar e aggiungilo allo schermo esterno.
6. Da lì potrai aprire "App Aperte" direttamente sullo schermo esterno.

## Limiti da sapere

- Android non permette a un'app normale di vedere i "processi realmente in
  esecuzione" di altre app (restrizione di sistema dalla versione Lollipop in
  poi, per privacy). La lista mostrata è quindi quella delle **app usate più
  di recente** (ultime 24 ore), aggiornata ogni 3 secondi — è il modo più
  vicino possibile a "app aperte" ottenibile da un'app di terze parti, ed è
  lo stesso approccio usato da qualsiasi app-switcher non di sistema.
- Il rendering effettivo sullo schermo esterno dipende da MultiStar/Good
  Lock, non dall'app in sé: su alcuni firmware potrebbe volerci un riavvio
  del telefono dopo aver installato MultiStar perché il widget compaia tra
  quelli disponibili.

## Personalizzazioni rapide

- Intervallo di aggiornamento: `refreshIntervalMs` in `MainActivity.kt`.
- Numero massimo di app mostrate: `.take(20)` in `MainActivity.kt`.
- Finestra temporale (ora è 24h): variabile `start` in `loadRecentApps()`.
- Colori/tema: `app/src/main/res/values/colors.xml` e `themes.xml`.
