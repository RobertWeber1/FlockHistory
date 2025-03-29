# Aktionen/Vorgänge

1. Zutritt
2. Bestandsaufnahme
3. Behandlung
4. Nachwuchs
	- Anzahl Lämmer
	- temporäre Nummer (1-100 Seite[links, rechts] + Farbe[Rot, Grün])
5. Abgang
	- Verkauf
	- Schlachtung
	- Wolfsriss
	- Diebstahl
6. Pension


* Welche infos sind relevant?
* Wie interagiert man mit der App?
* Wie sind die Vorgänge? -> Welche Rolle spielt die App dabei?

## Zutritt

Infos: TagID, Timestamp, Alter (default 12 Monate), Geschlecht, Genotyp
Ablauf:
	* Tag wird zum ersten mal gescannt
	* Lamm bekommt Tag eingesetzt
	* Infos in App erfassen (Felder sind mit letzten Werten vorausgefüllt)
	* Prozess abschließen
	* App ist wieder bereit für nächsten Zugang

## Bestandsaufnahme

Infos: TagID, Timestamp
Ablauf:
	* alle Tiere werden gescannt um verluste zu erfassen

## Behandlung

Infos: TagID, Timestamp, Behandlungsart (z.B. Wurmkur) als Freitextfeld, Feld für Bemerkungen
Ablauf:
	* Schaf wird behandelt
	* Schaf wird gescannt
	* Behandlungsart auswählen
	* Prozess abschließen

## Nachwuchs

Infos (Mutter): TagID, Timestamp, Lämmeranzahl, temporäre Nummer
Infos (Lämmerpool pro Lammzeit): Timestamp (geburt)

Ablauf (Gutfall):
	* Lamm/Lämmer wird/werden geboren
	* Mutter scannen
	* Lamm und Mutter bekommen temporäre Nummer von App zugewiesen (voerausgefüllt aber editierbar)
	* Anzahl der Lämmer erfassen
	* Prozess abschließen

TODO: Randfälle

## Abgang
### Einzeltiergekennzeichnet

Infos: TagID, Timestamp, Grund (Freitextfeld mit Dropdown), Freitextfeld für Bemerkungen

Ablauf:
	* Tag scannen
	* Grund auswählen
	* Prozess abschließen

### Nicht Einzeltiergekennzeichnet

Infos: Timestamp, Lämmerpool, Grund (Freitextfeld mit Dropdown), Freitextfeld für Bemerkungen

Ablauf:
	* Lämmerpool auswählen
	* Grund auswählen
	* Prozess abschließen

## Pension

TODO: noch zu spezifizeiren
