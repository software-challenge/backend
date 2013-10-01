unit UMyBoard;

interface
  uses UBoard, UDefines, UInteger, UPlayer, UPirate, UMove, UCard, UMovePart, UField, Classes, UUtil, SysUtils;

  type
    TMyBoard = class(TBoard)
     public
        function findAllValidMoves(playerId : Integer; moveNumber : Integer) : TList;
        function canPlayerReachFieldForward(playerId : Integer; FieldIndex : Integer) : Boolean;
        function canPlayerReachFieldBackward(playerId : Integer; FieldIndex : Integer) : Boolean;
        function isMoveValid(teilzug : TMovePart; playerId : Integer) : Boolean;
    end;
implementation

uses Math;

  function TMyBoard.findAllValidMoves(playerId : Integer; moveNumber : Integer) : TList;
  var
    MoveList : TList;
    Player : TPlayer;
    Pirate : TPirate;
    n, m : Integer;
    MovePart : TMovePart;
    Card : TCard;
  begin
    Player := getPlayer(playerId);
    MoveList := TList.Create;
    for n := 0 to Player.Pirates.Count - 1 do begin
      Pirate := Player.getPirate(n);
      // Alle Vorwärtszüge
      for m := 0 to Player.Cards.Count - 1 do begin
        Card := TCard(Player.Cards[m]);
        MovePart := TMovePart.create(moveNumber, Card.Symbol, Pirate.Field);
        if isMoveValid(MovePart, playerId) then MoveList.Add(MovePart)
        else FreeAndNil(MovePart);
      end;
      // Rückwärtszug
      MovePart := TMovePart.create(moveNumber, Pirate.Field);
      if isMoveValid(MovePart, playerId) then MoveList.Add(MovePart)
      else FreeAndNil(MovePart);
    end;
    Result := MoveList;
  end;

  function TMyBoard.canPlayerReachFieldBackward(playerId : Integer; FieldIndex: Integer) : Boolean;
  var
    Field : TField;
    OtherField : TField;
    Player : TPlayer;
    n : Integer;
  begin
    Result := False;
    Field := getField(FieldIndex);
    Player := getPlayer(playerId);
    // Handelt es sich nicht um das Start- oder Zielfeld?
    if (Field.isStart) or (Field.isFinish) then Exit;
    // Auf dem nächsten besetzten folgenden Feld muss eine Spielfigur
    // dieses Spielers stehen.
    for n := FieldIndex + 1 to 31 do begin
      OtherField := getField(n);
      if (OtherField.isOccupied) then begin
        if Player.hasPirateOnField(n) then begin
          Result := True;
          Exit;
        end
        else Exit;
      end;
    end;
    // Wenn wir hier angekommen sind, hat der Spieler keine Figuren hinter
    // dem zu prüfenden Feld. Gib dann False zurück.
  end;

  function TMyBoard.canPlayerReachFieldForward(playerId : Integer; FieldIndex : Integer) : Boolean;
  var
    Field : TField;
    OtherField : TField;
    Player : TPlayer;
    n : Integer;
  begin
    Result := False;
    Field := getField(FieldIndex);
    Player := getPlayer(playerId);
    // Wenn das Feld besetzt ist, ist es nicht erreichbar
    if Field.isOccupied then Exit;
    // Wenn das Feld das Startfeld ist, ist es nicht erreichbar
    if Field.isStart then Exit;
    // Prüfe, ob der Spieler eine Karte mit diesem Symbol besitzt
    if not Player.hasCardWithSymbol(Field.Symbol) then Exit;
    // Prüfe die vorangehenden Felder. Zwischen dem nächsten (vorangehenden) Feld
    // mit demselben Symbol muss eine Spielfigur des Spielers stehen
    for n := FieldIndex - 1 downto 0 do begin
      OtherField := getField(n);
      if Player.hasPirateOnField(n) then begin
        Result := True;
        Exit;
      end;
      if (OtherField.Symbol = Field.Symbol) and not OtherField.isOccupied then Exit;
    end;
    // Wenn wir hier angekommen sind, hat der Spieler keine Spielfiguren vor
    // dem zu erreichenden Feld, gib also False zurück.
  end;

  function TMyBoard.isMoveValid(teilzug : TMovePart; playerId : Integer) : Boolean;
  var
    Player : TPlayer;
    n : Integer;
    Field : TField;
    canFallBack : Boolean;
  begin
    Player := getPlayer(playerId);
    Result := False;
    if teilzug.ForwardMove then begin
      // Prüfe, ob eine Karte mit dem Symbol vorhanden ist
      if not Player.hasCardWithSymbol(teilzug.Symbol) then Exit;
      // Prüfe, ob der Spieler eine Figur auf dem Zugfeld hat
      if not Player.hasPirateOnField(teilzug.Field) then Exit;
      // Prüfe, ob die entsprechende Figur nicht auf dem Zielfeld steht
      if getField(teilzug.Field).Symbol = FINISH then Exit;
    end
    else begin
      // Prüfe, ob die Figur nicht auf dem Startfeld steht
      if getField(teilzug.Field).Symbol = START then Exit;
      // Prüfe, ob es ein Feld gibt, auf das zurückgegangen werden kann
      canFallBack := False;
      for n := teilzug.Field - 1 downto 1 do begin
        // Prüfe, ob auf dieses Feld zurückgefallen werden kann
        Field := getField(n);
        if Field.canBeFallenBackTo then begin
          canFallBack := True;
        end;
      end;
      if not canFallBack then Exit;
    end;
    Result := True;
  end;

end.
