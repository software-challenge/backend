unit UClient;

interface
  uses UPlayer, UBoard, UDebugHint, UMyBoard, UMove, USheep, SysUtils, Classes, UInteger;
  type
    TClient = class
      protected
        FMyId : Integer;
        FPlayers : array [0..1] of TPlayer;
        FBoard : TMyBoard;
        FActivePlayer : String;
        FTurn : Integer;
        FHaveToEatSalad : Boolean;
        Me : TPlayer;
        Opponent : TPlayer;
      public
        function getPlayer(playerNum : Integer) : TPlayer; overload;
        function getPlayer(displayName : String) : TPlayer; overload;
        function getBoard : TBoard;
        function zugAngefordert : TMove;
        procedure setId(playerId : Integer);

        property MyId : Integer read FMyId write FMyId;
        property CurrentTurn : Integer read FTurn write FTurn;
        constructor Create(board : TMyBoard);
        destructor destroy; override;
    end;

implementation

uses UNetwork;
  destructor TClient.destroy;
  begin
    if(FPlayers[0] <> nil) then FPlayers[0].Free;
    if(FPlayers[1] <> nil) then FPlayers[1].Free;

    inherited;
  end;

  function TClient.zugAngefordert : TMove;
    var
      mov : TMove;
      n, m, o : Integer;
      sheeps : TList;
      targets : TList;
      die : Integer;
      sheep : TSheep;
    begin
      Result := nil;
      // Die beiden Spieler zur Übersicht als Ich und Gegner ordnen
      if(FPlayers[0].PlayerID = FMyId) then begin
        Me := FPlayers[0];
        Opponent := FPlayers[1];
      end
      else begin
        me := FPlayers[1];
        opponent := FPlayers[0];
      end;

      mov := TMove.create;

      // Die Wurfel der Reihe nach durchprobieren bis irgendein gultiger Zug gefunden wurde.
      for n := 0 to Length(FBoard.getDice) - 1 do begin
        // Den n-ten Wurfel holen
        die := FBoard.getDice[n];
        sheeps := TList.create;
        // Meine Schafe holen
        FBoard.getSheepsFor(FMyId, sheeps);
        for m := 0 to sheeps.Count - 1 do begin
          sheep := TSheep(sheeps[m]);
          targets := TList.Create;
          // Die Felder holen, die ich mit dem n-ten Wurfel und dem gewahlten Schaf erreichen kann
          FBoard.getNeighbourFields(TSheep(sheeps[m]).Node, die, targets);
          for o := 0 to targets.Count - 1 do begin
            // Den Zug probeweise vorbereiten
            mov.moveTo(TSheep(sheeps[m]).SheepIndex, TInteger(targets[o]).Value);
            // Wenn der Zug tatsachlich gultig ist, bin ich fertig.
            if(FBoard.isMoveValid(mov)) then begin
              Result := mov;
              break;
            end;
          end;
          FreeAndNil(targets);
          if(Result <> nil) then break;
        end;
        FreeAndNil(sheeps);
        if(Result <> nil) then break;
      end;

      // Einen Debug-Hinweis mit dem Zug ubertragen. Dieser wird dann im Server auf der GUI
      // angezeigt.
      mov.addHint(TDebugHint.create('Hinweis', 'Das ist ein zufalliger Zug!'));
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

      FHaveToEatSalad := false;
      FBoard := board;
    end;
end.
