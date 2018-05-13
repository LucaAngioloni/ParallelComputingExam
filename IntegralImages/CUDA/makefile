BINARY=cuda
SOURCES= main.cu
LIBRARY=exec
INCPATHS= ../utils
LIBPATHS=
CFLAGS=-fopenmp -c -Wall
CC=nvcc

OBJECTS=$(SOURCES:.cu=.o)
INCFLAGS=$(foreach TMP,$(INCPATHS),-I$(TMP))
LIBFLAGS=$(foreach TMP,$(LIBPATHS),-L$(TMP))


all:
	$(CC) $(INCFLAGS) $(SOURCES) -o $(BINARY)

distclean: clean
	rm -f $(BINARY)

clean:
	rm -f $(OBJECTS)