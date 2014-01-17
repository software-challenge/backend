unit UBoard;

interface
  uses AdomCore_4_3, UMove, UMovePart, UPlayer, UPirate, UDefines, UCard, UField, SysUtils, Classes;

  type
    TBoard = class
      private
        FLastMove : TMove;
      protected
        FPlayers : array [0..1] of TPlayer;
        FCurrentPlayer : Integer;
        FOpenCards : TList;
        FFields : TList;
      public
        function getScores : TScores;
        function getScoresForPlayer(playerId : Integer) : Integer;
        procedure playerDrawCards(playerId : Integer; numCards : Integer);
        function numberOfCardsDrawnByMove(playerId : Integer; MovePart : TMovePart) : Integer;
        procedure applyMovePart(playerId : Integer; MovePart : TMovePart);
        procedure movePirate(playerId : Integer; FromFieldIndex : Integer; ToFieldIndex : Integer);
        function getPlayer(PlayerID : Integer) : TPlayer;
        function getField(FieldIndex : Integer) : TField;
        function findNextFieldIndexWithSymbol(StartIndex : Integer; Symbol : TSymbol) : Integer;
        function findNextValidForwardFieldIndexWithSymbol(StartIndex : Integer; Symbol : TSymbol) : Integer;
        function findFieldToFallbackTo(StartIndex : Integer) : Integer;
        procedure updatePlayers(player1 : TPlayer; player2 : TPlayer);
        procedure updatePlayersWithPirates;
        procedure updateOpenCards(xml : TDomNode);
        procedure updateBoard(xml : TDomNode);
        procedure updateLastMove(xml : TDomNode);

        property CurrentPlayer : Integer read FCurrentPlayer write FCurrentPlayer;
        property OpenCards : TList read FOpenCards write FOpenCards;
        property LastMove : TMove read FLastMove;
        property Fields : TList read FFields write FFields;

        constructor create;
        destructor destroy; override;
    end;


