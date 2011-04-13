CC=gcc -g
CFLAGS=-c -Wall -Wextra
LDFLAGS=-lm
#SRCDIR=./src
#OBJDIR=./obj
#BINDIR=./bin
#SRC=$(wildcard $(SRCDIR)/*.c)
#OBJ=$(SRC:$(SRCDIR)/%.c=$(OBJDIR)/%.o)
#BIN=$(BINDIR)/premier
SRC=$(wildcard *.c)
OBJ=$(SRC:%c=%.o)
BIN=connexionArbitre

all: $(BIN)
	
$(BIN): $(OBJ)
	$(CC) $(LDFLAGS) $(OBJ) -o $@

%.o: %.c %.h
	$(CC) $(CFLAGS) $< -o $@

%.o: %.c
	$(CC) $(CFLAGS) $< -o $@

clean:
	rm -rf $(OBJ);

veryclean: clean
	rm -rf $(BIN)

