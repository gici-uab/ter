#!/bin/bash
docFinal=./TERUserManual

pdflatex $docFinal".tex"

rm -rf $docFinal".log" $docFinal".aux"

