unit UBoard;

(*
 * Representation of the board
 *)

interface
  uses AdomCore_4_3, UMove, USetMove, UPlayer, UDefines, UField, UPenguin, SysUtils, Classes, Contnrs;

  type
    TFieldColumn = Array[0..7] of TField;
    TFields = Array[0..7] of TFieldColumn;
    TBoard = class
      protected
        FPlayers : array [0..1] of TPlayer;   // Participating players
        FCurrentPlayer : Integer;             // Current player number (0 or 1)
        FFields : TFields;                    // Field matrix (8 by 8)
        FLastMove : TMove;                    // Last performed move
        FIsRunMove : Boolean;                  // Is the current move a RunMove?
      public
        function getScores : TScores;
        function getScoresForPlayer(playerId : Integer) : Integer;
        function getPlayer(PlayerID : Integer) : TPlayer;
        function getField(FieldX : Integer; FieldY : Integer) : TField;

        function getPenguin(x : Integer; y : Integer) : TPenguin;
        function hasPenguin(x : Integer; y : Integer) : Boolean;
        function hasPlayerPenguin(x : Integer; y : Integer; playerId : Integer) : Boolean;
        function getFishNumber(x : Integer; y : Integer) : Integer;

        function toString : String;


        procedure updatePlayers(player1 : TPlayer; player2 : TPlayer); dynamic;
        procedure updateBoard(xml : TDomNode); dynamic;
        procedure updateLastMove(xml : TDomNode); dynamic;

        property CurrentPlayer : Integer read FCurrentPlayer write FCurrentPlayer;
        property LastMove : TMove read FLastMove;
        property Fields : TFields read FFields write FFields;
        property IsRunMove : Boolean read FIsRunMove write FIsRunMove;

        constructor create;
        destructor destroy; override;
    end;


implementation
  uses Math;

  function TBoard.getPenguin(x : Integer; y : Integer) : TPenguin;
  begin
    Result := FFields[x][y].getPenguin();
  end;

  function TBoard.hasPenguin(x : Integer; y : Integer) : Boolean;
  begin
    if (FFields[x][y].getPenguin() = nil)
		  then Result := false
		  else Result := true;
  end;

  function TBoard.hasPlayerPenguin(x : Integer; y : Integer; playerId : Integer) : Boolean;
  begin
    if (FFields[x][y].getPenguin() = nil)
			then Result := false
      else if (FFields[x][y].getPenguin().PlayerId = playerId)
			       then Result := true
             else Result := false;
  end;

  function TBoard.getFishNumber(x : Integer; y : Integer) : Integer;
  begin
    Result := FFields[x][y].getFish();
  end;

  function TBoard.toString : String;
  var
    n, o : Integer;
    field : TField;
  begin
    Result := 'Current player: ' + IntToStr(FCurrentPlayer) + sLineBreak;
    for n := 0 to 7 do begin
      for o := 0 to 7 do begin
        field := FFields[n][o];
        if field.getPenguin() <> nil then begin
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
    XmlSubNode, XmlSubSubNode : TDomNode;
    n, m : Integer;
    Field : TField;
  begin
  //if FLayedStones <> nil then FreeAndNil(FLayedStones);
  //  FLayedStones := TObjectList.Create;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if XmlSubNode.NodeName = 'field-array' then begin
        for m := 0 to XmlSubNode.ChildNodes.Length - 1 do begin
          XmlSubSubNode := XmlSubNode.ChildNodes.Item(m);
          Field := TField.create(XmlSubSubNode);
          FFields[n][m] := Field;
        end;
      end;
    end;
  end;

  destructor TBoard.destroy;
  begin
  //  FreeAndNil(FNextStones);
  //  FreeAndNil(FLayedStones);
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
