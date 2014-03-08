unit UMyBoard;

interface
  uses UBoard, UDefines, UInteger, UPlayer, UMove, Classes, UTower, UUtil, SysUtils;

  type
    TPlayerStats = array [0..3] of Integer;
    TGameStats = array [0..1] of TPlayerStats;
    TMyBoard = class(TBoard)
     public
        function isMoveValid(Move : TMove; playerId : Integer) : Boolean;
        function getGameStats : TGameStats;
        function getPlayerStats(playerId : Integer) : TPlayerStats;
    end;
implementation

  (*
  Gibt an, ob der gegebene Zug zu diesem Zeitpunkt ein gültiger
  Zug für den Spieler mit gegebener ID ist.
  *)
  function TMyBoard.isMoveValid(Move : TMove; playerId : Integer) : Boolean;
    var
      tower : TTower;
      count : Integer;
      segmentsRetained : TSegmentCounts;
      n : Integer;
    begin
      Result := True;
      // Ist der Zugtyp korrekt?
      if(Move.isBuildMove = FIsSelection) then begin
        Result := False;
        exit;
      end;

      if(Move.isBuildMove) then begin
        // Erlaubt der gewählte Turm den gewünschten Zug?
        tower := getTowerAt(Move.City, Move.Slot);
        if((tower <> nil) and (not tower.canAddPart(playerId, Move.Size))) then begin
          Result := False;
          exit;
        end;
      end
      else begin
        // Prüfe, ob von den gewählten Elementen auch ausreichend vorhanden sind
        segmentsRetained := getPlayer(playerId).getRetainedSegmentCounts;
        for n := 1 to 4 do begin
          if(Move.Selection[n] > segmentsRetained[n]) then begin
            Result := False;
            exit;
          end;
          count := count + Move.Selection[n];
        end;
        // Prüfe, ob die richtige Anzahl Segmente gezogen wurde
        if(count <> 6) then begin
          Result := False;
          exit;
        end;
      end;
    end;

  (*
  Liefert den Status des Spielers mit der gegebenen ID als Array (TPlayerStats)
  mit folgendem Inhalt:
  [0] - Anzahl der Türme des Spielers
  [1] - Anzahl der Städte des Spielers
  [2] - 1, falls der Spieler den höchsten Turm hat, 0 sonst
  [3] - Punktestand des Spielers
  *)
  function TMyBoard.getPlayerStats(playerId : Integer) : TPlayerStats;
    begin
      Result := getGameStats[playerId];
    end;

  (*
  Liefert den Status beider Spieler als Array von TPlayerStats (TGameStats)
  (siehe getPlayerStats).
  [0] - TPlayerStats des ersten Spielers (rot)
  [1] - TPlayerStats des zweiten Spielers (blau)
  *)
  function TMyBoard.getGameStats : TGameStats;
    var
      gameStats : TGameStats;
      cityTowers : array[0..3] of Integer;
      highestHeight : Integer;
      highestOwnerId : Integer;
      n, m : Integer;
      tower : TTower;
      player : Integer;
    begin
      // Variablen initialisieren
      highestOwnerId := -1;
      highestHeight := 0;
      for n := 0 to 1 do begin
        for m := 0 to 3 do begin
          gameStats[n][m] := 0;
        end;
      end;
      for n := 0 to 3 do begin
        cityTowers[n] := 0;
      end;

      // Türme für jede Stadt zusammenzählen.
      // Dabei laufend den höchsten Turm sowie
      // die Vorherrschaft des Spielers in jeder Stadt ermitteln
      for n := 0 to FTowers.Count - 1 do begin
        tower := TTower(FTowers[n]);
        if(tower.getHeight > 0) then begin
          player := tower.OwnerId;

          // Vorherrschaftsarray anpassen
          if(player = PLAYER_RED) then begin
            cityTowers[tower.City] := cityTowers[tower.City] + 1;
          end
          else begin
            cityTowers[tower.City] := cityTowers[tower.City] - 1;
          end;

          // Turm dem Spieler anrechnen
          gameStats[player][0] := gameStats[player][0] + 1;

          // Höchsten Turm prüfen
          if(tower.getHeight > highestHeight) then begin
            highestHeight := tower.getHeight;
            highestOwnerId := player;
          end
          else if(tower.getHeight = highestHeight) then begin
            if(tower.OwnerId <> highestOwnerId) then begin
              highestOwnerId := -1;
            end;
          end;
        end;
      end;

      // Ermittelte Vorherrschaften aufaddieren
      for n := 0 to 3 do begin
        if(cityTowers[n] > 0) then begin
          gameStats[0][1] := gameStats[0][1] + 1;
        end
        else if(cityTowers[n] < 0) then begin
          gameStats[1][1] := gameStats[1][1] + 1;
        end;
      end;

      // Höchsten Turm auswerten
      if(highestOwnerId > -1) then begin
        gameStats[highestOwnerId][2] := 1;
      end;

      // Punkte auslesen
      gameStats[0][3] := getPlayer(0).Points;
      gameStats[1][3] := getPlayer(1).Points;

      Result := gameStats;
    end;
end.
