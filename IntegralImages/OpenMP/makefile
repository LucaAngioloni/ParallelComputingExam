BINARY=openmp
SOURCES= main.cpp openmp_integral_image.cpp
LIBRARY=exec
INCPATHS= ../utils
LIBPATHS=
CFLAGS=-fopenmp -c -Wall

UNAME_S := $(shell uname -s)

CC=g++

ifeq ($(UNAME_S),Darwin)
		CC=g++-8
endif

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