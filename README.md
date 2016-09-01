# Simulation von betriebswirtschaftlichen Kennzahlen
Diese Projekt wurde im Rahmen der Ausarbeitung zur Vorlesung 'Modellierung dynamischer Systeme' erstellt. 
Dabei handelt es sich um die Simulation eines Mobiltelefonmarktes mit mehreren Teilnehmern und einer Lieferkette.
Das Ziel dieser Simulation ist eine möglichst realistische Simulation, in der ein Teilnehmer den Markt verlassen soll und die
Produktion einstellen soll, sobald diese Unwirtschaftlich ist.

## Installation
Da es sich hierbei um ein Maven-Projekt handelt werden fast alle, bis auf eine, Abhängigkeiten automatisch runtergeladen und in das Projekt eingebunden. Die JavaFx-Bibliothek muss im Build-Path angepasst werden, diese liegt im Java Verzeichniss, z.B. unter: *\Java\jre1.8.0_xx\lib\ext\jfxrt.jar. Dafür muss jedoch mindestens Java 8 installiert sein.

## Ausführen
Das Projekt besteht zum einen aus einer Vorabversion und der finalen Version. Beide liegen im folgenden Packege: "de.haw.company.gui.main". Die Vorabversion, ohne ein Multi-Agenten-System, wird mithilfe der "GuiMain.java" gestartet. Die finale Version mit "GuiMultiAgentMain.java".
