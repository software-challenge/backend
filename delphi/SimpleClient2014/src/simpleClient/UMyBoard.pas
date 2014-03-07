unit UMyBoard;

// Hier können eigene Funktionen implementiert werden, um die Funktionalität
// des Boards zu erweitern

interface
  uses UBoard, UDefines, UInteger, UPlayer, UMove, UStone, UField, Classes, UUtil, SysUtils;

  type
    TMyBoard = class(TBoard)
     public

    end;
implementation

uses Math;


end.
