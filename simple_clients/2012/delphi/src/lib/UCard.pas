unit UCard;

(*
Eine Karte für einen bestimmten Bauplatz
*)
interface
  type
    TCard = class
      private
        FSlot : Integer;
      public
        property Slot : Integer read FSlot write FSlot;

        constructor Create(slot : Integer);
    end;

implementation
  constructor TCard.Create(slot : Integer);
    begin
      inherited Create;
      FSlot := slot;
    end;
end.

