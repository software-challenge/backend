unit UPirate;

interface
  uses UCard, AdomCore_4_3;

  type
    TPirate = class
      private
        FField : Integer;
        FOwnerId : Integer;

      public
        property Field : Integer read FField write FField;
        property OwnerId : Integer read FOwnerId write FOwnerId;

        constructor create(OwnerId : Integer); overload;
        constructor create(OwnerId : Integer; Field : Integer); overload;
        destructor destroy; override;
    end;

implementation
  uses UBoard;

  constructor TPirate.create(OwnerId : Integer);
  begin
    FOwnerId := OwnerId;
  end;

  constructor TPirate.create(OwnerId : Integer; Field : Integer);
  begin
    FOwnerId := OwnerId;
    FField := Field;
  end;

  destructor TPirate.destroy;
  begin
    inherited;
  end;
end.
 