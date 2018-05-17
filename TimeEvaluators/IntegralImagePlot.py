import numpy as np
import matplotlib.pyplot as plt

sequential_results = np.load("sequential_results_integral.npy")
omp_results = np.load("omp_results_integral.npy")
cuda_results = np.load("cuda_results_integral.npy")
cuda_max_threads = np.load("cuda_results_integral_max-threads.npy")

sequential_results_mean = np.mean(sequential_results, axis=0)
omp_results_mean = np.mean(omp_results, axis=0)
cuda_results_mean = np.mean(cuda_results, axis=0)
cuda_max_mean = np.mean(cuda_max_threads, axis=0)

omp_min = np.min(omp_results_mean, axis=1)

#sizes = np.power(np.array([100, 200, 500, 1000, 2000, 5000, 10000]), 1)
sizes = np.power(np.array([16000, 64000, 400000, 1600000, 6400000, 40005000, 160010000]), 1)

threads = np.array([1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096])

plt.xlabel('Pixels')
plt.ylabel('time(seconds)')
plt.plot(sizes, sequential_results_mean, label='Sequential', marker='o')
plt.plot(sizes, omp_results_mean[:,6], label='OpenMP', marker='o')
plt.plot(sizes, cuda_results_mean[:,7], label='Cuda', marker='o')
plt.plot(sizes, cuda_max_mean, label='Cuda Max', marker='o')
# plt.plot(sizes, omp_min, label='OMP min', marker='o')
plt.legend(framealpha=0.5)
plt.show()

plt.xlabel('threads')
plt.ylabel('time(seconds)')
plt.plot(threads, omp_results_mean[6,:], label='OpenMP', marker='o')
plt.legend(framealpha=0.5)
plt.show()

threads = np.append(threads, 10000) 
to_plot = cuda_results_mean[6,:]

to_plot = np.append(to_plot, cuda_max_mean[-1])

plt.xlabel('threads')
plt.ylabel('time(seconds)')
plt.plot(threads, to_plot, label='Cuda', marker='o')
plt.legend(framealpha=0.5)
plt.show()



#Speed up

speed_omp = sequential_results_mean / omp_results_mean[:,6]
speed_cuda = sequential_results_mean / cuda_results_mean[:,7]
speed_cuda_max = sequential_results_mean / cuda_max_mean

plt.xlabel('Pixels')
plt.ylabel('Speed Up Factor')
plt.plot(sizes, speed_omp, label='OpenMP', marker='o')
plt.plot(sizes, speed_cuda, label='Cuda', marker='o')
# plt.plot(sizes, omp_min, label='OMP min', marker='o')
plt.legend(framealpha=0.5)
plt.show()

plt.xlabel('Pixels')
plt.ylabel('Speed Up Factor')
plt.plot(sizes, speed_cuda_max, label='Cuda Max', marker='o')
# plt.plot(sizes, omp_min, label='OMP min', marker='o')
plt.legend(framealpha=0.5)
plt.show()