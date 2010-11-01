unit UDebugHint;

interface

  type
    TDebugHint = class
      private
        FContent : String;
      public
        property Content : String read FContent write FContent;

        constructor create(Content : String);
  end;

  implementation
    constructor TDebugHint.create(Content : String);
    begin
      FContent := Content;
    end;
  end.

