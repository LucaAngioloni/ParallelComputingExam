BINARY=openmp
SOURCES= main.cpp openmp_integral_image.cpp
LIBRARY=exec
INCPATHS= ../utils
LIBPATHS=
CFLAGS=-fopenmp -c -Wall -O3
CC=g++-8

OBJECTS=$(SOURCES:.cpp=.o)
INCFLAGS=$(foreach TMP,$(INCPATHS),-I$(TMP))
LIBFLAGS=$(foreach TMP,$(LIBPATHS),-L$(TMP))


all: $(SOURCES) $(BINARY)

$(BINARY): $(OBJECTS)
	$(CC) $(LIBFLAGS) $(OBJECTS) -fopenmp -o $@

.cpp.o:
	$(CC) $(INCFLAGS) $(CFLAGS) $< -o $@

distclean: clean
	rm -f $(BINARY)

clean:
	rm -f $(OBJECTS)