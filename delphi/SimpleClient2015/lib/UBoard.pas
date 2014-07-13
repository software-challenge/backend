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
        function getScoresForPlayer(playerId : Integer) : TScoreData;
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

  (*
   * Gibt den Pinguin, der auf einem bestimmten Feld steht, aus
   *)
  function TBoard.getPenguin(x : Integer; y : Integer) : TPenguin;
  begin
    Result := FFields[x][y].getPenguin();
  end;

  (*
   * Gibt aus, ob auf einem bestimmten Feld ein Pinguin steht
   *)
  function TBoard.hasPenguin(x : Integer; y : Integer) : Boolean;
  begin
    if (FFields[x][y].getPenguin() = nil)
		  then Result := false
		  else Result := true;
  end;

  (*
   * Gibt aus, ob auf einem bestimmten Feld ein Pinguin eines bestimmten Spielers steht
   *)
  function TBoard.hasPlayerPenguin(x : Integer; y : Integer; playerId : Integer) : Boolean;
  begin
    if (FFields[x][y].getPenguin() = nil)
			then Result := false
      else if (FFields[x][y].getPenguin().PlayerId = playerId)
			       then Result := true
             else Result := false;
  end;

  (*
   * Gibt die Anzahl der Fische, die auf einem bestimmten Feld liegen, aus
   *)
  function TBoard.getFishNumber(x : Integer; y : Integer) : Integer;
  begin
    Result := FFields[x][y].getFish();
  end;

  function TBoard.toString : String;
  var
    x, y : Integer;
    field : TField;
  begin
    Result := 'Current player: ' + IntToStr(FCurrentPlayer) + sLineBreak + sLineBreak
              + '  ' + char(186) + ' 0 1 2 3 4 5 6 7' + sLineBreak
              + char(205) + char(205) + char(206) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + char(205) + sLineBreak;
    for y := 0 to 7 do begin
      Result := Result + inttostr(y) + ' ' + char(186) + ' ';
      for x := 0 to 7 do begin
        field := FFields[x][y];
        Result := Result + field.toString() + ' ';
      end;
      Result := Result + sLineBreak;
    end;
  end;

  (*
   * Returns the scores for the player with the given player ID
   *)
  function TBoard.getScoresForPlayer(playerId : Integer) : TScoreData;
  var
    Player : TPlayer;
    ScoreData : TScoreData;
  begin
    Player := Self.getPlayer(playerId);
    ScoreData[POINTS_ID] := Player.Points;
    ScoreData[FIELDS_ID] := Player.Fields;
    Result := ScoreData;
  end;

  (*
   * Returns array with scores for both players
   *)
  function TBoard.getScores : TScores;
  var
    Scores : TScores;
  begin
    Scores[0] := Self.getScoresForPlayer(0);
    Scores[1] := Self.getScoresForPlayer(1);
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
    FreeAndNil(FLastMove);
    FLastMove := TMove.fromXml(xml);
  end;

  (*
   * Update the field with the given XML data
   *)
  procedure TBoard.updateBoard(xml : TDomNode);
  var
    XmlSubNode, XmlSubSubNode, XmlSub : TDomNode;
    n, m, x, y : Integer;
    Field : TField;
  begin
    XmlSub := xml.ChildNodes.Item(1);
    if XmlSub.NodeName = 'fields' then begin
      x := 0;
      for n := 0 to XmlSub.ChildNodes.Length - 1 do begin
        XmlSubNode := XmlSub.ChildNodes.Item(n);
        if XmlSubNode.NodeName = 'field-array' then begin
          y := 0;
          for m := 0 to XmlSubNode.ChildNodes.Length - 1 do begin
            XmlSubSubNode := XmlSubNode.ChildNodes.Item(m);
            if XmlSubSubNode.NodeName = 'field' then begin
              Field := TField.create(XmlSubSubNode);
              FreeAndNil(FFields[x][y]);
              FFields[x][y] := Field;
              y := y + 1;
            end;
          end;
          x := x + 1;
        end;
      end;
    end;
  end;

  destructor TBoard.destroy;
  begin
    FreeAndNil(FPlayers[0]);
    FreeAndNil(FPlayers[0]);
    FreeAndNil(FFields);
    FreeAndNil(FLastMove);
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
      FreeAndNil(FPlayers[0]);
      FreeAndNil(FPlayers[0]);
      FPlayers[0] := player1;
      FPlayers[1] := player2;
    end;

  constructor TBoard.create;
    begin
      inherited create;
      FPlayers[0] := TPlayer.Create;
      FPlayers[1] := TPlayer.Create;
    end;
end.
