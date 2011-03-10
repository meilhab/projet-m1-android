
CC=gcc -g
CFLAGS=-c -Wall -Wextra
LDFLAGS=-lm
SRC=libgraphe.c libliste.c
OBJDIR=../../obj/lib
OBJDDIR=../../obj
OBJ=$(OBJDIR)/libgraphe.o $(OBJDIR)/libliste.o
LIBDIR=../../lib
LIB=$(LIBDIR)/libgraphe.a $(LIBDIR)/libliste.a

all: $(LIB)

$(OBJDIR)/%.o: %.c %.h $(OBJDIR)
	$(CC) $(CFLAGS) $< -o $@

$(LIBDIR)/%.a: $(OBJDIR)/%.o $(LIBDIR)
	ar r $@ $<;
	ar t $@;
	ranlib $@

$(OBJDIR): $(OBJDDIR)
	mkdir $(OBJDIR)

$(OBJDDIR):
	mkdir $(OBJDDIR)

$(LIBDIR):
	mkdir $(LIBDIR)