implementation
  uses Math;

  function TBoard.getScoresForPlayer(playerId : Integer) : Integer;
  var
    Player : TPlayer;
    Pirate : TPirate;
    n : Integer;
    scores : Integer;
  begin
    Player := getPlayer(playerId);
    scores := 0;
    for n := 0 to Player.Pirates.Count - 1 do begin
      Pirate := TPirate(Player.Pirates[n]);
      scores := scores + ((Pirate.Field + 5) div 6);
    end;
    Result := scores;
  end;

  function TBoard.getScores : TScores;
  var
    Scores : TScores;
  begin
    Scores[0] := getScoresForPlayer(0);
    Scores[1] := getScoresForPlayer(1);
    Result := Scores;
  end;

  (*
  This DOES NOT check if the draw move is actually valid!
  *)
  procedure TBoard.playerDrawCards(playerId : Integer; numCards : Integer);
  var
    n : Integer;
    Card : TCard;
    Player : TPlayer;
  begin
    Player := getPlayer(playerId);
    for n := numCards downto 1 do begin
      Card := OpenCards.First;
      OpenCards.Remove(Card);
      Player.Cards.Add(Card);
    end;
  end;

  function TBoard.numberOfCardsDrawnByMove(playerId : Integer; MovePart : TMovePart) : Integer;
  var
    Field : TField;
    numPirates : Integer;
    Player : TPlayer;
  begin
    if MovePart.ForwardMove then begin
      // Ein Vorwärtszug bringt niemals Karten ein
      Result := 0;
    end
    else begin
      Field := getField(findFieldToFallbackTo(MovePart.Field - 1));
      Player := getPlayer(playerId);
      numPirates := Field.numberOfPirates;
      if numPirates > 2 then Result := 0
      else Result := numPirates;
      // Es kann höchstens bis auf einen Vorrat von 8 Karten aufgefüllt werden.
      Result := Min(8 - Player.numCards, Result);
    end;
  end;

  function TBoard.findFieldToFallbackTo(StartIndex : Integer) : Integer;
  var
    n : Integer;
  begin
    for n := StartIndex downto 0 do begin
      if getField(n).canBeFallenBackTo then begin
        Result := n;
        Exit;
      end;
    end;
    Result := -1;
  end;

  function TBoard.findNextValidForwardFieldIndexWithSymbol(StartIndex : Integer; Symbol : TSymbol) : Integer;
  var
    n : Integer;
    field : TField;
  begin
    for n := StartIndex to 31 do begin
      field := getField(n);
      if ((field.Symbol = Symbol) and (not field.isOccupied)) or (field.isFinish) then begin
        Result := n;
        Exit;
      end;
    end;
    Result := -1;
  end;

  function TBoard.findNextFieldIndexWithSymbol(StartIndex : Integer; Symbol : TSymbol) : Integer;
  var
    n : Integer;
  begin
    for n := StartIndex to 31 do begin
      if getField(n).Symbol = Symbol then begin
        Result := n;
        Exit;
      end;
    end;
    Result := -1;
  end;

  (*
  This DOES NOT check if the move is actually valid!
  *)
  procedure TBoard.movePirate(playerId : Integer; FromFieldIndex : Integer; ToFieldIndex : Integer);
  var
    FromField : TField;
    ToField : TField;
    Player : TPlayer;
    Pirate : TPirate;
  begin
    FromField := getField(FromFieldIndex);
    ToField := getField(ToFieldIndex);
    Player := getPlayer(playerId);
    Pirate := Player.getPirateOnField(FromFieldIndex);
    if Pirate = nil then Exit;
    Pirate.Field := ToFieldIndex;
    FromField.Pirates.Remove(Pirate);
    ToField.Pirates.Add(Pirate);
  end;

  (*
  This DOES NOT check if the move is actually valid!
  *)
  procedure TBoard.applyMovePart(playerId : Integer; MovePart : TMovePart);
  var
    Symbol : TSymbol;
    Pirate : TPirate;
    Field : TField;
    Player : TPlayer;
    TargetFieldIndex : Integer;
    CardsDrawn : Integer;
  begin
    if MovePart.ForwardMove then begin
      TargetFieldIndex := findNextValidForwardFieldIndexWithSymbol(MovePart.Field + 1, MovePart.Symbol);
      if TargetFieldIndex < 0 then Exit;
      movePirate(playerId, MovePart.Field, TargetFieldIndex);
      Field := getField(MovePart.Field);
      Player := getPlayer(playerId);
      Player.removeCardWithSymbol(MovePart.Symbol);
    end
    else begin
      TargetFieldIndex := findFieldToFallbackTo(MovePart.Field - 1);
      if TargetFieldIndex < 0 then Exit;
      CardsDrawn := numberOfCardsDrawnByMove(playerId, MovePart);
      movePirate(playerId, movePart.Field, TargetFieldIndex);
      playerDrawCards(playerId, CardsDrawn);
    end;
  end;

  function TBoard.getField(FieldIndex : Integer) : TField;
  begin
    Result := TField(FFields[FieldIndex]);
  end;

  procedure TBoard.updateLastMove(xml : TDomNode);
  begin
    FLastMove := TMove.create(xml);
  end;

  procedure TBoard.updatePlayersWithPirates;
  var
    n, o : Integer;
    Pirate : TPirate;
    Pirates : array [0..1] of TList;
    Field : TField;
  begin
    Pirates[0] := TList.Create;
    Pirates[1] := TList.Create;
    for n := 0 to FFields.Count - 1 do begin
      Field := TField(FFields[n]);
      for o := 0 to Field.Pirates.Count - 1 do begin
        Pirate := TPirate(Field.Pirates[o]);
        Pirates[Pirate.OwnerId].Add(Pirate);
      end;
    end;
    FPlayers[0].updatePirates(Pirates[0]);
    FPlayers[1].updatePirates(Pirates[1]);
  end;

  procedure TBoard.updateBoard(xml : TDomNode);
  var
    XmlSubNode : TDomNode;
    n, o, fieldIndex : Integer;
    XmlFieldNode : TDomNode;
    Field : TField;
  begin
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if XmlSubNode.NodeName = 'fields' then begin
        if not (FFields = nil) then FreeAndNil(FFields);
        FFields := TList.Create;
        fieldIndex := 0;
        for o := 0 to XmlSubNode.ChildNodes.Length - 1 do begin
          XmlFieldNode := XmlSubNode.ChildNodes.Item(o);
          if XmlFieldNode.NodeName = 'field' then begin
            Field := TField.create(fieldIndex, XmlFieldNode);
            fieldIndex := fieldIndex + 1;
            FFields.Add(Field);
          end;
        end;
      end;
    end;
  end;

  procedure TBoard.updateOpenCards(xml : TDomNode);
  var
    n : Integer;
    XmlCardNode : TDomNode;
    Card : TCard;
  begin
    if not (FOpenCards = nil) then FreeAndNil(FOpenCards);
    FOpenCards := TList.Create;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlCardNode := xml.ChildNodes.Item(n);
      if XmlCardNode.NodeName = 'card' then begin
        Card := TCard.create(XmlCardNode);
        FOpenCards.Add(Card);
      end;
    end;
  end;

  destructor TBoard.destroy;
  begin
    inherited;
  end;

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

  procedure TBoard.updatePlayers(player1 : TPlayer; player2 : TPlayer);
    begin
      FPlayers[0] := player1;
      FPlayers[1] := player2;
      updatePlayersWithPirates;
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
