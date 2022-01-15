# Was ist FCanvas?

FCanvas ist eine schlanke leicht zu benutzende Java-Library zum Zeichnen und Animieren von einfachen Grafikobjekten. Sie setzt keine Kenntnisse über Java, Grafische Benutzerschnittstellen oder Computergrafik voraus, die über das Aufrufen von statischen Java-Methoden hinaus geht. Im Endeffekt handelt es sich dabei um eine Kapselung der Java 2D API durch die Klasse `de.thm.mni.oop.fcanvas.FCanvas` als einzige Benutzerschnittstelle.

Die Library wurde entwickelt um Studenten beim Lernen der Programmiersprache Java schnell zu ermöglichen auch grafische Beispielaufgaben zu lösen und sogar kleine Spiele selbst zu programmieren. Sie bietet eine extrem einfache Bedienung und bewahrt dabei noch so viel Performance und Reaktivität wie möglich.

# Einbinden in Eclipse

Eine JAR-Datei können sie auf verschiedenen Wegen als Library in Eclipse einbinden: Im ganzen Workspace als Teil der System Library oder nur in einem einzelnen Projekt.

## In einem Einzelprojekt

Um FCanvas in einem Einzelprojekt in Eclipse einzubinden machen sie einen Rechtsklick auf das Projekt und wählen sie dann Properties → Java Build Path → Libraries → Add external JARs.. In dem dortigen Auswahldialog müssen sie nun den Pfad zur JAR-Datei von FCanvas angeben und die Auswahl bestätigen.

## Im ganzen Workspace

Das Einbinden für den gesamten Workspace ist ein wenig komplizierter. Eclipse erlaubt es zu einer System Library weitere JAR-Dateien hinzuzufügen. Klicken sie dazu im Eclipse-Hauptmenü auf Window → Preferences → Java → Installed JREs. Dort sollte sich ihr Java 8 JDK befinden und mit einem Häkchen als default markiert sein. Klicken sie auf dieses JDK und wählen sie aus dem rechten Menü den Button Edit.. um nun im dort erscheinenden Dialogfenster unter Add external JARs.. wieder die JAR-Datei von FCanvas anzugeben.

Ab sofort können Sie nun FCanvas in allen Projekten verwenden

## Durchsuchen der Dokumentation und Quelldateien

Die JAR-Datei der FCanvas-Library enthält auch die Quelldateien. Das erlaubt ihnen nicht nur die Javadoc-Kommentare durchzulesen, indem sie wie bei der Java API die Maus über den jeweiligen Funktionsaufruf halten oder FCanvas. eingeben und die angezeigte Auswahlliste zur Code-Komplettierung durchgehen, sondern auch mit dem Drücken der Taste F3 jederzeit in den Quellcode der jeweiligen Funktion zu springen. Auf diese Weise können sie sich genau anschauen, wie die Library im Einzelnen realisiert ist, wenn Sie sich dafür interessieren.

# Zeichnen

FCanvas arbeitet mit einem einzigen Grafikfenster, das mit der Methode `FCanvas.show()` angezeigt werden kann. Auf diesem Fenster lassen sich einfache Grafikelemente zeichnen, die im folgenden aufgeführt sind.

Wie in der Computergrafik üblich beginnt das Koordinatensystem dieses Fensters mit dem Punkt (0,0) an der linken oberen Ecke des Fensters. Die Größe des Zeichenbereichs lässt sich dabei mit der Methode `FCanvas.setCanvasSize(int,int)` einstellen.

## Grafikelmente

### Rechteck

Eines der einfachsten Grafikelemente ist das Rechteck. Folgendes Codebeispiel zeichnet ein 100 Pixel breites und 200 Pixel hohes Rechteck mit schwarzem Rand und transparenter Füllung dessen obere linke Ecke 10 Pixel vom oberen und 30 Pixel vom linken Rand entfernt ist.

```java
FCanvas.drawRectangle(30,10,100,200);
FCanvas.show();
```

Die Farbe, Liniengröße, Füllung und andere Eigenschaften eines Rechtecks lassen sich natürlich auch verändern. Wie das funktioniert wird in Abschnitt <a href="#modifikationen">Modifikationen</a> beschrieben.

### Oval

Ovale lassen sich genauso zeichnen wie Rechtecke, nur dass hier die obere linke Ecke die Ecke der bounding box - also des umschließenden Rechtecks - bezeichnet wird. Der Effekt lässt sich am einfachsten mit dem folgenden Codebeispiel erklären:

```java
FCanvas.drawRectangle(20,10,100,75);
FCanvas.drawOval(20,10,100,75);
FCanvas.show();
```

### Linie

