import numpy as np
import matplotlib.pyplot as plt

sequential_results = np.load("sequential_results_integral.npy")
omp_results = np.load("omp_results_integral.npy")
cuda_results = np.load("cuda_results_integral.npy")

sequential_results_mean = np.mean(sequential_results, axis=0)
omp_results_mean = np.mean(omp_results, axis=0)
cuda_results_mean = np.mean(cuda_results, axis=0)

sizes = np.power(np.array([100, 200, 500, 1000, 2000, 5000, 10000]), 1)

threads = np.array([1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024])

plt.plot(sizes, sequential_results_mean)
plt.plot(sizes, omp_results_mean[:,3])
plt.plot(sizes, cuda_results_mean[:,10])
plt.show()

plt.plot(np.log(threads), omp_results_mean[6,:])
plt.show()