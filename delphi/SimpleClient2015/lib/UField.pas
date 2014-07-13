unit UField;

(*
 * Represents a single field of the board
 *)

interface
  uses UDefines, UUtil, UPenguin, Classes, AdomCore_4_3;

  type
    TField = class
      private
        FPenguin : TPenguin;      // The penguin placed on this field if any
        FFish    : Integer;       // Ammount of fish on the field
      public
        function hasPenguin : Boolean;
        function getPenguin : TPenguin;
        function getFish : Integer;

        function toString : String;
        function toXml(parent : TDomDocument) : TDomElement;

        constructor create; overload;
        constructor create(f : Integer; penguin : TPenguin); overload;
        constructor create(f : Integer); overload;
        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;

implementation

uses SysUtils, Math;

  function TField.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
    xmlPenguin : TDomElement;
  begin
    xmlElement := TDomElement.Create(parent, 'field');
    xmlElement.SetAttribute('fish', IntToStr(FFish));

    if FPenguin <> nil then begin
      xmlPenguin := TDomElement.Create(parent, 'penguin');
      if FPenguin.PlayerId = PLAYER_RED
        then xmlPenguin.SetAttribute('owner', 'red')
        else xmlPenguin.SetAttribute('owner', 'blue')
    end;

    Result := xmlElement;
  end;

  function TField.toString : String;
  begin
    Result := '';
    if FPenguin <> nil
      then Result := FPenguin.toString()
      else Result := inttostr(FFish);

  end;

  (*
   * Returns if this field is occupied by a penguin
   *)

  function TField.hasPenguin : Boolean;
  begin
    Result := FPenguin <> nil;
  end;

  (*
   * Returns this field's penguin
   *)
  function TField.getPenguin : TPenguin;
  begin
    Result := FPenguin;
  end;

  (*
   * Returns this field's amount of fish
   *)
  function TField.getFish : Integer;
  begin
    Result := FFish;
  end;

  constructor TField.create(xml : TDomNode);
  var
    n, o : Integer;
    XmlSubNode : TDomNode;
  begin
    FFish := StrToInt(xml.Attributes.getNamedItem('fish').NodeValue);
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if XmlSubNode.NodeName = 'penguin' then begin
        FPenguin := TPenguin.create(XmlSubNode);
      end;
    end;
  end;

  constructor TField.create;
  begin
    FFish := 0;
    FPenguin := nil;
  end;

  constructor TField.create(f : Integer; penguin : TPenguin);
  begin
    FFish := f;
    FPenguin := penguin;
  end;

  constructor TField.create(f : Integer);
  begin
    FFish := f;
    FPenguin := nil;
  end;

  destructor TField.destroy;
  begin
    FreeAndNil(FPenguin);
    inherited;
  end;

end.
