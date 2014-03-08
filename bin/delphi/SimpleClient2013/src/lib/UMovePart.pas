unit UMovePart;

interface
  uses UPirate, TypInfo, UCard, UDebugHint, AdomCore_4_3, SysUtils, Classes, UUtil, UDefines;

  type
    TMovePart = class
      private
        FHints : TList;
        FHintCount : Integer;
        FSymbol : TSymbol;
        FField : Integer;
        FForward : Boolean;
        FMoveNumber : Integer;

        function moveName : String;
        function moveClass : String;
      public
        property Symbol : TSymbol read FSymbol write FSymbol;
        property Field : Integer read FField write FField;
        property Hints : TList read FHints write FHints;
        property MoveNumber : Integer read FMoveNumber;
        property ForwardMove : Boolean read FForward write FForward;

        procedure moveBackward(Pirate : TPirate); overload;
        procedure moveBackward(FieldNum : Integer); overload;
        procedure moveForward(Pirate : TPirate; Card : TCard); overload;
        procedure moveForward(FieldNum : Integer; Card : TCard); overload;
        procedure moveForward(FieldNum : Integer; Symbol : TSymbol); overload;
        function toXml(parent : TDomDocument) : TDomElement;
        procedure addHint(Hint : TDebugHint);
        constructor create(xml : TDomNode); overload;
        constructor create(MoveNumber : Integer); overload;
        constructor create(MoveNumber : Integer; Symbol : TSymbol; Field : Integer); overload;
        constructor create(MoveNumber : Integer; Field : Integer); overload;
        destructor destroy; override;
    end;
implementation

  procedure TMovePart.moveBackward(FieldNum : Integer);
  begin
    FField := FieldNum;
    FForward := False;
  end;

  procedure TMovePart.moveBackward(Pirate : TPirate);
  begin
    FField := Pirate.Field;
    FForward := False;
  end;

  procedure TMovePart.moveForward(FieldNum : Integer; Symbol : TSymbol);
  begin
    FSymbol := Symbol;
    FField := FieldNum;
    FForward := True;
  end;

  procedure TMovePart.moveForward(FieldNum : Integer; Card : TCard);
  begin
    FSymbol := Card.Symbol;
    FField := FieldNum;
    FForward := True;
  end;

  procedure TMovePart.moveForward(Pirate : TPirate; Card : TCard);
  begin
    FSymbol := Card.Symbol;
    FField := Pirate.Field;
    FForward := True;
  end;

  function TMovePart.toXml(parent : TDomDocument) : TDomElement;
  var
    name : String;
    moveElement : TDomElement;
    xmlHint : TDomElement;
    n : Integer;
  begin
    moveElement := TDomElement.Create(parent, moveName());
    moveElement.SetAttribute('class', moveClass());
    if FForward then begin
      moveElement.SetAttribute('fieldIndex', IntToStr(FField));
      moveElement.SetAttribute('symbol', symbolToString(FSymbol));
    end
    else begin
      moveElement.SetAttribute('fieldIndex', IntToStr(FField));
    end;
    for n := 0 to FHints.Count - 1 do begin
      xmlHint := TDomElement.Create(parent, 'hint');
      xmlHint.SetAttribute('content', TDebugHint(FHints[n]).Content);
      moveElement.AppendChild(xmlHint);
    end;
    Result := moveElement;
  end;

  procedure TMovePart.addHint(Hint : TDebugHint);
  begin
    FHints.Add(Hint);
    FHintCount := FHintCount + 1;
  end;

  constructor TMovePart.create(MoveNumber : Integer; Field : Integer);
  begin
    FMoveNumber := MoveNumber;
    FField := Field;
    FForward := False;
    FHints := TList.Create;
  end;

  constructor TMovePart.create(MoveNumber : Integer; Symbol : TSymbol; Field : Integer);
  begin
    FMoveNumber := MoveNumber;
    FSymbol := Symbol;
    FField := Field;
    FForward := True;
    FHints := TList.Create;
  end;

  constructor TMovePart.create(xml : TDomNode);
  begin
    if xml.NodeName = 'firstMove' then FMoveNumber := 0
    else if xml.NodeName = 'secondMove' then FMoveNumber := 1
    else FMoveNumber := 2;
    FForward := xml.Attributes.GetNamedItem('class').NodeValue = 'forwardMove';
    FField := StrToInt(xml.Attributes.GetNamedItem('fieldIndex').NodeValue);
    if FForward then begin
      FSymbol := symbolFromString(xml.Attributes.getNamedItem('symbol').NodeValue);
    end;
    FHints := TList.Create;
  end;

  constructor TMovePart.create(MoveNumber : Integer);
  begin
    FForward := false;
    FMoveNumber := MoveNumber;
    FHints := TList.Create;
  end;

  destructor TMovePart.destroy;
  begin
    inherited;
    FreeAndNil(FHints);
  end;

  function TMovePart.moveName : String;
  begin
    case FMoveNumber of
      0: Result := 'firstMove';
      1: Result := 'secondMove';
      2: Result := 'thirdMove';
    end;
  end;

  function TMovePart.moveClass : String;
  begin
    if FForward then begin
      Result := 'forwardMove';
    end
    else begin
      Result := 'backwardMove';
    end;
  end;

end.
 