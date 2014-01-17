unit UExchangeMove;

(*
 * Move that exchanges one or more of the owned stones for new ones from
 * the open stash.
 *)

interface
  uses UMove, UStone, AdomCore_4_3, SysUtils, Classes, Contnrs;

  type
    TExchangeMove = class(TMove)
      private
        FStones : TObjectList;
      public
        procedure addStoneToExchange(stone : TStone);
        function toXml(parent : TDomDocument) : TDomElement; override;
        property stonesToExchange : TObjectList read FStones write FStones;
        constructor create; overload;
        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;
implementation

 (*
  * Add the given stone to the list of stones to exchange
  *)
  procedure TExchangeMove.addStoneToExchange(stone : TStone);
  begin
    FStones.Add(stone);
  end;

  function TExchangeMove.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
    n : Integer;
  begin
    xmlElement := inherited toXml(parent);
    xmlElement.SetAttribute('class', 'exchangemove');
    for n := 0 to FStones.count - 1 do begin
      xmlElement.AppendChild((TStone(FStones[n])).toXml(parent, 'select'));
    end;
    Result := xmlElement;
  end;

  constructor TExchangeMove.create;
  begin
    FStones := TObjectList.Create(false);
  end;

  constructor TExchangeMove.create(xml : TDomNode);
  begin
    FStones := TObjectList.Create(false);
  end;

  destructor TExchangeMove.destroy;
    begin
      FStones.Free;
      inherited;
    end;

end.

