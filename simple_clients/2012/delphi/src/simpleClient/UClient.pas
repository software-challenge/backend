unit UClient;

interface
  uses UPlayer, UBoard, UCard, USegment, UTower, UDebugHint, UMyBoard, UMove, SysUtils, Classes, UInteger;
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
        function waehleSteineAus : TMove;
        function baueHochhaus : TMove;
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

  (*
  Berechne einen Auswahlzug. In diesem Beispiel werden
  6 zufällige Bauteile gewählt.
  *)
  function TClient.waehleSteineAus : TMove;
    var
      mov : TMove;
      auswahl : TSelection;
      ausgewaehlt : Integer;
      segmentCounts : TSegmentCounts;
      n : Integer;
    begin
      mov := TMove.Create;
      ausgewaehlt := 0;
      segmentCounts := Me.getRetainedSegmentCounts;
      for n := 1 to 4 do begin
        auswahl[n] := 0;
      end;
      while ausgewaehlt < 6 do begin
        n := random(4) + 1;
        if(segmentCounts[n] > 0) then begin
          auswahl[n] := auswahl[n] + 1;
          segmentCounts[n] := segmentCounts[n] - 1;
          ausgewaehlt := ausgewaehlt + 1;
        end;
      end;
      mov.select(auswahl);
      Result := mov;
    end;

  (*
  Berechne einen Bauzug. In diesem Beispiel wird der
  erstbeste Zug ausgewählt.
  *)
  function TClient.baueHochhaus : TMove;
    var
      curCardNum : Integer;
      curCard : TCard;
      curCityNum : Integer;
      segments : TSegmentCounts;
      curSegmentSize : Integer;
      tower : TTower;
      mov : TMove;
    begin
      mov := TMove.create;
      Result := nil;
      segments := Me.getUsableSegmentCounts;
      for curCardNum := 0 to Me.Cards.Count - 1 do begin
        curCard := TCard(Me.Cards[curCardNum]);
        for curCityNum := 0 to 3 do begin
          for curSegmentSize := 1 to 4 do begin
            if(segments[curSegmentSize] > 0) then begin
              tower := FBoard.getTowerAt(curCityNum, curCard.Slot);
              if(tower = nil) then begin
                mov.build(curCityNum, curCard.Slot, curSegmentSize);
                Result := mov;
                break;
              end
              else begin
                if(tower.canAddPart(Me, curSegmentSize)) then begin
                  mov.build(curCityNum, curCard.Slot, curSegmentSize);
                  Result := mov;
                  break;
                end;
              end;
            end;
          end;
          if(Result <> nil) then break;
        end;
        if(Result <> nil) then break;
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

      // Zug berechnen lassen, je nachdem ob ein Auswahlzug oder
      // ein Bauzug gefordert ist
      if(FBoard.IsSelection) then begin
        mov := waehleSteineAus;
      end
      else begin
        mov := baueHochhaus;
      end;

      // Einen Debug-Hinweis mit dem Zug ubertragen. Dieser wird dann im Server auf der GUI
      // angezeigt.
      mov.addHint(TDebugHint.create('Das ist ein zufalliger Zug!'));

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
      FHaveToEatSalad := false;
      FBoard := board;
    end;
end.
