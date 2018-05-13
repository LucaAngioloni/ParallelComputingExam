// MIT License

// Copyright (c) 2018 SqrtPapere and Luca Angioloni

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

#include "openmp_integral_image.h"

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"
#include <iostream>
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

#include "input_parser.h"

#include <omp.h>

void print_help(){
    std::cout << "usage: openmp -p <input image path> [-t <number of threads (int) (default:platform dependent)>]" << std::endl << std::endl;

    std::cout << "To see this menu again: openmp -h" << std::endl;
}

int main(int argc, char **argv){
    InputParser input(argc, argv);
    if(input.cmdOptionExists("-h")){
        print_help();
        return 0;
    }

    if(input.cmdOptionExists("-p")){
        std::string in_file = input.getCmdOption("-p");
        if (in_file == "")
        {
            std::cout << "No input file!\n\n";
            print_help();
            return 2;
        }
    	int width, height, bpp;
    	uint8_t* image = stbi_load(in_file.c_str(), &width, &height, &bpp, 1);

    	std::cout << "w: " << width << " h: " << height << " b: " << bpp << std::endl;

        std::cout << "Calculating Integral Image..." << std::endl;

        int threads = 0;

        if(input.cmdOptionExists("-t")){
            std::string num_threads_string = input.getCmdOption("-t");
            if (num_threads_string != ""){
                threads = atoi(num_threads_string.c_str());
            }
        }

        double start_omp = omp_get_wtime();

        unsigned long* integral_image = integralImageMP(image, height, width, threads);

        double end_omp = omp_get_wtime();

        std::cout << "Total time wall time omp: " << end_omp - start_omp <<std::endl;

        delete [] integral_image;

        stbi_image_free(image);
        
    	return 0;
    } else {
        std::cout << "No input file!\n\n";
        print_help();
        return 1;
    }
}