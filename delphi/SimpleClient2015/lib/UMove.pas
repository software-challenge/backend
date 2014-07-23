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
  uses URunMove, USetMove, UNullMove;

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
  begin
    Result := TMove.Create;
    if xml.Attributes.getNamedItem('class').NodeValue = 'SetMove' then begin
      Result := TSetMove.create(xml);
    end else if xml.Attributes.getNamedItem('class').NodeValue = 'RunMove' then begin
      Result := TRunMove.create(xml);
    end else if xml.Attributes.getNamedItem('class').NodeValue = 'RunMove' then begin
      Result := TNullMove.create;
    end;
  end;

end.
