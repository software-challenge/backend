unit UBoard;

(*
 * Representation of the board
 *)

interface
  uses AdomCore_4_3, UMove, ULayMove, UStoneToField, UPlayer, UDefines, UField, UStone, SysUtils, Classes, Contnrs;

  type
    TFieldColumn = Array[0..15] of TField;
    TFields = Array[0..15] of TFieldColumn;
    TBoard = class
      protected
        FPlayers : array [0..1] of TPlayer;   // Participating players
        FCurrentPlayer : Integer;             // Current player number (0 or 1)
        FNextStones : TObjectList;            // Stones in open stash
        FFields : TFields;                    // Field matrix (16 by 16)
        FLastMove : TMove;                    // Last performed move
        FLayedStones : TObjectList;           // Stones about to be placed on the board
      public
        function getScores : TScores;
        function getScoresForPlayer(playerId : Integer) : Integer;
        function getPlayer(PlayerID : Integer) : TPlayer;
        function getField(FieldX : Integer; FieldY : Integer) : TField;
        function getStoneWithID(ID : Integer) : TStone;
        function toString : String;

        function isStoneValidAt(stone : TStone; x : Integer; y : Integer) : Boolean;
        function isOccupied(x : Integer; y : Integer) : Boolean;
        function layStoneAt(stone : TStone; x : Integer; y : Integer) : Boolean;
        function createLayMove : TLayMove;

        procedure updatePlayers(player1 : TPlayer; player2 : TPlayer); dynamic;
        procedure updateNextStones(xml : TDomNode); dynamic;
        procedure updateBoard(xml : TDomNode); dynamic;
        procedure updateLastMove(xml : TDomNode); dynamic;

        property CurrentPlayer : Integer read FCurrentPlayer write FCurrentPlayer;
        property NextStones : TObjectList read FNextStones write FNextStones;
        property LastMove : TMove read FLastMove;
        property Fields : TFields read FFields write FFields;

        constructor create;
        destructor destroy; override;
    end;


