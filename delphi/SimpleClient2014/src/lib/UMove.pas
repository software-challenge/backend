unit UMove;

(*
 * A move to be send back to the server
 *)

interface
  uses UDebugHint, UUtil, UDefines, AdomCore_4_3, SysUtils, Classes, Contnrs;

  type
    TMove = class
      private
        FHints : TObjectList;
      public
        procedure addHint(hint : TDebugHint);
        function toXml(parent : TDomDocument) : TDomElement; dynamic;
        function toString : String; dynamic;
        class function fromXml(xml : TDomNode) : TMove;
    end;
implementation
  uses ULayMove, UExchangeMove;

  procedure TMove.addHint(hint : TDebugHint);
  begin
    if (FHints = nil) then FHints := TObjectList.Create;
    FHints.Add(hint);
  end;

  function TMove.toXml(parent : TDomDocument) : TDomElement;
    var
      xmlElement : TDomElement;
      xmlHint : TDomElement;
      n : Integer;
    begin
      xmlElement := TDomElement.Create(parent, 'data');
      if FHints <> nil then begin
        for n := 0 to FHints.Count - 1 do begin
          xmlHint := TDomElement.Create(parent, 'hint');
          xmlHint.SetAttribute('content', TDebugHint(FHints[n]).Content);
          xmlElement.AppendChild(xmlHint);
        end;
      end;
      Result := xmlElement;
    end;

  function TMove.toString : String;
  begin
    Result := 'Unknown move';
  end;

  class function TMove.fromXml(xml : TDomNode) : TMove;
  var
    moveType : TMoveType;
  begin
    moveType := moveTypeFromString(xml.Attributes.getNamedItem('type').NodeValue);
    if moveType = LAY then begin
      Result := TLayMove.create(xml);
    end else if moveType = EXCHANGE then begin
      Result := TExchangeMove.create(xml);
    end;
  end;
    
end.
