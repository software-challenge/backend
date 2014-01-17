unit UUtil;

interface
  uses Classes, UDefines, TypInfo;
  function isIntInList(Int : Integer; List : TList) : Boolean;
  function symbolFromString(str : String) : TSymbol;
  function symbolToString(Symbol : TSymbol) : String;
implementation
  uses UInteger;

  function symbolFromString(str : String) : TSymbol;
  begin
    Result := TSymbol(GetEnumValue(TypeInfo(TSymbol), str));
  end;

  function symbolToString(Symbol : TSymbol) : String;
  begin
    Result := GetEnumName(TypeInfo(TSymbol), integer(Symbol));
  end;

  function isIntInList(Int : Integer; List : TList) : Boolean;
    var
      n : Integer;
    begin
      Result := false;
      for n := 0 to List.Count - 1 do begin
        if(TInteger(List[n]).Value = Int) then begin
          Result := true;
          exit;
        end;
      end;
    end;
end.
 