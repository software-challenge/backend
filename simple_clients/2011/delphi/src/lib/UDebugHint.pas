unit UDebugHint;

interface

  type
    TDebugHint = class
      private
        FKey : String;
        FValue : String;
      public
        property Key : String read FKey write FKey;
        property Value : String read FValue write FValue;

        constructor create(Key : String; Value : String);
  end;

  implementation
    constructor TDebugHint.create(Key : String; Value : String);
    begin
      FKey := Key;
      FValue := Value;
    end;
  end.

