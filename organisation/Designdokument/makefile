all: clean heft

heft:
	@pdflatex pfHeft.tex -interaction=scrollmode
	# for glossary; see makeindex --help for parameters
	@makeindex -s pfHeft.ist -t pfHeft.glg -o pfHeft.gls pfHeft.glo # glossar erzeugen
	@makeindex -s pfHeft.ist -t pfHeft.alg -o pfHeft.acr pfHeft.acn # acronyme erzeugen
	@pdflatex pfHeft.tex -interaction=scrollmode
	@pdflatex pfHeft.tex -interaction=scrollmode
	
clean:
	@del *.ist *.glo *.acn