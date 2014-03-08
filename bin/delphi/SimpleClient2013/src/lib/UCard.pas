unit UCard;

interface
  uses AdomCore_4_3, UUtil, UDefines;
  type
    TCard = class
      private
        FSymbol : TSymbol;
      public
        property Symbol : TSymbol read FSymbol write FSymbol;
        function symbolName : String;
        constructor create(Symbol : TSymbol); overload;
        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;

implementation

uses TypInfo;

  function TCard.symbolName : String;
  begin
    Result := GetEnumName(TypeInfo(TSymbol), integer(FSymbol));
  end;

  constructor TCard.create(xml : TDomNode);
  begin
    FSymbol := symbolFromString(xml.Attributes.getNamedItem('symbol').NodeValue);
  end;

  constructor TCard.create(Symbol : TSymbol);
    begin
      inherited create;
      FSymbol := Symbol;
    end;

  destructor TCard.destroy;
    begin
      inherited;
    end;

end.
 