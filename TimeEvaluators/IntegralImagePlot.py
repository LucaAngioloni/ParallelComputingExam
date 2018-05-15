import numpy as np
import matplotlib.pyplot as plt

sequential_results = np.load("sequential_results_integral.npy")
omp_results = np.load("omp_results_integral.npy")
cuda_results = np.load("cuda_results_integral.npy")

sequential_results_mean = np.mean(sequential_results, axis=0)
omp_results_mean = np.mean(omp_results, axis=0)
cuda_results_mean = np.mean(cuda_results, axis=0)

#sizes = np.power(np.array([100, 200, 500, 1000, 2000, 5000, 10000]), 1)
sizes = np.power(np.array([16000, 64000, 400000, 1600000, 6400000, 40005000, 160010000]), 1)

threads = np.array([1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096])

plt.xlabel('Pixels')
plt.ylabel('time(seconds)')
plt.plot(sizes, sequential_results_mean, label='Sequential')
plt.plot(sizes, omp_results_mean[:,3], label='OpenMP')
plt.plot(sizes, cuda_results_mean[:,7], label='Cuda')
plt.legend(framealpha=0.5)
plt.show()

plt.xlabel('threads')
plt.ylabel('time(seconds)')
plt.plot(threads, omp_results_mean[6,:], label='OpenMP')
plt.plot(threads, cuda_results_mean[6,:], label='Cuda')
plt.legend(framealpha=0.5)
plt.show()