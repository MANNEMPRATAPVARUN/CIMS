#!/bin/bash
#
# Performs QA checks of an XML file
#
help=0
while [[ "$#" -gt 0 ]]; do case $1 in
    --help) help=1;;
    *) arr=( "${arr[@]}" "$1" );;
esac; shift; done

if [ ${#arr[@]} -ne 1 ] || [ $help -eq 1 ]; then
    echo "Usage: $0 [--help] <claml file>"
    exit 1
fi

file=${arr[0]}
cci=0
if [[ $file =~ "cci" ]]; then
	cci=1
fi
lang=en
if [[ $file =~ "fra" ]]; then
	lang=fr
fi

# Set directory of this script so we can call relative scripts
#DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

echo "--------------------------------------------------"
echo "Starting ...`/bin/date`"
echo "--------------------------------------------------"
echo "file=$file"
echo "cci=$cci"
echo ""

# All "xml:lang" attributes must be "fr" or "en"
echo "  Check xml:lang ...`/bin/date`"
ct=`grep "xml:lang" $file | perl -pe 's/.*xml:lang="//; s/".*//;' | sort -u | wc -l`
if [[ $ct -gt 1 ]]; then
    grep "xml:lang" $file | perl -pe 's/.*xml:lang="//; s/".*//;' | sort -u | sed 's/^/    /;'
    echo "ERROR: too many different values for xml:lang in file"
	error=1
fi

ct=`grep "xml:lang" $file | perl -pe 's/.*xml:lang="//; s/".*//;' | grep -v '^'"$lang"'$' | sort -u | wc -l`
if [[ $ct -gt 1 ]]; then
    grep "xml:lang" $file | perl -pe 's/.*xml:lang="//; s/".*//;' | grep -v '^'"$lang"'$'  | sort -u | sed 's/^/    /;'
    echo "ERROR: lang values other than '$lang'"
	error=1
fi

# Meta should have just a "name"="year" entry and not "contextId"
echo "  Check <Meta> tags ...`/bin/date`"
ct=`grep "^        <Meta" $file | perl -pe 's/.*name="//; s/".*//;' | egrep -v 'year|contextId' | sort -u | wc -l`
if [[ $ct -ne 0 ]]; then
    grep "^        <Meta" $file | perl -pe 's/.*name="//; s/".*//;' | egrep -v 'year|contextId' | sort -u | sed 's/^/    /' 
    echo "ERROR: unexpected <Meta> tags at beginning of the file"
	error=1
fi

# Class kinds should be lowercase and /[a-z-]*/
# Class kinds should all be used somewhere in the file
echo "  Check class kinds ...`/bin/date`"
ct=`grep '<ClassKind name="' $file | perl -pe 's/[^"]*"//; s/".*//;' | perl -ne 'print unless /^[a-z-]*$/' | wc -l`
if [[ $ct -ne 0 ]]; then
	grep '<ClassKind name="' $file | perl -pe 's/[^"]*"//; s/".*//;' | perl -ne 'print unless /^[a-z-]*$/' | sed 's/^/    /' 
    echo "ERROR: class kind not matching expected pattern /[a-z-]*/"
	error=1
fi
grep '<ClassKind name="' $file | perl -pe 's/[^"]*"//; s/".*//;' | sort -u -o /tmp/x.$$
grep '<Class ' $file | perl -pe 's/.*kind="//; s/".*//;'  | sort -u -o /tmp/y.$$
ct=`diff /tmp/x.$$ /tmp/y.$$ | wc -l`
if [[ $ct -ne 0 ]]; then
	diff /tmp/x.$$ /tmp/y.$$ | sed 's/^/    /' 
    echo "ERROR: mismatches between defined and used class kinds"
	error=1
fi

# Rubric kinds should be lowercase and /[a-z-]*/
# Rubric kinds should all be used somewhere in the file
echo "  Check rubric kinds ...`/bin/date`"
ct=`grep '<RubricKind name="' $file | perl -pe 's/[^"]*"//; s/".*//;' | perl -ne 'print unless /^[a-z-]*$/' | wc -l`
if [[ $ct -ne 0 ]]; then
	grep '<RubricKind name="' $file | perl -pe 's/[^"]*"//; s/".*//;' | perl -ne 'print unless /^[a-z-]*$/' | sed 's/^/    /' 
    echo "ERROR: rubric kind not matching expected pattern /[a-z-]*/"
	error=1
fi

grep '<RubricKind name="' $file | perl -pe 's/.*name="//; s/".*//;' | sort -u -o /tmp/x.$$
grep '<Rubric ' $file | perl -pe 's/.*kind="//; s/".*//;' | sort -u -o /tmp/y.$$
ct=`diff /tmp/x.$$ /tmp/y.$$ | wc -l`
if [[ $ct -ne 0 ]]; then
    diff /tmp/x.$$ /tmp/y.$$ | sed 's/^/    /;'
    echo "ERROR: mismatches between defined and used rubric kinds"
	error=1
fi

# All classes except for front-matter and back-matter should have codes
echo "  Check Class codes ...`/bin/date`"
ct=`grep '<Class ' $file | grep -v 'code="' | grep -v matter | sort -u | wc -l`
if [[ $ct -ne 0 ]]; then
    grep '<Class ' $file | grep -v 'code="' | grep -v matter | sort | uniq -c | sed 's/^/    /;'
    echo "ERROR: Classes without codes "
	error=1
fi

# Class codes should be unique across the file
# Class codes should exist for all classes except for "front-matter" and "back-matter"
ct=`grep '<Class ' $file | grep 'code="' | perl -pe 's/.*code="//; s/".*//;' | sort | uniq -d | wc -l`
if [[ $ct -ne 0 ]]; then
    grep '<Class ' $file | grep 'code="' | perl -pe 's/.*code="//; s/".*//;' | sort | uniq -d | sed 's/^/    /;'
    echo "ERROR: Duplicate codes"
	error=1
fi

ct=`grep '<Class ' $file | grep -v front-matter | grep -v back-matter | grep -v 'code="' | sort -u | wc -l`
if [[ $ct -ne 0 ]]; then
	grep '<Class ' $file | grep -v front-matter | grep -v back-matter | grep -v 'code="' | sort -u | sed 's/^/    /;'
    echo "ERROR: Classes other than front-matter and back-matter without codes"
	error=1
fi

# Embedded a href references should all be class codes
echo "  Check embedded references ...`/bin/date`"
grep '<Class ' $file | grep 'code="' | perl -pe 's/.*code="//; s/".*//;' | sort -u -o /tmp/x.$$
# extract all embedded reference from the line and keep just the codes
perl -ne 's/.*?(<a href=[^>]+>)/$1|/g; foreach $href (split/\|/) { next unless $href=~/href/; $href=~s/.*href="#(.*)".*/$1/; print "$href\n";   } ' $file | sort -u -o /tmp/y.$$
ct=`comm -13 /tmp/x.$$ /tmp/y.$$ | wc -l`
if [[ $ct -ne 0 ]]; then
	comm -13 /tmp/x.$$ /tmp/y.$$ | sed 's/^/    /;'
    echo "ERROR: embedded reference codes that are not class codes"
	echo "NOTE: things ending in . or - should show that way in the text, but those should be trimmed for the reference code"
	echo "NOTE: blank or just - references should probably not be turned into links at all, but just rendered as simple characters"
	error=1
fi

# also check that embedded references don't contain "null"
ct=`grep -c '>null<' $file`
if [[ $ct -ne 0 ]]; then
	grep '>null<' $file | perl -pe 's/ +//;' | sed 's/^/    /;'
    echo "ERROR: embedded references with 'null' values"
	error=1
fi

# Classes for chapter/block/category should all be linked with SubClass/SuperClass references
echo "  Check SuperClass/SubClass codes ...`/bin/date`"
grep '<Class ' $file | grep 'code="' | perl -pe 's/.*code="//; s/".*//;' | sort -u -o /tmp/x.$$
grep '<SuperClass ' $file | grep 'code="' | perl -pe 's/.*code="//; s/".*//;' | sort -u -o /tmp/y.$$
ct=`comm -13 /tmp/x.$$ /tmp/y.$$ | wc -l`
if [[ $ct -ne 0 ]]; then
	comm -13 /tmp/x.$$ /tmp/y.$$ | sed 's/^/    /;'
    echo "ERROR: SuperClass codes that are not class codes"
	error=1
fi

grep '<SubClass ' $file | grep 'code="' | perl -pe 's/.*code="//; s/".*//;' | sort -u -o /tmp/y.$$
ct=`comm -23 /tmp/x.$$ /tmp/y.$$ | wc -l`
ct2=`egrep '"chapter"|"book-index"|"attribute"' $file | grep '<Class ' | wc -l`
if [[ $ct -ne $ct2 ]]; then
	comm -23 /tmp/x.$$ /tmp/y.$$ | sed 's/^/    /;'
	comm -23 /tmp/x.$$ /tmp/y.$$ | wc -l
    echo "ERROR: Class codes that are not SubClass codes (chapter classes should be here only - ct = $ct2)"
	error=1
fi

# Class->SubClass should align with Class<-SuperClass
perl -ne '
  chop;
  if (/<Class / && /code"=/) {
      $inclass = 1;
	  s/.*code="//; s/".*//; $par = $_;
  }
  if ($inclass && /<SubClass/) {
	  s/.*code="//; s/".*//; $chd = $_;
	  print "$par|$chd\n";
  }
  if (/<\/Class/) {
      $inclass = 0; $par="";
  }' $file | sort -u -o /tmp/parchd-subclass.$$.txt

perl -ne '
  chop;
  if (/<Class / && /code"=/) {
      $inclass = 1;
	  s/.*code="//; s/".*//; $chd = $_;
  }
  if ($inclass && /<SuperClass/) {
	  s/.*code="//; s/".*//; $par = $_;
	  print "$par|$chd\n";
  }
  if (/<\/Class/) {
      $inclass = 0; $par="";
  }' $file | sort -u -o /tmp/parchd-superclass.$$.txt
ct=`diff /tmp/parchd-{sub,super}class.$$.txt | wc -l`
if [[ $ct -ne 0 ]]; then
    diff /tmp/parchd-{sub,super}class.$$.txt | sed 's/^/    /;'
    echo "ERROR: Class->Subclass does not match SuperClass->Class"
	error=1
fi

# Classes for book-index/letter-index/index-term should all be linked with SubClass/SuperClass references
for type in book-index letter-index; do
    perl -ne '
      chop;
      if (/<Class / && /code"=/ && /'"$type"'/) {
          $found = 0;
		  $inclass = 1;
	      s/.*code="//; s/".*//; $code = $_;
      }
      if (/<SubClass/) {
          $found = 1;
      }
      if ($inclass && /<\/Class/ && !$found) {
          print "$code\n";
      }
      if ($inclass && /<\/Class/) {
          $inclass = 0;
      }
	  ' $file | sort -u -o /tmp/x.$$
    ct=`cat /tmp/x.$$ | wc -l`
    if [[ $ct -ne 0 ]]; then
        cat /tmp/x.$$ | sed 's/^/    /;'
        echo "ERROR: $type without SubClass"
	    error=1
    fi
done

for type in letter-index index-term tabular-term; do
    perl -ne '
      chop;
      if (/<Class / && /code"=/ && /'"$type"'/) {
          $found = 0;
		  $inclass = 1;
	      s/.*code="//; s/".*//; $code = $_;
      }
      if (/<SuperClass/) {
          $found = 1;
      }
      if ($inclass && /<\/Class/ && !$found) {
          print "$code\n";
      }
      if ($inclass && /<\/Class/) {
          $inclass = 0;
      }
	  ' $file | sort -u -o /tmp/x.$$
    ct=`cat /tmp/x.$$ | wc -l`
    if [[ $ct -ne 0 ]]; then
        cat /tmp/x.$$ | head | sed 's/^/    /;'
        echo "ERROR: $type without SuperClass (first 10 shown)"
	    error=1
    fi
done

# Verify no local file paths in claml
ct=`grep -c $'file:/C:/' $file`
if [[ $ct -ne 0 ]]; then
    grep $'file:/C:/' $file | perl -pe 's/^ +//; s/(.{1,80}).*/$1.../;' | head |sed 's/^/    /;'
    echo "ERROR: local file paths in ClaML (only top 10 shown)"
	error=1
fi


if [[ $cci -eq 0 ]]; then

    #ICD10CA VALIDATION
    echo "  ICD10CA Validation ...`/bin/date`"

    echo "    Verify 23 chapters ...`/bin/date`"
    ct=`grep '<Class ' $file | grep '"chapter"' | wc -l`
    if [[ $ct -ne 23 ]]; then
	    grep '<Class ' $file | grep '"chapter"' | sed 's/^/    /;'
        echo "ERROR: expected exactly 23 chapters"
	    error=1
    fi

#* Class A06.4 should have a <Usage kind="+"/>
#* Class D63 should have a <Usage kind="*"/>
#* Class A06.5 has complicated rubrics
#* Class A06.8 has even more complicated rubrics
#* Class A56 has nested list rubric
#* Class A52.3 has a brace rubric

fi

if [[ $cci -eq 1 ]]; then

    #CCI VALIDATION
    echo "  CCI Validation ...`/bin/date`"

    echo "    Verify 7 chapters ...`/bin/date`"
    ct=`grep '<Class ' $file | grep '"chapter"' | wc -l`
    if [[ $ct -ne 7 ]]; then
	    grep '<Class ' $file | grep '"chapter"' | sed 's/^/    /;'
        echo "ERROR: expected exactly 7 chapters (Section 4 is missing)"
	    error=1
    fi

    #* UsageKinds should not be defined in the file as they are not used
    echo "    Verify UsageKinds not defined ...`/bin/date`"
    ct=`grep '<UsageKind ' $file | wc -l`
    if [[ $ct -ne 0 ]]; then
        grep '<UsageKind ' $file | sed 's/^/    /;'
        echo "ERROR: CCI with UsageKind"
	    error=1
    fi
fi

# Cleanup
echo "  Cleanup ...`/bin/date`"
/bin/rm -rf /tmp/[xyz].$$
/bin/rm -f /tmp/parchd-{sub,super}class.$$.txt

echo ""
if [[ $error -eq 1 ]]; then
    echo "Finished with errors..."
    echo ""
	exit 1
fi
echo "Finished successfully..."
echo ""

echo "--------------------------------------------------"
echo "Finished ...`/bin/date`"
echo "--------------------------------------------------"
