CC=gcc -g
CFLAGS=-c -Wall -Wextra
LDFLAGS=-lm
BINNAME=gestionGraphe
LIBNAME=lib
SRCDIR=./src
OBJDIR=./obj
BINDIR=./bin
LIBDIR=./$(LIBNAME)
SRCLIBDIR=$(SRCDIR)/$(LIBNAME)
OBJLIBDIR=$(OBJDIR)/$(LIBNAME)
SRC=$(wildcard $(SRCDIR)/*.c)
OBJ=$(SRC:$(SRCDIR)/%.c=$(OBJDIR)/%.o)
SRCLIB=$(wildcard $(SRCLIBDIR)/*.c)
OBJLIB=$(SRCLIB:$(SRCLIBDIR)/%/c=$(OBJLIBDIR)/%.o)
BIN=$(BINDIR)/$(BINNAME)

all: $(BIN)

	
$(BIN): $(OBJ) $(BINDIR)
	$(CC) $(LDFLAGS) $(OBJ) -o $@

$(OBJDIR)/%.o: $(SRCDIR)/%.c $(SRCDIR)/%.h $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@

$(OBJDIR)/%.o: $(SRCDIR)/%.c $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@



bibliotheques: $(OBJLIB)

$(OBJLIBDIR)/%.o: $(SRCLIBDIR)/%.c $(SRCLIBDIR)/%.h $(OBJLIBDIR)
	$(CC) $(CFLAGS) $< -o $@

$(LIBDIR)/%.a: $(OBJLIBDIR)/%.o $(LIBDIR)
	ar r $< $@;
	ar t $<;
	ranlib $<



$(OBJLIBDIR): $(OBJDIR)
	mkdir $(OBJLIBDIR)

$(LIBDIR):
	mkdir $(LIBDIR)




$(OBJDIR):
	mkdir $(OBJDIR)

$(BINDIR):
	mkdir $(BINDIR)

clean:
	rm -rf $(OBJDIR)

veryclean: clean
	rm -rf $(BINDIR)

cp: $(BIN)
	cp $(BIN) $(BINNAME)
