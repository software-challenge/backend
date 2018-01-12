#!/bin/bash
echo "Dieses Script erstellt einen neuen Release durch Erstellen eines git Tags und Starten eines Builds."
read -p "Sollen wir fortfahren? [J/n] " -n 1 -r
if [[ $REPLY =~ ^[JjYy]$ ]]
then
    echo "Okay, dann los."
    last_tag_version=$(git tag -l --sort=-v:refname | grep mq_0 | head -n 1)
    [[ "$last_tag_version" =~ (.*[^0-9])([0-9]+)$ ]] && next_version="${BASH_REMATCH[1]}$((${BASH_REMATCH[2]} + 1))";
    echo "Die nächste Version ist $next_version"
    read -p "Kurze beschreibung dieser Version: " -r description
    git tag -a -m "$description" $next_version
    git push --tags
    ant
    echo "==================================================="
    echo "Fertig! Jetzt noch folgende Schritte ausfuehren:"
    echo " - auf der Website (http://www.software-challenge.de/wp-admin) unter Medien die Dateien ersetzen"
    echo " - unter Seiten die Downloadseite aktualisieren (neue Version in Versionshistorie eintragen)"
    echo
    echo "Dann auf der Wettkampfseite (http://contest.software-challenge.de) was unter Aktuelles schreiben und auf die Downloadseite verlinken:"
    echo
    echo "Eine neue Version der Software ist verfügbar! $description"
    echo "Dafür gibt es einen neuen Server und Simpleclient im [Download-Bereich der Website][1]."
    echo
    echo "[1]: http://www.software-challenge.de/downloads/"
    echo
    echo "Dann im Discord-Chat unter News noch etwas schreiben:"
    echo "Good news @everyone! Neue Version der Software mit Fehlerbehebungen! http://www.software-challenge.de/downloads/"
fi
echo # Newline
