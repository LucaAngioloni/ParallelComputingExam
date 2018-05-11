all: program

program: cudacode.o
    g++ -o program -L/usr/local/cuda/lib64 -lcuda -lcudart main.cpp  cudacode.o 

cudacode.o:
    nvcc -c cudacode.cu 

clean: rm -rf *o program