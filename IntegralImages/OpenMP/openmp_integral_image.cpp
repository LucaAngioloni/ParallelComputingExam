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

unsigned long * integralImageMP(img_type*x, int n, int m){
    unsigned long * out = new unsigned long[n*m];
    unsigned long * rows = new unsigned long[n*m];

    int i, j;

    #pragma omp parallel
    #pragma omp for
    for (i = 0; i < n; ++i)
    {
        for (j = 0; j < m; ++j)
        {
            if (j >=1)
            {
                rows[i*m + j] = x[i*m + j] + rows[i*m + j - 1];
            } else {
                rows[i*m + j] = x[i*m + j];
            } 
        }
    }

    #pragma omp parallel
    #pragma omp for
    for (j = 0; j < m; ++j)
    {
        for (i = 0; i < n; ++i)
        {
            if (i >=1)
            {
                out[i*m + j] = rows[i*m + j] + out[(i-1)*m + j];
            } else {
                out[i*m + j] = rows[i*m + j];
            } 
        }
    }

    delete [] rows;
    return out;
}