implementation
  uses Math;

  (*
   * Returns the stone with the given ID from the open stash
   *)
  function TBoard.getStoneWithID(ID : Integer) : TStone;
  var
    stone : TStone;
    n : Integer;
  begin
    for n := 0 to FNextStones.Count - 1 do begin
      stone := TStone(FNextStones[n]);
      if stone.ID = ID then begin
        Result := stone;
        Break;
      end;
    end;
  end;

  (*
   * Creates and returns a LAY move of the stones placed on the board in this turn.
   * Removes the placed stones from the board in the process.
   *)
  function TBoard.createLayMove : TLayMove;
  var
    move : TLayMove;
  begin
    if (FLayedStones = nil) or (FLayedStones.Count = 0) then begin
      Result := nil;
      exit;
    end;
    move := TLayMove.create;
    move.setStonesToAdd(FLayedStones);
    FLayedStones := nil;
    Result := move;
  end;

  (*
   * Place the given stone on the given field of the board.
   * The stone will be included in the LAY move returned by createLayMove.
   *
   * Returns true if the stone was successfully placed.
   * Returns false otherwise (i.e. the location was invalid).
   *)
  function TBoard.layStoneAt(stone : TStone; x : Integer; y : Integer) : Boolean;
  begin
    if isStoneValidAt(stone, x, y) then begin
      FFields[x][y].Stone := stone;
      if FLayedStones = nil then FLayedStones := TObjectList.Create;
      FLayedStones.Add(TStoneToField.create(stone, getField(x, y)));
      Result := True;
    end else begin
      Result := False;
    end;
  end;

  (*
   * Checks if the stone can be put at position x, y given that the current board is valid
   *)
  function TBoard.isStoneValidAt(stone : TStone; x : Integer; y : Integer) : Boolean;
  var
    n : Integer;
    stoneToField : TStoneToField;
    stones : TObjectList;
  begin
    Result := False;

    if (x < 0) or (x > 15) or (y < 0) or (y > 15) then exit;

    // Check if occupied
    if isOccupied(x, y) then exit;

    // Check if in same row with previously layed stones in this turn
    stoneToField := TStoneToField.create(stone, TField.create(x, y));
    if (FLayedStones <> nil) and (not stoneToField.isInRowWith(FLayedStones)) then begin
      stoneToField.Free;
      exit;
    end;

    // For each direction, check if stone fits into
    // Check horizontal directions first
    stones := TObjectList.create(false);
    n := 1;
    while isOccupied(x - n, y) do begin
      stones.Add(FFields[x - n][y].Stone);
      n := n + 1;
    end;
    n := 1;
    while isOccupied(x + n, y) do begin
      stones.Add(FFields[x + n][y].Stone);
      n := n + 1;
    end;
    if not stone.canBeInSameRowWith(stones) then begin
      stones.Free;
      stoneToField.Free;
      exit;
    end;

    // Now check vertical directions
    stones.Clear;
    n := 1;
    while isOccupied(x, y - n) do begin
      stones.Add(FFields[x][y - n].Stone);
      n := n + 1;
    end;
    n := 1;
    while isOccupied(x, y + n) do begin
      stones.Add(FFields[x][y + n].Stone);
      n := n + 1;
    end;
    if not stone.canBeInSameRowWith(stones) then begin
      stones.Free;
      stoneToField.Free;
      exit;
    end;

    stones.Free;
    stoneToField.Free;
    Result := True;
  end;

  (*
   * Returns true if the field x, y is already occupied, false otherwise.
   *)
  function TBoard.isOccupied(x : Integer; y : Integer) : Boolean;
  begin
    Result := (x >= 0) and (x < 16) and (y >= 0) and (y < 16) and (FFields[x][y].isOccupied());
  end;

  function TBoard.toString : String;
  var
    n, o : Integer;
    field : TField;
  begin
    Result := 'Current player: ' + IntToStr(FCurrentPlayer) + sLineBreak;
    Result := Result + 'Next stones:' + sLineBreak;
    for n := 0 to FNextStones.Count - 1 do begin
      Result := Result + (TStone(FNextStones[n])).toString() + sLineBreak;
    end;
    Result := Result + sLineBreak + 'Occupied fields:' + sLineBreak;
    for n := 0 to 15 do begin
      for o := 0 to 15 do begin
        field := FFields[n][o];
        if field.Stone <> nil then begin
          if Result <> '' then begin
            Result := Result + sLineBreak;
          end;
          Result := Result + field.toString();
        end;
      end;
    end;
  end;

  (*
   * Returns the scores for the player with the given player ID
   *)
  function TBoard.getScoresForPlayer(playerId : Integer) : Integer;
  var
    Player : TPlayer;
    n : Integer;
    scores : Integer;
  begin
    Player := getPlayer(playerId);
    scores := 0;
    
    Result := scores;
  end;

  (*
   * Returns array with scores for both players
   *)
  function TBoard.getScores : TScores;
  var
    Scores : TScores;
  begin
    Scores[0] := getScoresForPlayer(0);
    Scores[1] := getScoresForPlayer(1);
    Result := Scores;
  end;

  (*
   * Returns the field at position FieldX, FieldY
   *)
  function TBoard.getField(FieldX : Integer; FieldY : Integer) : TField;
  begin
    Result := FFields[FieldX][FieldY];
  end;

  (*
   * Set the last move to the one defined by the given XML data
   *)
  procedure TBoard.updateLastMove(xml : TDomNode);
  begin
    FLastMove := TMove.fromXml(xml);
  end;

  (*
   * Update the field and stones with the given XML data
   *)
  procedure TBoard.updateBoard(xml : TDomNode);
  var
    XmlSubNode : TDomNode;
    n : Integer;
    Field : TField;
  begin
    if FLayedStones <> nil then FreeAndNil(FLayedStones);
    FLayedStones := TObjectList.Create;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if XmlSubNode.NodeName = 'field' then begin
        Field := TField.create(XmlSubNode);
        FFields[Field.FieldX][Field.FieldY] := Field;
      end;
    end;
  end;

  (*
   * Update the open stash with the given XML data
   *)
  procedure TBoard.updateNextStones(xml : TDomNode);
  var
    n : Integer;
    XmlStoneNode : TDomNode;
    Stone : TStone;
  begin
    if not (FNextStones = nil) then FreeAndNil(FNextStones);
    FNextStones := TObjectList.create;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlStoneNode := xml.ChildNodes.Item(n);
      if XmlStoneNode.NodeName = 'qw:stone' then begin
        Stone := TStone.create(XmlStoneNode);
        FNextStones.Add(Stone);
      end;
    end;
  end;

  destructor TBoard.destroy;
  begin
    FreeAndNil(FNextStones);
    FreeAndNil(FLayedStones);
    inherited;
  end;

  (*
   * Returns the player with the given player ID
   *)
  function TBoard.getPlayer(PlayerID : Integer) : TPlayer;
    var
      n : Integer;
    begin
      for n := 0 to Length(FPlayers) - 1 do begin
        if(FPlayers[n].PlayerID = PlayerID) then begin
          Result := FPlayers[n];
          exit;
        end;
      end;
      Result := nil;
    end;

  (*
   * Replaces the two player objects with the given ones
   *)
  procedure TBoard.updatePlayers(player1 : TPlayer; player2 : TPlayer);
    begin
      FPlayers[0] := player1;
      FPlayers[1] := player2;
    end;

  constructor TBoard.create;
    var
      i : Integer;
    begin
      inherited create;
      FPlayers[0] := nil;
      FPlayers[1] := nil;
    end;
end.
