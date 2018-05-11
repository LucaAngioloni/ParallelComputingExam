#include <iostream>
#include <cuda_runtime.h>
#include "cudacode.h"

using namespace std;

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