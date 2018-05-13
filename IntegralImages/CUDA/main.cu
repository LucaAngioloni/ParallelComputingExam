#include <iostream>
#include <cuda_runtime.h>
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"
#include <sys/time.h>

#include "input_parser.h"

using namespace std;


int print = 0;

void print_help(){
    std::cout << "usage: cuda -p <input image path> [-t <number of threads (int) (default:platform dependent)>] [-json]" << std::endl << std::endl;

    std::cout << "To see this menu again: cuda -h" << std::endl;
}

unsigned long * integralImage(uint8_t*x, int n, int m){
    unsigned long * out = (unsigned long  *)malloc(n*m*sizeof(unsigned long));
    
    for (int i = 0; i < n; ++i)
    {
        for (int j = 0; j < m; ++j)
        {
            unsigned long val = x[i*m + j];
            if (i>=1)
            {
                val += out[(i-1)*m + j];
                if (j>=1)
                {
                    val += out[i*m + j - 1] - out[(i-1)*m + j - 1];
                }
            } else {
                if (j>=1)
                {
                    val += out[i*m + j -1];
                }
            }
            out[i*m + j] = val;
        }
    }
    
    return out;
}

__global__ void sum_rows(unsigned long *a, unsigned long *b, int rowsTotal, int colsTotal, int n_thread)
{
    // Thread Ids equal to block Ids because the each blocks contains one thread only.
    //int col = blockIdx.x;
    int row = blockIdx.x;
    int size_per_thread = rowsTotal/n_thread;
    int start = row*size_per_thread;
    int end = start + size_per_thread;

    if (row==n_thread-1)
    {
        start = (n_thread-1)*size_per_thread;
        end = rowsTotal;
    }
    for (int k = start; k < end; ++k)
    {
            for (int j = 0; j < colsTotal; ++j)
            {
                if (j >=1)
                {
                    b[k*colsTotal + j] = a[k*colsTotal + j] + b[k*colsTotal + j - 1];
                } else {
                    b[k*colsTotal + j] = a[k*colsTotal + j];
                } 
            }
    }

}

__global__ void sum_columns(unsigned long *a, unsigned long *b, int rowsTotal, int colsTotal, int n_thread)
{
    // Thread Ids equal to block Ids because the each blocks contains one thread only.
    int col = blockIdx.x;
    //int row = blockIdx.y;
    int size_per_thread = colsTotal/n_thread;
    int start = col*size_per_thread;
    int end = start + size_per_thread;

    if (col==n_thread-1)
    {
        start = (n_thread-1)*size_per_thread;
        end = colsTotal;
    }
    for (int k = start; k < end; ++k)
    {
        for (int i = 0; i < rowsTotal; ++i)
            {
                if (i >=1)
                {
                    b[i*colsTotal + k] = a[i*colsTotal + k] + b[(i-1)*colsTotal + k];
                } else {
                    b[i*colsTotal + k] = a[i*colsTotal + k];
                } 
        }

    }
}



int main(int argc, char **argv)
{
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

        bool json = input.cmdOptionExists("-json");

        int width, height, bpp;
        uint8_t* matrix_a = stbi_load(in_file.c_str(), &width, &height, &bpp, 1);
        int total_e = width*height;
        int widthstep = total_e*sizeof(unsigned long);

        unsigned long * a = (unsigned long  *)malloc(widthstep);

        for (int i = 0; i < width *height; ++i)
        {
            a[i] = (unsigned long)matrix_a[i];
        }

        if (print==1)
        {
            cout << "Input"<<endl;
            for(int r=0;r<height;r++)
            {
                for(int c=0; c<width;c++)
                {
                    cout << a[r*width+c]<<" ";
                }
                cout << endl;
            }
        }

        if(!json){
        std::cout << "w: " << width << " h: " << height << " b: " << bpp << std::endl;

        std::cout << "Calculating Integral Image..." << std::endl;
        }

        unsigned long * matrix_b= (unsigned long  *)malloc(widthstep);
        unsigned long * matrix_t= (unsigned long  *)malloc(widthstep);


        for(int r=0;r<height;r++)
        {
            for(int c=0; c<width;c++)
            {
                matrix_b[r*width+c]=0;
                matrix_t[r*width+c]=0;
            }
        }

        if(!json){
        std::cout << "Copied image" << std::endl;
        }

        unsigned long * d_matrix_a, * d_matrix_b, * d_matrix_t;


        cudaMalloc(&d_matrix_a,widthstep);
        cudaMalloc(&d_matrix_b,widthstep);
        cudaMalloc(&d_matrix_t,widthstep);


        cudaMemcpy(d_matrix_a,a,widthstep,cudaMemcpyHostToDevice);
        cudaMemcpy(d_matrix_b,matrix_b,widthstep,cudaMemcpyHostToDevice);
        cudaMemcpy(d_matrix_t,matrix_t,widthstep,cudaMemcpyHostToDevice);

        if(!json){
        std::cout << "starting cuda" << std::endl;
        }

        struct timeval start, end;
        gettimeofday(&start, NULL);

        int num_thread = 3000;

        
        sum_rows<<<num_thread,1>>>(d_matrix_a, d_matrix_t,height,width, num_thread);
        sum_columns<<<num_thread,1>>>(d_matrix_t, d_matrix_b,height,width, num_thread);

        cudaThreadSynchronize();


        gettimeofday(&end, NULL);

        double time_tot = ((end.tv_sec  - start.tv_sec) * 1000000u + end.tv_usec - start.tv_usec) / 1.e6;

        if(!json){
        std::cout << "Total parallel time: " << time_tot <<std::endl;
        }

        cudaMemcpy(matrix_b,d_matrix_b,widthstep,cudaMemcpyDeviceToHost);
        cudaMemcpy(matrix_t,d_matrix_t,widthstep,cudaMemcpyDeviceToHost);


        if (print==1)
        {
            cout << "Cuda Output"<<endl;
            for(int r=0;r<height;r++)
            {
                for(int c=0; c<width;c++)
                {
                    cout << matrix_b[r*width+c]<<" ";
                }
                cout << endl;
            }
        }
        
        if(!json){
        std::cout << "starting serial" << std::endl;
        }

        gettimeofday(&start, NULL);

        unsigned long* integral_image = integralImage(matrix_a, height, width);

        gettimeofday(&end, NULL);

        double time_tot_serial = ((end.tv_sec  - start.tv_sec) * 1000000u + end.tv_usec - start.tv_usec) / 1.e6;

        if(!json){
        std::cout << "Total serial time: " << time_tot_serial <<std::endl;

        std::cout << "finish serial" << std::endl;
        }

        int count =0;

        for (int i = 0; i < width*height; ++i)
        {
            if (integral_image[i]!=matrix_b[i])
            {
                //std::cout<<"errore";
                count++;
            }
        }

        if(!json){
        std::cout<<"Errors ";
        std::cout<<count;
        std::cout<<" over ";
        std::cout<<width*height<<std::endl;
        }

        if (json)
        {
            std::cout << "{time: " << time_tot << ", width: " << width << ", height: " << height << ", errors: " << count << ", time_serial: " << time_tot_serial << "}" << std::endl;   
        }

        cudaFree(d_matrix_a);
        cudaFree(d_matrix_b);
        free(a);
        free(matrix_b);

        stbi_image_free(matrix_a);
        return 0;
    } else { // no valid arguments
        std::cout << "No input file!\n\n";
        print_help();
        return 1;
    }
}