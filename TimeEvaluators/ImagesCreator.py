import numpy as np
import cv2

img = cv2.imread('images/original.jpg')

height = img.shape[0]
width = img.shape[1]

sizes = [100, 200, 500, 1000, 2000, 5000, 10000]

res_sizes = []

num = 0

for size in sizes:
	scale_x = size/height
	out = cv2.resize(img, None, fx=scale_x, fy=scale_x)
	cv2.imwrite('images/size_' + str(num) + '.jpg', out, [int(cv2.IMWRITE_JPEG_QUALITY), 25])
	res_sizes.append(out.shape[0]*out.shape[1])
	num = num+1

print(res_sizes)