#!/bin/bash
docFinal=./TERInstallManual

pdflatex $docFinal".tex"

rm -rf $docFinal".log" $docFinal".aux"