Linien definieren sich durch einen Start- und einen Endpunkt. Das folgende Beispiel zeichnet eine schwarze Linie vom Punkt (100,200) zum Punkt (300,400):

```java
FCanvas.drawLine(100,200,300,400);
FCanvas.show();
```

### Text

Auch ein Text kann an eine beliebige Position geschrieben werden. Dabei wird nun aber nicht der linke *obere* Punkt angegeben, sondern der Ankerpunkt liegt auf der Baseline des Textes. Das Konzept der Baseline lässt sich am leichtesten an einem Beispiel demonstrieren:

```java
FCanvas.drawLine(100,100,300,100);
FCanvas.drawText("Alpaca",100,100);
FCanvas.show();
```

### Polygon

Das Polygon ist das einzige kompliziertere Grafikelement von FCanvas. Es besteht aus einer Reihe von Punkten *p*<sub>1</sub>, *p*<sub>2</sub>, ..., *p*<sub>*n*</sub> wobei jeweils eine Linie zwischen *p*<sub>1</sub> und *p*<sub>2</sub>, *p*<sub>2</sub> und *p*<sub>3</sub> usw gezeichnet wird bis zur letzten Linie von *p*<sub>*n*</sub> zu *p*<sub>1</sub>, die die Form abschließt. Die Punkte werden als zwei Arrays übergeben bei denen das erste Array die x-Koordinaten der Punkte enthält und das zweite Array die y-Koordinaten.

Das folgende Beispiel zeichnet einen einfachen vierzackigen Stern:

```java
int[] x = {100,110,150,110,100, 90, 50,90};
int[] y = { 50, 90,100,110,150,110,100,90};
FCanvas.drawPolygon(x,y);
FCanvas.show();
```

## Modifikationen

Alle Elemente aus dem Abschnitt <a href="#grafikelemente">Grafikelemente</a> lassen sich natürlich auch weiter modifizieren.

Die folgende Tabelle zeigt welche Funktionen es gibt, um Elementeigenschaften zu ändern und auf welche Elemente sie wirken:

Table: Übersicht, welche Eigenschaften auf welche Grafikelemente zutreffen

| Funktion          | Eigenschaft                  | Rechteck | Oval | Polygon | Linie | Text |
|:------------------|:-----------------------------|:---------|:-----|:--------|:------|:-----|
| `setStrokeWidth ` | Strichbreite in Pixeln       |     ✓    |  ✓   |    ✓    |   ✓   |      |
| `setStrokeColor ` | Strichfarbe in RGB(A)        |     ✓    |  ✓   |    ✓    |   ✓   |   ✓  |
| `setFillColor `   | Füllfarbe in RGB(A)          |     ✓    |  ✓   |    ✓    |       |      |
| `setRotation `    | Rotation in Grad             |     ✓    |  ✓   |    ✓    |   ✓   |   ✓  |
| `move `           | Position als Punkt (*x*,*y*) |     ✓    |  ✓   |    ✓    |   ✓   |   ✓  |
| `setFontSize `    | Schriftgröße                 |          |      |         |       |   ✓  |

Um einzelne Elemente identifizieren zu können, liefert jede `drawX`-Methode eine ID vom Typ `long` zurück. Alle Modifizierungsmethoden erwarten eine solche ID als Parameter.

Das folgende Beispiel zeigt, wie man ein um 45 Grad gedrehtes Rechteck mit roter halbtransparenter Füllung zeichnet:

```java
long id = FCanvas.drawRectangle(100,100,50,50);
FCanvas.setFillColor(id,255,0,0,128);
FCanvas.setRotation(id,45);
FCanvas.show();
```

# Animationen

## Zeichnen in Schritten

Um Animationen zu realisieren, muss die Ausführung von einzelnen Zeichenbefehlen verzögert werden. Dazu gibt es in Java die Methode `Thread.sleep(long)` mit der man den aktuellen Prozess schlafenlegen kann für eine in Millisekunden gemessene Zeitspanne. Da diese Methode die Ausnahme `InterruptedException` werfen kann, muss sie mit einem try-catch-Block umgeben werden wie in folgendem Beispiel:

```java
FCanvas.show();
long id = FCanvas.drawOval(10,10,20,20);
//eine halbe Sekunde warten
try {Thread.sleep(500);} catch (Exception e) {}
//dann den Kreis mit blauer Farbe füllen
FCanvas.setFillColor(id,0,0,255);
```

Falls Sie sich noch nicht mit Ausnahmen in Java beschäftigt haben, lassen Sie sich nicht verwirren. Sie können die Zeile mit dem Aufruf von `Thread.sleep` erst einmal einfach kopieren und genau so in ihrem Code verwenden. Sie müssen nur statt der 500 natürlich die Anzahl an Millisekunden eintragen, die sie für ihre Animation warten wollen.

