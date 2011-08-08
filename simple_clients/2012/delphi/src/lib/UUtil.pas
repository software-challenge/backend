unit UUtil;

interface
  uses Classes;
  function isIntInList(Int : Integer; List : TList) : Boolean;
implementation
  uses UInteger;
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
 