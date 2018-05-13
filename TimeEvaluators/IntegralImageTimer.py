import numpy as np

import os
import subprocess
import json

repeats = 5

threads = [1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024]

directory = 'images/'

images = []

for dirpath,_,filenames in os.walk(directory):
    for f in filenames:
        if f.endswith('.jpg') and 'size' in f:
            images.append(os.path.abspath(os.path.join(dirpath, f)))

images.sort()

sequential_comand = "../IntegralImages/Sequential/sequential -json"
omp_comand = "../IntegralImages/OpenMP/openmp -json"
cuda_comand = "../IntegralImages/CUDA/cuda -json"

squential_results = np.zeros((repeats,len(images)))
omp_results = np.zeros((repeats,len(images), len(threads)))
cuda_results = np.zeros((repeats,len(images), len(threads)))

j = 0
for image in images:
    print("Working on image: " + str(j))
    for i in range(repeats):
        com_sequential = sequential_comand + " -p " + image
        process = subprocess.Popen(com_sequential.split(), stdout=subprocess.PIPE)
        output, error = process.communicate()
        output = output.decode("utf-8")
        json_data = json.loads(output)
        squential_results[i,j] = json_data["time"]

        t = 0
        for thread in threads:
            com_omp = omp_comand + " -p " + image + " -t " + str(thread)
            process = subprocess.Popen(com_omp.split(), stdout=subprocess.PIPE)
            output, error = process.communicate()
            output = output.decode("utf-8")
            json_data = json.loads(output)
            omp_results[i,j,t] = json_data["time"]

            com_cuda = cuda_comand + " -p " + image + " -t " + str(thread)
            process = subprocess.Popen(com_cuda.split(), stdout=subprocess.PIPE)
            output, error = process.communicate()
            output = output.decode("utf-8")
            json_data = json.loads(output)
            cuda_results[i,j,t] = json_data["time"]
            t = t+1
    j = j+1

print(squential_results)
print(omp_results)
print(cuda_results)

np.save("sequential_results_integral.npy", squential_results)
np.save("omp_results_integral.npy", omp_results)
np.save("cuda_results_integral.npy", cuda_results)
