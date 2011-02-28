CC=gcc -g
CFLAGS=-c -Wall -Wextra
LDFLAGS=-lm
SRCDIR=./src
OBJDIR=./obj
BINDIR=./bin
BINNAME=liste
SRC=$(wildcard $(SRCDIR)/*.c)
OBJ=$(SRC:$(SRCDIR)/%.c=$(OBJDIR)/%.o)
BIN=$(BINDIR)/$(BINNAME)

all: $(BIN)
	
$(BIN): $(OBJ) $(BINDIR)
	$(CC) $(LDFLAGS) $(OBJ) -o $@

$(OBJDIR)/%.o: $(SRCDIR)/%.c $(SRCDIR)/%.h $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@

$(OBJDIR)/%.o: $(SRCDIR)/%.c $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@

$(OBJDIR):
	mkdir $(OBJDIR)

$(BINDIR):
	mkdir $(BINDIR)

clean:
	rm -rf $(OBJDIR);

veryclean: clean
	rm -rf $(BINDIR)

cp: $(BIN)
	cp $(BIN) $(BINNAME)
