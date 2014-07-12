unit URunMove;

(*
 * Move that exchanges one or more of the owned stones for new ones from
 * the open stash.
 *)

interface
  uses UMove, AdomCore_4_3, SysUtils, Classes, Contnrs;

  type
    TRunMove = class(TMove)
      private
        FFromX : Integer;
        FFromY : Integer;
        FToX : Integer;
        FToY : Integer;
      public
        function toString : String; override;
        function toXml(parent : TDomDocument) : TDomElement; override;
        property FromX : Integer read FFromX write FFromX;
        property FromY : Integer read FFromY write FFromY;
        property ToX : Integer read FToX write FToX;
        property ToY : Integer read FToY write FToY;
        constructor create; overload;
        constructor create(fromX : Integer; fromY : Integer; toX : Integer; toY : Integer); overload;
        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;
implementation

  function TRunMove.toString : String;
  begin
    Result := 'RUN (' + inttostr(FFromX) + ', ' + inttostr(FFromY) + ') TO (' + inttostr(FToX) + ', ' + inttostr(FToY) + ')' + sLineBreak;
  end;

  function TRunMove.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := inherited toXml(parent);
    xmlElement.SetAttribute('class', 'RunMove');

    xmlElement.SetAttribute('fromX', inttostr(FFromX));
    xmlElement.SetAttribute('fromY', inttostr(FFromY));
    xmlElement.SetAttribute('toX', inttostr(FToX));
    xmlElement.SetAttribute('toY', inttostr(FToY));
    Result := xmlElement;
  end;

  constructor TRunMove.create;
  begin
    FFromX := -1;
    FFromY := -1;
    FToX := -1;
    FToY := -1;
  end;

  constructor TRunMove.create(fromX : Integer; fromY : Integer; toX : Integer; toY : Integer);
  begin
    FFromX := fromX;
    FFromY := fromY;
    FToX := toX;
    FToY := toY;
  end;

  constructor TRunMove.create(xml : TDomNode);
  begin
    FFromX := StrToInt(xml.Attributes.getNamedItem('fromX').NodeValue);
    FFromY := StrToInt(xml.Attributes.getNamedItem('fromY').NodeValue);
    FToX := StrToInt(xml.Attributes.getNamedItem('toX').NodeValue);
    FToY := StrToInt(xml.Attributes.getNamedItem('toY').NodeValue);
  end;

  destructor TRunMove.destroy;
    begin
 //     FStones.Free;
      inherited;
    end;

end.

