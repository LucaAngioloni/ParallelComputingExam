import numpy as np

import os
import subprocess
import json

from timeit import default_timer as timer

repeats = 5

threads = [1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096]

directory = 'images/'

images = []

for dirpath,_,filenames in os.walk(directory):
    for f in filenames:
        if f.endswith('.jpg') and 'size' in f:
            images.append(os.path.abspath(os.path.join(dirpath, f)))

images.sort()

cuda_comand = "../IntegralImages/CUDA/cuda -json"

cuda_results = np.zeros((repeats,len(images)))

start_time = timer()

j = 0
for image in images:
    print("Working on image: " + str(j))
    for i in range(repeats):
        com_cuda = cuda_comand + " -p " + image
        process = subprocess.Popen(com_cuda.split(), stdout=subprocess.PIPE)
        output, error = process.communicate()
        output = output.decode("utf-8")
        json_data = json.loads(output)
        cuda_results[i,j] = json_data["time"]
    j = j+1

end_time = timer()

print(cuda_results)

print("\n\nTime elapsed: " + "{0:.2f}".format((end_time - start_time)) + " s")

np.save("cuda_results_integral_max-threads.npy", cuda_results)
