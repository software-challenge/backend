unit UStone;

(*
 * Represents a stone
 *)

interface
  uses UUtil, UDefines, AdomCore_4_3, Classes, Contnrs;
  type
    TStone = class
      private
        FSymbol : TStoneSymbol;     // Symbol on this stone
        FColor : TStoneColor;       // Color of this stone
        FID : Integer;              // Identifier of this stone
      public
        property Symbol : TStoneSymbol read FSymbol write FSymbol;
        property Color : TStoneColor read FColor write FColor;
        property ID : Integer read FID write FID;

        function canBeInSameRowWith(stones : TObjectList) : Boolean; overload;
        function canBeInSameRowWith(stone : TStone) : Boolean; overload;
        function toString : String;
        function toXml(parent : TDomDocument; tagName : String) : TDomElement; overload;
        function toXml(parent : TDomDocument) : TDomElement; overload;

        constructor Create(Symbol : TStoneSymbol; Color : TStoneColor; ID : Integer); overload;
        constructor Create(xml : TDomNode); overload;
    end;

implementation

uses SysUtils;

  function TStone.toXml(parent : TDomDocument) : TDomElement;
  begin
    Result := toXml(parent, 'stone');
  end;

  function TStone.toXml(parent : TDomDocument; tagName : String) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := TDomElement.Create(parent, tagName);
    xmlElement.SetAttribute('color', colorToString(FColor));
    xmlElement.SetAttribute('shape', symbolToString(FSymbol));
    xmlElement.SetAttribute('identifier', IntToStr(FID));
    Result := xmlElement;
  end;

  (*
   * Returns true if this stone can be placed in the same
   * connected row with the given stone
   *)
  function TStone.canBeInSameRowWith(stone : TStone) : Boolean;
  var
    stones : TObjectList;
  begin
    stones := TObjectList.Create(false);
    stones.Add(stone);
    Result := canBeInSameRowWith(stones);
    stones.Clear;
    FreeAndNil(stones);
  end;

  (*
   * Returns true if this stone can be placed in the same
   * connected row with the given set of stones
   *
   * To be placed in the same connected row, all stones must be
   * of same color and different symbols or of same symbols with
   * different symbols.
   *)
  function TStone.canBeInSameRowWith(stones : TObjectList) : Boolean;
  var
    allSameColors, allSameSymbols : Boolean;
    allDifferentColors, allDifferentSymbols : Boolean;
    usedColors, usedSymbols : Integer;
    n : Integer;
    stone : TStone;
  begin
    allDifferentColors := True;
    allDifferentSymbols := True;
    allSameColors := True;
    allSameSymbols := True;
    usedColors := 1 shl integer(FColor);
    usedSymbols := 1 shl integer(FSymbol);
    // Check every stone and update flags
    for n := 0 to stones.Count - 1 do begin
      stone := TStone(stones[n]);
      if allDifferentColors then begin
        if (usedColors and (1 shl integer(stone.Color)) > 0) then allDifferentColors := False;      // Color already encountered
        usedColors := usedColors or (1 shl integer(stone.Color));                                   // Update color map
      end;
      if allDifferentSymbols then begin
        if (usedSymbols and (1 shl integer(stone.Symbol)) > 0) then allDifferentSymbols := False;   // Symbol already encountered
        usedSymbols := usedSymbols or (1 shl integer(stone.Symbol));                                // Update symbol map
      end;
      if FColor <> stone.Color then allSameColors := False;                                         // Stone of different color encountered
      if FSymbol <> stone.Symbol then allSameSymbols := False;                                      // Stone with different symbol encountered
    end;
    Result := ((allSameColors and allDifferentSymbols) or (allSameSymbols and allDifferentColors));
  end;

  function TStone.toString : String;
  begin
    Result := 'Symbol (' + symbolToString(FSymbol) + '), Color (' + colorToString(FColor) + '), ID (' + IntToStr(FID) + ')';
  end;

  constructor TStone.Create(Symbol : TStoneSymbol; Color : TStoneColor; ID : Integer);
  begin
    FSymbol := Symbol;
    FColor := Color;
    FID := ID;
  end;

  constructor TStone.Create(xml : TDomNode);
  begin
    FColor := colorFromString(xml.Attributes.getNamedItem('color').NodeValue);
    FSymbol := symbolFromString(xml.Attributes.getNamedItem('shape').NodeValue);
    FID := StrToInt(xml.Attributes.getNamedItem('identifier').NodeValue);
  end;

end.
 