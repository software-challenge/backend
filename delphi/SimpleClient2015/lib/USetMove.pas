unit USetMove;

(*
 * Move to place Penguin on the board
 *)

interface
  uses UMove, UPenguin, UField, UUtil, UDefines, AdomCore_4_3, SysUtils, Classes, Contnrs;

  type
    TSetMove = class(TMove)
      private
        FSetX : Integer;
        FSetY : Integer;
      public
        property SetX : Integer read FSetX write FSetX;
        property SetY : Integer read FSetY write FSetY;
        function toXml(parent : TDomDocument) : TDomElement; override;
        function toString : String; override;
        constructor create; overload;
        constructor create(setX : Integer; setY : Integer); overload;

        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;

implementation


  function TSetMove.toString : String;
  begin
    Result := 'SET (' + inttostr(FSetX) + ', ' + inttostr(FSetY) + ')' + sLineBreak;
  end;

  function TSetMove.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := inherited toXml(parent);
    xmlElement.SetAttribute('class', 'SetMove');
    xmlElement.SetAttribute('setX', inttostr(FSetX));
    xmlElement.SetAttribute('setY', inttostr(FSetY));
    Result := xmlElement;
  end;


  constructor TSetMove.create;
  begin

  end;

  constructor TSetMove.create(setX : Integer; setY : Integer);
  begin
    FSetX := setX;
    FSetY := setY;
  end;

  constructor TSetMove.create(xml : TDomNode);
  begin
    FSetX := StrToInt(xml.Attributes.getNamedItem('setX').NodeValue);
    FSetY := StrToInt(xml.Attributes.getNamedItem('setY').NodeValue);
  end;

  destructor TSetMove.destroy;
    begin
 //     FStonesToFields.Free;
      inherited;
    end;

end.
