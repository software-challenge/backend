unit UDefines;

interface

(*
 * Static constants used in the program
 *)

const

  // Player IDs

  NO_PLAYER             = -1;
  PLAYER_RED            = 0;
  PLAYER_BLUE           = 1;

  // Score Data IDs

  POINTS_ID                = 0;
  FIELDS_ID                = 1;

type
  TIntArray = Array of Integer;
  TIntIntArray = Array of Array of Integer;

  TScoreData = Array[0..1] of Integer;

  TMoveType     = (SETMOVE, RUNMOVE);                                  // Possible move types
  TScores = array [0..1] of TScoreData;

  TStringTokenizer = class
  private
    Fstr: String;
    Fcur: Integer;
    Fend: Integer;
  public
    constructor Create(const s: String);
    function nextToken(): String;
  end;

implementation

uses UNetwork;

constructor TStringTokenizer.Create(const s: String);
begin
  inherited Create();
  Fstr := s;
  Fcur := 1;
  Fend := 1;
end;

function TStringTokenizer.nextToken(): String;
var
  i: Integer;
  ret: String;
begin
  ret := '';
  Fcur := Fend;
  for i := Fcur to Length( Fstr ) do
  begin
    if ( Fstr[i] = ' ' ) then
    begin
      ret := Copy( Fstr, Fcur, i - Fcur );
      Fend := i + 1;
      Break;
    end;
  end;

  if ( (i >= (Length( Fstr )-1)) and (ret = '') ) then
  begin
    ret := Copy( Fstr, Fcur, Length( Fstr ) - Fcur + 1 );
  end;

  result := ret;
end;

end.
