include ../../MakeInclude
#CC=gcc -g
#CFLAGS=-c -Wall -Wextra
#LDFLAGS=-lm
#SRC=libgraphe.c libliste.c
#OBJDIR=../../obj/lib
#OBJDDIR=../../obj
#OBJ=$(OBJDIR)/libgraphe.o $(OBJDIR)/libliste.o
#LIBDIR=../../lib
#LIB=$(LIBDIR)/libgraphe.a $(LIBDIR)/libliste.a

all: $(LIBLIB)

$(OBJLIBDIR)/%.o: %.c %.h $(OBJLIBDIR)
	$(CC) $(CFLAGS) $< -o $@

$(LIBLIBDIR)/%.a: $(OBJLIBDIR)/%.o $(LIBLIBDIR)
	ar r $@ $<;
	ar t $@;
	ranlib $@

$(OBJLIBDIR): $(OBJPLIBDIR)
	mkdir $(OBJLIBDIR)

$(OBJPLIBDIR):
	mkdir $(OBJPLIBDIR)

$(LIBLIBDIR):
	mkdir $(LIBLIBDIR)

