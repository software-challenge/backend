unit UField;

interface
  uses UDefines, UPirate, UUtil, Classes, AdomCore_4_3;

  type
    TField = class
      private
        FSymbol : TSymbol;
        FPirates : TList;
        FFieldIndex : Integer;
      public
        function numberOfPirates : Integer;
        function numberOfPiratesForPlayer(PlayerId : Integer) : Integer;
        function isOccupied : Boolean;
        function canBeFallenBackTo : Boolean;

        property Symbol : TSymbol read FSymbol write FSymbol;
        property Pirates : TList read FPirates write FPirates;
        property FieldIndex : Integer read FFieldIndex write FFieldIndex;
        function isStart : Boolean;
        function isFinish : Boolean;
        constructor create(FieldIndex : Integer; Symbol : TSymbol; Pirates : TList); overload;
        constructor create(FieldIndex : Integer; xml : TDomNode); overload;
        destructor destroy; override;
    end;

implementation

uses SysUtils, Math;

  function TField.numberOfPiratesForPlayer(PlayerId : Integer) : Integer;
  var
    n : Integer;
    count : Integer;
  begin
    count := 0;
    for n := 0 to FPirates.Count - 1 do begin
      if (TPirate(FPirates[n]).OwnerId = PlayerId) then count := count + 1;
    end;
    Result := count;
  end;

  function TField.numberOfPirates : Integer;
  begin
    Result := FPirates.Count;
  end;

  function TField.isOccupied : Boolean;
  begin
    Result := numberOfPirates > 0;
  end;

  function TField.canBeFallenBackTo : Boolean;
  var
    numPirates : Integer;
  begin
    numPirates := numberOfPirates;
    Result := (numPirates > 0) and (numPirates < 3);
  end;

  function TField.isStart : Boolean;
  begin
    Result := FSymbol = START;
  end;

  function TField.isFinish : Boolean;
  begin
    Result := FSymbol = FINISH;
  end;

  constructor TField.create(FieldIndex : Integer; xml : TDomNode);
  var
    FieldType : String;
    n, o : Integer;
    XmlSubNode : TDomNode;
    XmlPirateNode : TDomNode;
    Pirate : TPirate;
    playerId : Integer;
  begin
    FFieldIndex := FieldIndex;
    FieldType := xml.Attributes.getNamedItem('type').NodeValue;
    if (FieldType = 'START') or (FieldType = 'FINISH') then begin
      FSymbol := symbolFromString(FieldType);
    end
    else begin
      FSymbol := symbolFromString(xml.Attributes.getNamedItem('symbol').NodeValue);
    end;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if XmlSubNode.NodeName = 'pirates' then begin
        FPirates := TList.Create;
        for o := 0 to XmlSubNode.ChildNodes.Length - 1 do begin
          XmlPirateNode := XmlSubNode.ChildNodes.Item(o);
          if XmlPirateNode.NodeName = 'pirate' then begin
            if xmlPirateNode.Attributes.getNamedItem('owner').NodeValue = 'RED' then begin
              playerId := PLAYER_RED;
            end
            else begin
              playerId := PLAYER_BLUE;
            end;
            Pirate := TPirate.create(playerId, FFieldIndex);
            FPirates.Add(Pirate);
          end;
        end;
      end;
    end;
  end;

  constructor TField.create(FieldIndex : Integer; Symbol : TSymbol; Pirates : TList);
  begin
    FSymbol := Symbol;
    FFieldIndex := FieldIndex;
    FPirates := Pirates;
  end;

  destructor TField.destroy;
  begin
    inherited;
    FreeAndNil(FPirates);
  end;

end.
 