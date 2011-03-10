include MakeInclude

#CC=gcc -g
#CFLAGS=-c -Wall -Wextra
#LDFLAGS=-lm
#BINNAME=gestionGraphe
#LIBNAME=lib
#SRCDIR=./src
#OBJDIR=./obj
#BINDIR=./bin
#LIBDIR=./$(LIBNAME)
#SRCLIBDIR=$(SRCDIR)/$(LIBNAME)
#SRC=$(wildcard $(SRCDIR)/*.c)
#OBJ=$(SRC:$(SRCDIR)/%.c=$(OBJDIR)/%.o)
#BIN=$(BINDIR)/$(BINNAME)
#LIB=$(LIBDIR)/libgraphe.a $(LIBDIR)/libliste.a
#MAKE=/usr/bin/make

all: $(BIN)

	
$(BIN): $(OBJ) $(BINDIR) bibliotheques
	$(CC) $(LDFLAGS) $(OBJ) -o $@ $(LIB)

$(OBJDIR)/%.o: $(SRCDIR)/%.c $(SRCDIR)/%.h $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@

$(OBJDIR)/%.o: $(SRCDIR)/%.c $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@


bibliotheques:
	(cd $(SRCLIBDIR) ; $(MAKE) all)

$(OBJDIR):
	mkdir $(OBJDIR)

$(BINDIR):
	mkdir $(BINDIR)

clean:
	rm -rf $(OBJDIR);
	rm -rf $(LIBDIR)

veryclean: clean
	rm -rf $(BINDIR)

cp: $(BIN)
	cp $(BIN) $(BINNAME)
