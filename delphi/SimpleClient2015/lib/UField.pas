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
    Result := 'Field';
    if FPenguin <> nil then begin
      Result := Result + ' with Penguin ' + FPenguin.toString();
    end;
  end;

  (*
   * Returns true if this field is occupied (has a stone placed on it)
   *)

  function TField.hasPenguin : Boolean;
  begin
    Result := FPenguin <> nil;
  end;

  function TField.getPenguin : TPenguin;
  begin
    Result := FPenguin;
  end;

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
    inherited;
//    FreeAndNil(FStone);
  end;

end.
