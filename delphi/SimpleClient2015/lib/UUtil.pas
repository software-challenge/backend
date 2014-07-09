unit UUtil;

(*
 * Defines some utility functions
 *)

interface
  uses Classes, UDefines, TypInfo, Contnrs;

  function isIntInList(Int : Integer; List : TObjectList) : Boolean;
  function moveTypeFromString(str : String) : TMoveType;
  function moveTypeToString(MoveType : TMoveType) : String;

  procedure log(text : String);
  procedure setLogFile(var f : TextFile);

implementation
  uses UInteger;

  var LogFile : ^TextFile;

  procedure log(text : String);
  begin
    Writeln(LogFile^, text);
  end;

  procedure setLogFile(var f : TextFile);
  begin
    LogFile := @f;
  end;

  (*
   * Reads a string and returns the corresponding move type enum entry
   *)
  function moveTypeFromString(str : String) : TMoveType;
  begin
    Result := TMoveType(GetEnumValue(TypeInfo(TMoveType), str));
  end;

  (*
   * Returns the given move type enum entry to a string that can be send via XML
   *)
  function moveTypeToString(MoveType : TMoveType) : String;
  begin
    Result := GetEnumName(TypeInfo(TMoveType), integer(MoveType));
  end;

  (*
   * Returns true if the given integer is present in the given list of integers
   *)
  function isIntInList(Int : Integer; List : TObjectList) : Boolean;
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
