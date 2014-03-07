unit UUtil;

(*
 * Defines some utility functions
 *)

interface
  uses Classes, UDefines, TypInfo, Contnrs;

  function isIntInList(Int : Integer; List : TObjectList) : Boolean;
  function symbolFromString(str : String) : TStoneSymbol;
  function colorFromString(str : String) : TStoneColor;
  function moveTypeFromString(str : String) : TMoveType;
  function symbolToString(Symbol : TStoneSymbol) : String;
  function colorToString(Color : TStoneColor) : String;
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
   * Reads a string and returns the corresponding stone symbol enum entry
   *)
  function symbolFromString(str : String) : TStoneSymbol;
  begin
    Result := TStoneSymbol(GetEnumValue(TypeInfo(TStoneSymbol), str));
  end;

  (*
   * Reads a string and returns the corresponding stone color enum entry
   *)
  function colorFromString(str : String) : TStoneColor;
  begin
    Result := TStoneColor(GetEnumValue(TypeInfo(TStoneColor), str));
  end;

  (*
   * Reads a string and returns the corresponding move type enum entry
   *)
  function moveTypeFromString(str : String) : TMoveType;
  begin
    Result := TMoveType(GetEnumValue(TypeInfo(TMoveType), str));
  end;

  (*
   * Returns the given stone symbol enum entry to a string that can be send via XML
   *)
  function symbolToString(Symbol : TStoneSymbol) : String;
  begin
    Result := GetEnumName(TypeInfo(TStoneSymbol), integer(Symbol));
  end;

  (*
   * Returns the given color symbol enum entry to a string that can be send via XML
   *)
  function colorToString(Color : TStoneColor) : String;
  begin
    Result := GetEnumName(TypeInfo(TStoneColor), integer(Color));
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
