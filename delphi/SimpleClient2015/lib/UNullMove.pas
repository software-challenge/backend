unit UNullMove;

(*
 * A move to do nothing
 *)

interface
  uses UMove, UPenguin, UField, UUtil, UDefines, AdomCore_4_3, SysUtils, Classes, Contnrs;

  type
    TNullMove = class(TMove)
      public
        function toXml(parent : TDomDocument) : TDomElement; override;
        function toString : String; override;
        constructor create;

        destructor destroy; override;
    end;

implementation


  function TNullMove.toString : String;
  begin
    Result := 'AUSSETZEN (NULL)' + sLineBreak;
  end;

  function TNullMove.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := inherited toXml(parent);
    xmlElement.SetAttribute('class', 'NullMove');
    Result := xmlElement;
  end;


  constructor TNullMove.create;
  begin

  end;

  destructor TNullMove.destroy;
    begin
 //     FStonesToFields.Free;
      inherited;
    end;

end.