Wichtig ist außerdem noch anzumerken, dass der Aufruf von `FCanvas.show()` natürlich immer als erste Zeile in einer Animation auftauchen muss. Vor diesem Aufruf wird das Fenster nicht angezeigt und die Animation läuft nur im Hintergrund ab.

## Animationsschleife

Wenn man schon dabei sind, grafische Animationen zu programmieren, will man in der Regel auch einen flüssigen Bildverlauf haben statt nur einigen ruckelig aneinandergereihten Bildern. Dazu bietet es sich an, eine Schleife zu verwenden, die genügend Bilder in der Sekunde verarbeiten kann, um den Eindruck einer flüssigen Bewegung zu erzeugen. Dazu reichen etwa 30 Bilder in der Sekunde aus, also eine Wartezeit von 1000/30 ≅ 33 Millisekunden.

Diese Animationsschleife kann dann entweder so lange laufen bis die Animation vorbei ist oder bis das Fenster geschlossen wurde. In letzterem Fall können Sie als Bedingung in ihrer while-Schleife die Abfrage `FCanvas.isVisible()` verwenden.

Das folgende Code-Stück zeigt genau dieses Verhalten am Beispiel einer um einen Punkt kreisenden gelben Kugel.

```java
FCanvas.show();
long id = FCanvas.drawOval(10, 10, 50, 50);
FCanvas.setFillColor(id, 255, 255, 0);
FCanvas.setStrokeColor(id, 0, 0, 0, 0);
int t = 0;
while(FCanvas.isVisible()) {
  int x = (int)Math.round(100*Math.sin(Math.toRadians(t)));
  int y = (int)Math.round(100*Math.cos(Math.toRadians(t)));
  FCanvas.move(id, 200+x, 200+y);
  t = (t+3) % 360;
  try {Thread.sleep(33);} catch (Exception e) {}
}
```

# Interaktive Animationen

Nun, wo wir es beherrschen flüssige Bewegungen darzustellen, fehlt zu einem ersten kleinen Spiel eigentlich nur noch die Interaktion mit dem Benutzer. Diese kann natürlich einfach mit `JOptionPane` über Dialogfenster realisiert werden, aber wer will schon jedesmal erst einen Dialog wegklicken bevor er irgendetwas in einem Spiel tun kann? FCanvas bietet deshalb auch die Möglichkeit auf Eingabe über Maus und Tastatur zu reagieren.

Als kleiner Disclaimer sei hier vorneweg angemerkt, dass diese Eingabeabfragen aufgrund der einfachen Struktur von FCanvas nicht völlig exakt realisiert werden können. Erwarten Sie also nicht eine völlig saubere Spielsteuerung zu erhalten, die nie eine Eingabe verpasst und innerhalb weniger Millisekunden reagiert. Dazu müssten sie die unterliegende Funktionalität des Swing-Toolkits direkt verwenden.

## Einzelne Klicks und Tastendrücke

Abfragen der Form Wurde die Taste X seit den letzten Y Millisekunden gedrückt? können mit den Funktionen `FCanvas.wasKeyPressed` bzw `FCanvas.wasMouseButtonPressed` realisiert werden. Die Definitionen der Zahlencodes für die einzelnen Tasten finden Sie jeweils in den Klassen `java.awt.event.KeyEvent` bzw. `java.awt.event.MouseEvent`.

Die beiden oben genannten Methoden benötigen eine explizite Angabe, wieviele Millisekunden man in die Vergangenheit schauen will. Natürlich ist es schwierig hier den richtigen Wert zu wählen. Ist die Zeitspanne zu kurz, kann es sein dass man einen Klick verpasst. Ist die Zeitspanne aber zu groß, kann es passieren, dass man einen einzelnen Klick mehrfach zählt.

Im folgenden Beispiel wird als Wartezeit genau der Wert verwendet, der auch an `Thread.sleep` übergeben wird, um einen Kreis rot bzw. blau zu Färben wenn die Pfeiltaste nach links bzw nach rechts gedrückt wurde.

```java
FCanvas.show();
long id = FCanvas.drawOval(10, 10, 50, 50);
while(FCanvas.isVisible()) {
  if (FCanvas.wasKeyPressed(KeyEvent.VK_LEFT, 30)) {
    FCanvas.setFillColor(id, 255, 0, 0);
  } else if (FCanvas.wasKeyPressed(KeyEvent.VK_RIGHT, 30)) {
    FCanvas.setFillColor(id, 0, 0, 255);
  }
  try {Thread.sleep(30);} catch (Exception e) {}
}
```

