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

// #define ROUND_UP(x, s) (((x)+((s)-1)) & -(s))

// #include <x86intrin.h>

#include "openmp_integral_image.h"

// inline void transpose_scalar_block(unsigned long *A, unsigned long *B, const int lda, const int ldb, const int block_size) {
//     #pragma omp parallel for
//     for(int i=0; i<block_size; i++) {
//         for(int j=0; j<block_size; j++) {
//             B[j*ldb + i] = A[i*lda +j];
//         }
//     }
// }

// inline void transpose_block(unsigned long *A, unsigned long *B, const int n, const int m, const int lda, const int ldb, const int block_size) {
//     #pragma omp parallel for
//     for(int i=0; i<n; i+=block_size) {
//         for(int j=0; j<m; j+=block_size) {
//             transpose_scalar_block(&A[i*lda +j], &B[j*ldb + i], lda, ldb, block_size);
//         }
//     }
// }

void transpose(unsigned long *src, unsigned long *dst, const int N, const int M) {
    #pragma omp parallel for
    for(int n = 0; n<N*M; n++) {
        int i = n/N;
        int j = n%N;
        dst[n] = src[M*j + i];
    }
}

// void transpose_sequential(unsigned long *src, unsigned long *dst, const int N, const int M){
//     for (int i = 0; i < N; ++i)
//     {
//         for (int j = 0; j < M; ++j)
//         {
//             dst[j*N + i] = src[i*M + j];
//         }
//     }
// }

unsigned long * integralImageMP(unsigned long*x, int n, int m, int threads){
    // int lda = ROUND_UP(m, 16);
    // int ldb = ROUND_UP(n, 16);
    unsigned long * out = new unsigned long[n*m];
    unsigned long * rows = new unsigned long[n*m];
    // unsigned long * out = new unsigned long[lda*ldb];
    // unsigned long * rows = new unsigned long[lda*ldb];

    if(threads != 0){
        omp_set_dynamic(0);     // Explicitly disable dynamic teams
        omp_set_num_threads(threads); // Use n threads for all consecutive parallel regions
    }

    // #pragma omp parallel
    // #pragma omp for
    #pragma omp parallel for
    for (int i = 0; i < n; ++i)
    {
        for (int j = 0; j < m; ++j)
        {
            if (j >=1)
            {
                rows[i*m + j] = x[i*m + j] + rows[i*m + j - 1];
            } else {
                rows[i*m + j] = x[i*m + j];
            } 
        }
    }

    transpose(rows, out, n, m);
    //transpose_sequential(rows, out, n, m);
    //transpose_block(rows, out, n, m, lda, ldb, 16);

    // #pragma omp for
    // for (int j = 0; j < m; ++j)
    // {
    //     for (int i = 0; i < n; ++i)
    //     {
    //         if (i >=1)
    //         {
    //             out[i*m + j] = x[i*m + j] + out[(i-1)*m + j];
    //         } else {
    //             out[i*m + j] = x[i*m + j];
    //         } 
    //     }
    // }

    #pragma omp parallel for
    for (int i = 0; i < n; ++i)
    {
        for (int j = 0; j < m; ++j)
        {
            if (j >=1)
            {
                rows[i*m + j] = out[i*m + j] + rows[i*m + j - 1];
            } else {
                rows[i*m + j] = out[i*m + j];
            } 
        }
    }

    transpose(rows, out, m, n);
    //transpose_sequential(rows, out, m, n);
    //transpose_block(rows, out, m, n, ldb, lda, 16);

    delete [] rows;
    return out;
}