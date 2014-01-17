unit UMove;

interface
  uses UMovePart, UDebugHint, AdomCore_4_3, SysUtils, Classes;

  type
    TMove = class
      private
        FNumMoves : Integer;
        FMoveParts : array [0..2] of TMovePart;
      public
        property NumMoveParts : Integer read FNumMoves;
        function teilzuege : TList;
        function nextMove : TMovePart; overload;
        procedure nextMove(MovePart : TMovePart); overload;
        function movesLeft : Integer;
        function toXml(parent : TDomDocument) : TDomElement;
        constructor create; overload;
        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;
implementation

  function TMove.teilzuege : TList;
  var
    MovePartsList : TList;
    n : Integer;
  begin
    MovePartsList := TList.Create;
    for n := 0 to 2 do begin
      if not (FMoveParts[n] = nil) then MovePartsList.Add(FMoveParts[n]);
    end;
    Result := MovePartsList;
  end;

  procedure TMove.nextMove(MovePart : TMovePart);
  begin
    FMoveParts[FNumMoves] := MovePart;
    FNumMoves := FNumMOves + 1;
  end;

  function TMove.nextMove : TMovePart;
  var
    move : TMovePart;
  begin
    move := TMovePart.create(FNumMoves);
    FMoveParts[FNumMoves] := move;
    FNumMoves := FNumMoves + 1;
    Result := move;
  end;

  function TMove.movesLeft : Integer;
  begin
    Result := 3 - FNumMoves;
  end;

  function TMove.toXml(parent : TDomDocument) : TDomElement;
    var
      xmlElement : TDomElement;
      xmlMovePart : TDomElement;
      n : Integer;
    begin
      xmlElement := TDomElement.Create(parent, 'data');
      xmlElement.SetAttribute('class', 'moveContainer');
      for n := 0 to FNumMoves - 1 do begin
        xmlMovePart := TMovePart(FMoveParts[n]).toXml(parent);
        xmlElement.AppendChild(xmlMovePart);
      end;
      Result := xmlElement;
    end;

  constructor TMove.create(xml : TDomNode);
  var
    XmlMovePart : TDomNode;
    MovePart : TMovePart;
    n : Integer;
    numParts : Integer;
  begin
    numParts := 0;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlMovePart := xml.ChildNodes.Item(n);
      if (XmlMovePart.NodeName = 'firstMove') or (XmlMovePart.NodeName = 'secondMove') or (XmlMovePart.NodeName = 'thirdMove') then begin
        MovePart := TMovePart.create(XmlMovePart);
        FMoveParts[MovePart.MoveNumber] := MovePart;
        if MovePart.MoveNumber >= numParts then numParts := MovePart.MoveNumber + 1;
      end;
    end;
    FNumMoves := numParts;
  end;

  constructor TMove.create;
    begin
      inherited create;
      FNumMoves := 0;
    end;

  destructor TMove.destroy;
    begin
      FreeAndNil(FMoveParts);
      inherited;
    end;

end.
