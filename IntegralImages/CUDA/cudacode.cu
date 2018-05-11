#include "cudacode.h"
#include <cuda.h>

__global__ void image_integral(int *a, int*b, int rowsTotal,int colsTotal)
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