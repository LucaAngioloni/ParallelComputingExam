BINARY=sequential
SOURCES= main.cpp sequential_integral_image.cpp
LIBRARY=exec
INCPATHS= ../utils
LIBPATHS=
CFLAGS=-c -Wall
CC=g++

OBJECTS=$(SOURCES:.cpp=.o)
INCFLAGS=$(foreach TMP,$(INCPATHS),-I$(TMP))
LIBFLAGS=$(foreach TMP,$(LIBPATHS),-L$(TMP))


all: $(SOURCES) $(BINARY)

$(BINARY): $(OBJECTS)
	$(CC) $(LIBFLAGS) $(OBJECTS) -o $@

.cpp.o:
	$(CC) $(INCFLAGS) $(CFLAGS) $< -o $@

distclean: clean
	rm -f $(BINARY)

clean:
	rm -f $(OBJECTS)