Wenn Sie diesen Code ausführen, werden sie bemerken, dass sie manchmal mehrfach eine Taste drücken müssen, bevor die Farbe sich ändert. Das liegt daran, dass zu den 30 Millisekunden ja auch noch ein paar Millisekunden für die Ausführung der eigentlichen Animationsschleife hinzukommt.

Um dieses Problem zu umgehen, gibt es für Animationsschleifen, die sowieso immer wieder periodisch Eingaben abfragen die Methoden `FCanvas.getKeyPressesSinceLastAsked` bzw. `FCanvas. getMouseButtonPressesSinceLastAsked`. Wie der Name schon sagt zählen diese Methoden die Tastendrücke seit der letzten Abfrage. Beim allerersten Aufruf werden dann natürlich alle Tastendrücke seit dem Start des Programms gezählt.

Das gleiche Beispiel würde dann wie folgt aussehen:

```java
FCanvas.show();
long id = FCanvas.drawOval(10, 10, 50, 50);
while(FCanvas.isVisible()) {
  if (FCanvas.getKeyPressesSinceLastAsked(KeyEvent.VK_LEFT) > 0) {
    FCanvas.setFillColor(id, 255, 0, 0);
  } else if (FCanvas.getKeyPressesSinceLastAsked(KeyEvent.VK_RIGHT) > 0) {
    FCanvas.setFillColor(id, 0, 0, 255);
  }
  try {Thread.sleep(30);} catch (Exception e) {}
}
```

## Gedrückthalten

Wenn es auch möglich sein soll, eine Taste dauerhaft gedrückt zu halten, gibt es außerdem noch die Methoden `FCanvas.isKeyDown` bzw. `FCanvas.isMouseButtonDown`. Diese geben jeweils den Zustand der Taste (gedrückt oder nicht gedrückt) zum aktuellen Zeitpunkt der Abfrage an.

Das folgende Beispiel zeigt einen Kreis, der durch Gedrückthalten der Pfeiltasten nach links oder nach rechts bewegt werden kann.

```java
FCanvas.show();
int x = 300;
int y = 300;
long id = FCanvas.drawOval(x, y, 50, 50);
while(FCanvas.isVisible()) {
  if (FCanvas.isKeyDown(KeyEvent.VK_LEFT)) {
    x -= 3;
  } else if (FCanvas.isKeyDown(KeyEvent.VK_RIGHT)) {
    x += 3;
  }
  FCanvas.move(id,x,y);
  try {Thread.sleep(30);} catch (Exception e) {}
}
```

## Mausposition

Auch die Mausposition lässt sich natürlich abfragen. Dazu dienen die Methoden `FCanvas. getLastMouseX` und `FCanvas.getLastMouseY`. Das folgende Beispiel implementiert einen Kreis, der der Maus folgt.

```java
FCanvas.show();
int x = 300;
int y = 300;
long id = FCanvas.drawOval(x, y, 50, 50);
while(FCanvas.isVisible()) {
  x = FCanvas.getLastMouseX()-25;
  y = FCanvas.getLastMouseY()-25;
  FCanvas.move(id,x,y);
  try {Thread.sleep(30);} catch (Exception e) {}
}
```

# Sonstiges

Dieser Abschnitt behandelt alle weiteren Besonderheiten von FCanvas, die in keinen der anderen Abschnitte passen.

## Verdeckungen

Wenn zwei Grafikelemente sich überlappen, liegt das später erzeugte Element über dem zuerst erzeugten. Diese Reihenfolge lässt sich nicht ändern. Sollte es einmal unbedingt nötig sein, können Sie aber einfach das Element was nach oben rutschen soll zuerst mit `FCanvas.remove` löschen und dann wieder neu erzeugen.

## Performance und Update-Verhalten

FCanvas zeichnet normalerweise bei jedem Aufruf einer Zeichenmethode das gesamte Bild neu. Wenn man eine Animation mit sehr kleinen Zeitschritten programmieren möchte, kann es Sinn machen diese automatischen Updates auszuschalten mit der Methode `FCanvas.setAutoUpdate`. Man muss dann das Zeichnen manuell mit der Methode `FCanvas.update` ausführen.

## Canvas Width/Height

Auf die Größe des Zeichenbereichs (Fenstergröße ohne den Rahmen) können Sie mit den Methoden `FCanvas.getCanvasWidth` bzw. `FCanvas.getCanvasHeight` zugreifen. Sie können die Größe des Zeichenbereichs auch aus dem Code heraus festsetzen mit der Methode `FCanvas.setCanvasSize`.

## Speichern als Bilddatei

Seit Version 1.3 kann man den Inhalt des Canvas-Fensters mit der Methode `FCanvas.saveToImage` als Bilddatei abspeichern.
