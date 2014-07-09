unit UPenguin;

(*
 * Represents a penguin
 *)

interface
  uses UUtil, UDefines, AdomCore_4_3, Classes, Contnrs;
  type
    TPenguin = class
      private
        FPlayerId : Integer;       // Color of this penguin
      public
        property PlayerId : Integer read FPlayerId write FPlayerId;

        function toString : String;
        function toXml(parent : TDomDocument; tagName : String) : TDomElement; overload;
        function toXml(parent : TDomDocument) : TDomElement; overload;

        constructor Create(player : Integer); overload;
        constructor Create(xml : TDomNode); overload;
    end;

implementation

uses SysUtils;

  function TPenguin.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := TDomElement.Create(parent, 'penguin');
    if FPlayerId = PLAYER_RED
      then xmlElement.SetAttribute('owner', 'red')
      else xmlElement.SetAttribute('owner', 'blue');

    Result := xmlElement;
  end;

  function TPenguin.toXml(parent : TDomDocument; tagName : String) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := TDomElement.Create(parent, tagName);
    if FPlayerId = PLAYER_RED
      then xmlElement.SetAttribute('owner', 'red')
      else xmlElement.SetAttribute('owner', 'blue');

    Result := xmlElement;
  end;

  function TPenguin.toString : String;
  begin
    Result := 'Color (';
    if FPlayerId = PLAYER_RED
      then Result := Result + 'red)'
      else Result := Result + 'blue)';
  end;

  constructor TPenguin.Create(Player : Integer);
  begin
    FPlayerId := Player;
  end;

  constructor TPenguin.Create(xml : TDomNode);
  begin
    if xml.Attributes.getNamedItem('color').NodeValue = 'red'
      then FPlayerId := PLAYER_RED
      else FPlayerID := PLAYER_BLUE;
  end;

end.
 