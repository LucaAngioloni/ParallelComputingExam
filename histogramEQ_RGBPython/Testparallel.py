import cv2
import numpy as np

image = cv2.imread('poppy.jpg', 0)

pixels = image.flatten()

hist, be = np.histogram(pixels, 256, range=(0,255))
#print(hist)

chisto = np.cumsum(hist)
#print(chisto)

lookup = np.array(255*chisto/pixels.shape[0], dtype=np.uint8)
#lookup = 255*chisto/pixels.shape[0]

hist0, b0 = np.histogram(pixels[0:int(pixels.shape[0]/4)], 256, range=(0,255))
chisto0 = np.cumsum(hist0)
lookup0 = 255*chisto0/pixels.shape[0]
#print(chisto0)
#print(hist0)

hist1, b1 = np.histogram(pixels[int(pixels.shape[0]/4):int((pixels.shape[0]/4))*2], 256, range=(0,255))
chisto1 = np.cumsum(hist1)
lookup1 = 255*chisto1/pixels.shape[0]
#print(chisto1)
#print(hist1)

hist2, b2 = np.histogram(pixels[int((pixels.shape[0]/4))*2:int((pixels.shape[0]/4))*3], 256, range=(0,255))
chisto2 = np.cumsum(hist2)
lookup2 = 255*chisto2/pixels.shape[0]
#print(chisto2)
#print(hist2)

hist3, b3 = np.histogram(pixels[int((pixels.shape[0]/4))*3:pixels.shape[0]], 256, range=(0,255))
chisto3 = np.cumsum(hist3)
lookup3 = 255*chisto3/pixels.shape[0]
#print(chisto3)
#print(hist3)


hist_final = np.array(hist0+hist1+hist2+hist3, dtype=np.float64)

diff2 = np.abs(hist_final-np.array(hist, dtype=np.float64))

print("Hist diff")
print("mean: " + str(np.mean(diff2)))
print("std: " + str(np.std(diff2)))
print("max: " + str(np.max(diff2)))
print("min: " + str(np.min(diff2)))

lookup_final = np.array(lookup0+lookup1+lookup2+lookup3, dtype=np.uint8)
#lookup_final = lookup0+lookup1+lookup2+lookup3

diff = np.abs(lookup-lookup_final)

print("Lookup diff")
print("mean: " + str(np.mean(diff)))
print("std: " + str(np.std(diff)))
print("max: " + str(np.max(diff)))
print("min: " + str(np.min(diff)))


chisto_final = np.array(chisto0+chisto1+chisto2+chisto3, dtype=np.float64)

diff1 = np.abs(chisto-chisto_final)

print("Chisto diff")
print("mean: " + str(np.mean(diff1)))
print("std: " + str(np.std(diff1)))
print("max: " + str(np.max(diff1)))
print("min: " + str(np.min(diff1)))