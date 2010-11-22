echo -------------------------------
echo "Comparing: $1 <====> $2"
a=`cat $1 | wc -l`
b=`cat $2 | wc -l`
echo "Lines of Code:"
echo "  $1 => $a"
echo "  $2 => $b"
c=`comm -1 -2 --nocheck-order $1 $2`
d=`comm -1 -2 --nocheck-order $1 $2 | wc -l`
echo "There are $d identical lines of code!"
echo "Identical ones:"
echo "$c"
echo "RESULT:"+$a+"|"+$d+"|"+$b
