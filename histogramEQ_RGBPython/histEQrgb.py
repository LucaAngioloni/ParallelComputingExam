import cv2
import numpy as np

image = cv2.imread('poppy.jpg')

img0 = image[:,:,0]
img1 = image[:,:,1]
img2 = image[:,:,2]

equ0 = cv2.equalizeHist(img0)
equ1 = cv2.equalizeHist(img1)
equ2 = cv2.equalizeHist(img2)

equ = np.zeros(image.shape)
equ[:,:,0] = equ0
equ[:,:,1] = equ1
equ[:,:,2] = equ2

cv2.imwrite('equ.jpg', equ)
