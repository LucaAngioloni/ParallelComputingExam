import numpy as np
import matplotlib.pyplot as plt

serial_results = np.load("serial_results.npy")
parallel_results = np.load("parallel_results.npy")

serial_results_mean = np.mean(serial_results, axis=0)
parallel_results_mean = np.mean(parallel_results, axis=0)

sizes = np.power(np.array([16000, 64000, 400000, 1600000, 6400000, 40005000, 160010000]), 1)

threads = np.array([1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024])

plt.plot(sizes, serial_results_mean, label='Serial', marker='o')
plt.xlabel('pixel')
plt.ylabel('time(seconds)')
best_wrt_nthread = np.min(parallel_results_mean, axis=1)
plt.plot(sizes, best_wrt_nthread, label='Java Thread', marker='o')
plt.legend(framealpha=0.5)

# plt.plot(sizes, parallel_results_mean[:,3])
plt.show()

plt.xlabel('threads')
plt.ylabel('time(seconds)')
plt.plot(threads, parallel_results_mean[6,:], marker='o')
plt.show()

#Speed Up

speed_parallel = serial_results_mean / parallel_results_mean[:,3]

plt.xlabel('pixel')
plt.ylabel('Speed Up Factor')
plt.plot(sizes, speed_parallel, marker='o')
plt.show()