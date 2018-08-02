#!/bin/bash

latex schaefchen_rules.tex
dvips schaefchen_rules.dvi
ps2pdf -dSAFER -sPAPERSIZE=a4 schaefchen_rules.ps

latex schaefchen_server.tex
dvips schaefchen_server.dvi
ps2pdf -dSAFER -sPAPERSIZE=a4 schaefchen_server.ps
