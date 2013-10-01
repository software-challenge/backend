unit UPlayer;

interface
  uses UDefines, UCard, UPirate, AdomCore_4_3, Classes;
  type
    TPlayer = class
      private
        FPlayerID : Integer;
        FDisplayName : String;
        FPoints : Integer;
        FCards : TList;
        FPirates : TList;
      public
        property PlayerID : Integer read FPlayerID write FPlayerID;
        property DisplayName : String read FDisplayName write FDisplayName;
        property Points : Integer read FPoints write FPoints;
        property Cards : TList read FCards write FCards;
        property Pirates : TList read FPirates write FPirates;

        procedure removeCardWithSymbol(Symbol : TSymbol);
        procedure removeCard(Card : TCard);
        function getPirate(PirateIndex : Integer) : TPirate;
        function getPirateOnField(FieldIndex : Integer) : TPirate;
        function getCardWithSymbol(Symbol : TSymbol) : TCard;
        function getCard(CardIndex : Integer) : TCard;
        function numPiratesOnField(Field : Integer) : Integer;
        function piratesOnField(Field : Integer) : TList;
        function addCard(Card : TCard) : Integer;
        function numCards : Integer;
        function numCardsWithSymbol(Symbol : TSymbol) : Integer;
        function hasCardWithSymbol(Symbol : TSymbol) : Boolean;
        function hasPirateOnField(Field : Integer) : Boolean;

        procedure fromXml(xml : TDomNode);
        procedure updatePirates(PiratesList : TList);
        constructor Create(DisplayName : String);
        destructor destroy; override;
    end;

implementation

uses SysUtils, TypInfo;

  procedure TPlayer.removeCardWithSymbol(Symbol : TSymbol);
  var
    Card : TCard;
  begin
    Card := getCardWithSymbol(Symbol);
    if not (Card = nil) then removeCard(Card);
  end;

  procedure TPlayer.removeCard(Card : TCard);
  begin
    FCards.Remove(Card);
  end;

  function TPlayer.getPirateOnField(FieldIndex : Integer) : TPirate;
  var
    n : Integer;
    Pirate : TPirate;
  begin
    for n := 0 to FPirates.Count do begin
      Pirate := TPirate(FPirates[n]);
      if (Pirate.Field = FieldIndex) then begin
        Result := Pirate;
        Exit;
      end;
    end;
    Result := nil;
  end;

  function TPlayer.getCardWithSymbol(Symbol : TSymbol) : TCard;
  var
    n : Integer;
    Card : TCard;
  begin
    for n := 0 to FCards.Count - 1 do begin
      Card := TCard(FCards[n]);
      if Card.Symbol = Symbol then begin
        Result := Card;
        Exit;
      end;
    end;
    Result := nil;
  end;

  function TPlayer.hasPirateOnField(Field : Integer) : Boolean;
  begin
    Result := numPiratesOnField(Field) > 0;
  end;

  function TPlayer.getPirate(PirateIndex : Integer) : TPirate;
  begin
    Result := TPirate(FPirates[PirateIndex]);
  end;

  function TPlayer.getCard(CardIndex : Integer) : TCard;
  begin
    Result := TCard(FCards[CardIndex]);
  end;

  procedure TPlayer.updatePirates(PiratesList : TList);
  begin
    if not (FPirates = nil) then FreeAndNil(FPirates);
    FPirates := PiratesList;
  end;

  function TPlayer.numPiratesOnField(Field : Integer) : Integer;
  var
    PiratesOnFieldList : TList;
  begin
    PiratesOnFieldList := piratesOnField(Field);
    Result := PiratesOnFieldList.Count;
    FreeAndNil(PiratesOnFieldList);
  end;

  function TPlayer.piratesOnField(Field : Integer) : TList;
  var
    PiratesOnFieldList : TList;
    n : Integer;
    Pirate : TPirate;
  begin
    PiratesOnFieldList := TList.Create;
    for n := 0 to FPirates.Count - 1 do begin
      Pirate := TPirate(FPirates[n]);
      if (Pirate.Field = Field) then PiratesOnFieldList.Add(Pirate);
    end;
    Result := PiratesOnFieldList;
  end;

  function TPlayer.addCard(Card : TCard) : Integer;
  begin
    if FCards.Count < MAX_PLAYER_CARDS then FCards.Add(Card);
    Result := FCards.Count;
  end;

  function TPlayer.numCards : Integer;
  begin
    Result := FCards.Count;
  end;

  function TPlayer.numCardsWithSymbol(Symbol : TSymbol) : Integer;
  var
    num : Integer;
    n : Integer;
  begin
    num := 0;
    for n := 0 to FCards.Count - 1 do begin
      if TCard(FCards[n]).Symbol = Symbol then num := num + 1;
    end;
    Result := num;
  end;

  function TPlayer.hasCardWithSymbol(Symbol : TSymbol) : Boolean;
  begin
    Result := numCardsWithSymbol(Symbol) > 0;
  end;

  procedure TPlayer.fromXml(xml : TDomNode);
  var
    n, o : Integer;
    XmlSubNode : TDomNode;
    XmlCard : TDomNode;
    Card : TCard;
  begin
    // Receive player data
    FDisplayName := xml.Attributes.getNamedItem('displayName').NodeValue;
    FPoints := StrToInt(xml.Attributes.getNamedItem('points').NodeValue);
    if xml.NodeName = 'red' then begin
      FPlayerID := PLAYER_RED;
    end
    else begin
      FPlayerID := PLAYER_BLUE;
    end;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if (XmlSubNode.NodeName = 'cards') then begin
        FreeAndNil(FCards);
        FCards := TList.Create;
        for o := 0 to XmlSubNode.ChildNodes.Length - 1 do begin
          XmlCard :=  XmlSubNode.ChildNodes.Item(o);
          if XmlCard.NodeName = 'card' then begin
            Card := TCard.create(XmlCard);
            FCards.Add(Card);
          end;
        end;
      end;
    end;
  end;

  constructor TPlayer.Create(DisplayName : String);
    begin
      inherited Create;
      FDisplayName := DisplayName;
    end;

  destructor TPlayer.destroy;
    begin
      inherited;
    end;
end.
