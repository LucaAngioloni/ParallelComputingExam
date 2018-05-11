#include <iostream>
#include <cuda_runtime.h>
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"
#include <sys/time.h>

using namespace std;

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

__global__ void image_integral(unsigned long *a, unsigned long *b, int rowsTotal, int colsTotal)
{
    // Thread Ids equal to block Ids because the each blocks contains one thread only.
    int col = blockIdx.x;
    int row = blockIdx.y;
    int temp=0;

    if(col < colsTotal && row < rowsTotal)
    {
        // The first loop iterates from zero to the Y index of the thread which represents the corresponding element of the output/input array.  
        for(int r=0;r<=row;r++)
        {
            // The second loop iterates from zero to the X index of the thread which represents the corresponding element of the output/input array  
            for(int c=0; c<=col; c++)
            {
                temp = temp+a[r*colsTotal+c];
            }
        }
    }

    //Transfer the final result to the output array
    b[row*colsTotal+col]=temp;
}

int main()
{

    int width, height, bpp;
    uint8_t* matrix_a = stbi_load("poppy.jpg", &width, &height, &bpp, 1);
    int total_e = width*height;
    int widthstep = total_e*sizeof(unsigned long);

    unsigned long * a = (unsigned long  *)malloc(widthstep);

    for (int i = 0; i < width *height; ++i)
    {
        a[i] = (unsigned long)matrix_a[i];
    }

    std::cout << "w: " << width << " h: " << height << " b: " << bpp << std::endl;

    std::cout << "Calculating Integral Image..." << std::endl;

    unsigned long * matrix_b= (unsigned long  *)malloc(widthstep);


    for(int r=0;r<height;r++)
    {
        for(int c=0; c<width;c++)
        {
            matrix_b[r*width+c]=0;
        }
    }

    std::cout << "Copied image" << std::endl;

    unsigned long * d_matrix_a, * d_matrix_b;


    cudaMalloc(&d_matrix_a,widthstep);
    cudaMalloc(&d_matrix_b,widthstep);

    cudaMemcpy(d_matrix_a,a,widthstep,cudaMemcpyHostToDevice);
    cudaMemcpy(d_matrix_b,matrix_b,widthstep,cudaMemcpyHostToDevice);

    //Creating a grid where the number of blocks are equal to the number of pixels or input matrix elements.

    //Each block contains only one thread.

    dim3 grid(height,width);

    std::cout << "starting cuda" << std::endl;


    // struct timeval start, end;
    // gettimeofday(&start, NULL);

    image_integral<<<grid,1>>>(d_matrix_a, d_matrix_b,height,width);

    cudaThreadSynchronize();

    // gettimeofday(&end, NULL);

    // double time_tot = ((end.tv_sec  - start.tv_sec) * 1000000u + end.tv_usec - start.tv_usec) / 1.e6;

    // std::cout << "Total time: " << time_tot <<std::endl;

    cudaMemcpy(matrix_b,d_matrix_b,widthstep,cudaMemcpyDeviceToHost);

    std::cout << "starting serial" << std::endl;

    unsigned long* integral_image = integralImage(matrix_a, height, width);

    std::cout << "finish serial" << std::endl;
int count =0;

    for (int i = 0; i < width*height; ++i)
    {
        if (integral_image[i]!=matrix_b[i])
        {
            //std::cout<<"errore";
            count++;
        }
    }

    std::cout<<count<<std::endl;
    std::cout<<width*height<<std::endl;


    cudaFree(d_matrix_a);
    cudaFree(d_matrix_b);
    free(a);
    free(matrix_b);

    stbi_image_free(matrix_a);
}