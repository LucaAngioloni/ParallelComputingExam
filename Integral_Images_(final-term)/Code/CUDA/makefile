BINARY=cuda
SOURCES= main.cu
INCPATHS= ../utils
CC=nvcc

OBJECTS=$(SOURCES:.cu=.o)
INCFLAGS=$(foreach TMP,$(INCPATHS),-I$(TMP))


all:
	$(CC) $(INCFLAGS) $(SOURCES) -o $(BINARY)

distclean:
	rm -f $(BINARY)

clean:
	rm -f $(BINARY)