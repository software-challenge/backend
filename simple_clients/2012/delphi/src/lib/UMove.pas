unit UMove;

interface
  uses UDebugHint, AdomCore_4_3, SysUtils, Classes;

  type
    TSelection = array [1..4] of Integer;
    TMove = class
      private
        FHints : TList;
        FHintCount : Integer;
        FCity : Integer;
        FSlot : Integer;
        FSize : Integer;
        FSelection : TSelection;
        FBuildMove : Boolean;
      public
        procedure build(city : Integer; slot : Integer; size : Integer);
        procedure select(selections : TSelection); overload;
        procedure select(size1 : Integer; size2 : Integer; size3 : Integer; size4 : Integer); overload;

        property Hints : TList read FHints write FHints;
        property BuildMove : Boolean read FBuildMove write FBuildMove;
        property City : Integer read FCity write FCity;
        property Slot : Integer read FSlot write FSlot;
        property Size : Integer read FSize write FSize;
        property Selection : TSelection read FSelection write FSelection;

        function toXml(parent : TDomDocument) : TDomElement;
        function isBuildMove : Boolean;
        procedure addHint(Hint : TDebugHint);
        constructor create;
        destructor destroy; override;
    end;
implementation
  function TMove.toXml(parent : TDomDocument) : TDomElement;
    var
      xmlElement : TDomElement;
      xmlSelect : TDomElement;
      xmlHint : TDomElement;
      n : Integer;
    begin
      xmlElement := TDomElement.Create(parent, 'data');
      if(FBuildMove = True) then begin
        xmlElement.SetAttribute('class', 'manhattan:build');
        xmlElement.SetAttribute('city', IntToStr(FCity));
        xmlElement.SetAttribute('slot', IntToStr(FSlot));
        xmlElement.SetAttribute('size', IntToStr(FSize));
      end
      else begin
        xmlElement.SetAttribute('class', 'manhattan:select');
        for n := 1 to 4 do begin
          xmlSelect := TDomElement.Create(parent, 'select');
          xmlSelect.SetAttribute('size', IntToStr(n));
          xmlSelect.SetAttribute('amount', IntToStr(FSelection[n]));
          xmlElement.AppendChild(xmlSelect);
        end;
      end;

      for n := 0 to FHints.Count - 1 do begin
        xmlHint := TDomElement.Create(parent, 'hint');
        xmlHint.SetAttribute('content', TDebugHint(FHints[n]).Content);
        xmlElement.AppendChild(xmlHint);
      end;
      result := xmlElement;
    end;

  function TMove.isBuildMove : Boolean;
    begin
      Result := FBuildMove;
    end;

  procedure TMove.build(city : Integer; slot : Integer; size : Integer);
    begin
      FCity := city;
      FSlot := slot;
      FSize := size;
      FBuildMove := True;
    end;

  procedure TMove.select(selections : TSelection);
    begin
      FSelection[1] := selections[1];
      FSelection[2] := selections[2];
      FSelection[3] := selections[3];
      FSelection[4] := selections[4];
      FBuildMove := False;
    end;

  procedure TMove.select(size1 : Integer; size2 : Integer; size3 : Integer; size4 : Integer);
    begin
      FSelection[1] := size1;
      FSelection[2] := size2;
      FSelection[3] := size3;
      FSelection[4] := size4;
      FBuildMove := False;
    end;

  constructor TMove.create;
    begin
      inherited create;
      FHints := TList.Create;
      FHintCount := 0;
      //FBuildMove := False;
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
