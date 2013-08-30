unit UClient;

(*
 * Diese Unit enthält die Spiellogik
 *
 * Im Auslieferungszustand ist hier beispielhaft eine einfache Spiellogik implementiert, die das Spiel
 * fehlerfrei spielen kann. Die Züge werden allerdings größtenteils zufällig ausgeführt.
 *)

interface
  uses UPlayer, UBoard, UDebugHint, UMyBoard, UMove, UField, ULayMove, UExchangeMove, UStone, UUtil, SysUtils, Classes, UInteger;
  type
    TClient = class
      protected
        FMyId : Integer;                        // Die SpielerID dieses Clients (0 oder 1)
        FPlayers : array [0..1] of TPlayer;     // Die beiden teilnehmenden Spieler
        FBoard : TMyBoard;                      // Das Board des Spiels
        FActivePlayer : String;                 // Der Spieler, der gerade an der Reihe ist ("red" oder "blue")
        FTurn : Integer;                        // Nummer des aktuellen Zuges
        Me : TPlayer;                           // Der Spieler, der von diesem Client gesteuert wird
        Opponent : TPlayer;                     // Der Spieler, der vom Client des Gegenspielers gesteuert wird
        LastMove : TMove;                       // Der in diesem Spiel zuletzt getätigte Zug oder nil
      public
        function getPlayer(playerNum : Integer) : TPlayer; overload;
        function getPlayer(displayName : String) : TPlayer; overload;
        function getBoard : TBoard;
        function zugAngefordert : TMove;
        function macheZufallszug : TMove;
        function platziereStein(stone : TStone) : Boolean;

        procedure setId(playerId : Integer);

        property MyId : Integer read FMyId write FMyId;
        property CurrentTurn : Integer read FTurn write FTurn;
        constructor Create(board : TMyBoard);
        destructor destroy; override;
    end;

implementation

