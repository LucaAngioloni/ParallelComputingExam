import numpy as np
import matplotlib.pyplot as plt

serial_results = np.load("serial_results.npy")
parallel_results = np.load("parallel_results.npy")

serial_results_mean = np.mean(serial_results, axis=0)
parallel_results_mean = np.mean(parallel_results, axis=0)

sizes = np.power(np.array([100, 200, 500, 1000, 2000, 5000, 10000]), 1)

threads = np.array([1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024])

plt.plot(sizes, serial_results_mean)
plt.plot(sizes, parallel_results_mean[:,3])
plt.show()


plt.plot(np.log(threads), parallel_results_mean[6,:])
plt.show()