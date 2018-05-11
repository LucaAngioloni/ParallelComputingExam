#include <iostream>
#include <cuda_runtime.h>

using namespace std;

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

void main()
{
    //M is number of rows
    //N is number of columns

    int M=3,N=2, m_e=0;
    int total_e=M*N;
    int widthstep=total_e*sizeof(int);

    int * matrix_a= (int *)malloc(widthstep);
    int * matrix_b= (int *)malloc(widthstep);

    cout<<"Enter elements for "<< M<<"x"<<N<<" matrix";

    for(int r=0;r<M;r++)
    {
        for(int c=0; c<N;c++)
        {
            cout<<"Enter Matrix element [ "<<r<<","<<c<<"]";
            cin>>m_e;
            matrix_a[r*N+c]=m_e;
            matrix_b[r*N+c]=0;
        }
    }

    int * d_matrix_a, * d_matrix_b;

    cout<<"Input:"<<endl;

    for(int r=0;r<M;r++)
    {
        for(int c=0; c<N;c++)
        {
            cout << matrix_a[r*N+c]<<" ";
        }
        cout << endl;
    }

    cout<<endl;

    cudaMalloc(&d_matrix_a,widthstep);
    cudaMalloc(&d_matrix_b,widthstep);

    cudaMemcpy(d_matrix_a,matrix_a,widthstep,cudaMemcpyHostToDevice);
    cudaMemcpy(d_matrix_b,matrix_b,widthstep,cudaMemcpyHostToDevice);

    //Creating a grid where the number of blocks are equal to the number of pixels or input matrix elements.

    //Each block contains only one thread.

    dim3 grid(N,M);

    image_integral<<<grid,1>>>(d_matrix_a, d_matrix_b,M,N);

    cudaThreadSynchronize();

    cudaMemcpy(matrix_b,d_matrix_b,widthstep,cudaMemcpyDeviceToHost);

    cout<<"The Summed Area table is: "<<endl;

    for(int r=0;r<M;r++)
    {
        for(int c=0; c<N;c++)
        {
            cout << matrix_b[r*N+c]<<" ";
        }
        cout << endl;
    }

    system("pause");

    cudaFree(d_matrix_a);
    cudaFree(d_matrix_b);
    free(matrix_a);
    free(matrix_b);
}