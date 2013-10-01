unit UClient;

interface
  uses UPlayer, UBoard, UDebugHint, UMyBoard, UMove, UMovePart, SysUtils, Classes, UInteger;
  type
    TClient = class
      protected
        FMyId : Integer;
        FPlayers : array [0..1] of TPlayer;
        FBoard : TMyBoard;
        FActivePlayer : String;
        FTurn : Integer;
        Me : TPlayer;
        Opponent : TPlayer;
      public
        function getPlayer(playerNum : Integer) : TPlayer; overload;
        function getPlayer(displayName : String) : TPlayer; overload;
        function getBoard : TBoard;
        function zugAngefordert : TMove;
        function macheZufallszug : TMove;
        function macheZufallsTeilzug(Teilzugnummer : Integer) : TMovePart;
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

  function TClient.macheZufallsTeilzug(TeilzugNummer : Integer) : TMovePart;
  var
    TeilZugListe : TList;
    TeilZug : TMovePart;
    Zugnummer : Integer;
  begin
    // Einen Debug-Hinweis mit dem Zug ubertragen. Dieser wird dann im Server auf der GUI
    // angezeigt.
    TeilZugListe := FBoard.findAllValidMoves(Me.PlayerID, TeilzugNummer);
    if TeilZugListe.Count > 0 then begin
      Zugnummer := RandomRange(0, TeilZugListe.Count - 1);
      TeilZug := TMovePart(TeilZugListe[Zugnummer]);
      TeilZug.addHint(TDebugHint.create('Das ist ein Zufallszug!'));
      Result := TeilZug;
    end
    else Result := nil;
  end;

  function TClient.macheZufallszug : TMove;
    var
      mov : TMove;
      teilzug : TMovePart;
      n : Integer;
    begin
      mov := TMove.Create;
      for n := 0 to 2 do begin
        teilzug := macheZufallsTeilzug(n);
        if teilzug = nil then begin
          Result := mov;
          Exit;
        end;
        FBoard.applyMovePart(Me.PlayerID, teilzug);
        mov.nextMove(teilzug);
        write('Punkte nach Teilzug ' + IntToStr(n) + ': ');
        writeln(IntToStr(FBoard.getScoresForPlayer(Me.PlayerID)));
      end;
      writeln('');
      writeln('Mache ' + IntToStr(mov.NumMoveParts) + ' Teilzüge');
      write('Punkte danach: ');
      writeln(IntToStr(FBoard.getScoresForPlayer(Me.PlayerID)));
      Result := mov;
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
      // Zug berechnen lassen, je nachdem ob ein Auswahlzug oder
      // ein Bauzug gefordert ist
      mov := macheZufallszug;

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
