unit UMove;

interface
  uses UDebugHint, AdomCore_4_3, SysUtils, Classes;

  type
    TMove = class
      private
        FSheep : Integer;
        FTarget : Integer;
        FHints : TList;
        FHintCount : Integer;
      public
        procedure moveTo(Sheep : Integer; Field : Integer);

        property Sheep : Integer read FSheep write FSheep;
        property Target : Integer read FTarget write FTarget;
        property Hints : TList read FHints write FHints;
        function toXml(parent : TDomDocument) : TDomElement;
        procedure addHint(Hint : TDebugHint);
        constructor create;
        destructor destroy; override;
    end;
implementation
  function TMove.toXml(parent : TDomDocument) : TDomElement;
    var
      xmlElement : TDomElement;
      xmlHint : TDomElement;
      n : Integer;
    begin
      xmlElement := TDomElement.Create(parent, 'data');
      xmlElement.SetAttribute('class', 'sit:move');
      xmlElement.SetAttribute('sheep', IntToStr(FSheep));
      xmlElement.SetAttribute('target', IntToStr(FTarget));
      for n := 0 to FHints.Count - 1 do begin
        xmlHint := TDomElement.Create(parent, 'hint');
        xmlHint.SetAttribute('content', TDebugHint(FHints[n]).Content);
        xmlElement.AppendChild(xmlHint);
      end;
      result := xmlElement;
    end;

  procedure TMove.moveTo(Sheep : Integer; Field : Integer);
    begin
      FSheep := Sheep;
      FTarget := Field;
    end;

  constructor TMove.create;
    begin
      inherited create;
      FHints := TList.Create;
      FHintCount := 0;
    end;

  destructor TMove.destroy;
    begin
      FreeAndNil(FHints);
      inherited;
    end;

  procedure TMove.addHint(Hint : TDebugHint);
    begin
      FHints.Add(Hint);
      FHintCount := FHintCount + 1;
    end;

end.
