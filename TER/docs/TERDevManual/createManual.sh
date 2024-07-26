#!/bin/bash
docFinal=./TERDevManual

pdflatex $docFinal".tex"

rm -rf $docFinal".log" $docFinal".aux"

