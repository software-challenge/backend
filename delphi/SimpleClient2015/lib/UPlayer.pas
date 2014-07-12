unit UPlayer;

(*
 * Represents a player
 *)

interface
  uses UDefines, AdomCore_4_3, Classes;
  type
    TPlayer = class
      private
        FPlayerID : Integer;      // Player's ID (0 or 1)
        FDisplayName : String;    // Player's name
        FPoints : Integer;        // Player's current score
        FFields : Integer;        // Player's number of collected pieces
      public
        property PlayerID : Integer read FPlayerID write FPlayerID;
        property DisplayName : String read FDisplayName write FDisplayName;
        property Points : Integer read FPoints write FPoints;
        property Fields : Integer read FFields write FFields;

        procedure fromXml(xml : TDomNode);
        constructor Create(DisplayName : String); overload;
        constructor Create(xml : TDomNode); overload;
        destructor destroy; override;
    end;

implementation

uses SysUtils, TypInfo;

  procedure TPlayer.fromXml(xml : TDomNode);
  var
    n, o : Integer;
    XmlSubNode : TDomNode;
  begin
    // Receive player data
    FDisplayName := xml.Attributes.getNamedItem('displayName').NodeValue;
    FPoints := StrToInt(xml.Attributes.getNamedItem('points').NodeValue);
    FFields := StrToInt(xml.Attributes.getNamedItem('fields').NodeValue);
    if xml.NodeName = 'RED' then begin
      FPlayerID := PLAYER_RED;
    end
    else begin
      FPlayerID := PLAYER_BLUE;
    end;
  end;

  constructor TPlayer.Create(DisplayName : String);
    begin
      inherited Create;
      FDisplayName := DisplayName;
      FPoints := 0;
      FFields := 0;
    end;

  constructor TPlayer.Create(xml : TDomNode);
  var
    n, o : Integer;
    XmlSubNode : TDomNode;
  begin
    inherited Create;
    // Receive player data
    FDisplayName := xml.Attributes.getNamedItem('displayName').NodeValue;
    FPoints := StrToInt(xml.Attributes.getNamedItem('points').NodeValue);
    FFields := StrToInt(xml.Attributes.getNamedItem('fields').NodeValue);
    if xml.NodeName = 'RED' then begin
      FPlayerID := PLAYER_RED;
    end
    else begin
      FPlayerID := PLAYER_BLUE;
    end;
  end;

  destructor TPlayer.destroy;
    begin
      inherited;
    end;
end.