uses UNetwork, Math;
  destructor TClient.destroy;
  begin
    if(FPlayers[0] <> nil) then FPlayers[0].Free;
    if(FPlayers[1] <> nil) then FPlayers[1].Free;
    inherited;
  end;

  (*
   * Platziert den gegebenen Stein an einer zufälligen, gültigen Position
   * (Probiert alle freien Felder aus, die an bereits belegte Felder angrenzen)
   *)
  function TClient.platziereStein(stone : TStone) : Boolean;
  var
    x, y : Integer;
  begin
    Result := true;
    for x := 0 to 15 do begin
      for y := 0 to 15 do begin
        if FBoard.isOccupied(x, y) then begin  // Finde belegtes Feld
          if FBoard.layStoneAt(stone, x - 1, y) then exit; // Probiere links davon
          if FBoard.layStoneAt(stone, x + 1, y) then exit; // Probiere rechts davon
          if FBoard.layStoneAt(stone, x, y - 1) then exit; // Probiere oberhalb
          if FBoard.layStoneAt(stone, x, y + 1) then exit; // Probiere unterhalb
        end;
      end;
    end;
    Result := false;
  end;

  (*
   * Macht einen mehr oder weniger zufällig ausgewählten Zug.
   *
   * 1. Fall: Mache den allerersten Zug des Spiels:
   * Versuche, zwei Steine zu finden, die zusammen zufällig auf dem Brett platziert
   * werden können.
   *
   * 2. Fall:
   * Probiere der Reihe nach alle Steine durch. Versuche, für jeden Stein eine
   * gültige Position zum Anlegen zu finden.
   *
   * In beiden Fällen wird, falls kein Legezug gefunden wird, eine zufällige Anzahl
   * an Steinen eingetauscht.
   *)
  function TClient.macheZufallszug : TMove;
    var
      layMove : TLayMove;               // Der Legezug, der gemacht wird, wenn einer gefunden wird
      exchangeMove : TExchangeMove;     // Der Tauschzug, der gemacht wird, wenn kein Legezug gefunden wurde
      n, o, x, y: Integer;
      stone, stone2 : TStone;
    begin
      writeln('');
      write('Punkte danach: ');
      writeln(IntToStr(FBoard.getScoresForPlayer(Me.PlayerID)));

      layMove := nil;
      exchangeMove := TExchangeMove.create;
      // Wenn Anfangszug: Zwei Steine finden, die zusammenpassen und diese zufällig positionieren
      if LastMove = nil then begin
        for n := 0 to Me.Stones.Count - 1 do begin
          if layMove <> nil then Break; // Wenn bereits ein Zug gefunden wurde, breche ab
          stone := TStone(Me.Stones[n]);
          if (RandomRange(0, 2) = 0) then exchangeMove.addStoneToExchange(stone);  // Füge Stein möglicherweise zum Tauschzug hinzu
          for o := n + 1 to Me.Stones.Count - 1 do begin
            if layMove <> nil then Break; // Wenn bereits ein Zug gefunden wurde, breche ab
            stone2 := TStone(Me.Stones[o]);
            // Prüfe, ob die beiden Steine zusammen liegen dürfen
            if stone.canBeInSameRowWith(stone2) then begin
              // Passende Steine gefunden, positioniere sie irgendwo horizontal nebeneinander
              y := RandomRange(0, 16);
              x := RandomRange(0, 15);
              layMove := TLayMove.create;
              layMove.addStoneToField(stone, FBoard.getField(x, y));
              layMove.addStoneToField(stone2, FBoard.getField(x + 1, y));
            end;
          end;
        end;
        if layMove = nil then begin
          // Sicherstellen, dass der Tauschzug mindestens einen Stein enthält
          if exchangeMove.stonesToExchange.Count = 0 then exchangeMove.addStoneToExchange(TStone(Me.Stones[0]));
          Result := exchangeMove;
        end else begin
          Result := layMove;
        end;
      end else begin
        // Wenn kein Anfangszug, dann alle liegenden Steine durchgehen
        // Jeden Stein zufällig für einen möglichen Tauschzug auswählen
        // Außerdem für jeden Stein prüfen, ob er irgendwo angelegt werden kann
        // Wenn ein Stein zum Anlegen gefunden wurde, diesen Zug ausführen.
        for n := 0 to Me.Stones.Count - 1 do begin
          stone := TStone(Me.Stones[n]);
          if (RandomRange(0, 2) = 0) then exchangeMove.addStoneToExchange(stone);  // Füge Stein möglicherweise zum Tauschzug hinzu
          if platziereStein(stone) then Break; // Versuche, den Stein anzulegen
        end;

        // Wenn ein möglicher Anlegezug gefunden wurde, diesen ausführen, ansonsten den Tauschzug ausführen
        layMove := FBoard.createLayMove;
        if (layMove = nil) then begin
          // Sicherstellen, dass der Tauschzug mindestens einen Stein enthält
          if exchangeMove.stonesToExchange.Count = 0 then exchangeMove.addStoneToExchange(TStone(Me.Stones[0]));
          Result := exchangeMove;
        end else begin
          exchangeMove.Free;
          Result := layMove;
        end;
      end;
    end;

  (*
  Wird aufgerufen, wenn ein Zug angefordert wurde.
  Soll einen gültigen Zug zurückliefern. Gibt diese Funktion keinen
  oder einen ungültigen Zug zurück, ist das Spiel verloren.
  *)
  function TClient.zugAngefordert : TMove;
    var
      mov : TMove;
    begin
      // Die beiden Spieler zur Übersicht als Ich und Gegner ordnen
      if(FPlayers[0].PlayerID = FMyId) then begin
        Me := FPlayers[0];
        Opponent := FPlayers[1];
      end
      else begin
        me := FPlayers[1];
        opponent := FPlayers[0];
      end;

      write('Punkte vor dem Zug: ');
      writeln(IntToStr(FBoard.getScoresForPlayer(Me.PlayerID)));

      if FBoard.LastMove <> nil then begin
        LastMove := FBoard.LastMove;
        write('Letzter Zug: ');
        writeln(FBoard.LastMove.toString());
      end;

      writeln(FBoard.toString());

      // Zufälligen Zug berechnen lassen
      mov := macheZufallszug;

      if (mov <> nil) then begin
        writeln('Zug gefunden: ');
        if mov is TLayMove then begin
          writeln('Legezug');
          mov.addHint(TDebugHint.create('Legezug'));
        end else if mov is TExchangeMove then begin
          writeln('Tauschzug');
          mov.addHint(TDebugHint.create('Tauschzug'));
        end;
        writeln(mov.toString);
      end else begin
        writeln('KEIN ZUG GEFUNDEN!');
      end;

      Result := mov;
    end;

  procedure TClient.setId(playerId : Integer);
    begin
      FMyId := playerId;
    end;

  function TClient.getPlayer(playerNum : Integer) : TPlayer;
    begin
      Result := FPlayers[playerNum];
    end;

  function TClient.getPlayer(displayName : String) : TPlayer;
    begin
      Result := nil;
      if(FPlayers[0].DisplayName = displayName) then begin
        Result := FPlayers[0];
      end
      else if(FPlayers[1].DisplayName = displayName) then begin
        Result := FPlayers[1];
      end;
    end;

  function TClient.getBoard : TBoard;
    begin
      Result := FBoard;
    end;

  constructor TClient.Create(board : TMyBoard);
    begin
      inherited Create;
      // Die zwei Spieler werden schonmal erstellt.
      FPlayers[0] := TPlayer.Create('Spieler 1');
      FPlayers[1] := TPlayer.Create('Spieler 2');

      Randomize;
      FBoard := board;
    end;
end.
